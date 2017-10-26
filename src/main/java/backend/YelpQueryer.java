package backend;

import org.json.*;

import org.scribe.oauth.OAuthService;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Response;

import org.scribe.builder.ServiceBuilder;

import java.util.List;
import java.util.ArrayList;

public class YelpQueryer {

    private static final String API_HOST = "api.yelp.com";
    private static final String BUSINESS_PATH = "/v3/businesses/search";

    private OAuthService _Service;
    //private Token _AccessToken;

    public static final String ConsumerKey = "7r4LuY9FHvFlaLzVkVv43A";
    public static final String ConsumerSecret = "GQotqUVXvzRCjKsC0Eme4fhd841NVNg7OuFkobJYBE5ZtPEfpB3fpSzkciz2UND6";
    public static final String Token = "";
    public static final String TokenSecret = "";

    public YelpQueryer(
        String consumerKey,
        String consumerSecret,
        String token,
        String tokenSecret         
    ) { 
        _Service = new ServiceBuilder()
            .provider(TwoStepOAuth.class)
            .apiKey(consumerKey)
            .apiSecret(consumerSecret)
            .build();
        //_AccessToken = new Token(token, tokenSecret);
    }

    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, 
            "https://" + API_HOST + path);
        return request;
    }

    private String sendRequestAndGetResponse(OAuthRequest request) {
        //_Service.signRequest(_AccessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    public List<YelpBusiness> Query(String location) 
        throws JSONException
    {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH);
        request.addOAuthParameter("location", location);
        String body = sendRequestAndGetResponse(request);
        JSONObject obj = new JSONObject(body);
        JSONArray businesses = obj.getJSONArray("businesses");
        
        List<YelpBusiness> yelpBus = new ArrayList<YelpBusiness>();
        for(int i = 0; i < businesses.length(); i++) {
            JSONObject bus = businesses.getJSONObject(i);
            yelpBus.add(YelpBusiness.ParseJSON(bus));
        }

        return yelpBus;
    }
}