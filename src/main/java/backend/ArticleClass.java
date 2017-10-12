package backend;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

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