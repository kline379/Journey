package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import backend.*;
import org.apache.solr.common.SolrDocumentList;
import java.nio.file.Paths;
import java.io.File;
import java.util.function.*;
import com.google.gson.*;

@Controller
public class ResultCardsController {

  @RequestMapping("/results")
  public String populateCards(
    @RequestParam(value = "query", required = true, defaultValue = "World") String query,
    @RequestParam(value = "rank", required = false, defaultValue = "unranked") String rank, 
    Model model
  )
    throws Exception 
  {
    List<Article> results = process(query);
    if(rank.equals("ranked")) {
      results = rankArticles(results, query);
    }
    model.addAttribute("query", query);
    model.addAttribute("results", results);
    model.addAttribute("ranked", rank);    
    return "cards";
  }

  @RequestMapping(
    value = "/yelpreview", 
    method = RequestMethod.GET,
    produces = "application/json" 
  )
  public String YelpReview(
    @RequestParam(value = "location", required = true) String location
  ) throws Exception {
    List<YelpBusiness> bus = _YelpRetriever.get().Query(location);
    return new Gson().toJson(bus);
  }

  private static final String _ArticlesFile = "id_matching";
  private static final Lazy<ArticleClassifier> _ArticleClassifier = 
    new Lazy<ArticleClassifier>(() -> 
    {
      try {
        String path = _GetArticlePath(
          Paths.get("").toAbsolutePath().toString(),
          _ArticlesFile
        );
        ArticleClassifier c = ArticleClassifier.ParseClasses(path);
        return c;
      } catch(Exception e) { throw new RuntimeException(e); }
    });

  private static final Lazy<QueryRetriever> _QueryRetriver = 
    new Lazy<QueryRetriever>(() ->
    {
      try {
        QueryRetriever qr = new QueryRetriever();
        qr.Initialize();
        return qr;
      } catch(Exception e) { throw new RuntimeException(e); }
    });

  private static final Lazy<YelpQueryer> _YelpRetriever = 
    new Lazy<YelpQueryer>(() ->
    {
      try {
        return new YelpQueryer(
          YelpQueryer.ConsumerKey, YelpQueryer.ConsumerSecret,
          YelpQueryer.Token, YelpQueryer.TokenSecret
        );
      } catch(Exception e) { throw new RuntimeException(e); }

    });

  static void _GetAllSubdirs(String path, ArrayList<File> files) {
    File directory = new File(path);

    File[] fList = directory.listFiles();
    for(int i = 0; i < fList.length; i++) {
      File f = fList[i];
      if(f.isFile()) {
        files.add(f);
      } else if (f.isDirectory()) {
        _GetAllSubdirs(f.getAbsolutePath(), files);
      }      
    }
  }

  static String _GetArticlePath(String dir, String lookingFor) 
    throws Exception
  {
    ArrayList<File> files = new ArrayList<File>();
    _GetAllSubdirs(Paths.get("").toAbsolutePath().toString(), files);
    
    for(File f : files) {
      String fullPath = f.getAbsolutePath().toString();
      if(fullPath.toLowerCase().contains(lookingFor.toLowerCase())) {
        return fullPath; 
      }
    }
    throw new Exception("file: " + lookingFor + " could not be found");
  }

  private List<Article> process(String query) throws Exception {  
	  List<Article> cardList = new ArrayList<Article>();  

    SolrDocumentList documents = _QueryRetriver.get()
      .RetrieveQueries(query, 5);
    ImageFetcher imageFetcher = new ImageFetcher();
	  for(int i = 0; i < documents.size(); i++) {
      String title = documents.get(i).getFieldValue("title").toString();
      String body = documents.get(i).getFieldValues("body").toString();
      String id = documents.get(i).getFieldValues("id").toString();
      String imageURL = imageFetcher.getBannerURL(title);
      id = id.replace("[", "").replace("]", "");
      List<ArticleClass> acs = _ArticleClassifier.get()
        .GetArticleClasses(id);
		  cardList.add(new Article(title, id, body, imageURL, acs));
	  }	  
	  return cardList;
  }
  
  private List<Article> rankArticles(List<Article> articles, String query) {   
    QueryClassifier classifier = new QueryClassifier();
    List<QueryClass> classes = classifier.GetClasses(query);   

    Comparator<QueryClass> qcC = new QueryClass.QcCompartor();
    classes.sort(qcC);

    QueryClass topClass = classes.get(0);
    Comparator<Article> comp = new Article.ArticleComparator(topClass);
    articles.sort(comp);

    return articles;
  }
}
