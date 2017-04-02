package edu.temple.tuhub.models;

/**
 * Created by laurenlezberg on 3/26/17.
 */

import android.support.annotation.Nullable;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Grade {
    public String name;
    public String grade;
    public String updated;

    public Grade(String name, String grade, String updated){
        this.name=name;
        this.grade=grade;
        this.updated=updated;
    }

    @Nullable
    public String[] createGrade(JSONObject jsonObject, String[] gradeString) throws JSONException {

        String name = jsonObject.getString("name");
        String grade = jsonObject.getString("value");
        String updated = jsonObject.getString("updated");
        System.out.println(name+ " "+grade+" "+updated);
        gradeString[0] = name;
        gradeString[1] = grade;
        gradeString[2] = updated;
        return gradeString;

    }

    public static void retrieveGrades(final Course.GradeRequestListener gradeRequestListener, String term, String section) {
        if (User.CURRENT == null) {
            return;
        }
        // Generate parameters
        Map<String, String> params = new HashMap<>(2);
        params.put("term", term);
        params.put("section", section);
        NetworkManager.SHARED.requestFromEndpoint(NetworkManager.Endpoint.GRADES,
                User.CURRENT.getTuID(),
                params,
                User.CURRENT.getCredential(),
                new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String[] gradeString = new String[3];
                        try {
                            JSONArray terms = response.getJSONArray("terms");
                            //first level - terms
                            for (int i = 0; i < terms.length(); i++) {
                                JSONObject obj1 = terms.getJSONObject(i);
                                //second level - sections
                                JSONArray sections = obj1.getJSONArray("sections");
                                for (int j = 0; j < sections.length(); j++) {
                                    JSONObject obj = sections.getJSONObject(j);
                                    //third level - grades
                                    JSONArray gradesResponse = obj.getJSONArray("grades");

                                    for (int k = 0; k < gradesResponse.length(); k++) {
                                        JSONObject grades = gradesResponse.getJSONObject(k);
                                        //create Grade object for related course

                                        gradeString[0] = grades.getString("name");
                                        gradeString[1] = grades.getString("value");
                                        gradeString[2] = grades.getString("updated");
                                    }
                                }
                            }
                           gradeRequestListener.onResponse(gradeString);
                        } catch (JSONException e) {
                            // TODO: Handle error
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    public String getName() {
        return name;
    }

    public String getGrade() {
        return grade;
    }

    public String getUpdated() {
        return updated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
