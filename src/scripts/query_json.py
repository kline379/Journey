import json
from json2html import *

jsonPath = "query.json"
with open(jsonPath, 'r') as file:
    data = file.read()

query = json.loads(data)
for key, value in query.items():
    with open(key + ".json", 'w') as file:
        file.write(json.dumps(value))

    with open(key + ".html", 'w') as file:
        html = json2html.convert(value)
        file.write(html)
'''
rs = query["relations"]
rs = sorted(rs, key=lambda x: -float(x["score"]))
html = json2html.convert(json=rs)
print(json.dumps(html, indent=2))
'''