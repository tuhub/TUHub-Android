package edu.temple.tuhub;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.temple.tuhub.models.Course;
import edu.temple.tuhub.models.CourseMeeting;
import edu.temple.tuhub.models.Term;
import edu.temple.tuhub.models.User;


public class CourseCalendarFragment extends Fragment implements CalendarView.EventHandler {

    @BindView(R.id.calendar_view)
    CalendarView calendar;
    @BindView(R.id.calendar_scroll_contents)
    LinearLayout eventList;

    private CalendarClickListener mListener;
    private ArrayList<Term> allSemesters;

    public CourseCalendarFragment() {
        // Required empty public constructor
    }


    public static CourseCalendarFragment newInstance() {
        CourseCalendarFragment fragment = new CourseCalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_course_calendar, container, false);
        ButterKnife.bind(this, rootView);

        calendar.setEventHandler(CourseCalendarFragment.this);

        if (User.CURRENT != null) {
            allSemesters = new ArrayList<>(Arrays.asList(User.CURRENT.getTerms()));
            onDayClick(new Date());
        }


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CalendarClickListener) {
            mListener = (CalendarClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof CalendarClickListener) {
            mListener = (CalendarClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Interface method for CalendarView -- Populates the event window with the given date's events
    @Override
    public void onDayClick(Date date) {

        Log.d("Day clicked: ", date.toString());
        if(allSemesters != null) {
            Term semester = getSemester(date);
            if (semester != null) {
                populateEventList(semester, date);
            } else {
                eventList.removeAllViews();
            }
        }

    }

    //Gets the Term that includes the given date
    private Term getSemester(Date date) {
        for (int i = 0; i < allSemesters.size(); i++) {

                Date startDate = allSemesters.get(i).getStartDate();
            Log.d("Start Date: ", startDate.toString());

                Date endDate = allSemesters.get(i).getEndDate();

                if (!date.before(startDate) && !date.after(endDate)) {
                    Log.d("Found Semester: ", String.valueOf(i));
                    return allSemesters.get(i);
                }

        }
        Log.d("No Semester Found: ", "null");
        return null;
    }

    //Adds EventListElements to the event section given the date and the semester
    private void populateEventList(Term semester, Date date) {
        eventList.removeAllViews();

        //Retrieve list of courses
            List<Course> courses = semester.getCourses();

        //Get meeting times for each course and add an event if the meeting time is on the given date
            for(int i = 0; i<courses.size(); i++){
                Course thisCourse = courses.get(i);
                Log.d("Adding Course: " , thisCourse.toString());
                String title = thisCourse.getTitle();
                List<CourseMeeting> meetingPatterns = thisCourse.getMeetings();
                if(meetingPatterns == null){
                    continue;
                }
                //Traverse meetingPatterns backwards because meetings are stored by time of day latest first
                for(int j = meetingPatterns.size() - 1; j >= 0; j--) {
                    CourseMeeting meeting = meetingPatterns.get(j);
                    String startTime = meeting.getStartTimeWTz();
                    String endTime = meeting.getEndTimeWTz();
                    String room = meeting.getRoom();
                    String building = meeting.getBuildingName();

                    //Check to see if meeting is on this day. Use date.getDay() + 1 because the day index in the API starts at 1 not 0
                    if (meeting.isOnThisDay(date.getDay() + 1)) {

                        EventListElement event = new EventListElement(getActivity());
                        event.setEventText(title + "\n" + building + ": " + room + "\n" + startTime + " - " + endTime);
                        event.setOnClickListener(new EventClickListener(thisCourse));
                        eventList.addView(event);
                    }
                }
            }

    }

    //Create an error message in the event list window
    private void setError(String error) {
        TextView errorMessage = new TextView(getActivity());
        errorMessage.setText("An error occurred while retrieving course information: " + error);
        eventList.removeAllViews();
        eventList.addView(errorMessage);
    }

    //OnClickListener for event list items. Relays the course that was clicked to the attached CalendarClickListener
    private class EventClickListener implements View.OnClickListener{
        private Course section;

        public EventClickListener(Course section){
            this.section = section;
        }

        @Override
        public void onClick(View v) {
                mListener.showCourseDetails(section);

        }
    }

    //Interface for fragment -> parent communication
    public interface CalendarClickListener {
        void showCourseDetails(Course course);
    }

}
