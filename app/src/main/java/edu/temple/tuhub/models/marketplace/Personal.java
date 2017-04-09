package edu.temple.tuhub.models.marketplace;

import android.util.Log;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.tuhub.models.NetworkManager;

/**
 * Created by Tom on 4/9/2017.
 */

public class Personal {
    public static final String INSERT_URL = "/insert_personal.jsp?";
    public static final String SELECT_BY_OWNER_URL = "/find_personals_by_user_id.jsp?";
    public static final String LIMIT_KEY = "limit";
    public static final String OFFSET_KEY = "offset";
    public static final String TITLE_KEY = "title";
    public static final String PERSONAL_ID_KEY = "personalId";
    public static final String DESCRIPTION_KEY = "description";
    public static final String LOCATION_KEY = "location";
    public static final String IS_ACTIVE_KEY = "isActive";
    public static final String OWNER_ID_KEY = "ownerId";
    public static final String USER_ID_KEY = "userId";
    public static final String PIC_FOLDER_NAME_KEY = "picFolder";
    public static final String DATE_POSTED_KEY = "datePosted";
    public static final String PERSONAL_LIST_KEY = "personalList";
    public static final String ERROR_KEY = "error";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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

    public interface PersonalRequestListener {
        void onResponse(Personal personal);
        void onError(ANError error);
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
