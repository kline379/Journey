package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import backend.QueryRetriever;
import backend.QueryClassifier;
import backend.QueryClass;
import backend.Article;
import org.apache.solr.common.SolrDocumentList;
import java.util.Iterator;

@Controller
public class ResultCardsController {

  @RequestMapping("/results")
  public String populateCards (
      @RequestParam(value = "searchQuery", required = true, defaultValue = "World") String query, Model model) 
    		  throws Exception {

    List<Article> results = process(query);
    model.addAttribute("results", results);
    return "cards";
  }

  private List<Article> process(String query) throws Exception {
	  List<Article> cardList = new ArrayList<Article>();
	  
	  QueryRetriever retriever = new QueryRetriever();
	  retriever.Initialize();
	  SolrDocumentList documents = retriever.RetrieveQueries(query);
	  for(int i = 0; i < documents.size(); i++) {
      String title = documents.get(i).getFieldValue("title").toString();
      String body = documents.get(i).getFieldValues("body").toString();
      String id = documents.get(i).getFieldValues("id").toString();
		  cardList.add(new Article(title, id, body));
	  }
	  
	  return cardList;
  }

  private List<QueryClass> getClasses(String query) {    
    QueryClassifier classifier = new QueryClassifier();
    classifier.SetClassifier(classifier.GetClassifierIds().get(0));
    List<QueryClass> classes = classifier.GetClasses(query);   
    return classes;
  }
  
}
