package edu.temple.tuhub;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.temple.tuhub.models.Course;
import edu.temple.tuhub.models.Entry;
import edu.temple.tuhub.models.Term;
import edu.temple.tuhub.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCourseSearchFragment extends Fragment {
    ListView lv;

    public MyCourseSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_course_search, container, false);

        lv = (ListView) v.findViewById(R.id.myCourseSearchList);
        populateCourses(lv, "");
        final EditText et = (EditText) v.findViewById(R.id.myCoursesSearchEditText);
        Button searchBtn = (Button) v.findViewById(R.id.btnMyCourseSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                populateCourses(lv, et.getText().toString());
            }
        });


        return v;
    }
    public void populateCourses(final ListView lv, final String filter){
        final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        int terms;
        try {
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        User.CURRENT.retrieveCourses(new User.CoursesRequestListener() {

            @Override
            public void onResponse(Term[] terms) {
                for(int i = 0; i<terms.length; i++)
                {
                    for(int k = 0; k<terms[i].getCourses().size(); k++)
                    {

                        Map<String, String> datum = new HashMap<String, String>(2);
                        datum.put("First Line", (terms[i].getCourses().get(k).getTitle()));
                        datum.put("Second Line", (terms[i].getCourses().get(k).getName()));
                        if(filter.equals("")||terms[i].getCourses().get(k).getTitle().contains(filter)){
                        data.add(datum);}
                        SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), data, android.R.layout.simple_list_item_2,
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
                        lv.setAdapter(adapter);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {

                                //activity.searchAllResultsInterface(((Entry)tempCourses.get(position)));
                            }
                        });
                        adapter.notifyDataSetChanged();

                }
                }
            }

            @Override
            public void onError(ANError error) {
                System.out.println("no terms found error");
            }
        });


    }

    CourseFragment.showCourseDetails activity;

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (CourseFragment.showCourseDetails) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public interface showCourseDetails{
        void showCourseDetails(Course course);
    }

}
