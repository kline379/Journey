package backend;

public class ArticleClass {

	private String _Class;
	private double _Confidence;

	public ArticleClass(
		String articleClass,
		double confidence
	) {
		_Class = articleClass;
		_Confidence = confidence;
	}

	public String Class() {
		return _Class;
	}

	public double Confidence() { 
		return _Confidence;
	}
}