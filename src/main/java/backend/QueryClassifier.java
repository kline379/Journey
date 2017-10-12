package backend;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1;

public class QueryClassifier {

    private final static String _NLCUsername = "9a25363d-7524-4904-ae1d-f55fa833a1ca";
    private final static String _NLCPassword = "a2gbUnUVx78B";

    private NaturalLanguageClassifier _Service = new NaturalLanguageClassifier();
    private String _Classifier = null;

    public QueryClassifier() {
        _Service.setUsernameAndPassword(_NLCUsername, _NLCPassword);        
    }

    public List<String> GetClassifierIds() {
        List<Classifier> classifiers = _Service.getClassifiers()
            .execute()
            .getClassifiers(); 
        List<String> ids = new ArrayList<String>();
        for (Classifier c : classifiers) {
            ids.add(c.getId());
        }
        return ids;
    }

    public void SetClassifier(String classifierId) {
        _Classifer = classifierId;
    }

    public List<QueryClass> GetClasses(String query) {
        ArrayList<QueryClass> classes = new ArrayList<QueryClass>();

        if(_Classifier == null) 
            return classes;

        List<ClassifiedClass> classified = _Service
            .classify(_Classifier, query)
            .execute()
            .getClasses();
        
        Iterator<ClassifiedClass> it = classified.iterator();
        while(it.hasNext()) { 
            ClassifiedClass next = it.next();
            QueryClass qc = new QueryClass(
                next.getName(), next.getConfidence()
            );
            classes.add(qc);
        }
            
        return qc;
    }

}