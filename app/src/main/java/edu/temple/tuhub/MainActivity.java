package edu.temple.tuhub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import java.io.Serializable;

import edu.temple.tuhub.CourseFragment.showCourseDetailsInterface;
import edu.temple.tuhub.MapsFragment.loadBuildingDetailsInterface;
import edu.temple.tuhub.MarketTableFragment.newListingInterface;
import edu.temple.tuhub.models.Building;
import edu.temple.tuhub.models.Course;
import edu.temple.tuhub.models.Entry;
import edu.temple.tuhub.models.FoodTruck;
import edu.temple.tuhub.models.Marketitem;
import edu.temple.tuhub.models.Newsitem;
import edu.temple.tuhub.models.User;
import edu.temple.tuhub.models.marketplace.Listing;
import static edu.temple.tuhub.CourseCalendarFragment.*;
import static edu.temple.tuhub.CourseFragment.*;
import static edu.temple.tuhub.CoursesSearchAllFragment.*;
import static edu.temple.tuhub.FilterMenuFrag.*;
import static edu.temple.tuhub.MapsFragment.*;
import static edu.temple.tuhub.MarketTableFragment.*;
import static edu.temple.tuhub.NewsTableFragment.*;

public class MainActivity extends AppCompatActivity implements newsshow, filterbutton, selectorinterface, showCourseDetailsInterface, CalendarClickListener, courseSearchHandlerInterface, searchAllResultsInterface, newListingInterface, marketshow, loadBuildingDetailsInterface, loadFoodTruckDetailsInterface, reloadMapInterface, mapSearch {
    static Fragment[] fraghold = new Fragment[3];//For TUNews and some TUmarketplace
    FilterMenuFrag tufilter;//For TUNews

    FragmentManager manage;//For TUNews
    FragmentTransaction transact;//For TUNews

    private SharedPreferences preferences;

    CourseFragment cf;
    CourseSearchAllDetail csad;
    CoursesSearchAllFragment csaf;
    MyCourseSearchFragment mcsf;
    CourseDetailsFragment cdf;
    MapsFragment mf;
    PageDeniedFragment pf;
    BuildingDetailFragment bdf;
    FoodTruckDetailFragment ftdf;
    MapSearchFragment msf;
    MoreFragment mof;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String username;
            if (preferences == null) {
                preferences = getApplication().getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
            }
            switch (item.getItemId()) {
                case R.id.navigation_courses:
                    username = preferences.getString(getResources().getString(R.string.username_key), "");
                    if (username.length() != 0) {
                        loadFragment(R.id.contentFragment, CourseFragment.newInstance(), false, true);
                    } else {
                        loadFragment(R.id.contentFragment, PageDeniedFragment.newInstance(), false, true);
                    }
                    return true;
                case R.id.navigation_marketplace:
                    username = preferences.getString(getResources().getString(R.string.username_key), "");
                    if (username.length() != 0) {
                        loadMarketplace();
                    } else {
                        loadFragment(R.id.contentFragment, PageDeniedFragment.newInstance(), true, true);
                    }
                    return true;
                case R.id.navigation_maps:
                    MapsFragment.ignoreSharedPreferences = false;
                    loadFragment(R.id.contentFragment, mf, false, true);
                    return true;
                case R.id.navigation_news:
                    loadFragment(R.id.contentFragment, fraghold[1], false, true);
                    return true;
                case R.id.navigation_more:
                    loadFragment(R.id.contentFragment, mof, false, true);
                    return true;
            }
            return false;
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            case R.id.login:
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        String username = preferences.getString(getString(R.string.username_key), "");
        if (username.length() == 0) {
            inflater.inflate(R.menu.login_menu, menu);
        } else {
            inflater.inflate(R.menu.logout_menu, menu);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().addOnBackStackChangedListener(backStackChangedListener);
        preferences = getApplication().getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        csad = new CourseSearchAllDetail();
        csaf = new CoursesSearchAllFragment();
        mcsf = new MyCourseSearchFragment();
        cdf = new CourseDetailsFragment();
        cf = new CourseFragment();
        mf = new MapsFragment();
        bdf = new BuildingDetailFragment();
        ftdf = new FoodTruckDetailFragment();
        msf = new MapSearchFragment();
        mof = new MoreFragment();
        if (savedInstanceState == null) {
            String username = preferences.getString(getString(R.string.username_key), "");
            //If logged in, go to the first item in menu, otherwise go to navigation
            if (username.length() != 0) {
                int selectedID = navigation.getSelectedItemId();
                View selectedView = navigation.findViewById(selectedID);
                selectedView.performClick();
            } else {
                navigation.setSelectedItemId(R.id.navigation_maps);
            }
        }
        if (fraghold[1] == null)//For TUNews
        {
            fraghold[0] = new NewsFragment();
            fraghold[1] = new NewsTableFragment();
            fraghold[2] = new MarketTableFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getFragmentManager().removeOnBackStackChangedListener(backStackChangedListener);
    }

    private void loadFragment(int ID, Fragment fragment, boolean backStack, boolean clearBackStack) {
        final FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().detach(cf).replace(ID, fragment).attach(fragment);
        if (fraghold[2] != null) {
            if (fraghold[2].equals(fragment)) {
                if (cf != null) {
                    getFragmentManager().beginTransaction().remove(cf).commit();
                }
            }
        }
        if (clearBackStack) {
            fm.popBackStack();
        }
        if (backStack && fm.getBackStackEntryCount()>1) {
            ft.addToBackStack(null);
        }
        ft.commit();
        fm.executePendingTransactions();
    }

    /*
   Checks to see if the user is registered in the marketplace,
   if not, displayAddPhoneNumberDialog() is called
    */
    private void loadMarketplace() {
        final String username = preferences.getString(getString(R.string.username_key), "");
        boolean registeredForMarketplace = preferences.getBoolean(username + getString(R.string.in_marketplace_key), false);
        if (registeredForMarketplace) {
            loadFragment(R.id.contentFragment, fraghold[2], false, true);
        } else {
            loadFragment(R.id.contentFragment, LoadingFragment.newInstance(), false, true);
            User.isInMarketplace(username, new User.MarketplaceRequestListener() {
                @Override
                public void onResponse(boolean isInMarketplace) {
                    if (isInMarketplace) {
                        loadFragment(R.id.contentFragment, fraghold[2], false, true);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(username + getString(R.string.in_marketplace_key), true);
                        editor.apply();
                    } else {
                        displayAddPhoneNumberDialog(username);
                    }
                }

                @Override
                public void onError(ANError error) {
                    Toast.makeText(MainActivity.this, "Error checking if user is registered in marketplace", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*
    Displays dialog for user to add phone number the first time they are registered for the marketplace
    After the user responds, the user's information is stored in the marketplace using storeUserInMarket()
     */
    public void displayAddPhoneNumberDialog(final String username) {
        final String tuid = preferences.getString(getString(R.string.user_id_key), "");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_phone_dialog, null);
        final EditText phoneInput = (EditText) dialogView.findViewById(R.id.phone_numer_input);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.add_phone_number, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                storeUserInMarket(username, tuid, null);
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        final AlertDialog alert = builder.show();
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phoneInput.getText().toString();
                if (number.length() != 10) {
                    phoneInput.setError(getString(R.string.invalid_length));
                } else {
                    try {
                        Double.parseDouble(number);
                        storeUserInMarket(username, tuid, number);
                        alert.dismiss();
                    } catch (NumberFormatException e) {
                        phoneInput.setError(getString(R.string.phone_number_error));
                    }
                }
            }
        });
    }

    /*
    Inserts the user data into the marketplace db. Once the data is stored, the marketplace fragment is loaded
     */
    public void storeUserInMarket(final String username, String tuid, String phoneNumber) {
        User.addToMarketplace(username, tuid, phoneNumber, new User.MarketplaceRequestListener() {
            @Override
            public void onResponse(boolean isInMarketplace) {
                if (isInMarketplace) {
                    loadFragment(R.id.contentFragment, fraghold[2], false, true);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(username + getString(R.string.in_marketplace_key), true);
                    editor.apply();
                } else {
                    Toast.makeText(MainActivity.this, "MainActivity: storeUserInMarket returned false ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError error) {
                Log.d("STORING", error.getErrorBody());
                Toast.makeText(MainActivity.this, "Error: User was not registered in marketplace", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private FragmentManager.OnBackStackChangedListener backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                if (cf != null) {
                    getFragmentManager().beginTransaction().remove(cf).commit();
                }
            }
        }
    };


    @Override
    public void showCourseDetails(Course course) {
        Bundle bundle = new Bundle();
        bundle.putString("name", course.getName());
        bundle.putString("fullname", course.getTitle());
        bundle.putString("date", course.getStartDate() + " to " + course.getEndDate());
        bundle.putSerializable("course", course);
        bundle.putSerializable("meetings", (Serializable) course.getMeetings());
        bundle.putSerializable("faculty", (Serializable) course.getInstructors());
        bundle.putStringArray("roster", course.getRoster());
        cdf.setArguments(bundle);
        loadFragment(R.id.contentFragment, cdf, true, false);
    }

    @Override
    public void searchAllResults(Entry courseDetails) {
        Bundle bundle = new Bundle();
        bundle.putString("course", courseDetails.getCourse());
        bundle.putString("crseId", courseDetails.getCrseId());
        bundle.putString("description", courseDetails.getDescription());
        bundle.putString("creditHr", courseDetails.getCreditHr());
        bundle.putString("college", courseDetails.getCollege());
        bundle.putString("division", courseDetails.getDivision());
        bundle.putString("department", courseDetails.getDepartment());
        bundle.putString("schedule", courseDetails.getSchedule());
        csad.setArguments(bundle);
        loadFragment(R.id.contentFragment, csad, true, false);
    }

    @Override
    public void courseSearchHandler(String query, boolean myCourses) {
        if (myCourses) {
            loadFragment(R.id.contentFragment, mcsf, true, false);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            csaf.setArguments(bundle);
            loadFragment(R.id.contentFragment, csaf, true, false);
        }
    }

    @Override
    public void shownews(Newsitem t) {// part of the shownews interface. Takes in a newsitem. Uses the newscontent from the news item
        //as input for the constructor of the show news fragment
        String newshtml = t.newscontent;
        String newurl = t.newsurl;
        ((NewsFragment) fraghold[0]).news = newshtml;
        ((NewsFragment) fraghold[0]).newsurl = newurl;
        manage = getFragmentManager();
        transact = manage.beginTransaction();
        transact.replace(R.id.contentFragment, fraghold[0]).commit();
        manage.executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().findFragmentById(R.id.contentFragment) instanceof NewsFragment) {
            manage = getFragmentManager();
            transact = manage.beginTransaction();
            transact.replace(R.id.contentFragment, fraghold[1]).commit();
            manage.executePendingTransactions();
        } else if (getFragmentManager().findFragmentById(android.R.id.content) instanceof FilterMenuFrag) {
            manage = getFragmentManager();
            transact = manage.beginTransaction();
            findViewById(R.id.filterbutton).setEnabled(true);
            transact.remove(getFragmentManager().findFragmentById(android.R.id.content)).commit();//assumed that the variable tufilter would keep its refrence id. However that id changes on context switch. Using the verison of the fragment obtained by going through the fragment manger instead.
            manage.executePendingTransactions();
        }
       /* else if(getFragmentManager().findFragmentById(android.R.id.content) instanceof ) //TODO for when we display the marketfrag that showcases the data in the market item
        {
            manage=getFragmentManager();
            transact=manage.beginTransaction();

        }*/
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void filterbuttonpresseed() { //part of the filterbutton interface. Takes in nothing and returns nothing. Adds a new tufilter fragment in an area on top of where fragments normally are put.(Allowing for an overlay type effect)
        if (getFragmentManager().findFragmentById(android.R.id.content) instanceof FilterMenuFrag) {
        } else {
            manage = getFragmentManager();
            transact = manage.beginTransaction();
            tufilter = new FilterMenuFrag();//new filter because the checkboxes keep procting with state changes if it is the same menufrrag
            transact.add(android.R.id.content, tufilter).commit();
            manage.executePendingTransactions();
        }
    }

    @Override
    public void newslink(String x) {//part of the newsshow interface. Takes in a string that should be the link to be loaded for the NewsTableFragment. Removes the filter fragment and uses a public method of the NewsTableFragment to load news based on the string (link) provided.
        ((NewsTableFragment) fraghold[1]).finallink = x;
        manage = getFragmentManager();
        transact = manage.beginTransaction();
        findViewById(R.id.filterbutton).setEnabled(true);
        transact.remove(getFragmentManager().findFragmentById(android.R.id.content)).commit();
        manage.executePendingTransactions();
        ((NewsTableFragment) fraghold[1]).loadnews();
    }

    @Override
    public void newListing(int i) {
        if (i == 0) {
            loadFragment(R.id.contentFragment, new InsertProductFragment(), true, false);
        } else if (i == 1) {
            loadFragment(R.id.contentFragment, new MarketJobListingFragment(), true, false);
        } else if (i == 2) {
            loadFragment(R.id.contentFragment, new MarketPersonalListingFragment(), true, false);
        }
    }

    @Override
    public void editListing(Listing listing) {
        loadFragment(R.id.contentFragment, EditListingFragment.newInstance(listing.toHashMap()), true, false);
    }

    @Override
    public void showListingDetails(Marketitem item) {
        loadFragment(R.id.contentFragment, ListingDetailsFragment.newInstance(item), true, false);
    }

    @Override
    public void showmarket(Marketitem t) {/// gives the clicked marketitem
        //should probally assign the market item to a variable inside the shower class
        //should replace the marketfragment here with the fragment that shows the market information
        // on back pressed it should go back to the market fragment! (implement line 228-233)
    }

    public void loadBuildingDetails(String name, String imageUrl, String latitude, String longitude) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("latitude", latitude);
        bundle.putString("longitude", longitude);
        bdf.setArguments(bundle);
        loadFragment(R.id.contentFragment, bdf, true, false);
    }

    public void loadFoodTruckDetails(String name, String rating, String isClosed, String latitude, String longitude, String imageUrl, String phone) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("rating", rating);
        bundle.putString("isClosed", isClosed);
        bundle.putString("latitude", latitude);
        bundle.putString("longitude", longitude);
        bundle.putString("imageURL", imageUrl);
        bundle.putString("phone", phone);
        ftdf.setArguments(bundle);
        loadFragment(R.id.contentFragment, ftdf, true, false);
    }

    @Override
    public void reloadMap() {
        final FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().detach(mf).replace(R.id.contentFragment, mf).attach(mf);
        ft.commit();
        fm.executePendingTransactions();
    }

    public void mapSearchResults(Building[] buildings, FoodTruck[] foodTrucks) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Buildings", buildings);
        bundle.putSerializable("FoodTrucks", foodTrucks);
        msf.setArguments(bundle);
        loadFragment(R.id.contentFragment, msf, true, false);
    }
}

