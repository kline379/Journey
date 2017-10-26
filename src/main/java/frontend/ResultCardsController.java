package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import backend.QueryRetriever;
import backend.QueryClassifier;
import backend.QueryClass;
import backend.Article;
import backend.ArticleClassifier;
import backend.ArticleClass;
import backend.ImageFetcher;
import org.apache.solr.common.SolrDocumentList;
import java.nio.file.Paths;
import java.io.File;

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

  private static Object _ArticleLock = new Object();
  private static ArticleClassifier _ArticleClassifier = null;
  private static final String _ArticlesFile = "id_matching";

  private void _InitArticleClassifer() throws Exception {
    if(_ArticleClassifier == null) {
      synchronized(_ArticleLock) {
        String path = _GetArticlePath(
          Paths.get("").toAbsolutePath().toString(),
          _ArticlesFile
        );
        _ArticleClassifier = ArticleClassifier.ParseClasses(path);      
      }
    }
  }

  private static Object _QueryLock = new Object();
  private static QueryRetriever _QueryRetriver = null;

  private void _InitQueryRetriever() throws Exception {
    if(_QueryRetriver == null) {
      synchronized(_QueryLock) {
        _QueryRetriver = new QueryRetriever();
        _QueryRetriver.Initialize();
      }
    }
  }

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
    _InitArticleClassifer();
    _InitQueryRetriever();

	  List<Article> cardList = new ArrayList<Article>();  

    SolrDocumentList documents = _QueryRetriver.RetrieveQueries(query, 5);
    ImageFetcher imageFetcher = new ImageFetcher();
	  for(int i = 0; i < documents.size(); i++) {
      String title = documents.get(i).getFieldValue("title").toString();
      String body = documents.get(i).getFieldValues("body").toString();
      String id = documents.get(i).getFieldValues("id").toString();
      String imageURL = imageFetcher.getBannerURL(title);
      id = id.replace("[", "").replace("]", "");
      List<ArticleClass> acs = _ArticleClassifier.GetArticleClasses(id);
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
