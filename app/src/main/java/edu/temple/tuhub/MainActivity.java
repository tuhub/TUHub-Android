package edu.temple.tuhub;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import edu.temple.tuhub.models.Course;

public class MainActivity extends AppCompatActivity implements CourseListFragment.OnListFragmentInteractionListener {

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
}
