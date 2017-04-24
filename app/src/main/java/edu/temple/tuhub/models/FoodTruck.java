package edu.temple.tuhub.models;

import android.support.annotation.Nullable;

import com.androidnetworking.error.ANError;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by laurenlezberg on 4/17/17.
 */

public class FoodTruck implements Serializable {

    public interface FoodTruckRequestListener {
        void onResponse(FoodTruck[] foodTrucks);

        void onError(ANError error);
    }

    private static final String DEFAULT_TERM = "Food Trucks";
    private static final String DEFAULT_LOCATION = "Temple University, Philadelphia, PA";
    private static final String SEARCH_LIMIT = "20";

    private static final String CONSUMER_KEY = "p9eiiQKJqloSQvFa8A3a0A";
    private static final String CONSUMER_SECRET = "FUFyMzXh_FE9ZdxsLaWbyxyaQto";
    private static final String TOKEN = "cwiEOUMlecclHHj2mPvk-bFcxZ5OW394";
    private static final String TOKEN_SECRET = "mDKbu3OKIbPmOYjz-UNqX-GrTnA";

    private String name;
    private String rating;
    private String isClosed;
    private String longitude;
    private String latitude;
    private String imageURL;
    private String phone;

    private FoodTruck(String name, String rating, String isClosed, String longitude, String latitude, String imageURL, String phone) {
        this.name = name;
        this.rating = rating;
        this.isClosed = isClosed;
        this.longitude = longitude;
        this.latitude = latitude;
        this.imageURL = imageURL;
        this.phone = phone;
    }

    @Nullable
    public static FoodTruck createFoodTruck(String name, String rating, String isClosed, String longitude, String latitude,String imageURL, String phone) {
        if (name != null && rating != null && isClosed != null && longitude != null && latitude != null)
            return new FoodTruck(name, rating, isClosed, longitude, latitude, imageURL, phone);
        return null;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getIsClosed() {
        return isClosed;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getImageURL(){return imageURL;}

    public String getPhone(){return phone;}


    public static void retrieveFoodTrucks(final FoodTruckRequestListener foodTruckRequestListener) {

        YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();
        Map<String, String> params = new HashMap<>();

        // general params
        params.put("term", DEFAULT_TERM);
        params.put("limit", SEARCH_LIMIT);

        // locale params
        params.put("lang", "en");

        Call<SearchResponse> call = yelpAPI.search(DEFAULT_LOCATION, params);
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                SearchResponse searchResponse = response.body();
                ArrayList<Business> businesses = searchResponse.businesses();
                FoodTruck[] foodTrucks = new FoodTruck[businesses.size()];
                for (int i = 0; i < businesses.size(); i++) {
                    FoodTruck ft = FoodTruck.createFoodTruck(
                            businesses.get(i).name(),
                            businesses.get(i).rating().toString(),
                            businesses.get(i).isClosed().toString(),
                            businesses.get(i).location().coordinate().longitude().toString(),
                            businesses.get(i).location().coordinate().latitude().toString(),
                            businesses.get(i).imageUrl(),
                            businesses.get(i).displayPhone()
                    );
                    foodTrucks[i] = ft;
                }
                foodTruckRequestListener.onResponse(foodTrucks);
                // Update UI text with the searchResponse.
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {

            }
        };

        call.enqueue(callback);
    }

}
