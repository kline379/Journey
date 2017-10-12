package backend;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class ArticleClass {

	private String _ArticleId;
	private String _Class;
	private Double _Confidence;


	public ArticleClass(
		String articleId, 
		String articleClass,
		Double confidence
	) {
		_ArticleId = articleId;
		_Class = articleClass;
		_Confidence = confidence;
	}

	public static List<ArticleClass> ParseClasses(String path)
		throws FileNotFoundException, IOException
	{
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		List<ArticleClass> rtn = new ArrayList<ArticleClass>();

		String line = null;
		while((line = br.readLine()) != null) {
			List<String> cols = Arrays.asList(line.split(","));
			String id = cols.get(0);
			String aClass = cols.get(1);
			Double confidence = Double.valueOf(cols.get(2));
			rtn.add(new ArticleClass(id, aClass, confidence));
		}
		return rtn;
	}

}