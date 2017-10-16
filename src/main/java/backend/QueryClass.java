package backend;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.*;

public class QueryClass {

    public QueryClass(String className, double score) {
        _Classes = Arrays.asList(className.split(","));
        _RawClass = className;
        _Score = score;
    }

    private String _RawClass;
    private List<String> _Classes;
    private double _Score;

    public List<String> Classes() {
        return _Classes;
    }

    public String RawClass() {
        return _RawClass;
    }

    public double Score() {
        return _Score;
    }

    public boolean InQuery(String category) {
        Iterator<String> it = _Classes.iterator();
        while(it.hasNext()) {
            String next = it.next();
            if(next.equals(category)) return true;
        }      
        return false;
    }

    public static final class QcCompartor 
        implements Comparator<QueryClass> 
    {
        public QcCompartor() { }
        @Override
        public int compare(QueryClass lhs, QueryClass rhs) {
            return -Double.compare(lhs._Score, rhs._Score);
        }
    }
}
