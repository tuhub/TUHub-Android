package edu.temple.tuhub;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.temple.tuhub.models.Marketitem;

import static android.content.ContentValues.TAG;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListingDetailsFragment extends Fragment implements ImageScroller.ImageScrollerFragment {

    private ImageScroller imageScroller;

    String listingType = "";
    private Handler handler;

    String seller = "";
    String price;
    String description;
    String title = "";
    String startdate;
    String hours;
    String location;
    String pay;
    String picfolder;
    String dateposted;

    TextView sellerView;
    TextView priceView;
    TextView descriptionView;
    TextView titleView;
    TextView locationView;
    TextView startDateView;
    TextView hoursView;
    TextView payView;
    @BindView(R.id.seller_text) TextView sellerHeader;
    @BindView(R.id.price_view) TextView priceHeader;
    @BindView(R.id.location_text) TextView locationHeader;
    @BindView(R.id.start_text) TextView startDateHeader;
    @BindView(R.id.hours_text) TextView hoursHeader;
    @BindView(R.id.pay_text) TextView payHeader;
    @BindView(R.id.contact_owner_button) Button contactButton;
    @BindView(R.id.date_posted_input) TextView datePostedInput;
    @BindView(R.id.date_posted_text) TextView datePostedHeader;

    Bundle data;

    public static ListingDetailsFragment newInstance(Marketitem item) {
        ListingDetailsFragment fragment = new ListingDetailsFragment();
        Bundle args = item.toBundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ListingDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            data = bundle;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
          View v;
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout
                .fragment_listing_details, container, false);
        isStoragePermissionGranted();
        handler = new Handler();
        listingType = data.getString("listingType");
        picfolder = data.getString("picfolder");
        titleView = (TextView) v.findViewById(R.id.title_view);// to be able to set the text of these fields later on!
        startDateView = (TextView) v.findViewById(R.id.start_input);
        priceView = (TextView) v.findViewById(R.id.price_input);
        descriptionView = (TextView) v.findViewById(R.id.description_input);
        locationView = (TextView) v.findViewById(R.id.location_input);
        hoursView = (TextView) v.findViewById(R.id.hours_input);
        payView = (TextView) v.findViewById(R.id.pay_input);
        sellerView = (TextView) v.findViewById(R.id.seller_input);
        imageScroller = (ImageScroller) v.findViewById(R.id.listing_details_image_scroller);
        ButterKnife.bind(this, v);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getApplication().getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
                String username = preferences.getString(getString(R.string.username_key), "");
                if(username.length()!=0 && seller.length() != 0){
                    displayContactOwnerDialog(username, seller);
                }
            }
        });



        getListingData(data);
        //setListingData(v);
        switch (listingType) {
            case "Product":
                titleView.setText(title);
                datePostedInput.setText(dateposted);
                descriptionView.setText(description);
                priceView.setVisibility(View.VISIBLE);
                priceView.setText(price);
                sellerView.setVisibility(View.VISIBLE);
                sellerView.setText(seller);
                locationView.setVisibility(View.GONE);
                hoursView.setVisibility(View.GONE);
                payView.setVisibility(View.GONE);
                startDateView.setVisibility(View.GONE);
                locationHeader.setVisibility(View.GONE);
                hoursHeader.setVisibility(View.GONE);
                payHeader.setVisibility(View.GONE);
                startDateHeader.setVisibility(View.GONE);
                break;
            case "Job":
                titleView.setText(title);
                datePostedInput.setText(dateposted);
                sellerView.setVisibility(View.VISIBLE);
                sellerView.setText(seller);
                startDateView.setVisibility(View.VISIBLE);
                startDateView.setText(startdate);
                if (pay != null && pay.length() != 0) {
                    payView.setVisibility(View.VISIBLE);
                    payView.setText(pay);
                } else // if there is no pay information hide the area to put the pay information
                {
                    payView.setVisibility(View.GONE);
                    payHeader.setVisibility(View.GONE);
                }
                locationView.setText(location);
                hoursView.setText(hours);
                descriptionView.setText(description);
                priceView.setVisibility(View.GONE);
                priceHeader.setVisibility(View.GONE);
                break;
            case "Personal":
                titleView.setText(title);
                datePostedInput.setText(dateposted);
                startDateView.setVisibility(View.GONE);
                startDateHeader.setVisibility(View.GONE);
                locationView.setText(location);
                descriptionView.setText(description);
                priceView.setVisibility(View.GONE);
                hoursView.setVisibility(View.GONE);
                sellerView.setVisibility(View.GONE);
                payView.setVisibility(View.GONE);
                priceHeader.setVisibility(View.GONE);
                hoursHeader.setVisibility(View.GONE);
                sellerHeader.setVisibility(View.GONE);
                payHeader.setVisibility(View.GONE);
                break;
        }

        //Set up the ImageScroller
        imageScroller.verifyStoragePermissions(getActivity());
        imageScroller.setImageScrollerFragment(ListingDetailsFragment.this);
        imageScroller.setCredentialsProvider();
        imageScroller.submitButton.setVisibility(View.GONE);
        imageScroller.addImageButton.setVisibility(View.GONE);

        /*Retrieve a list of object keys stored in the Listing's folder. This
        cannot be done in the ImageScroller object, so the list is passed to the ImageScroller
        once it has been retrieved. Then, the ImageScroller's getImagesFromS3 method is called.
        */
        final CognitoCachingCredentialsProvider credentialsProvider = imageScroller.getAwsCredentialsProvider();
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                final List<S3ObjectSummary> s3ObjectSummaries = s3.listObjects(ImageScroller.BUCKET_NAME, picfolder).getObjectSummaries();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageScroller.getImagesFromS3(picfolder, s3ObjectSummaries, true);
                    }
                });
                return null;
            }
        }.execute();





        return v;
    }

    private void getListingData(Bundle data){

        seller = data.getString("seller");
        price = data.getString("price");
        description = data.getString("description");
        title = data.getString("title");
        startdate = data.getString("startdate");
        hours = data.getString("hours");
        location = data.getString("location");
        pay = data.getString("pay");
        dateposted = data.getString("dateposted");
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

    public void displayContactOwnerDialog(final String username, final String ownerUsername){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.contact_owner_dialog, null);

        final EditText subjectInput = (EditText)dialogView.findViewById(R.id.contact_owner_subject);
        String defaultSubject = getResources().getString(R.string.default_subject) + title;
        subjectInput.setText(defaultSubject);

        final EditText messageInput = (EditText)dialogView.findViewById(R.id.contact_owner_message);
        String defaultMessage = getResources().getString(R.string.default_message_intro) + " " + title + " on the TUHub Marketplace. " +
                getResources().getString(R.string.default_message_outro) + username;
        messageInput.setText(defaultMessage);

        builder.setView(dialogView);
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subject = subjectInput.getText().toString();
                String message = messageInput.getText().toString();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ownerUsername + "@temple.edu"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(emailIntent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

       AlertDialog alert = builder.show();
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

    }

    @Override
    public void sendSelectImageIntent(Intent intent, int requestCode) {

    }

    @Override
    public Activity obtainActivity() {
        return getActivity();
    }

    @Override
    public void submitListing() {

    }

    @Override
    public void deleteFilesFromS3(ArrayList<String> filesToDelete) {

    }
}

