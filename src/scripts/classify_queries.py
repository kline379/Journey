from watson_developer_cloud import \
    NaturalLanguageClassifierV1

username_nlc = "9a25363d-7524-4904-ae1d-f55fa833a1ca"
password_nlc = "a2gbUnUVx78B"
url_nlc = "https://gateway.watsonplatform.net/natural-language-classifier/api"

def make_classifier(name, nlc):
    classifier = nlc.create


if __name__ == "__main__":
    nlc = NaturalLanguageClassifierV1(
        username=username_nlc, password=password_nlc)

    