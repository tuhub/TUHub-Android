package edu.temple.tuhub;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import edu.temple.tuhub.imagepicker.ImagePicker;

import java.sql.Date;

import edu.temple.tuhub.models.User;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarketJobListingFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1894334154;
    private String title = "";
    private String description = "";
    private int pay = 0;
    private boolean isActive = true;
    private String ownerId = User.CURRENT.getTuID();
    private Date startDate = Date.valueOf("00-00-9999");
    private int hoursPerWeek = 0;
    private String location = "";
    private String insertAPI = "http://tuhubapi-env.us-east-1.elasticbeanstalk.com/insert_job.jsp?title=" + title + "&description=" + description + "&pay=" + pay + "&isActive=" + isActive + "&ownerId=" + ownerId + "&startDate=" + startDate + "&hoursPerWeek=" + hoursPerWeek + "&location=" + location;
    private int currentPicID = 1;
    Button imgBtn;
    Button cancelBtn;
    Button okayBtn;
    LinearLayout imgList;
    View v;

    public MarketJobListingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_market_job_listing, container, false);
        ImagePicker.setMinQuality(128, 128);
        //imgList = (LinearLayout) v.findViewById(R.id.imgLinearLayout);
        //imgBtn = (Button) v.findViewById(R.id.imgBtn);
        /*imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }
                ImagePicker.pickImage(MarketJobListingFragment.this, "Select Image");
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), requestCode, resultCode, data);
        if (bitmap != null) {
            System.out.println("image set");
            ImageView imageView = new ImageView(getActivity().getApplicationContext());
            //imageView.setId(((id) currentPicID));
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imgList.addView(imageView);

            //imgList.;
        }
        /*InputStream is = ImagePicker.getInputStreamFromResult(getActivity(), requestCode, resultCode, data);
        if (is != null) {
            textView.setText("Got input stream!");
            try {
                is.close();
            } catch (IOException ex) {
                // ignore
            }
        } else {
            textView.setText("Failed to get input stream!");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/
        return v;
    }
}
