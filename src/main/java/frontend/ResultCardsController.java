package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import backend.QueryRetriever;
import org.apache.solr.common.SolrDocumentList;

@Controller
public class ResultCardsController {

  @RequestMapping("/results")
  public String populateCards (
      @RequestParam(value = "query", required = true, defaultValue = "World") String query,
      @RequestParam(value = "rank", required = false, defaultValue = "unranked") String rank,
      Model model) throws Exception {

    List<Card> results;
    switch (rank) {
      case "unranked":
        model.addAttribute("ranked", false);
        results = processUnranked(query);
        break;
      case "ranked":
        model.addAttribute("ranked", true);
        results = processRanked(query);
        break;
      default:
        model.addAttribute("ranked", false);
        results = processUnranked(query);
        break;
    }

    model.addAttribute("query", query);
    model.addAttribute("results", results);
    return "cards";
  }

  private List<Card> processUnranked(String query) throws Exception {
	  List<Card> cardList = new ArrayList<Card>();
	  
	  QueryRetriever retriever = new QueryRetriever();
	  SolrDocumentList documents = retriever.RetrieveQueries(query);
	  for(int i = 0; i < documents.size(); i++) {
      String title = documents.get(i).getFieldValue("title").toString();
      String body = documents.get(i).getFieldValues("body").toString();
		  cardList.add(new Card(title, body));
	  }
	  
	  return cardList;
  }

  private List<Card> processRanked(String query) throws Exception {
    List<Card> cards = new ArrayList<Card>();
    Card zero = new Card(query, "This should be here too");
    Card one = new Card("Myrtle Beach, SC", "One beach");
    Card two = new Card("Daytona Beach, FL", "Two Beach");
    Card three = new Card("Miami, FL", "Red Beach");
    Card four = new Card("Malibu, CA", "Blue Beach");
    cards.add(zero);
    cards.add(one);
    cards.add(two);
    cards.add(three);
    cards.add(four);
    return cards;
  }
}
