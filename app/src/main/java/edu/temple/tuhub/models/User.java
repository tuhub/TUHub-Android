package edu.temple.tuhub.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public interface GradesRequestListener {
        void onResponse();
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

    /**
     * Retrieves the user's grades and inserts them into their corresponding courses
     * @param courseTerms The array of terms containing the courses to insert grades into
     * @param gradesRequestListener The request listener that is called when the grades are retrieved
     */
    private void retrieveGrades(final Term[] courseTerms, final GradesRequestListener gradesRequestListener) {
        NetworkManager.SHARED.requestFromEndpoint(NetworkManager.Endpoint.GRADES,
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
                                JSONObject termJSON = termsJSON.getJSONObject(i);
                                JSONArray coursesJSON = termJSON.getJSONArray("sections");
                                for (int j = 0; j < coursesJSON.length(); j++) {
                                    JSONObject courseJSON = coursesJSON.getJSONObject(j);

                                    // Find the corresponding course
                                    Course course = null;
                                    for (Term t: courseTerms) {
                                        if (t.getTermID().equals(termJSON.getString("id"))) {
                                            for (Course c: t.getCourses()) {
                                                if (c.getSectionID().equals(courseJSON.getString("sectionId"))) {
                                                    course = c;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    // Unable to find the corresponding course, skip grade creation
                                    if (course == null)
                                        continue;

                                    // Create the grade objects
                                    JSONArray gradesJSON = courseJSON.getJSONArray("grades");
                                    List<Grade> grades = new ArrayList<>(0);
                                    for (int k = 0; k < gradesJSON.length(); k++) {
                                        Grade grade = Grade.createGrade(gradesJSON.getJSONObject(k));
                                        if (grade != null)
                                            grades.add(grade);
                                    }

                                    // Set the course's grades to the created grades
                                    course.grades = grades;
                                }
                            }

                            gradesRequestListener.onResponse();
                        } catch (JSONException e) {
                            // TODO: Handle error
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        gradesRequestListener.onError(anError);
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
                            final Term[] terms = new Term[termsJSON.length()];
                            for (int i = 0; i < termsJSON.length(); i++) {
                                Term term = Term.createTerm(termsJSON.getJSONObject(i));
                                if (term != null)
                                    terms[i] = term;
                            }

                            retrieveGrades(terms, new GradesRequestListener() {
                                @Override
                                public void onResponse() {
                                    User.this.terms = terms;
                                    coursesRequestListener.onResponse(terms);
                                }

                                @Override
                                public void onError(ANError error) {

                                }
                            });
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
