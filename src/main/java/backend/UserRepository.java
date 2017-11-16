package backend;

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


@Service("UserRepository")
public class UserRepository{

  @Autowired
  DynamoDB client;


  public User findUserByEmail(String email){
    if(email != null){
      try {
        Table userTable = client.getTable("users");
        // See if the banner is already in the database
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", email);
        Item outcome = userTable.getItem(spec);
        if (outcome == null) {
          // Throw an Exception if it's not
          throw new Exception(email + ": No such entry");
        }
        User user = new User();
        user.setEmail(outcome.get("email").toString());
        user.setPassword(outcome.get("password").toString());
        return user;
      }
      catch (Exception e) {
        // The banner was not in the database
        System.err.println("Unable to read item: " + email);
        System.err.println(e.getMessage());
        return null;
      }
    }
    return new User();
  }

  public void save(User user){
    try {
      Table userTable = client.getTable("users");
      // Write the scraped banner url to the database
      Item item = new Item()
        .withKeyComponent("email", user.getEmail())
        .withString("password", user.getPassword());
      userTable.putItem(item);
    }
    catch (Exception nestedE) {
      System.err.println("Error writing (" + user.getEmail() + "," + user.getPassword() + ") to table");
      System.err.println(nestedE.getMessage());
    }
  }

}
