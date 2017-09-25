package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import backend.QueryRetriever;
import org.apache.solr.common.SolrDocumentList;

@Controller
public class ResultCardsController {

  @RequestMapping("/results")
  public String populateCards (
      @RequestParam(value = "searchQuery", required = true, defaultValue = "World") String query, Model model) 
    		  throws Exception {

    List<Card> results = process(query);
    model.addAttribute("results", results);
    return "cards";
  }

  private List<Card> process(String query) throws Exception {
	  List<Card> cardList = new ArrayList<Card>();
	  
	  QueryRetriever retriever = new QueryRetriever();
	  SolrDocumentList documents = retriever.RetrieveQueries();
	  for(int i = 0; i < documents.size(); i++) {
		  String title = documents.get(i).getFieldValue("title").toString();
		  cardList.add(new Card(title, ""));
	  }
	  
	  return cardList;
  }
}
