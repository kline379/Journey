from watson_developer_cloud import NaturalLanguageClassifierV1
from watson_developer_cloud import RetrieveAndRankV1
import sys
import json
from file_parsing import *

def get_ranker_id(rankers, name):
    for r in rankers:
        if r['name'] == name:
            return r['ranker_id']
    print("The ranker name {} does not have a ranker".format(name))
    exit()

def get_sort_func(articles, classes):
    def sorter(article):
        sorter.cs = classes
        sorter.ats = articles

        id = int(article['id'])
        a = sorter.ats.get_article_by_id(id)
        cats = a.category_rankings()

        sum = 0

        for cc in cats:
            for c in sorter.cs:
                if c.in_class(cc):
                    sum = sum + cats[cc]

        return -sum
    return sorter

username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"

username_rar = "638bd9ff-2179-469b-93ad-7ca894776fdd"
password_rar = "8slDdIXgCKjd"

file_directory = 'files/'

if __name__ == '__main__':
    nlc = NaturalLanguageClassifierV1(username=username_nlc,
        password = password_nlc)
    
    classifiers = nlc.list()
    classifier_ids = { }
    print("Available classifiers:")
    for c in classifiers['classifiers']:
        name = c['name']
        c_id = c['classifier_id']
        classifier_ids[name] = c_id
        status = nlc.status(c_id)['status']
        print('{}: {}'.format(name, status))

    print("\nWrite desired classifier:")
    classifier_name = sys.stdin.readline().rstrip()
    classifer_id = classifier_ids[classifier_name]
    status = nlc.status(classifer_id)

    if status['status'] != 'Available':
        print("Classifier is not avaiable")
        sys.exit(0)

    rar = RetrieveAndRankV1(username=username_rar, 
        password=password_rar)
    cluster_id = rar.list_solr_clusters()["clusters"][0]["solr_cluster_id"]
    client = rar.get_pysolr_client(cluster_id, "Wiki_Travel2")

    '''
    rankers = rar.list_rankers()['rankers']
    for r in rankers:
        status = rar.get_ranker_status(r['ranker_id'])
        name = r['name']
        print("Ranker: '{}' has status: '{}'".format(name, status['status']))
        if status['status'] != 'Avaiable':
            print("\tDescription: {}".format(status['status_description']))
    '''

    articles = Articles(file_directory)
    #cat_classes = CategoryClass.parse('cats.csv')

    while True:
        print("Write query: ")
        line = sys.stdin.readline().rstrip()
        if line == 'quit' or line == 'q()':
            sys.exit(1)

        classes = nlc.classify(classifer_id, line)
        classes['classes'] = sorted(classes['classes'], \
            key=lambda x: -float(x['confidence']))
        top_class = classes['classes'][0]['class_name']

        rslt = client.search(line)
        
        i = 0
        print("Before ranking\n\n")
        for r in rslt:
            print("Number {}: {}".format(i, r['title']))
            i = i + 1
'''
        print("After ranking\n\n")
        rslt = sorted(rslt, key=get_sort_func(articles, cat_classes))
        i = 0
        for r in rslt:
            print("Number {}: {}".format(i, r['title']))
            i = i + 1

        #ranker_id = get_ranker_id(rankers, top_class)
        #rank_rslt = rar.rank(ranker_id, "I want to go to the beach")
'''