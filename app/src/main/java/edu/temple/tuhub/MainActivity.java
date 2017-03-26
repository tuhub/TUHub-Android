package edu.temple.tuhub;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
public class MainActivity extends AppCompatActivity {

    public String user, tuId, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_bar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            user = "";
            tuId = "";
            password = "";
        } else {
            user = extras.getString("user");
            tuId = extras.getString("tuId");
            password = extras.getString("password");
        }

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.courses:
                        //Load fragment
                        break;
                    case R.id.marketplace:
                        // Load appropriate fragment
                        break;
                    case R.id.maps:
                        // Load appropriate fragment
                        break;
                    case R.id.news:
                        // Load appropriate fragment
                        break;
                    case R.id.account:
                        // Load appropriate fragment
                        break;
                }
                return true;
            }
        });

    }
    private void loadFragment(int ID, Fragment fragment, boolean backStack, boolean clearBackStack) {
        FragmentManager fm = getFragmentManager();
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

}
