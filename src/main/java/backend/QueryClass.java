package backend;

public class QueryClass {

    public QueryClass(String className, double score) {
        _ClassName = className;
        _Score = score;
    }

    private String _ClassName;
    private double _Score;

    public String ClassName() {
        return _ClassName;
    }

    public double Score() {
        return _Score;
    }
}