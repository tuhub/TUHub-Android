package edu.temple.tuhub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.temple.tuhub.models.Course;
import edu.temple.tuhub.models.CourseMeeting;
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
        return v;
    }

    public void populateCourses(final ListView lv, final String filter) {
        final List<Map<String, String>> data = new ArrayList<>();
        if (User.CURRENT != null) {
            User.CURRENT.retrieveCourses(new User.CoursesRequestListener() {
                @Override
                public void onResponse(Term[] terms) {
                    final List<Course> tempCourses = new ArrayList<>();
                    for (Term term : terms) {
                        for (int k = 0; k < term.getCourses().size(); k++) {
                            Map<String, String> datum = new HashMap<>(2);
                            datum.put("First Line", (term.getCourses().get(k).getTitle()));
                            datum.put("Second Line", (term.getCourses().get(k).getName()));
                            if (filter.equals("") || term.getCourses().get(k).getTitle().toLowerCase().contains(filter.toLowerCase())) {
                                tempCourses.add(term.getCourses().get(k));
                                data.add(datum);
                            }
                            if (getActivity() != null) {
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
                                        activity.showCourseDetails(tempCourses.get(position));
                                    }
                                });
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onError(ANError error) {
                    System.out.println("no terms found error");
                }
            });
        }
    }

    CourseFragment.showCourseDetailsInterface activity;

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (CourseFragment.showCourseDetailsInterface) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                populateCourses(lv, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    populateCourses(lv, "");
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
