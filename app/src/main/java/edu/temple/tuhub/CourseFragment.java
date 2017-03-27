package edu.temple.tuhub;

import android.app.Activity;
import android.os.Bundle;

import android.app.Fragment;
import android.app.FragmentManager;

import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.error.ANError;

import edu.temple.tuhub.models.Course;
import edu.temple.tuhub.models.Term;
import edu.temple.tuhub.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFragment extends Fragment implements CourseCalendarFragment.CalendarClickListener {

    private ViewPager mViewPager;
    private static int NUM_ITEMS = 2;

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
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.CURRENT != null) {
            if (User.CURRENT.getTerms() == null) {
                User.CURRENT.retrieveCourses(new User.CoursesRequestListener() {
                    @Override
                    public void onResponse(Term[] terms) {
                        CourseFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mViewPager != null)
                                    mViewPager.getAdapter().notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(ANError error) {
                        // TODO: Handle error
                        System.out.println(error.getErrorBody());
                    }
                });
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_course, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));

        return view;
    }

    @Override
    public void showCourseDetails(Course course) {
        //TODO implement loading the details fragment with the given course
        Log.d("Course selected:", course.getTitle());
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment[] fragments = new Fragment[NUM_ITEMS];

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            if (User.CURRENT != null && User.CURRENT.getTerms() != null)
                return NUM_ITEMS;
            return 0;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (fragments[position] == null)
                        fragments[position] = CourseCalendarFragment.newInstance();
                    break;
                case 1:
                    if (fragments[position] == null)
                        fragments[position] = CourseListPagerFragment.newInstance();
                    break;
                default:
                    return null;
            }
            return fragments[position];
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
                activity.courseSearchHandler(query,false);
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
                return true;
            case R.id.menu_your_courses:
                activity.courseSearchHandler("", true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface courseSearchHandler {
        void courseSearchHandler(String query, boolean myCourses);
    }
    public interface showCourseDetails{
        void showCourseDetails(Course course);
    }
}

