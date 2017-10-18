package frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.*;
import backend.QueryRetriever;
import backend.QueryClassifier;
import backend.QueryClass;
import backend.Article;
import backend.ArticleClassifier;
import backend.ArticleClass;
import org.apache.solr.common.SolrDocumentList;
import java.util.Iterator;
import java.nio.file.Paths;

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
  private static final String _ArticleClassesPath = "classes/id_matching.csv";

  private List<Article> process(String query) throws Exception {    
    if(_ArticleClassifier == null) {
      synchronized(_ArticleLock) {
        try {
          _ArticleClassifier = ArticleClassifier.ParseClasses(_ArticleClassesPath);
        } 
        catch (Exception e) {
          String what = "There was an error parsing article classes.";
          what += "Full path is: " + Paths.get("").toAbsolutePath().toString();
          what += ". Old exception: " + e.toString();
          throw new Exception(what);
        }
        finally { }
      }
    }

	  List<Article> cardList = new ArrayList<Article>();
	  
	  QueryRetriever retriever = new QueryRetriever();
	  retriever.Initialize();
	  SolrDocumentList documents = retriever.RetrieveQueries(query);
	  for(int i = 0; i < documents.size(); i++) {
      String title = documents.get(i).getFieldValue("title").toString();
      String body = documents.get(i).getFieldValues("body").toString();
      String id = documents.get(i).getFieldValues("id").toString();
      id = id.replace("[", "").replace("]", "");
      List<ArticleClass> acs = _ArticleClassifier.GetArticleClasses(id);
		  cardList.add(new Article(title, id, body, acs));
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
