import xml.etree.ElementTree as et
import codecs
import os
import json
import pysolr
import multiprocessing as mp
import unicodedata
from time import sleep
from watson_developer_cloud import RetrieveAndRankV1

base_path = R"bydoc\\"
xmlPath = R"wikitravel-en-20090302.xml"
url_s = R"https://gateway.watsonplatform.net/retrieve-and-rank/api"
username_s = "638bd9ff-2179-469b-93ad-7ca894776fdd"
password_s = "8slDdIXgCKjd"

def make_cluster(retrieve_and_rank):  
    
    cluster = retrieve_and_rank.create_solr_cluster(
        cluster_name="My Solr cluster", cluster_size=4)

    print(json.dumps(cluster, indent=2))

# For some reason this does not work
def make_collection(retrieve_and_rank, collection_name, _cluster_id):
    new_collection = retrieve_and_rank.create_collection(
        _cluster_id, collection_name, collection_name + "_config"
    )

    print(json.dumps(new_collection, indent=2))

def add_documents(
    retrieve_and_rank,
    cluster_id,
    collection_name,
    documents
):
    client = retrieve_and_rank.get_pysolr_client(cluster_id, collection_name)
    count = 0
    for doc in documents:
        count = count + 1
        if count % 1000:
            sleep(10)
            retrieve_and_rank = RetrieveAndRankV1(        
                username=username_s, password=password_s)
        else:
            sleep(0.1)

        try:
            newdoc = client.add([doc])
            print("Pass," + doc["id"])
        except:
            print("Fail," + doc["id"])

def parse_docs(el):
    id = el.find('id').text
    title = el.find('title').text
    text = el.find('revision').find('text').text
    text = text.encode('utf-8', 'ignore')
    text = text.decode('utf-8', 'strict')
    return { "id" : id, "title" : title, "body" : text }

def get_size(doc_list):
    size = 0
    for d in doc_list:
        size = size + len(d["body"])
    return size    

def group_docs(docs, size):
    grouped = [[] for x in range(size)]

    for doc in docs:
        min_grp = min(grouped, key=get_size)
        min_grp.append(doc)

    return grouped

def process_el(el):
    doc = parse_docs(el)
    id = doc["id"]
    title = doc["title"]
    text = doc["body"]
    with codecs.open(base_path + str(id) + '.html', 'w', "utf-8") as file:
        file.write('<html>')
        file.write('<title>' + title + '</title>')
        file.write('<body>')
        file.write(text)
        file.write('</body></html>')

def save_grp(docs):
    count = 0
    for dlist in docs:
        with codecs.open(base_path + str(count) + ".html", 'w', 'utf-8') as file:
            file.write('<html>')
            file.write('<title' + str(count) + '</title>')
            file.write('<body>')
            d_ct = 0
            for d in dlist:
                id_d = d["id"]
                title = d["title"]
                text = d["body"]

                file.write('<' + str(d_ct) + '>')

                file.write('<title>' + title + '</title>')
                file.write('<id>' + id_d + '</id>')
                file.write('<text>' + text + '</text>')
                
                file.write('</' + str(d_ct) + '>')
                d_ct = d_ct + 1
            file.write('</body></html>')
        count = count + 1

def parse_travel(xml_path):
    tree = et.parse(xml_path)
    root = tree.getroot()
    elements = list(root.iter('page'))

    cpu_ct = mp.cpu_count()
    pool = mp.Pool(cpu_ct)
    return pool.map(parse_docs, elements)

import sys

if __name__ == '__main__':    

    rar = RetrieveAndRankV1(        
        username=username_s, password=password_s)
    cluster_id = rar.list_solr_clusters()["clusters"][0]["solr_cluster_id"]

    client = rar.get_pysolr_client(cluster_id, "Wiki_Travel")
    solr_results = client.search(sys.stdin.readline())
       
    for r in solr_results:
        print(r["title"])
        text = r["body"]
        text = text.encode('ascii', 'ignore')
        text = text.decode('ascii')
        print(text)
        print("###")

'''
    docs = parse_travel(xmlPath)
    grps = group_docs(docs, 1000)
    print("Total size: " + str(len(grps)))
    for d in grps:

        char_size = get_size(d)
        print(str(len(d)) + " with " + str(char_size))

 
    rar = RetrieveAndRankV1(        
        username=username_s, password=password_s)
    cluster_id = rar.list_solr_clusters()["clusters"][0]["solr_cluster_id"]
    collection_name = "Wiki_Travel"
    add_documents(rar, cluster_id, collection_name, docs)
    '''
