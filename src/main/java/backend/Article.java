package backend;

import java.util.List;
import java.util.Iterator;

public class Article {

	private String title;
	private String id;
	private String description;
	private List<ArticleClass> classes;
  
	public Article(
		String title, 
		String id, 
		String description,
		List<ArticleClass> classes
	) {
	  this.title = title;
	  this.id = id;
		this.description = description;
		this.classes = classes;
	}
  
	public String getTitle() {
	  return this.title;
	}
  
	public String getId() {
	  return this.id;
	}
  
	public String getDescription() {
	  return this.description;
	}
	
	public boolean QueryInArticle(QueryClass qc) {
		Iterator<ArticleClass> it = classes.iterator();
		while(it.hasNext()) {
				ArticleClass next = it.next();
				String category = next.Class();
				if(qc.InQuery(category)) return true;
		}
		return false;
	}

}