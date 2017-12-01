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

      for (BigDecimal number : outcome.getNumberSet("favorites")) {
        ids.add(number.intValue());
      }
    } catch (Exception e) {
      System.err.println("Unable to read favorites of user " + user);
      System.err.println(e.getMessage());
    } finally {
      return ids;
    }
  }
}
