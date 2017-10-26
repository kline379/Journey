package backend;

import static org.mockito.Mockito.*;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.containsString;

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


import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

@RunWith(MockitoJUnitRunner.class)
public class QueryRetrieverTest {
	@Mock 
	private HttpSolrClient solrClient;
	
	@Mock 
	private QueryResponse response;
	
	@InjectMocks 
	private QueryRetriever queryRetriever;
    
	@Test
    public void verifyCallToCloudIsMade() throws Exception {
		when(solrClient.query(isA(String.class), isA(SolrQuery.class))).
			thenReturn(new QueryResponse());
				
		queryRetriever.RetrieveQueries("", 10);
		verify(solrClient).query(any(String.class), any(SolrQuery.class));
	}
}