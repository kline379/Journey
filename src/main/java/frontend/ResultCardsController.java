package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

@Controller
public class ResultCardsController {

  @RequestMapping("/results")
  public String populateCards(
      @RequestParam(value = "searchQuery", required = true, defaultValue = "World") String query, Model model) {

    List<Card> results = process(query);
    model.addAttribute("results", results);
    return "cards";
  }

  private List<Card> process(String query) {
    try {
      ProcessBuilder ps = new ProcessBuilder(
        "python3.6",
        "src/main/java/frontend/query.py"
      );

      ps.redirectErrorStream(true);
      Process p = ps.start();

      List<Card> cards = new ArrayList<Card>();

      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

      writer.write(query + "\n");
      writer.flush();

      try (BufferedReader oR = new BufferedReader(new InputStreamReader(p.getInputStream()));) {
        String s = oR.readLine();

        while (s != null) {
          String title = s;
          StringBuilder body = new StringBuilder();
          s = oR.readLine();

          while (!s.equals("###")) {
            body.append(s);
            s = oR.readLine();
          }

          cards.add(new Card(title, body.toString()));
          s = oR.readLine();
        }
      }
      p.waitFor();
      return cards;
    } catch (Throwable e) {
      return new ArrayList<Card>();
    }

    /*
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
    */
  }
}
