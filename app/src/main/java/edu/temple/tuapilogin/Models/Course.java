package edu.temple.tuapilogin.Models;

import android.support.annotation.Nullable;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 3/24/17.
 */

public class Course {

    public interface RosterRequestListener {
        void onResponse(String[] roster);
        void onError(ANError error);
    }

    private String name;
    private String title;
    private String description;
    private String sectionID;
    private String termID;
    private String sectionNumber;
    private Date startDate;
    private Date endDate;

    @Nullable
    private List<CourseMeeting> meetings;

    @Nullable
    private List<Instructor> instructors;

    // TODO: Add grades
//    @Nullable
//    private List<Grade> grades;

    @Nullable
    private String[] roster;

    public Course(String name,
                  String title,
                  String description,
                  String sectionID,
                  String sectionNumber,
                  Date startDate,
                  Date endDate,
                  String termID,
                  List<CourseMeeting> meetings,
                  List<Instructor> instructors) {

        this.name = name;
        this.title = title;
        this.description = description;
        this.sectionID = sectionID;
        this.termID = termID;
        this.sectionNumber = sectionNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.meetings = meetings;
    }

    public static Course createCourse(JSONObject jsonObject, String termID) throws JSONException {
        String name = jsonObject.getString("courseName");
        String title = jsonObject.getString("sectionTitle");
        String description = jsonObject.getString("courseDescription");
        String sectionID = jsonObject.getString("sectionId");
        String sectionNumber = jsonObject.getString("courseSectionNumber");
        String startDateStr = jsonObject.getString("firstMeetingDate");
        String endDateStr = jsonObject.getString("lastMeetingDate");

        // TODO: Parse string into date
        Date startDate = null;
        Date endDate = null;

        // Get course meetings
        JSONArray sectionJSON = jsonObject.getJSONArray("meetingPatterns");
        ArrayList<CourseMeeting> meetings = new ArrayList<>();
        for (int i = 0; i < sectionJSON.length(); i++) {
            JSONObject courseJSON = sectionJSON.getJSONObject(i);
            if (courseJSON == null)
                break;
            CourseMeeting meeting = CourseMeeting.createCourseMeeting(jsonObject);
            if (meeting != null)
                meetings.add(meeting);
        }

        // Get instructors
        JSONArray instructorsJSON = jsonObject.getJSONArray("meetingPatterns");
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
                endDate,
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
                                roster[i] = rosterJSON.getString(i);
                            }
                            rosterRequestListener.onResponse(roster);
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

}
