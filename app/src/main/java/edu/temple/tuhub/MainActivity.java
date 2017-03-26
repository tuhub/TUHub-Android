package edu.temple.tuhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import edu.temple.tuhub.models.Course;

public class MainActivity extends AppCompatActivity implements CourseListFragment.OnListFragmentInteractionListener, CourseCalendarFragment.CalendarClickListener{

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_courses:
                    loadFragment(R.id.content, CourseFragment.newInstance(), false, true);
                    return true;
                case R.id.navigation_marketplace:
                    // TODO: Load fragment
                    return true;
                case R.id.navigation_maps:
                    // TODO: Load fragment
                    return true;
                case R.id.navigation_news:
                    // TODO: Load fragment
                    return true;
                case R.id.navigation_more:
                    // TODO: Load fragment
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Retrieve login info and store in SharedPreferences
            Intent receivedIntent = getIntent();
            String username = receivedIntent.getStringExtra(getResources().getString(R.string.username_key));
            String password = receivedIntent.getStringExtra(getResources().getString(R.string.password_key));
            String id = receivedIntent.getStringExtra(getResources().getString(R.string.user_id_key));

            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            preferences.edit().putString(getResources().getString(R.string.username_key), username).commit();
            preferences.edit().putString(getResources().getString(R.string.password_key), password).commit();
            preferences.edit().putString(getResources().getString(R.string.user_id_key), id).commit();

        navigation.setSelectedItemId(R.id.navigation_courses);



    }

    private void loadFragment(int ID, Fragment fragment, boolean backStack, boolean clearBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(ID, fragment);
        if(clearBackStack){
            fm.popBackStack();
            fm.popBackStack();
        }
        if (backStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
        fm.executePendingTransactions();
    }

    @Override
    public void onListFragmentInteraction(Course item) {

    }

    @Override
    public void showCourseDetails(Course course) {
        CourseFragment cf = CourseFragment.newInstance();
        loadFragment(R.id.content, cf, false, true);
        cf.showCourseDetails(course);
    }
}
