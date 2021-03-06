package backend;

import java.util.List;
import java.util.Iterator;
import java.util.Comparator;

public class Article {

	public String title;
	public String id;
	public String description;
	public String imageURL;
	public String fave;
	private List<ArticleClass> classes;

	public Article(
		String title,
		String id,
		String description,
		String imageURL,
		List<ArticleClass> classes
	) {
	  this.title = title;
	  this.id = id;
		this.description = description;
		this.imageURL = imageURL;
		this.classes = classes;
		this.fave = "false";
	}

	public String getFave(){
		return this.fave;
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

	public int classSize()
	{
		return classes.size();
	}

	public ArticleClass getClass(int index)
	{
		return classes.get(index);
	}

	public double QueryScore(QueryClass qc) {
		if(classes == null) return 0;
		double score = 0;
		for(ArticleClass ac : classes)
		{
			score += qc.Score(ac);
		}
		return score;
	}

	public void makeFave(){
		this.fave = "true";
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
			return -Double.compare(lhsScore, rhsScore);
		}
	}
}
