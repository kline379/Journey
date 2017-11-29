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

@Controller
public class ResultCardsController {

  private static final Lazy<Logger> _Logger =
  new Lazy<Logger>(() ->
  {
    try{      
      //String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date());
      //return new Logger(timeStamp + ".txt");
      return new Logger("LogFile.txt");
    } catch(Exception e)
    {
      System.out.println("An error occured when creating logger");
      return null;
    }
  });

  @RequestMapping("/results")
  public String populateCards(
    @RequestParam(value = "query", required = true, defaultValue = "World") String query,
    @RequestParam(value = "rank", required = false, defaultValue = "unranked") String rank, 
    Model model
  )
    throws Exception 
  {
    LoggerSession sess = _Logger.get().getSession();

    List<Article> results = process(query);
    if(rank.equals("ranked")) {
      results = rankArticles(results, query, sess);
    }
    if(results.size() > 10)
    {
      results = results.subList(0, 9);
      
    }
    model.addAttribute("query", query);
    model.addAttribute("results", results);
    model.addAttribute("ranked", rank);    

    _Logger.get().Write(sess);
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
    try {
      List<YelpBusiness> bus = _YelpRetriever.get().Query(location);
      bus.sort((YelpBusiness lhs, YelpBusiness rhs) -> 
        Double.compare(rhs.Rating(), lhs.Rating()));
                        
      return new Gson().toJson(bus);      
    } catch (Exception e) {            
      return "[]";                                                                  
    }
  }

  private static final String _ArticlesFile = "id_matching2";
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
        return new YelpQueryer();
      } catch(Exception e) { throw new RuntimeException(e); }
    });

  private static final Lazy<ImageFetcher> _ImageFetcher = 
    new Lazy<ImageFetcher>(() ->
    {
      return new ImageFetcher();
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

  private static Article _GetArticle(SolrDocument doc)
    throws Exception
  {
    String title = doc.getFieldValue("title").toString();
    String body = doc.getFieldValues("body").toString();
    String id = doc.getFieldValues("id").toString();
    String imageURL = _ImageFetcher.get().getBannerURL(title);
    id = id.replace("[", "").replace("]", "");
    List<ArticleClass> acs = _ArticleClassifier.get()
      .GetArticleClasses(id);
    return new Article(title, id, body, imageURL, acs);
  }

  private List<Article> process(String query) throws Exception {  
	  List<Article> cardList = new ArrayList<Article>();  
    SolrDocumentList documents = _QueryRetriver.get()
      .RetrieveQueries(query, 15);    
	  for(int i = 0; i < documents.size(); i++) {
      Article a = _GetArticle(documents.get(i));
      if(!a.getTitle().toLowerCase().contains("disambiguation"))
        cardList.add(a);
    }	  
	  return cardList;
  }
  
  private List<Article> rankArticles(List<Article> articles, String query, LoggerSession ses)
    throws Exception  
  {   
    QueryClassifier classifier = new QueryClassifier();
    List<QueryClass> classes = classifier.GetClasses(query);   

    Comparator<QueryClass> qcC = new QueryClass.QcCompartor();
    classes.sort(qcC);
    QueryClass topClass = classes.get(0);
    try {
      String log = "Classes from top for query " + query + " are:";
      for(int i = 0; i < topClass.ClassSize(); i++)
      {
        if(i != 0) log += ",";
        log += topClass.GetClass(i);
      }
      ses.AddLog(log);

      for(int i = 0; i < articles.size(); i++)
      {
        log = String.format("Article Id: %s, has classes: ", articles.get(i).getId());
        for(int j = 0; j < articles.get(i).classSize(); j++)
        {
          String c = articles.get(i).getClass(j).Class();
          if(j == 0) log += c;
          else log += "," + c;
        }
        ses.AddLog(log);
      }
    } catch (Exception e) { }

    Comparator<Article> comp = new Article.ArticleComparator(topClass);
    articles.sort(comp);

    return articles;
  } 
}
