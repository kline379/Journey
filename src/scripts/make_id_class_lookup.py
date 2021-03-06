from file_parsing import *

class ArticleLookup:
    
    def __init__(self, id, label, score):
        self.id = str(id)
        self.label = label
        self.score = score

    @staticmethod
    def parse(line):
        brk = line.split(',')
        return ArticleLookup(brk[0], brk[1], brk[2])

    def to_string(self):
        return "{}|{}|{}".format(self.id, self.label, self.score)

if __name__ == '__main__':

    articles = Articles('files/')
    lookups = []

    for a in articles.articles:
        cats = a.get_file_type(Article.CATEGORIES)
        for c in cats:
            c_splits = c['label'].split('/')
            if len(c_splits) >= 1:
                for c in range(1, len(c_splits)):
                    cat = category_to_ranker(c_splits[1])
                    al = ArticleLookup(a.id, cat, c['score'])
                    lookups.append(al)

    with open('id_matching2.csv', 'w') as file:
        for l in lookups:
            file.write(l.to_string() + '\n')
