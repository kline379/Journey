from watson_developer_cloud import RetrieveAndRankV1
import sys
import json

username_rar = "638bd9ff-2179-469b-93ad-7ca894776fdd"
password_rar = "8slDdIXgCKjd"

if __name__ == '__main__':

    rar = RetrieveAndRankV1(username=username_rar, 
        password=password_rar)
    cluster_id = rar.list_solr_clusters()["clusters"][0]["solr_cluster_id"]
    client = rar.get_pysolr_client(cluster_id, "Wiki_Travel2")
    rankers = rar.list_rankers()['rankers']
    
    for r in rankers:
        status = rar.get_ranker_status(r['ranker_id'])
        name = r['name']
        print("Ranker: '{}' has status: '{}'".format(name, status['status']))
        if status['status'] != 'Avaiable':
            print("\tDescription: {}".format(status['status_description']))
