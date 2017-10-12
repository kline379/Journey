package backend;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

public class ArticleClassifier {
	private Map<String, List<ArticleClass>> _ArticleClasses;

	private ArticleClassifier(Map<String, List<ArticleClass>> mapping) 
	{
		_ArticleClasses = mapping;

	}
	
	public static ArticleClassifier ParseClasses(String path)
		throws FileNotFoundException, IOException, Exception
	{
		Map<String, List<ArticleClass>> mapping 
			= new HashMap<String, List<ArticleClass>>();

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line = br.readLine()) != null) {
			String c0, c1, c2;
			c0 = c1 = c2 = "";
			try {
				String[] cols = line.split("\\|");				
				c0 = cols[0]; c1 = cols[1]; c2 = cols[2];
				String id = c0;
				String aClass = c1;
				double confidence = Double.parseDouble(c2);
				ArticleClass ac = new ArticleClass(aClass, confidence);	

				if(mapping.containsKey(id)) {
					mapping.get(id).add(ac);
				} else {
					ArrayList<ArticleClass> articles = new ArrayList<ArticleClass>();
					articles.add(ac);
					mapping.put(id, articles);
				}
			} catch (Exception e) {
				String what = e.toString();
				what += ". At line: " + line + ". ";
				what += "c0: " + c0 + ". c1:" + c1 + ". c2: " + c2;
				throw new Exception(what);
			}
		}
		br.close();
		return new ArticleClassifier(mapping);
	}

	public List<ArticleClass> GetArticleClasses(String id) {
		List<ArticleClass> ac = _ArticleClasses.get(id);
		return ac;
	}

}