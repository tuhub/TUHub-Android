package edu.temple.tuhub.models;

/**
 * Created by laurenlezberg on 3/26/17.
 */

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class Grade {
    public String name;
    public String grade;
    public String updated;

    private Grade(String name, String grade, String updated){
        this.name=name;
        this.grade=grade;
        this.updated=updated;
    }

    @Nullable
    public static Grade createGrade(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString("name");
        String grade = jsonObject.getString("value");
        String updated = jsonObject.getString("updated");

        if (name != null && grade != null && updated != null)
            return new Grade(name, grade, updated);
        return null;

    }


}
