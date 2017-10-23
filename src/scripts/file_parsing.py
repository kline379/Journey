import json
import multiprocessing as mp
from os import listdir
from os.path import basename
from os.path import isdir
from os.path import join
from os.path import splitext

def is_int(s):
    try:
        int(s)
        return True
    except:
        return False

def category_to_ranker(category):
    return category.replace(' ', '').replace('/', '_')

### Article class
### Contains all the files of a single article
### broken down into the different NLU classes

class Article:

### Static members

    CATEGORIES = 'categories'
    CONCEPTS = 'concepts'
    ENTITIES = 'entities'
    KEYWORDS = 'keywords'
    LANGUAGE = 'language'
    RELATIONS = 'relations'
    SEMATIC = 'semantic_roles'
    USAGE = 'usage'

    valid_files = sorted([
            CATEGORIES, CONCEPTS, ENTITIES, KEYWORDS, 
        LANGUAGE, RELATIONS, SEMATIC, USAGE
    ])

### End static members

### Static functions

    @staticmethod
    def valid_article(files):
        files = sorted(files)
        if len(files) != len(Article.valid_files):
            return False

        for i in range(len(files)):
            if files[i] != Article.valid_files[i]:
                return False

        return True

### End Static functions

### Private functions

    def _add_entry(self, path):
        with open(path) as file:
            bsn = basename(path)			
            self.files[splitext(bsn)[0]] = json.load(file)

### End private functions

### Constructor

    def __init__(self, path):
        pathes = [join(path, f) for f in listdir(path) if '.json' in f]
        self.files = {}
        for f in pathes:
            self._add_entry(f)

        file_names = []
        self.id = -1
        for k, v in self.files.items():
            if is_int(k):
                self.id = int(k)
            else:
                file_names.append(k)

        self.valid = Article.valid_article(file_names) and self.id > 0

### End Constructor

### Member functions

    def get_file_type(self, file_type):
        return self.files[file_type]

    def categories(self):
        cats = self.get_file_type(Article.CATEGORIES)
        rtn = []
        for c in cats:
            lvl = c["label"]
            rtn.append(lvl)
        return rtn 

    def category_rankings(self):
        cats = self.get_file_type(Article.CATEGORIES)
        rtn = {}
        for c in cats:
            label = c['label']      
            if len(label) > 0:
                rtn[label[0]] = float(c['score'])
        return rtn

### End Member functions

### End Article class

### ------------------------------------------------------------- ###

### Articles class
### This class contains a collection of articles

class Articles:

### Constructor

    def __init__(self, path, par=True):
        pathes = [join(path, f) for f in listdir(path) if isdir(join(path, f))]
        if par:
            cpu_ct = mp.cpu_count()
            pool = mp.Pool(cpu_ct)
            self.articles = pool.map(Article, pathes)
            pool.terminate()
            
        else:
            self.articles = [Article(a) for a in pathes]
            
        self.articles = [a for a in self.articles if a.valid]

### End Constructor

### Member functions

    def relation_types(self):
        types = {}
        for a in self.articles:
            rels = a.get_file_type(Article.RELATIONS)
            cats = a.categories()
            for r in rels:            
                rel = {
                    'type' : r['type'],
                    'sentence' : r['sentence'],
                    'score' : float(r['score']),
                    'category' : cats[0] if len(cats) > 0 else '',
                    'id' : a.id
                }
                if rel['type'] not in types:
                    types[rel['type']] = []
                types[rel['type']].append(rel)

        return types

    def category_counts(self):
        cats_dict = { }
        for a in self.articles:
            cats = a.categories()
            for c in cats:
                if c not in cats_dict:
                    cats_dict[c] = 0
                cats_dict[c] = cats_dict[c] + 1
        return cats_dict

    def get_categories(self):
        cats = self.category_counts()
        set_cats = set()
        for k in cats:
            set_cats.add(k)
        return set_cats

    def first_level_categories(self):
        set_cats = set()
        for a in self.articles:
            cats = a.categories()
            if len(cats) > 0: 
                set_cats.add(cats[0])
        return set_cats

    def filter_categories(self):
        types = self.relation_types()
        by_class = { }
        for k in types:
            for rel in types[k]:
                cat = rel['category']       
                if cat not in by_class:
                    by_class[cat] = []
                by_class[cat].append(rel)

        return by_class

    def filter_sort_categories(self):
        cat_class = self.filter_categories()
        for k in cat_class:
            cat_class[k] = sorted(cat_class[k], key=lambda x: -x['score'])
        return cat_class

    def get_article_by_id(self, id):
        for a in self.articles:
            if a.id == id:
                return a
        print("Article with id: {} was not found".format(id))
        exit(1)        

### End Articles class