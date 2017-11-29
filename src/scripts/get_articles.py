import sys
import xml.etree.ElementTree as et
import multiprocessing as mp
import os
import codecs
import json
import sys
from time import sleep
from json2html import *

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

    with codecs.open(os.path.join(dir, id + ".json"), 'w', 'utf-8') as file:
        file.write(json.dumps(doc, indent=2))

xmlPath = R"wikitravel-en-20090302.xml"

output_path = "files"

if __name__ == "__main__":
    docs = parse_travel(xmlPath)

    for d in docs:
        process_el(d, output_path) 
