package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;
import backend.*;
import java.util.stream.Collectors;
import backend.UserFavorites;

import java.security.Principal;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FavoritesController {
  @RequestMapping("/favorites")
  public String currentUserName(Principal principal, Model model)
    throws Exception
  {
    List<Integer> ids = UserFavorites.getFavorites(principal.getName());
    List<String> ids_str = ids.stream().map(i -> i.toString())
      .collect(Collectors.toList());
    List<Article> results = APICaller.GetArticles(ids_str);    
    model.addAttribute("results", results);
    return "cards";
  }
}
