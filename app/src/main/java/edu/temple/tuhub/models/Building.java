package edu.temple.tuhub.models;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 4/15/2017.
 */

public class Building {

    private String name;
    private String imageUrl;
    private String latitude;
    private String longitude;
    private String error = "";

    public interface BuildingRequestListener {
        void onResponse(Building[] buildings);
        void onError(ANError error);
    }

    public Building(JSONObject object) {
        try {
            this.name = object.getString("name");
            this.imageUrl = object.getString("imageUrl");
            this.latitude = object.getString("latitude");
            this.longitude = object.getString("longitude");

        } catch (JSONException e) {
            this.error = e.toString();
        }
    }

    public Building(String name, String imageUrl, String latitude, String longitude) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public static void retrieveBuildings(String location, final BuildingRequestListener buildingRequestListener) {
        Map map = new HashMap<>(1);
        map.put("","MN");
        NetworkManager.SHARED.requestFromEndpoint(NetworkManager.Endpoint.MAP,
                null,
                map,
                null,
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray campusJson = response.getJSONArray("campuses");
                            for(int i = 0; i<campusJson.length(); i++) {
                                JSONObject campusObj = campusJson.getJSONObject(i);
                                JSONArray buildingJson = campusObj.getJSONArray("buildings");
                                Building[] buildings = new Building[buildingJson.length()];
                                for (int k = 0; k < buildingJson.length(); k++) {
                                    Building building = new Building(buildingJson.getJSONObject(k));
                                    buildings[k] = building;
                                    System.out.println(buildings[k].getName());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        buildingRequestListener.onError(anError);
                    }
                });
    }
}
