package backend;

import org.json.*;

public class YelpBusiness {

    private String _Name;
    private static final String _NameJSON = "name";

    private String _Phone;
    private static final String _PhoneJSON = "phone";

    private double _Rating;
    private static final String _RatingJSON = "rating";

    private String _Link;
    private static final String _URLJson = "url";

    public static YelpBusiness ParseJSON(JSONObject obj)
        throws JSONException
    {
        String name = obj.getString(_NameJSON);
        String phone = obj.getString(_PhoneJSON);
        String ratingStr = obj.getString(_RatingJSON);
        String link = obj.getString(_URLJson);
        double rating = Double.parseDouble(ratingStr);
        return new YelpBusiness(name, phone, rating, link);
    }

    public YelpBusiness(
        String name,
        String phone,
        double rating,
        String link
    ) {
        _Name = name;
        _Phone = phone;
        _Rating = rating;
        _Link = link;
    }

    public String Name() {
        return _Name;
    }

    public String Phone() {
        return _Phone;
    }

    public double Rating() {
        return _Rating;
    }   

    public String Link() {
        return _Link;
    }
}