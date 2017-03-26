package edu.temple.tuhub.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created on 3/24/17.
 */

public class CourseMeeting {

    private static final String startTimeKey = "sisStartTimeWTz";
    private static final String endTimeKey = "sisEndTimeWTz";

    private String buildingID;
    private String buildingName;
    private String room;
    private String startTime;
    private String endTime;
    private String startTimeWTz;
    private String endTimeWTz;
    private int[] daysOfWeek;

    private CourseMeeting(String buildingID,
                          String buildingName,
                          String room,
                          String startTime,
                          String endTime,
                          String startTimeWTz,
                          String endTimeWTz,
                          int[] daysOfWeek){

        this.buildingID = buildingID;
        this.buildingName = buildingName;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeWTz = startTimeWTz;
        this.endTimeWTz = endTimeWTz;
        this.daysOfWeek = daysOfWeek;
    }

    public static CourseMeeting createCourseMeeting(JSONObject jsonObject) throws JSONException {
        String buildingID = jsonObject.getString("buildingId");
        String buildingName = jsonObject.getString("building");
        String room = jsonObject.getString("room");
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        String startTimeWTz = toTwelveHour(jsonObject.getString(startTimeKey));
        String endTimeWTz = toTwelveHour(jsonObject.getString(endTimeKey));
        JSONArray daysOfWeekJSON = jsonObject.getJSONArray("daysOfWeek");

        int[] daysOfWeek = new int[daysOfWeekJSON.length()];
        for (int i = 0; i < daysOfWeekJSON.length(); i++) {
            daysOfWeek[i] = daysOfWeekJSON.optInt(i);
        }

        return new CourseMeeting(buildingID, buildingName, room, startTime, endTime, startTimeWTz, endTimeWTz, daysOfWeek);
    }

    //Determine if the course meets on the given day
    public boolean isOnThisDay(int thisDay){

            for (int j = 0; j < daysOfWeek.length; j++) {

                if (daysOfWeek[j] == thisDay) {
                    return true;
                }
            }

        return false;
    }

    private static String toTwelveHour(String time){
        time = time.substring(0, 5);
        try{
            String suffix;
            String hour = time.substring(0, 2);
            int timeValue = Integer.parseInt(hour);

            if(timeValue >= 13){
                timeValue = timeValue - 12;
                hour = String.valueOf(timeValue);
                suffix = " PM";
            } else {
                suffix = " AM";
            }

            return hour + time.substring(2) + suffix;
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        return null;
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

    public String getStartTimeWTz() {
        return startTimeWTz;
    }

    public String getEndTimeWTz() {
        return endTimeWTz;
    }
}
