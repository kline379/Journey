package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import backend.*;
import org.apache.solr.common.*;
import java.nio.file.Paths;
import java.io.File;
import java.util.function.*;
import com.google.gson.*;
import backend.UserFavorites;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FavoritesController {

  @RequestMapping("/favorites")
  public String currentUserName(Principal principal, Model model) {

    List<Integer> ids = UserFavorites.getFavorites(principal.getName());

    return "cards";
  }
}
