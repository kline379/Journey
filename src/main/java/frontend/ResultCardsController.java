package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;

@Controller
public class ResultCardsController {

  @RequestMapping("/results")
  public String populateCards(@RequestParam(value="searchQuery", required=true, defaultValue="World") String query, Model model) {

    List<Card> results = process(query);
    model.addAttribute("results", results);
    return "cards";
  }

  private List<Card> process(String query) {
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
