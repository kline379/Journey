package backend;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class ArticleClass {

	private String _ArticleId;
	private String _Class;
	private double _Confidence;

	public ArticleClass(
		String articleId, 
		String articleClass,
		double confidence
	) {
		_ArticleId = articleId;
		_Class = articleClass;
		_Confidence = confidence;
	}

	public String Id() {
		return _ArticleId;		
	}

	public String Class() {
		return _Class;
	}

	public double Confidence() { 
		return _Confidence;
	}
}