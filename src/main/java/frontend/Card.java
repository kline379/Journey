package frontend;

public class Card {

  private String title;
  private String description;

  public Card(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }
}
