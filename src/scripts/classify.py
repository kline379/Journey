from json2html import *
from watson_developer_cloud import NaturalLanguageClassifierV1
from watson_developer_cloud import RetrieveAndRankV1
import sys
import os
from file_parsing import *
    
file_directory = "files/"

username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"

username_rar = "638bd9ff-2179-469b-93ad-7ca894776fdd"
password_rar = "8slDdIXgCKjd"

if __name__ == '__main__':
    print("Write classifier name:")
    classifierName = sys.stdin.readline().rstrip()

    print("Write top number of entries:")
    numEntries = int(sys.stdin.readline().rstrip())

    articles = Articles(file_directory)  
    cat_classes = CategoryClass.parse('cats.csv')

    type_cts = articles.category_counts()
    sorted_cts = [(k, type_cts[k]) for \
        k in sorted(type_cts, key=lambda x: -type_cts.get(x))]
    filtered_types = [t[0] for t in sorted_cts \
        if 10 < t[1] < 3000]
    
    types = articles.relation_types()

    for k in types:        
        types[k] = [c for c in types[k] if \
            c['category'] in filtered_types]

        types[k] = sorted(types[k], key=lambda x: -x['score'])
        types[k] = types[k][:numEntries]

    training_data = ''
    ranking_data = {}
    for k, v in types.items():
        for dat in v:
            s = dat['sentence']
            s = s.replace('"', '""')
            cat = dat['category']
            score_of_1000 = int(dat['score']*1000)

            nextEntry = '"' + s + '",' + cat + '\n'
            if(len(nextEntry) < 1024):
                training_data = training_data + nextEntry

            if cat not in ranking_data:
                ranking_data[cat] = ''

            nextEntryRanker = '"' + s + '",' + str(score_of_1000) + \
                ',' + cat + "\n"
    
            ranking_data[cat] = ranking_data[cat] + nextEntryRanker

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
