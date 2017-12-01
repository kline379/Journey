package backend;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

public class UserFavorites {

  @Autowired
  DynamoDB client;

  public static List<Integer> getFavorites(String user) {

    return new ArrayList<Integer>();

  //   try {
  //     // See if the banner is already in the database
  //     GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", title);
  //     Item outcome = this.bannerTable.getItem(spec);
  //     if (outcome == null) {
  //       // Throw an Exception if it's not
  //       throw new Exception(title+ ": No such entry");
  //     }
  //     return outcome.get("url").toString();
  //   }
  //   catch (Exception e) {
  //     // The banner was not in the database
  //     System.err.println("Unable to read item");
  //     System.err.println(e.getMessage());
  //     // Scrape it from WikiTravel
  //     String imageURL = scrapeImageURL(title);
  //     try {
  //       // Write the scraped banner url to the database
  //       Item item = new Item()
  //         .withPrimaryKey("title", title)
  //         .withString("url", imageURL);
  //       this.bannerTable.putItem(item);
  //     }
  //     catch (Exception nestedE) {
  //       System.err.println("Error writing (" + title + "," + imageURL + ") to table");
  //       System.err.println(nestedE.getMessage());
  //     }
  //     return imageURL;
  //   }
  }
}
