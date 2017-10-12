public class Article {
	private String title;
	private String id;
	private String description;
  
	public Article(String title, String id, String description) {
	  this.title = title;
	  this.id = id;
	  this.description = description;
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
}