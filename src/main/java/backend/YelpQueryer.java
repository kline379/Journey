package backend;

import org.json.*;
import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.ArrayList;

import java.net.*;
import java.io.*;


public class YelpQueryer {

    private static final String API_HOST = "api.yelp.com";
    private static final String SEARCH_PATH = "v3/businesses/search";

    public static final String ConsumerKey = "7r4LuY9FHvFlaLzVkVv43A";
    public static final String ConsumerSecret = "GQotqUVXvzRCjKsC0Eme4fhd841NVNg7OuFkobJYBE5ZtPEfpB3fpSzkciz2UND6";
    public static final String Token = "48DdpO8DMG193zdbTR-yDIISgln0Rp4da4N19VA9qkTila8FcGhrhe9rJhFm1B29_fojUZVyA-zkwJIjp8G20zHB3s77TM6xjfhlnFATyJ3NK1niOgwU-BEhvGLyWXYx";

    private static String _GetURL(String location) {
        String request = String.format("https://%s", API_HOST);
        request += String.format("/%s", SEARCH_PATH);
        request += String.format("?location=%s", location);
        request += String.format("&limit=%d", 5);
        return request;
    }

    public YelpQueryer()  { }

    public List<YelpBusiness> Query(String location) 
        throws IOException, JSONException, Exception
    {
        String request = _GetURL(location);
        URL url = new URL(request);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", String.format("Bearer %s", Token));
        int httpResponse = con.getResponseCode();
        if(httpResponse == 200) {
            InputStream inputStr = con.getInputStream();
            String encoding = con.getContentEncoding() == null ? "UTF-8" :
                con.getContentEncoding();
            String response = IOUtils.toString(inputStr, encoding);

            System.out.println(response);
            
            JSONObject obj = new JSONObject(response);
            JSONArray businesses = obj.getJSONArray("businesses");

            List<YelpBusiness> yelpBus = new ArrayList<YelpBusiness>();
            for(int i = 0; i < businesses.length(); i++) {
                JSONObject bus = businesses.getJSONObject(i);
                yelpBus.add(YelpBusiness.ParseJSON(bus));
            }
    
            return yelpBus;
        }
        throw new Exception("There was an error connecting");
    }
}