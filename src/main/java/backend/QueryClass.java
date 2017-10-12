package backend;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class QueryClass {

    public QueryClass(String className, double score) {
        _Classes = Arrays.asList(className.split(","));
        _Score = score;
    }

    private List<String> _Classes;
    private double _Score;

    public List<String> Classes() {
        return _Classes;
    }

    public double Score() {
        return _Score;
    }

    public boolean InQuery(ArticleClass article) {
        Iterator<String> it = _Classes.iterator();
        while(it.hasNext()) {
            String next = it.next();
            if(next.equals(article.Class())) return true;
        }
        return false;
    }

}