package edu.temple.tuapilogin.Models;

/**
 * Created by laurenlezberg on 3/26/17.
 */

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

public class Grade {
    private String name;
    private String grade;
    private Date updated;

    private Grade(String name, String grade, Date updated){
        this.name=name;
        this.grade=grade;
        this.updated=updated;
    }




}
