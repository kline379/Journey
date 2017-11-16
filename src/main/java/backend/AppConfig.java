package backend;

import backend.UserRepository;
import backend.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

@Configuration
public class AppConfig{

  private AWSCredentials awsCreds =  new BasicAWSCredentials(
    "AKIAILFG7TKL3Y256ATA", "HpY/jI19qjnqT2EDQiJlbw9Y5ij7ETq3pUmWl387");
  private AmazonDynamoDB client;
  private DynamoDB db;


  @Bean
  public AmazonDynamoDB AmazonDynamoDB(){
    return AmazonDynamoDBClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(this.awsCreds))
      .withRegion(Regions.US_EAST_1)
      .build();
  }

  @Bean
  public DynamoDB dynamoDB(){
    return new DynamoDB(AmazonDynamoDBClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(this.awsCreds))
      .withRegion(Regions.US_EAST_1)
      .build());
  }
}
