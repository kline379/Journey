package backend;

import org.json.*;

public class YelpBusiness {

    private String _Name;
    private static final String _NameJSON = "name";

    private String _Phone;
    private static final String _PhoneJSON = "phone";

    private double _Rating;
    private static final String _RatingJSON = "rating";

    public static YelpBusiness ParseJSON(JSONObject obj)
        throws JSONException
    {
        String name = obj.getString(_NameJSON);
        String phone = obj.getString(_PhoneJSON);
        String ratingStr = obj.getString(_RatingJSON);
        double rating = Double.parseDouble(ratingStr);
        return new YelpBusiness(name, phone, rating);
    }

    public YelpBusiness(
        String name,
        String phone,
        double rating
    ) {
        _Name = name;
        _Phone = phone;
        _Rating = rating;
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
}