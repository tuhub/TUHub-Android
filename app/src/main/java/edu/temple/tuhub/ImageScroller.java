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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.temple.tuhub.models.marketplace.UserImagePreview;

import static android.R.attr.bitmap;

/**
 * Created by Ben on 4/8/2017.
 */

public class ImageScroller extends LinearLayout {

    @BindView(R.id.image_container)
    LinearLayout imageContainer;

    @BindView(R.id.add_image_button)
    Button addImageButton;

    @BindView(R.id.image_scroller_progress)
    ProgressBar progress;

    @BindView(R.id.image_scroller_submit)
    Button submitButton;

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String BUCKET_NAME = "tumobilemarketplace";

    private ImageScrollerFragment fragment;
    private CognitoCachingCredentialsProvider credentialsProvider;

    public ImageScroller(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView =  inflater.inflate(R.layout.image_scroller, this);

        ButterKnife.bind(this, rootView);

        progress.setVisibility(INVISIBLE);

        addImageButton.setOnClickListener(new AddImageOnClickListener());

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.submitListing();
               // submitButton.setEnabled(false);
                //addImageButton.setVisibility(INVISIBLE);
            }
        });
    }

    /** MUST BE CALLED IN ImageScrollerFragment onCreateView() AFTER BINDING VIEWS **/
    public void setCredentialsProvider(){
        credentialsProvider = getAwsCredentialsProvider();
    }

    /**
     * MUST BE CALLED IN ImageScrollerFragment onCreateView()
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
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

    public void setImageScrollerFragment(ImageScrollerFragment fragment){
        this.fragment = fragment;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //Detects request codes
        if(requestCode==SELECT_FILE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            addImagePreview(selectedImage, null, false);

        } else if(requestCode==REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data!=null){
            if(data.getExtras().get("data") instanceof Bitmap) {
                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                addImagePreview(null, selectedImage, true);
            }
            else{
                Uri selectedImage = data.getData();
                addImagePreview(selectedImage, null, true);
            }
        }
    }

    /** Called by attached fragment to notify that the database insert failed **/
    //TODO this isn't working for some reason
    public void submitFailed(){
        submitButton.setEnabled(true);
        addImageButton.setVisibility(View.VISIBLE);
    }

    /*
    Gets the bitmap from the given URI. Creates an imagePreview obejct to display the bitmap.
    Rotates the image 90 degrees if the image came from the camera.
    Adds a DisplayFullImageOnClickListener to the imagePreview object.
    Adds the imagePreview object to the layout
     */
    public void addImagePreview(Uri imageUri, Bitmap bmp, final boolean fromCamera){

        Bitmap bitmap;
        if(!fromCamera || imageUri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(fragment.obtainActivity().getContentResolver(), imageUri);
                UserImagePreview imagePreview = new UserImagePreview(fragment.obtainActivity());
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
        else{
            UserImagePreview imagePreview = new UserImagePreview(fragment.obtainActivity());
            imagePreview.setImageBitmap(bmp);
            imagePreview.rotateImage(90);
            imagePreview.setTag(bmp);
            imagePreview.getUserImage().setOnClickListener(new DisplayFullImageOnClickListener(bmp, fromCamera));
            imageContainer.addView(imagePreview);
        }
    }

    /*
   Creates a dialog asking the user if they want to take a new photo or upload an existing one.
   Creates the proper intent based on the user's choice.
    */
    private class AddImageOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(imageContainer.getChildCount() < 10) { //user can only upload 10 images

                final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragment.obtainActivity());
                builder.setTitle("Add an Image!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            fragment.sendSelectImageIntent(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("Choose from Library")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            fragment.sendSelectImageIntent(intent, SELECT_FILE);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                //TODO get neutral button to show up
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragment.obtainActivity());
                builder.setMessage(fragment.obtainActivity().getResources().getString(R.string.error_too_many_pics));
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
            final Dialog nagDialog = new Dialog(fragment.obtainActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            nagDialog.setContentView(R.layout.preview_image);
            ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
            ivPreview.setImageBitmap(bitmap);

            if(fromCamera){
                ivPreview.setRotation(90);
            }

            nagDialog.show();
        }
    }

    //Toggle ProgressBar visibility
    public void setProgressBarVisible(boolean isVisible){
        if(isVisible){
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.INVISIBLE);
        }
    }

    public void finalizeListing(){
        progress.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
        submitButton.setText(fragment.obtainActivity().getResources().getString(R.string.submitted));
        submitButton.setClickable(false);
        Toast.makeText(fragment.obtainActivity(),
                fragment.obtainActivity().getResources().getString(R.string.listing_published),
                Toast.LENGTH_SHORT)
                .show();

        for(int i = 0; i<imageContainer.getChildCount(); i++){
            ((UserImagePreview)imageContainer.getChildAt(i)).removeDeleteButton();
        }
    }

    /** S3 Utils **/


   /** This method takes an image URI and returns its true filepath as a String **/

    private String uriToFilePath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                fragment.obtainActivity(),
                uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }


    /** This method creates the credentials provider required to upload files to S3 **/

    public CognitoCachingCredentialsProvider getAwsCredentialsProvider() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                fragment.obtainActivity().getApplicationContext(),
                "us-east-1:bbb7121f-0ae4-4089-9165-55cd2ea4663d", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        return credentialsProvider;
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
            File imageFile = null;

            if(img.getTag() instanceof Uri) {
                Uri uri = ((Uri) img.getTag());
                imageFile = new File(uriToFilePath(uri));
            } else if(img.getTag() instanceof Bitmap){
                try {
                    Bitmap bitmap = (Bitmap)img.getTag();
                    String fileName = "imageScrollerTempFile" + String.valueOf(i);
                    imageFile = File.createTempFile(fileName, ".png", fragment.obtainActivity().getCacheDir());
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();

                } catch (IOException e) {
                    // Error while creating file
                    Log.d("ERROR CREATING FILE", e.toString());
                }


            }

            progress.setVisibility(View.VISIBLE);

            if(imageFile != null) {
                AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                TransferUtility transferUtility = new TransferUtility(s3, fragment.obtainActivity().getApplicationContext());
                TransferObserver observer = transferUtility.upload(BUCKET_NAME, picFileName + "/" + String.valueOf(i), imageFile);

                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        //do something
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        int percentage = (int) (bytesCurrent / bytesTotal * 100);

                        if (percentage == 100) {
                            finalizeListing();
                        } else {
                            submitButton.setText(fragment.obtainActivity().getResources().getString(R.string.submitting)
                                    + " " + String.valueOf(percentage) + "%");
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {

                        submitButton.setEnabled(true);
                        addImageButton.setVisibility(VISIBLE);
                        Toast.makeText(fragment.obtainActivity(), "Image Upload Error: " + ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(fragment.obtainActivity(),
                        fragment.obtainActivity().getResources().getString(R.string.image_upload_error),
                        Toast.LENGTH_SHORT).show();
            }

        }
        if(imageContainer.getChildCount() == 0){
            finalizeListing();
        }
    }


    /** Interface to communicate with parent fragment **/
    public interface ImageScrollerFragment {

        //Sends the intent with the request code, on response it calls ImageScroller.OnActivityResult
       void sendSelectImageIntent(Intent intent, int requestCode);

        //Just returns getActivity() in the fragment
        Activity obtainActivity();

        //Fragment submits its listing data, then calls loadImagesToS3(folderName) on the ImageScroller object
        void submitListing();

    }
}
