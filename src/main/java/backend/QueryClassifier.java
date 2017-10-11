package backend;

import java.util.List;

import com.sun.org.apache.xpath.internal.operations.String;

public class QueryClassifier {

    private final static String _NLCUsername = "9a25363d-7524-4904-ae1d-f55fa833a1ca";
    private final static String _NLCPassword = "a2gbUnUVx78B";

    private NaturalLanguageClassifier _Service = new NaturalLanguageClassifier();
    private String _Classifier = null;

    public QueryClassifer() {
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

    public SetClassifier(string classifierId) {
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
            
        for(ClassifiedClass class : classified) {
            QueryClass qc = new QueryClass(
                class.getName(), class.getConfidence()
            );
            classes.add(qc);            
        }
        return qc;
    }

}