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
import java.math.BigDecimal;
import java.util.ArrayList;

public class UserFavorites {

  public static List<Integer> getFavorites(String user) {

    List<Integer> ids = new ArrayList<Integer>();

    AWSCredentials awsCreds = new BasicAWSCredentials(
      "AKIAILFG7TKL3Y256ATA", "HpY/jI19qjnqT2EDQiJlbw9Y5ij7ETq3pUmWl387");
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
      .withRegion(Regions.US_EAST_1)
      .build();
    DynamoDB db = new DynamoDB(client);

    try {
      Table userTable = db.getTable("users");
      GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", user);
      Item outcome = userTable.getItem(spec);
      if (outcome == null) {
        throw new Exception(user + ": No such entry");
      }

<<<<<<< HEAD
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
=======
      for (BigDecimal number : outcome.getNumberSet("favorites")) {
        ids.add(number.intValue());
      }
    } catch (Exception e) {
      System.err.println("Unable to read favorites of user " + user);
      System.err.println(e.getMessage());
    } finally {
      return ids;
    }
>>>>>>> 3232e8f325a1ea389385aa5dd93aa5eb2e5bcf3f
  }
}
