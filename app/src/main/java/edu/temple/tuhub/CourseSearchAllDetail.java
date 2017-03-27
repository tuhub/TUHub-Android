package edu.temple.tuhub;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseSearchAllDetail extends Fragment {

    TextView course;
    TextView crseId;
    TextView description;
    TextView creditHr;
    TextView college;
    TextView division;
    TextView department;
    TextView schedule;
    Bundle data;

    public CourseSearchAllDetail() {
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
    public static CourseSearchAllDetail newInstance() {
        CourseSearchAllDetail fragment = new CourseSearchAllDetail();
//        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_course_search_all_detail, container, false);
        course = (TextView) v.findViewById(R.id.searchAllCourse);
        course.setText(data.getString("course"));
        crseId = (TextView) v.findViewById(R.id.crseIdAllCourse);
        crseId.setText(data.getString("crseId"));
        description = (TextView) v.findViewById(R.id.searchAllDescription);
        description.setText(data.getString("description"));
        creditHr = (TextView) v.findViewById(R.id.searchAllcredit);
        creditHr.setText(data.getString("creditHr"));
        college = (TextView) v.findViewById(R.id.collegeSearchAll);
        college.setText(data.getString("college"));
        division = (TextView) v.findViewById(R.id.searchAllDivision);
        division.setText(data.getString("division"));
        department = (TextView) v.findViewById(R.id.searchAllDepartment);
        department.setText(data.getString("department"));
        schedule = (TextView) v.findViewById(R.id.searchAllSchedule);
        schedule.setText(data.getString("schedule"));
        return v;
    }
}
