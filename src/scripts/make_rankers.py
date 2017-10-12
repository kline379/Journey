from file_parsing import *
from watson_developer_cloud import RetrieveAndRankV1

username_rar = "638bd9ff-2179-469b-93ad-7ca894776fdd"
password_rar = "8slDdIXgCKjd"

if __name__ == '__main__':

    rar = RetrieveAndRankV1(
        username=username_rar, password=password_rar)

    rankers = [r['ranker_id'] for r in rar.list_rankers()['rankers']]
    for r in rankers:
        rar.delete_ranker(r)

    a = Articles('files/')
    cc = CategoryClass.parse('cats.csv')
    by_c = a.filter_categories(cc)
    for c, v in by_c.items():    
        c_ranker = ','.join([CategoryClass.category_to_ranker(k) for \
            k in c.classes])
        training_data = ''
        for dat in v:
            s = dat['sentence']
            id = str(dat['id'])
            s = s.replace('"', '""')                        
            score = int(dat['score']*1000)
            next_entry = '"' + s + '",' + id + "," + "\n"
            if(len(next_entry) < 1024):
                training_data = training_data + next_entry

        with open(c_ranker + '.csv', 'w') as file:
            file.write(training_data)
            
        rslt = rar.create_ranker(training_data=training_data, 
            name=c_ranker) 
        print(json.dumps(rslt, indent=2))