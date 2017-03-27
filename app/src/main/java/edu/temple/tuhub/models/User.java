package edu.temple.tuhub.models;

import android.support.annotation.Nullable;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created on 3/24/17.
 */

public class User {

    public interface UserRequestListener {
        void onResponse(User user);
        void onError(ANError error);
    }

    public interface CoursesRequestListener {
        void onResponse(Term[] terms);
        void onError(ANError error);
    }

    @Nullable
    public static User CURRENT;

    private String username;
    private String tuID;
    private Credential credential;

    @Nullable
    Term[] terms;

    private User(String username, String tuID, Credential credential) {
        this.username = username;
        this.tuID = tuID;
        this.credential = credential;
    }

    public String getUsername() {
        return username;
    }

    public String getTuID() {
        return tuID;
    }

    public Credential getCredential() {
        return credential;
    }

    @Nullable
    public Term[] getTerms() {
        return terms;
    }

    static User createUser(JSONObject jsonObject, Credential credential) throws JSONException {
        String username = jsonObject.getString("authId");
        String tuID = jsonObject.getString("userId");

        return new User(username, tuID, credential);
    }


    public static void signInUser(String username,
                                  String password,
                                  final UserRequestListener userRequestListener) {
        System.out.println("sign in");

        final Credential credential = new Credential(username, password);
        NetworkManager.SHARED.requestFromEndpoint(NetworkManager.Endpoint.USER_INFO,
                null,
                null, credential,
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        User user = null;
                        try {
                            // Try to initialize the user with JSON
                            user = User.createUser(response, credential);
                            // TODO: Add persistence logic
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        User.CURRENT = user;
                        userRequestListener.onResponse(user);
                    }

                    @Override
                    public void onError(ANError anError) {
                        userRequestListener.onError(anError);
                    }
                });
    }

    public void retrieveCourses(final CoursesRequestListener coursesRequestListener) {
        NetworkManager.SHARED.requestFromEndpoint(NetworkManager.Endpoint.COURSES,
                tuID,
                null,
                credential,
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray termsJSON = response.getJSONArray("terms");
                            Term[] terms = new Term[termsJSON.length()];
                            for (int i = 0; i < termsJSON.length(); i++) {
                                Term term = Term.createTerm(termsJSON.getJSONObject(i));
                                if (term != null)
                                    terms[i] = term;
                            }
                            User.this.terms = terms;
                            coursesRequestListener.onResponse(terms);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        coursesRequestListener.onError(anError);
                    }
                });
    }

}
