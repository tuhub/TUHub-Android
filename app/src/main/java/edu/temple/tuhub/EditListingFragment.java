package edu.temple.tuhub;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.androidnetworking.error.ANError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.temple.tuhub.models.marketplace.Job;
import edu.temple.tuhub.models.marketplace.Listing;
import edu.temple.tuhub.models.marketplace.Personal;
import edu.temple.tuhub.models.marketplace.Product;

import static android.content.ContentValues.TAG;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;


public class EditListingFragment extends Fragment implements ImageScroller.ImageScrollerFragment{

    private static final String FIELD_MAP = "field_map";

    private LinkedHashMap<String, String> fieldMap;
    //ArrayList of all user inputs -> Stored in reverse, so to construct a listing, obtain values from end to beginning
    private ArrayList<InputAndKey> inputs;
    private boolean isActive;
    private String datePosted;
    private Listing editedListing;
    public Calendar myCalendar = Calendar.getInstance();
    public int requestCode;
    public Handler handler;

    @BindView(R.id.edit_listing_image_scroller)ImageScroller imageScroller;
    @BindView(R.id.edit_listing_input_container)LinearLayout inputContainer;

    public EditListingFragment() {
        // Required empty public constructor
    }

    public static EditListingFragment newInstance(LinkedHashMap<String, String> fieldMap) {
        EditListingFragment fragment = new EditListingFragment();
        Bundle args = new Bundle();
        args.putSerializable(FIELD_MAP, fieldMap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.fieldMap = (LinkedHashMap<String, String>) getArguments().getSerializable(FIELD_MAP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_edit_listing, container, false);
        ButterKnife.bind(this, v);

        handler = new Handler();

        //Set up the ImageScroller
        imageScroller.verifyStoragePermissions(getActivity());
        imageScroller.setImageScrollerFragment(EditListingFragment.this);
        imageScroller.setCredentialsProvider();
        imageScroller.submitButton.setText(getResources().getString(R.string.update));

        /*Retrieve a list of object keys stored in the Listing's folder. This
        cannot be done in the ImageScroller object, so the list is passed to the ImageScroller
        once it has been retrieved. Then, the ImageScroller's getImagesFromS3 method is called.
        */
        final CognitoCachingCredentialsProvider credentialsProvider = imageScroller.getAwsCredentialsProvider();
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                final List<S3ObjectSummary> s3ObjectSummaries = s3.listObjects(ImageScroller.BUCKET_NAME, fieldMap.get(Listing.PIC_FOLDER_NAME_KEY)).getObjectSummaries();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageScroller.getImagesFromS3(fieldMap.get(Listing.PIC_FOLDER_NAME_KEY), s3ObjectSummaries, false);
                    }
                });
                return null;
            }
        }.execute();

        //These fields do not need pairs of title TextView and EditText input
        isActive = (fieldMap.get(Listing.IS_ACTIVE_KEY).equalsIgnoreCase("true"));
        datePosted = fieldMap.get(Listing.DATE_POSTED);
        fieldMap.remove(Listing.IS_ACTIVE_KEY);
        fieldMap.remove(Listing.DATE_POSTED);

        /*
        Iterate through the keys and values in the HashMap, creating pairs of title TextView and EditText input
        when necessary
         */
        Object[] titlesArray = fieldMap.keySet().toArray();
        Object[] valuesArray = fieldMap.values().toArray();
        inputs = new ArrayList<>();
        for(int i = 0; i < titlesArray.length; i++){
            String title = ((String)titlesArray[i]);
            if(title.contains("Id") || title.contains(Listing.PIC_FOLDER_NAME_KEY) || title.contains(Listing.OWNER)){
                continue;
            }
            addInput(title, (String)valuesArray[i]);
        }

        //Display the date posted
        TextView datePostedTitle = new TextView(getActivity());
        datePostedTitle.setText(Listing.DATE_POSTED);
        TextView datePostedValue = new TextView(getActivity());
        datePostedValue.setText(datePosted);

        //Display a switch to handle activating/deactivating the listing
        final Switch activeSwitch = new Switch(getActivity());
        activeSwitch.setText(getResources().getString(R.string.deactivated)); //starts out false
        activeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isActive){
                    isActive = false;
                    activeSwitch.setText(getResources().getString(R.string.deactivated));
                } else {
                    isActive = true;
                    activeSwitch.setText(getResources().getString(R.string.activated));
                }
            }
        });
        if(isActive) {
            isActive = false;
            activeSwitch.performClick();
        }

        inputContainer.addView(activeSwitch);
        inputContainer.addView(datePostedTitle);
        inputContainer.addView(datePostedValue);

        return v;
    }

    /*
    Dynamically adds a TextView for the title and an EditText for user input.
    It sets the EditText text to the given value.
     */
    public void addInput(String title, String value){
        TextView textView = new TextView(getActivity());
        textView.setText(title);

        EditText editText = new EditText(getActivity());
        editText.setText(value);

        if(title.equals(Job.START_DATE)){
            editText.setFocusable(false);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        }

        inputs.add(new InputAndKey(editText, title));

        inputContainer.addView(textView);
        inputContainer.addView(editText);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void sendSelectImageIntent(Intent intent, int requestCode) {
        this.requestCode = requestCode;
        startActivityForResult(intent, requestCode);
    }

    /*
    Handles result of image choosing intent - i.e. what to do if it is a picture
    from the camera or from the gallery
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            if(isStoragePermissionGranted()) {
                imageScroller.onActivityResult(requestCode, resultCode, data);
            }
        }


    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(getActivity().getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    public Activity obtainActivity() {
        return getActivity();
    }

    /*
    Process for updating a listing in the marketplace.
    Parses the list of user inputs and stores their values in the HashMap.
    Then, a Listing object is created from the HashMap.
    Once the Listing object's fields have been validated, the object's
    update method is called to update the row in the DB.
     */
    @Override
    public void submitListing() {
        //Go through the editable fields and store them back in the listing hashmap
        for(int i = 0; i< inputs.size(); i++){
            InputAndKey inputAndKey = inputs.get(i);
            fieldMap.put(inputAndKey.key, inputAndKey.editText.getText().toString());
            inputAndKey.editText.setError(null);
        }
        //Store the value of isActive and datePosted in the listing hashmap
        fieldMap.put(Listing.IS_ACTIVE_KEY, String.valueOf(isActive));
        fieldMap.put(Listing.DATE_POSTED, datePosted);

        if(fieldMap.get(Product.PRODUCT_ID_KEY) != null){
            editedListing = (new Product()).fromMap(fieldMap);
        } else if(fieldMap.get(Job.JOB_ID_KEY) != null){
            editedListing = (new Job()).fromMap(fieldMap);
        } else if (fieldMap.get(Personal.PERSONAL_ID_KEY) != null){
            editedListing = (new Personal()).fromMap(fieldMap);
        } else {
            Toast.makeText(getActivity(), "Error Submitting Listing -> No Listing Type", Toast.LENGTH_SHORT).show();
        }

        if(editedListing.validateFields(inputs)){
            Toast.makeText(getActivity(), getResources().getString(R.string.submitting), Toast.LENGTH_SHORT).show();
            imageScroller.submitButton.setText(getResources().getString(R.string.submitting));

            editedListing.update(new Listing.ListingUpdateListener() {
                @Override
                public void onResponse(boolean success) {
                    if(success){
                        imageScroller.loadImagesToS3(editedListing.getPicFolderName());
                    } else {
                        imageScroller.submitFailed();
                    }
                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    imageScroller.submitFailed();
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(getActivity(), anError.toString(), Toast.LENGTH_LONG).show();
                    imageScroller.submitFailed();
                }
            });

        }
    }

    //Given a list of file keys, this method creates an AmazonS3Client and deletes
    // the object corresponding to each key in our bucket
    @Override
    public void deleteFilesFromS3(final ArrayList<String> filesToDelete) {

        final CognitoCachingCredentialsProvider credentialsProvider = imageScroller.getAwsCredentialsProvider();
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                for (int i = 0; i<filesToDelete.size(); i++){
                    Log.d("Key to Delete", filesToDelete.get(i));
                    s3.deleteObject(ImageScroller.BUCKET_NAME, filesToDelete.get(i));
                }
                return null;
            }
        }.execute();

    }

    //Wrapper class for an editText and its associated key in the HashMap of values
    public class InputAndKey{
        public EditText editText;
        public String key;

        InputAndKey(EditText editText, String key){
            this.editText = editText;
            this.key = key;
        }
    }

    //DialogListener for Choosing the start date of a job
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateStartDate();
        }

    };

    //Searches through the list of fields for the start date input and sets the  input to display the chosen date
    private void updateStartDate() {

        String myFormat = "MM-dd-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        for(int i = 0; i<inputs.size(); i++){
            InputAndKey inputAndKey = inputs.get(i);
            if(inputAndKey.key.equals(Job.START_DATE)){
                inputAndKey.editText.setText(sdf.format(myCalendar.getTime()));
            }
        }
    }


}
