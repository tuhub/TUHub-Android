package edu.temple.tuhub;

import android.app.Activity;
import android.os.Bundle;

import android.app.Fragment;
import android.app.FragmentManager;

import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import edu.temple.tuhub.models.Course;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFragment extends Fragment implements CourseCalendarFragment.CalendarClickListener {

    public CourseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseFragment newInstance() {
        CourseFragment fragment = new CourseFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ViewPager mPager;
    MyAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_course, container, false);

        mAdapter = new MyAdapter(getFragmentManager());

        mPager = (ViewPager) view.findViewById(R.id.view_pager);
        mPager.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void showCourseDetails(Course course) {
        //TODO implement loading the details fragment with the given course
        Log.d("Course selected:", course.getTitle());
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;
        private CourseCalendarFragment courseCalendarFragment;
        private CourseListFragment courseListFragment;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            courseCalendarFragment = new CourseCalendarFragment();
            courseListFragment = new CourseListFragment();
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return courseCalendarFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return courseListFragment;
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Calendar";
                case 1:
                    return "List";
                default:
                    return null;
            }
        }
    }

    courseSearchHandler activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (courseSearchHandler) c;
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
                activity.courseSearchHandler(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                // do s.th.
                return true;
            case R.id.menu_your_courses:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface courseSearchHandler {
        void courseSearchHandler(String query);
    }
}

