from watson_developer_cloud import NaturalLanguageUnderstandingV1
import watson_developer_cloud.natural_language_understanding.features.v1 \
  as Features
import sys
import xml.etree.ElementTree as et
import multiprocessing as mp
import os
import codecs
import json
import sys
from time import sleep
from json2html import *
from watson_developer_cloud import RetrieveAndRankV1

base_path = R"K:\OneDrive\CSE 5914\Python Scripts"
xmlPath = R"wikitravel-en-20090302.xml"

output_path = "files"

username_nlu = "0d758ea1-3d20-42f1-8140-75233955f6e5"
password_nlu = "Eozo4ibLtjVF"

username_rar = "638bd9ff-2179-469b-93ad-7ca894776fdd"
password_rar = "8slDdIXgCKjd"

def parse_docs(el):
    id = el.find('id').text
    title = el.find('title').text
    text = el.find('revision').find('text').text
    text = text.encode('utf-8', 'ignore')
    text = text.decode('utf-8', 'strict')
    return { "id" : id, "title" : title, "body" : text }

def parse_travel(xml_path):
    tree = et.parse(xml_path)
    root = tree.getroot()
    elements = list(root.iter('page'))

    cpu_ct = mp.cpu_count()
    pool = mp.Pool(cpu_ct)
    rtn = pool.map(parse_docs, elements)
    pool.terminate()
    return rtn

def process_el(doc, dir):
    id = doc["id"]
    title = doc["title"]
    text = doc["body"]

    if not os.path.exists(os.path.join(dir, id)):
        os.makedirs(os.path.join(dir, id))

    with codecs.open(os.path.join(dir, id, id + '.html'), 'w', "utf-8") as file:
        file.write('<html>')
        file.write('<title>' + title + '</title>')
        file.write('<body>')
        file.write(text)
        file.write('</body></html>')

    with codecs.open(os.path.join(dir, id, id + ".json"), 'w', 'utf-8') as file:
        file.write(json.dumps(doc))

def get_nlu(body, nlu):
    return nlu.analyze(
        text=body,
        features=[
            Features.Concepts(),
            Features.Categories(),
            Features.Keywords(),
            Features.Relations(),
            Features.Entities(),
            Features.SemanticRoles()
        ]
    )

def save_understanding(output_path, id, body, nlu):
    nlu_doc = get_nlu(body, nlu)
    for key, value in nlu_doc.items():
        with open(os.path.join(output_path, id, key + ".json"), 'w') as file:
            file.write(json.dumps(value))

        with open(os.path.join(output_path, id, key + ".html"), 'w') as file:
            html = json2html.convert(value)
            file.write(html)      

def add_doc(client, doc):
    try:
        client.add([doc])
        return True
    except:
        return False

if __name__ == "__main__":
    xmlPath = os.path.join(base_path, xmlPath)
    docs = parse_travel(xmlPath)

    nlu = NaturalLanguageUnderstandingV1(
        username=username_nlu, password=password_nlu,
        version="2017-02-27")
    rar = RetrieveAndRankV1(
        username=username_rar, password=password_rar)
    cluster_id = rar.list_solr_clusters()["clusters"][0]["solr_cluster_id"]
    client = rar.get_pysolr_client(cluster_id, "Wiki_Travel2")

    ct = 0
    for doc in docs:
        body = doc["body"]
        if "#REDIRECT" in body:
            continue

        sys.stdout.flush()
        process_el(doc, output_path)        
        id = doc["id"]   
        try:       
            ct = ct  + 1    
            save_understanding(output_path, id, body, nlu)   
            succ = add_doc(client, doc)         
            if ct % 1000:
                sleep(10)
                nlu = NaturalLanguageUnderstandingV1(
                    username=username_nlu, password=password_nlu,
                    version="2017-02-27")
                rar = RetrieveAndRankV1(
                    username=username_rar, password=password_rar)
                client = rar.get_pysolr_client(cluster_id, "Wiki_Travel2")
            else:
                sleep(0.1)
            if succ: print("Pass," + id)
            else: print("Fail," + id)
        except:
            print("Fail," + id)
