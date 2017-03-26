package edu.temple.tuhub.models;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by connorcrawford on 3/24/17.
 */

public class Instructor {

    private String instructorID;
    private String firstName;
    private String lastName;
    private String formattedName;

    private Instructor(String instructorID,
                       String firstName,
                       String lastName,
                       String formattedName) {

        this.instructorID = instructorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.formattedName = formattedName;
    }

    @Nullable
    public static Instructor createInstructor(JSONObject jsonObject) throws JSONException {
        String instructorID = jsonObject.getString("instructorId");
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        String formattedName = jsonObject.getString("formattedName");

        if (instructorID != null && firstName != null && lastName != null && formattedName != null)
            return new Instructor(instructorID, firstName, lastName, formattedName);
        return null;
    }


}
