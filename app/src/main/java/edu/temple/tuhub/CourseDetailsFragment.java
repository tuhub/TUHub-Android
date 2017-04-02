package edu.temple.tuhub;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.temple.tuhub.models.Course;
import edu.temple.tuhub.models.CourseMeeting;
import edu.temple.tuhub.models.Entry;
import edu.temple.tuhub.models.Instructor;

import static android.widget.AdapterView.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseDetailsFragment extends Fragment {

    String courseName;
    String courseTitle;
    String courseDates;
    List<CourseMeeting> meetingTimes;
    List<Instructor> faculty;
    ListView gradeList;
    String grade;
    String gradeDetails;
    String[] classRoster;
    ListView rosterList;
    ListView meetingsList;
    ListView facultyList;
    Course course;

    Bundle data;

    public CourseDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            data = bundle;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_course_details, container, false);
        gradeList = (ListView) v.findViewById(R.id.gradesList);
        rosterList = (ListView) v.findViewById(R.id.rosterList);
        meetingsList = (ListView) v.findViewById(R.id.courseMeetingsList);
        facultyList = (ListView) v.findViewById(R.id.facultyList);

        getCourseData(data);
        setCourseData(v);
        loadRoster(course);

        return v;
    }

    private void getCourseData(Bundle data){
        courseName = data.getString("name");
        courseTitle = data.getString("fullname");
        courseDates = data.getString("date");
        /*todo grades
        grade = data.getString("gradeName");
        gradeDetails = data.getString("gradeType")+" "+data.getString("gradeUpdated"); */
        classRoster = data.getStringArray("roster");
        meetingTimes = (List<CourseMeeting>) data.getSerializable("meetings");
        faculty = (List<Instructor>) data.getSerializable("faculty");
        course = (Course) data.getSerializable("course");

    }
    private void setCourseData(View v) {
        TextView name = (TextView) v.findViewById(R.id.courseName);
        name.setText(courseTitle + " - " + courseName);
        TextView courseTerm = (TextView) v.findViewById(R.id.courseMeetingTerm);
        courseTerm.setText(courseDates);


        //meetings
        List<Map<String, String>> meetingsData = new ArrayList<Map<String, String>>();
        for (int i = 0; i < meetingTimes.size(); i++) {
            Map<String, String> datum2 = new HashMap<>(2);
            datum2.put("First Line", meetingTimes.get(i).getStartTimeWTz() + " to " + meetingTimes.get(i).getEndTimeWTz());
            datum2.put("Second Line", meetingTimes.get(i).getBuildingName() + ", " + meetingTimes.get(i).getRoom());
            meetingsData.add(datum2);
            SimpleAdapter meetingsAdapter = new SimpleAdapter(getActivity().getApplicationContext(), meetingsData, android.R.layout.simple_list_item_2,
                    new String[]{"First Line", "Second Line"},
                    new int[]{android.R.id.text1, android.R.id.text2}) {

                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    text1.setTextColor(Color.BLACK);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    text2.setTextColor(Color.DKGRAY);
                    return view;
                }
            };
            meetingsList.setAdapter(meetingsAdapter);
            meetingsList.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                }
            });
            meetingsAdapter.notifyDataSetChanged();
        }
    //faculty

        ArrayList<String> facultyData = new ArrayList<>();
        for (int i = 0; i < faculty.size(); i++) {
            facultyData.add(faculty.get(i).getFirstName() + " " + faculty.get(i).getLastName());
        }
            facultyList.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, facultyData) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextColor(Color.BLACK);
                    return textView;
                }
            });
        }
        public void loadRoster(Course c){
            c.retrieveRoster(new Course.RosterRequestListener() {
                @Override
                public void onResponse(String[] roster) {
                    //Roster listview
                    rosterList.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, roster) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setTextColor(Color.BLACK);
                            return textView;
                        }
                    });
                }

                @Override
                public void onError(ANError error) {
                }
            });

        }
        public void loadGrades(Course c){
        //Grade listview
        List<Map<String, String>> gradeData = new ArrayList<Map<String, String>>();
        Map<String, String> datum = new HashMap<>(2);
        datum.put("First Line", grade);
        datum.put("Second Line", gradeDetails);
        gradeData.add(datum);
        SimpleAdapter gradeAdapter = new SimpleAdapter(getActivity().getApplicationContext(), gradeData, android.R.layout.simple_list_item_2,
                new String[]{"First Line", "Second Line"},
                new int[]{android.R.id.text1, android.R.id.text2}) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.BLACK);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(Color.DKGRAY);
                return view;
            }
        };
        gradeList.setAdapter(gradeAdapter);
        gradeList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        gradeAdapter.notifyDataSetChanged();

        }
    }

