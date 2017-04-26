package edu.temple.tuhub.models;

import android.support.annotation.Nullable;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * Created on 3/24/17.
 */

public class Course implements Serializable {

    public interface RosterRequestListener {
        void onResponse(String[] roster);

        void onError(ANError error);
    }

    public interface GradeRequestListener {
        void onResponse(String[] grades);

        void onError(ANError error);
    }

    private String name;
    private String title;
    private String description;
    private String sectionID;
    private String termID;
    private String sectionNumber;
    private String startDate;
    private String startDateStr;
    private String endDate;
    private String endDateStr;

    @Nullable
    private List<CourseMeeting> meetings;

    @Nullable
    private List<Instructor> instructors;

    @Nullable
    List<Grade> grades;

    @Nullable
    private String[] roster;

    public Course(String name,
                  String title,
                  String description,
                  String sectionID,
                  String sectionNumber,
                  String startDate,
                  String startDateStr,
                  String endDate,
                  String endDateStr,
                  String termID,
                  @Nullable List<CourseMeeting> meetings,
                  @Nullable List<Instructor> instructors) {

        this.name = name;
        this.title = title;
        this.description = description;
        this.sectionID = sectionID;
        this.termID = termID;
        this.sectionNumber = sectionNumber;
        this.startDate = startDate;
        this.startDateStr = startDateStr;
        this.endDate = endDate;
        this.endDateStr = endDateStr;
        this.meetings = meetings;
        this.instructors = instructors;
    }

    public static Course createCourse(JSONObject jsonObject, String termID) throws JSONException {
        String name = jsonObject.getString("courseName");
        String sectionID = jsonObject.getString("sectionId");
        String title = jsonObject.getString("sectionTitle");
        String description = jsonObject.getString("courseDescription");
        String sectionNumber = jsonObject.getString("courseSectionNumber");
        String startDateStr = jsonObject.getString("firstMeetingDate");
        String endDateStr = jsonObject.getString("lastMeetingDate");

        String startDate = formatDate(startDateStr);
        String endDate = formatDate(endDateStr);

        // Get course meetings
        JSONArray meetingsJSON = jsonObject.getJSONArray("meetingPatterns");
        ArrayList<CourseMeeting> meetings = new ArrayList<>();
        for (int i = 0; i < meetingsJSON.length(); i++) {
            JSONObject meetingJSON = meetingsJSON.getJSONObject(i);
            if (meetingJSON == null)
                break;
            CourseMeeting meeting = CourseMeeting.createCourseMeeting(meetingJSON);
            if (meeting != null)
                meetings.add(meeting);
        }

        // Get instructors
        JSONArray instructorsJSON = jsonObject.getJSONArray("instructors");
        ArrayList<Instructor> instructors = new ArrayList<>();
        for (int i = 0; i < instructorsJSON.length(); i++) {
            JSONObject instructorJSON = instructorsJSON.getJSONObject(i);
            if (instructorJSON == null)
                break;
            Instructor instructor = Instructor.createInstructor(instructorJSON);
            if (instructor != null)
                instructors.add(instructor);
        }

        return new Course(name,
                title,
                description,
                sectionID,
                sectionNumber,
                startDate,
                startDateStr,
                endDate,
                endDateStr,
                termID,
                meetings,
                instructors);
    }

    public void retrieveRoster(final RosterRequestListener rosterRequestListener) {
        if (User.CURRENT == null)
            return;

        // Generate parameters
        Map<String, String> params = new HashMap<>(2);
        params.put("term", termID);
        params.put("section", sectionID);

        NetworkManager.SHARED.requestFromEndpoint(NetworkManager.Endpoint.COURSE_ROSTER,
                User.CURRENT.getTuID(),
                params,
                User.CURRENT.getCredential(),
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray rosterJSON = response.getJSONArray("activeStudents");
                            String[] roster = new String[rosterJSON.length()];
                            for (int i = 0; i < rosterJSON.length(); i++) {
                                JSONObject student = rosterJSON.getJSONObject(i);
                                roster[i] = student.getString("name");
                            }
                            Course.this.roster = roster;
                            rosterRequestListener.onResponse(roster);
                        } catch (JSONException e) {
                            // TODO: Handle error
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Course", anError.getErrorBody());
                    }
                });
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSectionID() {
        return sectionID;
    }

    public String getTermID() {
        return termID;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public long getRawStartDate(){
        try {
            Date sd =  new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
            return sd.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }
    public long getRawEndDate(){
        try {
            Date ed = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
            return ed.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }
    public Date getStartDateFormat(){
        try {
            return new SimpleDateFormat("yy-MM-dd").parse(startDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Date getEndDateFormat(){
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getEndDate() {
        return endDate;
    }

    @Nullable
    public List<CourseMeeting> getMeetings() {
        return meetings;
    }

    @Nullable
    public List<Instructor> getInstructors() {
        return instructors;
    }

    @Nullable
    public String[] getRoster() {
        return roster;
    }

    @Nullable
    public List<Grade> getGrades() {
        return grades;
    }

    private static String formatDate(String date){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Date formatedDate;
        try {
            formatedDate = simpleDateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
        return newFormat.format(formatedDate);

    }

}
