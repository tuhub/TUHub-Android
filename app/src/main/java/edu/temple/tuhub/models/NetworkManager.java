package edu.temple.tuhub.models;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

/**
 * Created on 3/24/17.
 */

public class NetworkManager {
    public enum Endpoint {
        USER_INFO("https://prd-mobile.temple.edu/banner-mobileserver/api/2.0/security/getUserInfo"),
        GRADES("https://prd-mobile.temple.edu/banner-mobileserver/api/2.0/grades"),
        COURSES("https://prd-mobile.temple.edu/banner-mobileserver/api/2.0/courses/overview"),
        COURSE_ROSTER("https://prd-mobile.temple.edu/banner-mobileserver/api/2.0/courses/roster"),
        NEWS("https://prd-mobile.temple.edu/banner-mobileserver/rest/1.2/feed"),
        COURSE_SEARCH("https://prd-mobile.temple.edu/CourseSearch/searchCatalog.jsp");

        private final String url;

        Endpoint(final String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return url;
        }
    }

    public static NetworkManager SHARED = new NetworkManager();

    public void requestFromEndpoint(Endpoint endpoint,
                                    @Nullable String tuID,
                                    @Nullable Map<String, String> parameters,
                                    @Nullable Credential credential,
                                    JSONObjectRequestListener jsonObjectRequestListener) {

        String url = endpoint.toString();

        // Add TU ID to path if present
        if (tuID != null) {
            url += ("/" + tuID);
        }

        Log.d("url", url);

        // Base request
        ANRequest.GetRequestBuilder requestBuilder = AndroidNetworking.get(url);

        // Add basic auth header if credentials are present
        if (credential != null) {
            requestBuilder.addHeaders(NetworkManager.generateBasicAuthHeader(credential));
        }

        // Add parameters if present
        if (parameters != null) {
            requestBuilder.addQueryParameter(parameters);
        }

        // Make the request
        requestBuilder.build().getAsJSONObject(jsonObjectRequestListener);
    }

    public void requestFromEndpoint(Endpoint endpoint,
                                @Nullable String tuID,
                                @Nullable Map<String, String> parameters,
                                @Nullable Credential credential,
                                JSONArrayRequestListener jsonArrayRequestListener) {

        String url = endpoint.toString();

        // Add TU ID to path if present
        if (tuID != null) {
            url += ("/" + tuID);
        }

        // Base request
        ANRequest.GetRequestBuilder requestBuilder = AndroidNetworking.get(url);

        // Add basic auth header if credentials are present
        if (credential != null) {
            requestBuilder.addHeaders(NetworkManager.generateBasicAuthHeader(credential));
        }

        // Add parameters if present
        if (parameters != null) {
            requestBuilder.addQueryParameter(parameters);
        }

        // Make the request
        requestBuilder.build().getAsJSONArray(jsonArrayRequestListener);
    }

    static Map<String, String> generateBasicAuthHeader(Credential credential) {
        Map<String, String> map = new HashMap<>(1);
        map.put("Authorization", "Basic "
                + Base64.encodeToString((credential.username + ":" + credential.password)
                .getBytes(), Base64.NO_WRAP));
        return map;
    }


}
