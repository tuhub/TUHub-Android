package edu.temple.tuhub.models.marketplace;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import edu.temple.tuhub.EditListingFragment;

/**
 * Created by Ben on 4/19/2017.
 */

public class Listing {

    public static final String TITLE = "Title";
    public static final String DESCRIPTION = "Description";
    public static final String OWNER = "Owner";
    public static final String DATE_POSTED = "Date Posted";
    public static final String IS_ACTIVE_KEY = "isActive";
    public static final String ERROR = "error";
    public static final String PIC_FOLDER_NAME_KEY = "picFolder";
    public static final String DOLLAR_REGEX = "^^\\d+(\\.\\d{2})?$";

    public Listing(){

    }


    public LinkedHashMap<String, String> toHashMap(){
     return new LinkedHashMap<>();
    }

    public Listing fromMap(LinkedHashMap<String, String> fieldMap){
     return new Listing();
    }

    public void update(ListingUpdateListener listener){

    }

    public boolean validateFields(ArrayList<EditListingFragment.InputAndKey> inputs){
        return false;
    }

    public String getIsActive(){
        return "";
    }

    public String getId(){
        return "";
    }

    public String getDatePosted(){
        return "";
    }

    public String getPicFolderName(){
        return "";
    }

    public interface ListingUpdateListener {
        void onResponse(boolean success);
        void onError(Error error);
        void onError(ANError anError);
    }

    public class ListingError extends Error {
        public String body = "";
        @Override
        public String toString(){
            return body;
        }
    }

    public class UrlBuffer{
        private StringBuffer buffer;

        public UrlBuffer(String baseUrl){
            buffer = new StringBuffer(baseUrl);
        }

        public void append(String string){
            buffer.append(string);
        }

        public void urlArgAppend(String valueKey, String value){
            buffer.append(valueKey);
            buffer.append("=");
            buffer.append(value);
            buffer.append("&");
        }

        @Override
        public String toString(){
            return buffer.toString();
        }

        public StringBuffer getBuffer(){
            return buffer;
        }

    }

}


