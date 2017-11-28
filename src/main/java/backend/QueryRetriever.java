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

public class QueryRetriever 
{   
	final String USERNAME = "638bd9ff-2179-469b-93ad-7ca894776fdd";
	final String PASSWORD = "8slDdIXgCKjd";
	final String endPoint = "https://gateway.watsonplatform.net/retrieve-and-rank/api";
	
	private RetrieveAndRank service;
	private HttpSolrClient solrClient;
	
	public void Initialize() throws InterruptedException, SolrServerException, IOException{
		this.service = new RetrieveAndRank();
    	this.service.setUsernameAndPassword(this.USERNAME, this.PASSWORD);
    	
    	SolrCluster cluster = this.GrabSolrCluster();
    	
    	this.solrClient = this.CreateHttpSolrClient(cluster);
	}
	
	private SolrCluster GrabSolrCluster() throws InterruptedException, SolrServerException {
		List<SolrCluster> clusters = this.service.getSolrClusters().execute().getSolrClusters();
    	SolrCluster cluster = clusters.get(0);
    	
    	while(cluster.getStatus() == Status.NOT_AVAILABLE) {
    		Thread.sleep(10000); // sleep 10 seconds
    		cluster = this.service.getSolrCluster(cluster.getId()).execute();
    	}
    	
    	return cluster;
	}
	
	private HttpSolrClient CreateHttpSolrClient(SolrCluster cluster) {
		return new HttpSolrClient(this.service.getSolrUrl(cluster.getId()),
				HttpSolrClientUtils.createHttpClient(this.endPoint, this.USERNAME, this.PASSWORD));
	}

	public SolrDocumentList RetrieveQueries(String query, int count) 
		throws InterruptedException, SolrServerException, IOException
	{
		SolrQuery solrQuery = new SolrQuery(query);
		solrQuery.setRows(count);
    	QueryResponse response = this.solrClient.query("WikiTravel_5", solrQuery);
		return response.getResults();
    }
}

