package backend;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import org.apache.solr.common.*;
import java.nio.file.Paths;
import java.io.File;

public class APICaller
{
/*
Public Functions
*/

  public static List<Article> GetArticles(
    String query, 
    int count, 
    boolean sort
  ) throws Exception {
    LoggerSession sess = _Logger.get().getSession();
    
    List<Article> results = process(query);
    if(sort) {
      results = rankArticles(results, query, sess);
    }

    if(results.size() > count)
    {
      results = results.subList(0, count-1);
    }
        
    _Logger.get().Write(sess);

    return results;
  }

  public static List<Article> GetArticles(List<String> articleIds)
    throws Exception
  {
    List<Article> articles = new ArrayList<Article>();
    for(String id : articleIds)
    {
      List<Article> rslt = GetArticles("id:\"" + id + "\"", 1, false);
      for(Article r : rslt) articles.add(r);
    }
    return articles;
  }

  public static List<YelpBusiness> GetBusinesses(String location)
  {
    try {
      List<YelpBusiness> bus = _YelpRetriever.get().Query(location);
      bus.sort((YelpBusiness lhs, YelpBusiness rhs) -> 
        Double.compare(rhs.Rating(), lhs.Rating()));
      return bus;
    } catch (Exception e) {            
      return new ArrayList<YelpBusiness>();                                    
    }
  }

/*
Private functions
*/

    private static final Lazy<Logger> _Logger =
    new Lazy<Logger>(() ->
    {
      try{      
        return new Logger("LogFile.txt");
      } catch(Exception e)
      {
        System.out.println("An error occured when creating logger");
        return null;
      }
    });    
    
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

  private static void _GetAllSubdirs(String path, ArrayList<File> files) {
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

  private static List<Article> process(String query) throws Exception {  
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
  
  private static List<Article> rankArticles(
      List<Article> articles, String query, LoggerSession ses)
    throws Exception  
  {   
    QueryClassifier classifier = new QueryClassifier();
    List<QueryClass> classes = classifier.GetClasses(query);   

    Comparator<QueryClass> qcC = new QueryClass.QcCompartor();
    classes.sort(qcC);
    QueryClass topClass = classes.get(0);
    
    try {
      String log = "Classes from top for query '" + query + "' are:";
      for(int i = 0; i < topClass.ClassSize(); i++)
      {
        if(i != 0)  log += ",";   
        log += topClass.GetClass(i);
      }
      ses.AddLog(log);
      ses.AddLog("Raw classifier log: " + topClass.RawClass());

      for(Article a : articles)
      {
        log = String.format("Article Id: %s, has classes: ", a.getId());
        for(int j = 0; j < a.classSize(); j++)
        {
          if(j != 0) log += ",";
          log += a.getClass(j).Class();
        }
        log += "\n\tScore:" + a.QueryScore(topClass);
        ses.AddLog(log);
      }
    } catch (Exception e) { }

    Comparator<Article> comp = new Article.ArticleComparator(topClass);
    articles.sort(comp);

    return articles;
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
}