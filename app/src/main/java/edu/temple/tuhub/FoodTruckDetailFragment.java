package edu.temple.tuhub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import edu.temple.tuhub.models.Address;

public class FoodTruckDetailFragment extends Fragment {
    View v;
    Bitmap bitmap;
    ImageView i;
    String ImageURL = "";
    String name = "";
    String latitude = "";
    String longitude = "";
    String rating="";
    String isClosed = "";
    String phone = "";
    String reverseGeoCodeURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key="+R.string.google_android_map_api_key;
    Address addressObject;
    TextView addressTV;

    public FoodTruckDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            name = bundle.getString("name");
            rating = bundle.getString("rating");
            isClosed= bundle.getString("isClosed");
            latitude = bundle.getString("latitude");
            longitude = bundle.getString("longitude");
            ImageURL = bundle.getString("imageURL");
            phone = bundle.getString("phone");

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_food_truck_detail, container, false);
        TextView buildingName = (TextView) v.findViewById(R.id.foodTruckDetailName);
        addressTV = (TextView) v.findViewById(R.id.AddressText);
        buildingName.setText(name);
        TextView ratingView = (TextView) v.findViewById(R.id.ratingText);
        ratingView.setText(rating);
        TextView phoneView = (TextView) v.findViewById(R.id.phoneText);
        phoneView.setText(phone);
        if(isClosed.equals("false")) isClosed = "Open";
        else isClosed = "Closed";
        TextView closedView = (TextView) v.findViewById(R.id.closedText);
        closedView.setText(isClosed);
        i = (ImageView) v.findViewById(R.id.foodTruckImageView);
        fetchImage();
        fetchAddress();

        Button directions = (Button) v.findViewById(R.id.foodTruckDirectionButton);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            }
        });

        return v;
    }

    private void fetchAddress(){
        Thread t = new Thread(){
            @Override
            public void run(){

                URL addressURL;

                try {

                    addressURL = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key="+getString(R.string.google_android_map_api_key));

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    addressURL.openStream()));

                    String response = "", tmpResponse;

                    tmpResponse = reader.readLine();
                    while (tmpResponse != null) {
                        response = response + tmpResponse;
                        tmpResponse = reader.readLine();
                    }

                    JSONObject addressObject = new JSONObject(response);
                    Message msg = Message.obtain();
                    msg.obj = addressObject;
                    addressHandler.sendMessage(msg);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    Handler addressHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            JSONObject responseObject = (JSONObject) msg.obj;

            try {
                addressObject = new Address(responseObject.getJSONArray("results"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            updateViews();

            return false;
        }
    });

    private void updateViews() {
        addressTV.setText(addressObject.getLocation());
    }


    private void fetchImage() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(ImageURL).getContent());
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            i.setImageBitmap(bitmap);
                            i.invalidate();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }


}
