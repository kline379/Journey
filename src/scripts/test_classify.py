from watson_developer_cloud import NaturalLanguageClassifierV1
import sys
import json

username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"

# Old classifier
#classifer_id = 'bbb1c7x227-nlc-29075'
classifer_id = 'bbab2cx226-nlc-29393'

if __name__ == '__main__':
    nlc = NaturalLanguageClassifierV1(username=username_nlc,
        password = password_nlc)
    status = nlc.status(classifer_id)
    if status['status'] != 'Available':
        print("Classifier is not avaiable")
        sys.exit(0)

    while True:
        print("Write query: ")
        line = sys.stdin.readline()
        if line == 'quit' or line == 'q()':
            sys.exit(1)
		
        classes = nlc.classify(classifer_id, line)
        classes['classes'] = sorted(classes['classes'], \
            key=lambda x: -float(x['confidence']))

        for c in classes['classes']:            
            print("\tClass: %s, scores %s", c['class_name'], c['confidence'])
            