package backend;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.utils.HttpClientUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusters;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster.Status;


/**
 * Hello world!
 *
 */
public class QueryRetriever 
{    
    public SolrDocumentList RetrieveQueries(String query) throws InterruptedException, SolrServerException, IOException{
    	
    	final String USERNAME = "638bd9ff-2179-469b-93ad-7ca894776fdd";
    	final String PASSWORD = "8slDdIXgCKjd";
    	
    	RetrieveAndRank service = new RetrieveAndRank();
    	service.setUsernameAndPassword(USERNAME, PASSWORD);
    	
    	// Grab the Solr cluster
    	List<SolrCluster> clusters = service.getSolrClusters().execute().getSolrClusters();
    	SolrCluster cluster = clusters.get(0);
    	
    	while(cluster.getStatus() == Status.NOT_AVAILABLE) {
    		Thread.sleep(10000); // sleep 10 seconds
    		cluster = service.getSolrCluster(cluster.getId()).execute();
    	}	
    	
    	System.out.println("Cluster is available!");
    	
    	// Make a query
    	String endPoint = "https://gateway.watsonplatform.net/retrieve-and-rank/api";
    	HttpSolrClient solrClient = new HttpSolrClient(service.getSolrUrl(cluster.getId()),
							HttpSolrClientUtils.createHttpClient(endPoint, USERNAME, PASSWORD));
    	SolrQuery solrQuery = new SolrQuery(query);
    	QueryResponse response = solrClient.query("Wiki_Travel", solrQuery);
    	
    	return response.getResults(); //.get(0).getFieldValue("title"));
    	
    	//System.out.println(response);
    }
}

