package backend;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

public class ImageFetcher {

  private AWSCredentials awsCreds = new BasicAWSCredentials(
    "AKIAILFG7TKL3Y256ATA", "HpY/jI19qjnqT2EDQiJlbw9Y5ij7ETq3pUmWl387");
  private AmazonDynamoDB client;
  private DynamoDB db;
  private Table bannerTable;

  public ImageFetcher() {
    this.client = AmazonDynamoDBClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(this.awsCreds))
      .withRegion(Regions.US_EAST_1)
      .build();

    this.db = new DynamoDB(this.client);
    this.bannerTable = this.db.getTable("banner");
  }

  public String getBannerURL(String title) {

    try {
      // See if the banner is already in the database
      GetItemSpec spec = new GetItemSpec().withPrimaryKey("title", title);
      Item outcome = this.bannerTable.getItem(spec);
      if (outcome == null) {
        // Throw an Exception if it's not
        throw new Exception(title+ ": No such entry");
      }
      return outcome.get("url").toString();
    }
    catch (Exception e) {
      // The banner was not in the database
      System.err.println("Unable to read item");
      System.err.println(e.getMessage());
      // Scrape it from WikiTravel
      String imageURL = scrapeImageURL(title);
      try {
        // Write the scraped banner url to the database
        Item item = new Item()
          .withPrimaryKey("title", title)
          .withString("url", imageURL);
        this.bannerTable.putItem(item);
      }
      catch (Exception nestedE) {
        System.err.println("Error writing (" + title + "," + imageURL + ") to table");
        System.err.println(nestedE.getMessage());
      }
      return imageURL;
    }
  }

  private String scrapeImageURL(String title) {
    
    String URL = null;
    try {
      // Scrape the banner
      Document doc = Jsoup.connect("https://wikitravel.org/en/File:" + title + "_Banner.jpg").get();
      URL = doc.select(".fullImageLink > a").get(0).attr("href");
    }
    catch (Exception e) {
      // If anything went wrong, return the default banner
      URL = "https://wikitravel.org/upload/shared//6/6a/Default_Banner.jpg";
    }
    finally {
      return URL;
    }
  }

}
