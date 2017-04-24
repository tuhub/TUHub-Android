package edu.temple.tuhub;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.temple.tuhub.models.marketplace.Product;

import static android.content.ContentValues.TAG;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InsertProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InsertProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertProductFragment extends Fragment implements ImageScroller.ImageScrollerFragment {

    @BindView(R.id.insert_product_description_input)
    EditText descriptionInput;

    @BindView(R.id.insert_product_title_input)
    EditText titleInput;

    @BindView(R.id.insert_product_price_input)
    EditText priceInput;

    @BindView(R.id.insert_product_image_scroller)
    ImageScroller imageScroller;

    private OnFragmentInteractionListener mListener;
    private int requestCode;
    private String username;

    public InsertProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InsertProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InsertProductFragment newInstance(String param1, String param2) {
        InsertProductFragment fragment = new InsertProductFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public static InsertProductFragment newInstance() {
        InsertProductFragment fragment = new InsertProductFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_insert_product, container, false);
        ButterKnife.bind(this, v);
        imageScroller.verifyStoragePermissions(getActivity());
        imageScroller.setImageScrollerFragment(InsertProductFragment.this);
        imageScroller.setCredentialsProvider();
        if(username == null) {
            SharedPreferences pref = getActivity().getApplication().getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
            username = pref.getString(getResources().getString(R.string.username_key), "");
        }

        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /*
    Validates the user input and creates a product
    object out of it. Uses the Marketplace API to insert the new product. Once inserted,
     the response returns the folder name for the product's images.
     loadImagesToS3() is called with the given folder name.
     */
    @Override
    public void submitListing(){
        if (validateUserInput()) {
            Product product = new Product();
            product.setTitle(titleInput.getText().toString());
            product.setDescription(descriptionInput.getText().toString());
            product.setPrice(priceInput.getText().toString());
            product.setOwnerId(username);
            product.setIsActive(Product.TRUE);
            Toast.makeText(getActivity(), getString(R.string.submitting), Toast.LENGTH_SHORT).show();
            product.insert(new Product.ProductRequestListener() {
                @Override
                public void onResponse(Product product) {
                    Log.d("final product", product.toString());
                    if (product.getError().length() != 0) {
                        Toast.makeText(getActivity(), product.getError(), Toast.LENGTH_LONG).show();

                    } else {

                        imageScroller.loadImagesToS3(product.getPicFileName());
                    }
                }

                @Override
                public void onError(ANError error) {
                    error.printStackTrace();
                    imageScroller.submitFailed();
                    Toast.makeText(getActivity(), getString(R.string.error_publishing), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            imageScroller.submitFailed();
        }
    }

    @Override
    public void deleteFilesFromS3(ArrayList<String> filesToDelete) {

    }

    /*
    Checks to see that user entered a title, the description is less than 2000 characters,
    and the user entered a valid dollar amount for price.
     */
    private boolean validateUserInput(){
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        String price = priceInput.getText().toString();

        boolean valid = true;
        if(title == null || title.length() == 0){
            titleInput.setError(getActivity().getString(R.string.error_field_required));
            valid = false;
        }
        if(description.length() > 2000){
            descriptionInput.setError(getActivity().getString(R.string.error_field_too_long) + String.valueOf(description.length()));
            valid = false;
        }
        String regex = "^[0-9]+(\\.[0-9]{1,2})?$";
        if(price.length() == 0){
            priceInput.setError(getActivity().getString(R.string.error_field_required));
            valid = false;
        } else if(!price.matches(regex)){
            priceInput.setError(getActivity().getString(R.string.error_invalid_dollar_amount));
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
