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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Grade {
    public String name;
    public String grade;
    public Date updated;

    public Grade(String name, String grade, Date updated){
        this.name=name;
        this.grade=grade;
        this.updated=updated;
    }

    @Nullable
    public static Grade createGrade(JSONObject jsonObject) throws JSONException, ParseException {
        String name = jsonObject.getString("name");
        String grade = jsonObject.getString("value");
        String updatedStr = jsonObject.getString("updated");

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date updated = sdf.parse(updatedStr);

        if (name != null && grade != null && updated != null)
            return new Grade(name, grade, updated);
        return null;
    }

    public String getName() {
        return name;
    }

    public String getGrade() {
        return grade;
    }

    public Date getUpdated() {
        return updated;
    }

}
