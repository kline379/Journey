from json2html import *
from watson_developer_cloud import NaturalLanguageClassifierV1
import sys
import os
from file_parsing import *
    
file_directory = "files/"

username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"

if __name__ == '__main__':
    print("Write classifier name:")
    classifierName = sys.stdin.readline().rstrip()

    print("Write top number of entries:")
    numEntries = int(sys.stdin.readline().rstrip())

    articles = Articles(file_directory)  
    types = articles.filter_sort_categories()

    for k in types:        
        type_map = types[k]
        if len(type_map) > numEntries:
            types[k] = types[k][:numEntries]

    training_data = ''
    rankers = set()
    for c, v in types.items():
        ranker = category_to_ranker(c)
        for dat in v:
            s = dat['sentence']
            s = s.replace('"', '""')
            score_of_1000 = int(dat['score']*1000)
            nextEntry = '"' + s + '",' + ranker + '\n'
            if(len(nextEntry) < 1024):
                training_data = training_data + nextEntry
                rankers.add(ranker)
                
   
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
