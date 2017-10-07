from file_parsing import *

if __name__ == '__main__':

	a = Articles('files/')
	cc = CategoryClass.parse('cats.csv')
	by_c = a.filter_categories(cc)
	for k, v in by_c.items():
		print(','.join(k.classes) + ": " + str(len(v)))