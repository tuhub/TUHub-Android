package edu.temple.tuhub;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListingDetailsFragment extends Fragment {

    String listingType = "";

    String seller;
    String price;
    String description;
    String title;
    String startdate;
    String hours;
    String location;
    String pay;

    TextView sellerView;
    TextView priceView;
    TextView descritpionView;
    TextView titleView;
    TextView locationView;
    TextView startDateView;
    TextView hoursView;
    TextView payView;

    Bundle data;

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
        listingType = data.getString("listingType");

       /* if(listingType.equals("product"))
        {
            // Inflate the layout for this fragment
            v = inflater.inflate(R.layout.fragment_product_listing_details, container, false);
            sellerView = (TextView) v.findViewById(R.id.inputSeller);
            priceView = (TextView) v.findViewById(R.id.inputprice);
            descritpionView = (TextView) v.findViewById(R.id.inputDescription);
            titleView = (TextView) v.findViewById(R.id.inputtitle);
        }
            else if(listingType.equals("job"))
            {
                // Inflate the layout for this fragment
                v = inflater.inflate(R.layout.fragment_job_listing_details, container, false);
                hoursView = (TextView) v.findViewById(R.id.inputhours);
                payView = (TextView) v.findViewById(R.id.inputpay);
                descritpionView = (TextView) v.findViewById(R.id.inputDescription);
                titleView = (TextView) v.findViewById(R.id.inputtitle);
                startDateView = (TextView) v.findViewById(R.id.inputStart);
                locationView = (TextView) v.findViewById(R.id.inputLocation);
            }
            else if(listingType.equals("personal"))
            {
                // Inflate the layout for this fragment
                v = inflater.inflate(R.layout.fragment_personal_listing_details, container, false);
                locationView = (TextView) v.findViewById(R.id.inputLocation);
                descritpionView = (TextView) v.findViewById(R.id.inputDescription);
                titleView = (TextView) v.findViewById(R.id.inputtitle);
            }
        else
        {
            //invalid listing type, try to treat it as product
            v = inflater.inflate(R.layout.fragment_product_listing_details, container, false);
            sellerView = (TextView) v.findViewById(R.id.inputhours);
            priceView = (TextView) v.findViewById(R.id.inputprice);
            descritpionView = (TextView) v.findViewById(R.id.inputDescription);
            titleView = (TextView) v.findViewById(R.id.inputtitle);
        }

            getListingData(data);
            setListingData(v);
*/
       v= inflater.inflate(R.layout.fragment_listing_details, container, false);
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
    }

  /*  private void setListingData(View v) {

        if(listingType.equals("product"))
        {
            TextView titleView = (TextView) v.findViewById(R.id.inputtitle);
            titleView.setText(title);

            TextView priceView = (TextView) v.findViewById(R.id.inputprice);
            priceView.setText(price);

            TextView sellerView = (TextView) v.findViewById(R.id.inputhours);
            sellerView.setText(seller);

            TextView descriptionView = (TextView) v.findViewById(R.id.inputDescription);
            descriptionView.setText(description);
        }
            else if(listingType.equals("job"))
            {
                TextView titleView = (TextView) v.findViewById(R.id.inputtitle);
                titleView.setText(title);

                TextView payView = (TextView) v.findViewById(R.id.inputpay);
                payView.setText(pay);

                TextView descriptionView = (TextView) v.findViewById(R.id.inputDescription);
                descriptionView.setText(description);

                TextView startdateView = (TextView) v.findViewById(R.id.inputStart);
                startdateView.setText(startdate);

                TextView hoursView = (TextView) v.findViewById(R.id.inputhours);
                hoursView.setText(hours);

                TextView locationView = (TextView) v.findViewById(R.id.inputLocation);
                locationView.setText(location);
            }
            else if(listingType.equals("personal"))
            {
                TextView titleView = (TextView) v.findViewById(R.id.inputtitle);
                titleView.setText(title);

                TextView locationView = (TextView) v.findViewById(R.id.inputLocation);
                locationView.setText(location);

                TextView descriptionView = (TextView) v.findViewById(R.id.inputDescription);
                descriptionView.setText(description);
            }
        else
        {
            //invalid type treat as product for now
            TextView titleView = (TextView) v.findViewById(R.id.inputtitle);
            titleView.setText(title);

            TextView priceView = (TextView) v.findViewById(R.id.inputprice);
            priceView.setText(price);

            TextView sellerView = (TextView) v.findViewById(R.id.inputhours);
            sellerView.setText(seller);

            TextView descriptionView = (TextView) v.findViewById(R.id.inputDescription);
            descriptionView.setText(description);
        }

        } */
    }

