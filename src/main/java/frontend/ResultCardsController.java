package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import backend.*;
import com.google.gson.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.security.Principal;
import backend.UserFavorites;

@Controller
public class ResultCardsController {

  @RequestMapping("/fave")
  public String toggleFave(Principal principal, @RequestParam(value = "article") String id){
    UserFavorites.toggleFave(principal.getName(), id);
    return null;
  }

  @RequestMapping("/results")
  public String populateCards(Principal principal,
    @RequestParam(value = "query", required = true, defaultValue = "World") String query,
    @RequestParam(value = "rank", required = false, defaultValue = "unranked") String rank,
    Model model
  )
    throws Exception
  {
    System.out.println(rank);
    List<Article> results = APICaller.GetArticles(query, 10,
      rank.equals("ranked"));

    List<Integer> ids = UserFavorites.getFavorites(principal.getName());
    for(int i = 0; i<results.size(); i++){
      if(ids.contains(Integer.valueOf(results.get(i).getId()))){
         results.get(i).makeFave();
      }
    }
    model.addAttribute("query", query);
    model.addAttribute("results", results);
    model.addAttribute("ranked", rank);

    return "cards";
  }

  @RequestMapping(
    value = "/yelpreview",
    method = RequestMethod.POST,
    produces = "application/json"
  )
  public @ResponseBody String YelpReview(
    @RequestParam(value = "location", required = true) String location
  ) throws Exception {
    List<YelpBusiness> rslts =  APICaller.GetBusinesses(location);
    return new Gson().toJson(rslts);
  }
}
