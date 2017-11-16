package backend;

import org.junit.Test;
import static org.junit.Assert.*;


public class ImageFetcherTest {

  ImageFetcher imageFetcher = new ImageFetcher();

  @Test
  public void getBannerPageWithBanner() {
    String url = imageFetcher.getBannerURL("Palm Springs");
    assertEquals(url, "https://wikitravel.org/upload/shared//a/a6/Palm_Springs_Banner.jpg");
  }

  @Test
  public void getBannerPageDefault() {
    String url = imageFetcher.getBannerURL("Desert Hot Springs");
    assertEquals(url, "https://wikitravel.org/upload/shared//6/6a/Default_Banner.jpg");
  }

  @Test
  public void getBannerInvalidArticle() {
    String url = imageFetcher.getBannerURL("/\\/afdfjk/ldkfj");
    assertEquals(url, "https://wikitravel.org/upload/shared//6/6a/Default_Banner.jpg");
  }
}