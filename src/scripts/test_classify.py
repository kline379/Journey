from watson_developer_cloud import NaturalLanguageClassifierV1
import sys
import json

username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"

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

    while True:
        print("Write query: ")
        line = sys.stdin.readline().rstrip()
        if line == 'quit' or line == 'q()':
            sys.exit(1)
		
        classes = nlc.classify(classifer_id, line)
        classes['classes'] = sorted(classes['classes'], \
            key=lambda x: -float(x['confidence']))

        for c in classes['classes']:            
            print("\tClass: %s, scores %s", c['class_name'], c['confidence'])
            