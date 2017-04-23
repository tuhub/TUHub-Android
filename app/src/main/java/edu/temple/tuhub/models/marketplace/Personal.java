package edu.temple.tuhub.models.marketplace;

import android.util.Log;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.temple.tuhub.EditListingFragment;
import edu.temple.tuhub.models.NetworkManager;

/**
 * Created by Tom on 4/9/2017.
 */

public class Personal extends Listing{
    public static final String INSERT_URL = "/insert_personal.jsp?";
    public static final String UPDATE_URL = "/update_personal.jsp?";
    public static final String SELECT_BY_OWNER_URL = "/find_personals_by_user_id.jsp?";
    public static final String LIMIT_KEY = "limit";
    public static final String OFFSET_KEY = "offset";
    public static final String TITLE_KEY = "title";
    public static final String PERSONAL_ID_KEY = "personalId";
    public static final String DESCRIPTION_KEY = "description";
    public static final String LOCATION_KEY = "location";
    public static final String OWNER_ID_KEY = "ownerId";
    public static final String USER_ID_KEY = "userId";
    public static final String PIC_FOLDER_NAME_KEY = "picFolder";
    public static final String DATE_POSTED_KEY = "datePosted";
    public static final String PERSONAL_LIST_KEY = "personalList";
    public static final String ERROR_KEY = "error";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String DESCRIPTION = "Description";
    public static final String ID = "ID";
    public static final String LOCATION = "Location";
    public static final String OWNER = "Owner";
    public static final String DATE_POSTED = "Date Posted";
    public static final String TITLE = "Title";

    //Number of (non-final) fields
    public static final int FIELD_COUNT = 8;

    private String personalId = "";
    private String title="";
    private String description="";
    private String location = "";
    private String isActive = "";
    private String ownerId = "";
    private String datePosted = "";
    private String picFileName = "";
    private String error = "";

    public Personal(){

    }

    public Personal(JSONObject object){
        try{
            this.personalId = object.getString(PERSONAL_ID_KEY);
            this.title = object.getString(TITLE_KEY);
            this.description = object.getString(DESCRIPTION_KEY);
            this.location = object.getString(LOCATION_KEY);
            this.isActive = object.getString(IS_ACTIVE_KEY);
            this.ownerId = object.getString(OWNER_ID_KEY);
            this.datePosted = object.getString(DATE_POSTED_KEY);
            this.picFileName = object.getString(PIC_FOLDER_NAME_KEY);

        } catch (JSONException e){
            this.error = e.toString();
        }
    }

    public Personal(String personalId, String title, String description, String location, String isActive, String ownerId, String datePosted, String picFileName) {
        this.personalId = personalId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.isActive = isActive;
        this.ownerId = ownerId;
        this.datePosted = datePosted;
        this.picFileName = picFileName;
    }

    @Override
    public String toString(){
        return "Personal ID: " + personalId + " title: " + title + " description: " + description + " location: " + location
                + " active: " + isActive + " owner: " + ownerId + " date posted: " + datePosted + " picfilename: " + picFileName + " error: " + error;
    }

    public void insert(final Personal.PersonalRequestListener personalRequestListener){
        String insertUrl = createInsertUrl();
        Log.d("insertUrl", insertUrl);
        NetworkManager.SHARED.requestFromUrl(insertUrl,
                null,
                null,
                null,
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("personal", response.toString());
                            String error = response.getString(ERROR_KEY);
                            String ownerId = response.getString(OWNER_ID_KEY);
                            if(error.length() == 0) {

                                getPicFolderAfterInsert(ownerId, new Personal.PersonalRequestListener() {
                                    @Override
                                    public void onResponse(Personal newestPersonal) {
                                        personalRequestListener.onResponse(newestPersonal);
                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        Log.d("Personal Insert Error", error.toString());
                                        personalRequestListener.onError(error);
                                    }
                                });
                            } else {
                                Personal personal = new Personal(response);
                                personal.setError(error);
                                personalRequestListener.onResponse(personal);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        personalRequestListener.onError(anError);
                    }
                });
    }

    public void getPicFolderAfterInsert(String ownerId, final Personal.PersonalRequestListener personalRequestListener){

        String selectUrl = Personal.createSelectByOwnerIdUrl(ownerId, 1, 0);
        Log.d("selectUrl", selectUrl);

        NetworkManager.SHARED.requestFromUrl(selectUrl,
                null,
                null,
                null,
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("newest personal", response.toString());

                        try {
                            JSONArray resultArray = response.getJSONArray(PERSONAL_LIST_KEY);
                            JSONObject personalJSON = resultArray.getJSONObject(0);
                            Personal newestPersonal = new Personal(personalJSON);

                            personalRequestListener.onResponse(newestPersonal);
                        } catch(JSONException e){
                            Log.d("personal error", e.toString());
                            ANError error = new ANError();
                            error.setErrorBody(e.toString());
                            personalRequestListener.onError(error);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        personalRequestListener.onError(anError);
                    }
                });
    }

    public static String createSelectByOwnerIdUrl(String ownerId, int limit, int offset){
        StringBuffer selectUrl = new StringBuffer(NetworkManager.Endpoint.MARKETPLACE.toString());
        selectUrl.append(SELECT_BY_OWNER_URL);
        selectUrl.append(USER_ID_KEY);
        selectUrl.append("=");
        selectUrl.append(ownerId);
        selectUrl.append("&");
        selectUrl.append(LIMIT_KEY);
        selectUrl.append("=");
        selectUrl.append(String.valueOf(limit));
        selectUrl.append("&");
        selectUrl.append(OFFSET_KEY);
        selectUrl.append("=");
        selectUrl.append(String.valueOf(offset));

        return selectUrl.toString();
    }

    /*
    Create the url for the insert API call. Checks to see which arguments are not null and appends their values to the GET url
     */
    public String createInsertUrl(){
        Personal.UrlBuffer buffer = new Personal.UrlBuffer(NetworkManager.Endpoint.MARKETPLACE.toString());
        buffer.append(INSERT_URL);
        buffer.append("&");

        if(title != null){
            buffer.urlArgAppend(TITLE_KEY, title);
        }

        if( description != null){
            buffer.urlArgAppend(DESCRIPTION_KEY, description);
        }

        if(isActive != null){
            buffer.urlArgAppend(IS_ACTIVE_KEY, isActive);
        }

        if(ownerId != null){
            buffer.urlArgAppend(OWNER_ID_KEY, ownerId);
        }
        if(location != null) {
            buffer.urlArgAppend(LOCATION_KEY, location);
        }

        return buffer.toString();
    }

    public boolean isEmpty(){
        String allFields = personalId + title + description + location + isActive + ownerId + datePosted + picFileName + error;
        return (allFields.length() == 0);
    }

    public String getPersonalId() {
        return personalId;
    }

    @Override
    public String getId(){
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicFileName() {
        return picFileName;
    }

    public void setPicFileName(String picFileName) {
        this.picFileName = picFileName;
    }

    @Override
    public String getPicFolderName(){
        return picFileName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public LinkedHashMap<String, String> toHashMap() {

        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
        fieldMap.put(PERSONAL_ID_KEY, personalId);
        fieldMap.put(TITLE, title);
        fieldMap.put(DESCRIPTION, description);
        fieldMap.put(LOCATION, location);
        fieldMap.put(IS_ACTIVE_KEY, isActive);
        fieldMap.put(OWNER, ownerId);
        fieldMap.put(DATE_POSTED, datePosted);
        fieldMap.put(PIC_FOLDER_NAME_KEY, picFileName);
        return fieldMap;
    }

    @Override
    public Listing fromMap(LinkedHashMap<String, String> fieldMap) {
        if(fieldMap.size() !=FIELD_COUNT) {
            return null;
        }
        Personal personal = new Personal();
        personal.setPersonalId(fieldMap.get(PERSONAL_ID_KEY));
        personal.setTitle(fieldMap.get(TITLE));
        personal.setDescription(fieldMap.get(DESCRIPTION));
        personal.setLocation(fieldMap.get(LOCATION));
        personal.setIsActive(fieldMap.get(IS_ACTIVE_KEY));
        personal.setOwnerId(fieldMap.get(OWNER));
        personal.setDatePosted(fieldMap.get(DATE_POSTED));
        personal.setPicFileName(fieldMap.get(PIC_FOLDER_NAME_KEY));

        return personal;
    }

    @Override
    public boolean validateFields(ArrayList<EditListingFragment.InputAndKey> inputs) {
        boolean noErrors = true;
        for(int i = 0; i<inputs.size(); i++){
            EditListingFragment.InputAndKey field = inputs.get(i);
            String value = field.editText.getText().toString();
            switch (field.key){
                case TITLE:
                case LOCATION:
                    if(value.length() > 45){
                        noErrors = false;
                        field.editText.setError("Must be less than 45 characters");
                    } else if(value == null || value.length() ==0){
                        noErrors = false;
                        field.editText.setError("Required Field");
                    }
                    break;
            }
        }
        return noErrors;
    }

    @Override
    public void update(final ListingUpdateListener listener) {
        changeNullToSpacesForUpdate();
        String updateUrl = createUpdateUrl();
        Log.d("updateUrl", updateUrl);
        NetworkManager.SHARED.requestFromUrl(updateUrl,
                null,
                null,
                null,
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("personal", response.toString());
                            String error = response.getString(ERROR_KEY);
                            if(error.length() == 0) {

                                listener.onResponse(true);

                            } else {
                                Personal personal = new Personal(response);
                                personal.setError(error);
                                ListingError listingError = new ListingError();
                                listingError.body = personal.toString();
                                listener.onError(listingError);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        listener.onError(anError);
                    }
                });
    }

    //Change the non-required null fields to spaces so that the API stores the blank data in the DB
    public void changeNullToSpacesForUpdate(){
        if(description.length() == 0 || description == null){
            description = " ";
        }
        if(location.length() == 0 || location == null){
            location = " ";
        }
    }

    /*
  Create the url for the update API call. Checks to see which arguments are not null and appends their values to the GET url
   */
    public String createUpdateUrl(){
        UrlBuffer buffer = new UrlBuffer(NetworkManager.Endpoint.MARKETPLACE.toString());
        buffer.append(UPDATE_URL);

        String string = "/update_personal.jsp?personalId=[id]&title=[nullable]&description=[nullable]&isActive=[nullable]&location=[nullable]";

        if(personalId != null){
            buffer.urlArgAppend(PERSONAL_ID_KEY, personalId);
        }

        if(title != null){
            buffer.urlArgAppend(TITLE_KEY, title);
        }

        if( description != null){
            buffer.urlArgAppend(DESCRIPTION_KEY, description);
        }

        if(location != null){
            buffer.urlArgAppend(LOCATION_KEY, location);
        }

        if(isActive != null){
            buffer.urlArgAppend(IS_ACTIVE_KEY, isActive);
        }

        return buffer.toString();
    }

    public interface PersonalRequestListener {
        void onResponse(Personal personal);
        void onError(ANError error);
    }

}
