package edu.temple.tuhub;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import java.sql.Date;

import edu.temple.tuhub.models.User;
import edu.temple.tuhub.models.marketplace.Personal;

import static android.content.ContentValues.TAG;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarketPersonalListingFragment extends Fragment implements ImageScroller.ImageScrollerFragment {

 private ImageScroller imageScroller;
    private String username;
    private int requestCode;
    AutoCompleteTextView titleInput;
    AutoCompleteTextView descriptionInput;
    AutoCompleteTextView locationInput;
    Button imgBtn;
    Button cancelBtn;
    Button okayBtn;
    LinearLayout imgList;
    View v;


    public MarketPersonalListingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_market_personal_listing, container, false);

        imageScroller = (ImageScroller) v.findViewById(R.id.insert_personal_image_scroller);
        imageScroller.verifyStoragePermissions(getActivity());
        imageScroller.setImageScrollerFragment(MarketPersonalListingFragment.this);
        imageScroller.setCredentialsProvider();
        if(username == null) {
            SharedPreferences pref = getActivity().getApplication().getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
            username = pref.getString(getResources().getString(R.string.username_key), "");
        }
        titleInput = (AutoCompleteTextView) v.findViewById(R.id.editTitle);
        descriptionInput = (AutoCompleteTextView) v.findViewById(R.id.editDescription);
        locationInput = (AutoCompleteTextView) v.findViewById(R.id.editLocation);
        return v;
    }

    @Override
    public void sendSelectImageIntent(Intent intent, int requestCode) {
        this.requestCode = requestCode;
        startActivityForResult(intent, requestCode);
    }

    @Override
    public Activity obtainActivity(){
        return getActivity();
    }

    @Override
    public void submitListing() {
        if (validateUserInput()) {
            Personal personal = new Personal();
            personal.setTitle(titleInput.getText().toString());
            personal.setDescription(descriptionInput.getText().toString());
            personal.setLocation(locationInput.getText().toString());
            personal.setOwnerId(username);
            personal.setIsActive(Personal.TRUE);
            Toast.makeText(getActivity(), getString(R.string.submitting), Toast.LENGTH_SHORT).show();

            personal.insert(new Personal.PersonalRequestListener() {
                @Override
                public void onResponse(Personal personal) {
                    Log.d("final personal", personal.toString());
                    if (personal.getError().length() != 0) {
                        titleInput.setText(personal.getError());

                    } else {

                        imageScroller.loadImagesToS3(personal.getPicFileName());
                    }
                }

                @Override
                public void onError(ANError error) {
                    titleInput.setText(error.toString());
                    error.printStackTrace();

                    imageScroller.submitFailed();
                    Toast.makeText(getActivity(), getString(R.string.error_publishing), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            imageScroller.submitFailed();
        }
    }

    /*
    Validates the user input and creates a personal
    object out of it. Uses the Marketplace API to insert the new personal. Once inserted,
     the response returns the folder name for the personal's images.
     loadImagesToS3() is called with the given folder name.
     */
    private boolean validateUserInput(){
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        String location = locationInput.getText().toString();

        boolean valid = true;
        if(title == null || title.length() == 0){
            titleInput.setError(getActivity().getString(R.string.error_field_required));
            valid = false;
        }
        if(description.length() > 2000){
            descriptionInput.setError(getActivity().getString(R.string.error_field_too_long) + String.valueOf(description.length()));
            valid = false;
        }
       if(location.length()> 2000){
           descriptionInput.setError(getActivity().getString(R.string.error_field_too_long) + String.valueOf(description.length()));
           valid = false;
        }
        return valid;
    }



    /*
    Handles result of image choosing intent - i.e. what to do if it is a picture
    from the camera or from the gallery
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                imageScroller.onActivityResult(requestCode, resultCode, data);
    }
}
