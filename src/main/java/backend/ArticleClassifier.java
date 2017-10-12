package backend;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.*;

public class ArticleClassifier {
	private Map<String, List<ArticleClass>> _ArticleClasses;

	private ArticleClassifier(Map<String, List<ArticleClass>> mapping) {
		_ArticleClasses = mapping;
	}
	
	private static int _GetLineCount(String file) 
		throws FileNotFoundException, IOException
	{
		int lineCt = 0;
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		try {
			byte[] c = new byte[1024];
			int readChars = 0;
			while((readChars = is.read(c)) != -1) {
				for(int i = 0; i < readChars; ++i) {
					if(c[i] == '\n') ++lineCt;
				}
			}
			is.close();
			return lineCt;
		} finally {
			is.close();
			return -1;
		}
	}	

	public static ArticleClassifier ParseClasses(String path)
		throws FileNotFoundException, IOException
	{
		int lineCt = _GetLineCount(path);
		Map<String, List<ArticleClass>> mapping 
			= new HashMap<String, List<ArticleClass>>(lineCt);

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line = br.readLine()) != null) {
			List<String> cols = Arrays.asList(line.split(","));
			String id = cols.get(0);
			String aClass = cols.get(1);
			double confidence = Double.valueOf(cols.get(2));
			ArticleClass ac = new ArticleClass(id, aClass, confidence);	

			if(mapping.containsKey(id)) {
				mapping.get(id).add(ac);
			} else {
				ArrayList<ArticleClass> articles = new ArrayList<ArticleClass>();
				articles.add(ac);
				mapping.put(id, articles);
			}
		}
		br.close();
		return new ArticleClassifier(mapping);
	}

	public List<ArticleClass> GetArticleClasses(Article article) {
		return _ArticleClasses.get(article.getId());
	}

}