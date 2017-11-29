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

username_rar = "638bd9ff-2179-469b-93ad-7ca894776fdd"
password_rar = "8slDdIXgCKjd"

collection = "WikiTravel_5"

if __name__ == '__main__':
    rar = RetrieveAndRankV1(username=username_rar, 
        password=password_rar)
    cluster_id = rar.list_solr_clusters()["clusters"][0]["solr_cluster_id"]
    client = rar.get_pysolr_client(cluster_id, collection)

    query = sys.stdin.readline().rstrip()
    rslts = client.search(query)
    