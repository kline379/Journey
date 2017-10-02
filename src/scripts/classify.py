from os import listdir
from os.path import basename
from os.path import isdir
from os.path import join
from os.path import splitext
import json
import multiprocessing as mp
from json2html import *
from watson_developer_cloud import NaturalLanguageClassifierV1

def is_int(s):
    try:
        int(s)
        return True
    except:
        return False

CATEGORIES = 'categories'
CONCEPTS = 'concepts'
ENTITIES = 'entities'
KEYWORDS = 'keywords'
LANGUAGE = 'language'
RELATIONS = 'relations'
SEMATIC = 'semantic_roles'
USAGE = 'usage'

valid_files = [CATEGORIES, CONCEPTS, ENTITIES, KEYWORDS, 
    LANGUAGE, RELATIONS, SEMATIC, USAGE]

valid_files = sorted(valid_files)

def valid_article(files):
    files = sorted(files)
    if len(files) != len(valid_files):
        return False

    for i in range(len(files)):
        if files[i] != valid_files[i]:
            return False

    return True

class Article:

    def _add_entry(self, path):
        with open(path) as file:
            bsn = basename(path)			
            self.files[splitext(bsn)[0]] = json.load(file)

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

        self.valid = valid_article(file_names) and self.id > 0

    def get_file_type(self, file_type):
        return self.files[file_type]
        
def LoadArticle(path):
    return Article(path)

def LoadArticles(path):
    pathes = [join(path, f) for f in listdir(path) if isdir(join(path, f))]

    cpu_ct = mp.cpu_count()
    pool = mp.Pool(cpu_ct)
    rtn = pool.map(LoadArticle, pathes)
    pool.terminate()
    rtn = [a for a in rtn if a.valid]
    return rtn

def GetArticleCategories(article):
    cats = article.get_file_type(CATEGORIES)
    rtn = []
    for c in cats:
        lvls = c["label"].split('/')[1:]
        cur = ''
        for l in lvls:
            cur = cur + '/' + l
            rtn.append(cur)
    return rtn 

def GetRelationTypes(articles):
    types = {}
    for a in articles:
        rels = a.get_file_type(RELATIONS)
        cats = GetArticleCategories(a)
        cat = ''
        if(len(cats) > 0):
            cat = cats[1]
        for r in rels:            
            rel = {
                'type' : r['type'],
                'sentence' : r['sentence'],
                'score' : float(r['score']),
                'category' : cat
            }
            if rel['type'] not in types:
                types[rel['type']] = []
            types[rel['type']].append(rel)

    return types

def GetCategories(articles):
    set_cats = set()
    for a in articles:
        cats = GetArticleCategories(a)
        for c in cats:
            set_cats.add(c)
    return set_cats

'''
    Start main
'''

file_directory = "files/"
username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"

if __name__ == '__main__':
    articles = LoadArticles(file_directory)  
    types = GetRelationTypes(articles)    
    for k in types:
        types[k] = sorted(types[k], key=lambda x: -x['score'])
        types[k] = types[k][:100]

    training_data = ''
    for k, v in types.items():
        for dat in v:
            s = dat['sentence']
            s = s.replace('"', '""')
            cat = dat['category']
            training_data = training_data + '"' + s + '",' + cat + '\n'

    print("Generated: " + str(len(training_data.split('\n'))) + " entries")
    print()

    nlc = NaturalLanguageClassifierV1(username=username_nlc,
        password=password_nlc)

    classifer = nlc.create(
        training_data=training_data,
        name='C1',
        language='en'
    )

    print(json.dumps(classifer, indent=2))
