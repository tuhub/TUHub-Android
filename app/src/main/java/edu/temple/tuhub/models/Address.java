package edu.temple.tuhub.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 4/17/2017.
 */

public class Address {

    private String location;

    public Address(String location){
        this.location = location;
    }
    public Address(JSONArray addressArray) throws JSONException {
        this(addressArray.getJSONObject(0).getString("formatted_address"));
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
