package edu.temple.tuhub;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.androidnetworking.error.ANError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.temple.tuhub.models.marketplace.Product;
import edu.temple.tuhub.models.marketplace.UserImagePreview;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InsertProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InsertProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertProductFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.insert_product_submit)
    Button submitButton;

    @BindView(R.id.insert_product_add_image_button)
    Button addImageButton;

    @BindView(R.id.insert_product_description_input)
    EditText descriptionInput;

    @BindView(R.id.insert_product_title_input)
    EditText titleInput;

    @BindView(R.id.insert_product_price_input)
    EditText priceInput;

    @BindView(R.id.insert_product_progress)
    ProgressBar progressBar;

    @BindView(R.id.insert_product_image_container)
    LinearLayout imageContainer;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CognitoCachingCredentialsProvider credentialsProvider;


    private OnFragmentInteractionListener mListener;

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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename and change types and number of parameters
    public static InsertProductFragment newInstance() {
        InsertProductFragment fragment = new InsertProductFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_insert_product, container, false);
        ButterKnife.bind(this, v);
        verifyStoragePermissions(getActivity());
        submitButton.setOnClickListener(new submitOnClickListener());
        addImageButton.setOnClickListener(new addImageOnClickListener());
        progressBar.setVisibility(View.INVISIBLE);
        credentialsProvider = getAwsCredentialsProvider();
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

    /*
    OnClickListener for submit button. Validates the user input and creates a product
    object out of it. Uses the Marketplace API to insert the new product. Once inserted,
     the response returns the folder name for the product's images.
     loadImagesToS3() is called with the given folder name.
     */
    private class submitOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (validateUserInput()) {
                Product product = new Product();
                product.setTitle(titleInput.getText().toString());
                product.setDescription(descriptionInput.getText().toString());
                product.setPrice(priceInput.getText().toString());
                //TODO GET USER ID FROM SHARED PREFS
                product.setOwnerId("tue94788");
                product.setIsActive(Product.TRUE);

                progressBar.setVisibility(View.VISIBLE);

                product.insert(new Product.ProductRequestListener() {
                    @Override
                    public void onResponse(Product product) {
                        Log.d("final product", product.toString());
                        if (product.getError().length() != 0) {
                            titleInput.setText(product.getError());

                        } else {

                            loadImagesToS3(product.getPicFileName());
                        }

                        //TODO REMOVE?
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(ANError error) {
                        titleInput.setText(error.toString());
                        error.printStackTrace();

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
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
    Creates a dialog asking the user if they want to take a new photo or upload an existing one.
    Creates the proper intent based on the user's choice.
     */
    private class addImageOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(imageContainer.getChildCount() < 10) { //user can only upload 10 images

                final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Add an Image!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("Choose from Library")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, SELECT_FILE);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                //TODO get neutral button to show up
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setMessage(getActivity().getResources().getString(R.string.error_too_many_pics));
                builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        }

    }

    /*
    Handles result of image choosing intent - i.e. what to do if it is a picture
    from the camera or from the gallery
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==SELECT_FILE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            addImagePreview(selectedImage, false);

        } else if(requestCode==REQUEST_CAMERA && resultCode == Activity.RESULT_OK){
            Uri selectedImage = data.getData();
            addImagePreview(selectedImage, true);
        }
    }

    /*
    Gets the bitmap from the given URI. Creates an imagePreview obejct to display the bitmap.
    Rotates the image 90 degrees if the image came from the camera.
    Adds a DisplayFullImageOnClickListener to the imagePreview object.
    Adds the imagePreview object to the layout
     */
    public void addImagePreview(Uri imageUri, final boolean fromCamera){

        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            UserImagePreview imagePreview = new UserImagePreview(getActivity());
            imagePreview.setImageBitmap(bitmap);
            imagePreview.setTag(imageUri);
            if(fromCamera){
                imagePreview.rotateImage(90);
            }

            imagePreview.getUserImage().setOnClickListener(new DisplayFullImageOnClickListener(bitmap, fromCamera));

            imageContainer.addView(imagePreview);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    Given the folder name for S3, this method
    takes all imagePreview objects in the layout,
    creates files out of their URI and filepath,
    and uploads the files to the proper folder in the S3 bucket
     */
    public void loadImagesToS3(String picFileName){
        for(int i = 0; i < imageContainer.getChildCount(); i++){
            UserImagePreview img = (UserImagePreview)imageContainer.getChildAt(i);
            Uri uri = ((Uri)img.getTag());

            progressBar.setVisibility(View.VISIBLE);
            File imageFile = new File(uriToFilePath(uri));
            AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
            TransferUtility transferUtility = new TransferUtility(s3, getActivity().getApplicationContext());
            TransferObserver observer = transferUtility.upload("tumobilemarketplace", picFileName + "/" + String.valueOf(i), imageFile);



        }
    }

    /*
    This method creates the credentials provider required to upload files to S3
     */
    public CognitoCachingCredentialsProvider getAwsCredentialsProvider() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getActivity().getApplicationContext(),
                "us-east-1:bbb7121f-0ae4-4089-9165-55cd2ea4663d", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        return credentialsProvider;
    }

    /*
    This method takes an image URI and returns its true filepath as a String
     */
    private String uriToFilePath(Uri uri) {
            String[] proj = { MediaStore.Images.Media.DATA };

            //This method was deprecated in API level 11
            //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

            CursorLoader cursorLoader = new CursorLoader(
                    getActivity(),
                    uri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /*
    OnClickListener for ImagePreview objects. When clicked, the full image is displayed in a dialog.
     */
    private class DisplayFullImageOnClickListener implements View.OnClickListener{

        private boolean fromCamera;
        private Bitmap bitmap;

        public DisplayFullImageOnClickListener(Bitmap bitmap, boolean fromCamera){
            this.bitmap = bitmap;
            this.fromCamera = fromCamera;
        }

        @Override
        public void onClick(View v) {
            final Dialog nagDialog = new Dialog(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            // nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //nagDialog.setCancelable(false);
            nagDialog.setContentView(R.layout.preview_image);
            ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
            ivPreview.setImageBitmap(bitmap);

            if(fromCamera){
                ivPreview.setRotation(90);
            }

            nagDialog.show();
        }
    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
