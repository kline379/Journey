from watson_developer_cloud import NaturalLanguageClassifierV1
from sys.stdin import readline
from sys import exit
import json

username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"
classifer_id = 'C1'

if __name__ == '__main__':

    nlc = NaturalLanguageClassifierV1(username=username_nlc,
        password = password_nlc)
    while True:
        line = readline()
        if line == 'quit' or line == 'q()':
            exit(1)
		
        classes = nlc.classify(classifer_id, line)
        print(json.dumps(classes, indent=2))
