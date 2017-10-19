package backend;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;


public class ArticleClassifierTest {
	final String id1 = "10008";
	final String class1_1 = "travel";
	final double score1_1 = 0.888;
	final String class1_2 = "dogs";
	final double score1_2 = 0.777;
	
	final String id2 = "10009";
	final String class2 = "cats";
	final double score2 = 0.222;
	
	ArticleClassifier testClassifier;
	public ArticleClassifierTest() throws Exception {
		testClassifier = ArticleClassifier.ParseClasses("src/test/java/backend/Resources/id_matching.csv");
	}
			
    @Test
    public void testArticle1_1Info() throws Exception {
		List<ArticleClass> testArticle1List = testClassifier.GetArticleClasses(id1);
		ArticleClass firstInstance = testArticle1List.get(0);
		
		assertEquals(firstInstance.Class(), class1_1);
		assertEquals(firstInstance.Confidence(), score1_1);
	}
    
    @Test
    public void testArticle1_2Info() throws Exception {
		List<ArticleClass> testArticle1List = testClassifier.GetArticleClasses(id1);
		ArticleClass secondInstance = testArticle1List.get(1);
		
		assertEquals(secondInstance.Class(), class1_2);
		assertEquals(secondInstance.Confidence(), score1_2);
	}
    
    @Test
    public void testArticle2() throws Exception {
		List<ArticleClass> testArticle2List = testClassifier.GetArticleClasses(id2);
		ArticleClass testInstance = testArticle2List.get(0);
		
		assertEquals(testInstance.Class(), class2);
		assertEquals(testInstance.Confidence(), score2);
	}
}