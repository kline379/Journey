from os import listdir
from os.path import basename
from os.path import isdir
from os.path import join
from os.path import splitext
import json
import multiprocessing as mp
from json2html import *
from watson_developer_cloud import NaturalLanguageClassifierV1
import sys

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
            cur = cur + l + '/'            
            rtn.append(cur[:-1])
    return rtn 

def GetRelationTypes(articles):
    types = {}
    for a in articles:
        rels = a.get_file_type(RELATIONS)
        cats = GetArticleCategories(a)
        for r in rels:            
            rel = {
                'type' : r['type'],
                'sentence' : r['sentence'],
                'score' : float(r['score']),
                'category' : cats[-1] if len(cats) > 0 else ''
            }
            if rel['type'] not in types:
                types[rel['type']] = []
            types[rel['type']].append(rel)

    return types

def GetCategoryCounts(articles):
    cats_dict = { }
    for a in articles:
        cats = GetArticleCategories(a)
        for c in cats:
            if c not in cats_dict:
                cats_dict[c] = 0
            cats_dict[c] = cats_dict[c] + 1
    return cats_dict

def GetCategories(articles):
    cats = GetCategoryCounts(articles)
    set_cats = set()
    for k in cats:
        set_cats.add(k)
    return set_cats

'''
    Start main
'''

file_directory = "files/"
username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"

if __name__ == '__main__':
    print("Write classifier name:")
    classifierName = sys.stdin.readline().rstrip()

    print("Write top number of entries:")
    numEntries = int(sys.stdin.readline().rstrip())

    articles = LoadArticles(file_directory)  

    type_cts = GetCategoryCounts(articles)
    sorted_cts = [(k, type_cts[k]) for \
        k in sorted(type_cts, key=lambda x: -type_cts.get(x))]
    filtered_types = [t[0] for t in sorted_cts \
        if 10 < t[1] < 3000]
    
    types = GetRelationTypes(articles)    

    for k in types:        
        types[k] = [c for c in types[k] if \
            c['category'] in filtered_types]

        types[k] = sorted(types[k], key=lambda x: -x['score'])
        types[k] = types[k][:numEntries]

    training_data = ''
    for k, v in types.items():
        for dat in v:
            s = dat['sentence']
            s = s.replace('"', '""')
            cat = dat['category']
            nextEntry = '"' + s + '",' + cat + '\n'
            if(len(nextEntry) < 1024):
                training_data = training_data + nextEntry

    print("Generated: " + str(len(training_data.split('\n'))) + " entries")
    print()

    nlc = NaturalLanguageClassifierV1(username=username_nlc,
        password=password_nlc)

    classifer = nlc.create(
        training_data=training_data,
        name=classifierName,
        language='en'
    )

    print(json.dumps(classifer, indent=2))
