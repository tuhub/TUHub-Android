package edu.temple.tuapilogin.Models;

/**
 * Created by laurenlezberg on 3/26/17.
 */

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class Grade {
    private String name;
    private String grade;
    private String updated;

    private Grade(String name, String grade, String updated){
        this.name=name;
        this.grade=grade;
        this.updated=updated;
    }

    


}
