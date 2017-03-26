package edu.temple.tuhub.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created on 3/24/17.
 */

public class CourseMeeting {

    private String buildingID;
    private String buildingName;
    private String room;
    private String startTime;
    private String endTime;
    private int[] daysOfWeek;

    private CourseMeeting(String buildingID,
                          String buildingName,
                          String room,
                          String startTime,
                          String endTime,
                          int[] daysOfWeek){

        this.buildingID = buildingID;
        this.buildingName = buildingName;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = daysOfWeek;
    }

    public static CourseMeeting createCourseMeeting(JSONObject jsonObject) throws JSONException {
        String buildingID = jsonObject.getString("buildingId");
        String buildingName = jsonObject.getString("building");
        String room = jsonObject.getString("room");
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        JSONArray daysOfWeekJSON = jsonObject.getJSONArray("daysOfWeek");

        int[] daysOfWeek = new int[daysOfWeekJSON.length()];
        for (int i = 0; i < daysOfWeekJSON.length(); i++) {
            daysOfWeek[i] = daysOfWeekJSON.optInt(i);
        }

        return new CourseMeeting(buildingID, buildingName, room, startTime, endTime, daysOfWeek);
    }

    public String getBuildingID() {
        return buildingID;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getRoom() {
        return room;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int[] getDaysOfWeek() {
        return daysOfWeek;
    }
}
