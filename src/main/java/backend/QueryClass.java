package backend;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.*;

public class QueryClass {    

    public class QueryContribution
    {
        private String _Class;
        private double _Contribution;
    
        public QueryContribution(String queryClass, double contribution) {
            _Class = queryClass;
            _Contribution = contribution;
        }
    
        public String Class()
        {
            return _Class;
        }
    
        public double Contribution()
        {
            return _Contribution;
        }
    }

    private double _GetValue(int index) 
        throws Exception
    {
        if(index <= 0) throw new Exception("Index must be >= 1");
        return 0.125 * Math.exp(-0.693 * index);
    }

    private double _GetNormalization(int count)
        throws Exception
    {
        double sum = 0;
        for(int i = 0; i < count; i++)
        {
            sum += _GetValue(i + 1);
        }
        return sum;
    }

    private String _RawClass;
    private List<QueryContribution> _Classes;
    private double _Confidence;

    public QueryClass(String className, double score) 
        throws Exception
    {
        List<String> splits = Arrays.asList(className.split("_"));
        double norm = _GetNormalization(splits.size());
        _Classes = new ArrayList<QueryContribution>();
        for(int i = 0; i < splits.size(); i++)
        {
            String t = splits.get(i);
            double cont = _GetValue(i + 1);          
            _Classes.add(new QueryContribution(t, cont / norm));
        }

        _RawClass = className;
        _Confidence = score;
    }


    public String RawClass() {
        return _RawClass;
    }

    public int ClassSize() {
        return _Classes.size();
    }

    public String GetClass(int index)
    {
        return _Classes.get(index).Class();
    }

    public double Score(ArticleClass articleClass)
    {
        double score = 0;
        for(QueryContribution cont : _Classes)
        {
            if(cont.Class().equals(articleClass.Class()))
            {
                score += cont.Contribution() * _Confidence * articleClass.Confidence();
            }
        }
        return score;
    }

    public static final class QcCompartor 
        implements Comparator<QueryClass> 
    {
        public QcCompartor() { }
        @Override
        public int compare(QueryClass lhs, QueryClass rhs) {
            return -Double.compare(lhs._Confidence, rhs._Confidence);
        }
    }
}
