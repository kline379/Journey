package backend;

import java.util.List;
import java.util.Iterator;
import java.util.Comparator;

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
	
	public double QueryScore(QueryClass qc) {
		double score = 0;
		Iterator<ArticleClass> it = classes.iterator();
		while(it.hasNext()) {
				ArticleClass next = it.next();
				String category = next.Class();
				if(qc.InQuery(category)) 					
					score += next.Confidence();
		}
		return score;
	}

	public static final class ArticleComparator 
		implements Comparator<Article> 
	{
		
		private QueryClass _Class;

		public ArticleComparator(QueryClass qc) {
			_Class = qc;
		}

		@Override
		public int compare(Article lhs, Article rhs) {
			double lhsScore = lhs.QueryScore(_Class);
			double rhsScore = rhs.QueryScore(_Class);
			return Double.compare(lhsScore, rhsScore);
		}
	}
}
