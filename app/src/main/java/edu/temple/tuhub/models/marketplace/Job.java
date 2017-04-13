package edu.temple.tuhub.models.marketplace;

import android.util.Log;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import edu.temple.tuhub.models.NetworkManager;

/**
 * Created by laurenlezberg on 4/9/17.
 */

public class Job {
    public static final String INSERT_URL = "/insert_job.jsp?";
    public static final String SELECT_BY_OWNER_URL = "/find_jobs_by_user_id.jsp?";
    public static final String LIMIT_KEY = "limit";
    public static final String OFFSET_KEY = "offset";
    public static final String TITLE_KEY = "title";
    public static final String JOB_ID_KEY = "jobId";
    public static final String DESCRIPTION_KEY = "description";
    public static final String START_DATE_KEY = "startDate";
    public static final String LOCATION_KEY = "location";
    public static final String PAY_KEY = "pay";
    public static final String HOURS_KEY = "hoursPerWeek";
    public static final String IS_ACTIVE_KEY = "isActive";
    public static final String OWNER_ID_KEY = "ownerId";
    public static final String USER_ID_KEY = "userId";
    public static final String PIC_FOLDER_NAME_KEY = "picFolder";
    public static final String DATE_POSTED_KEY = "datePosted";
    public static final String JOB_LIST_KEY = "jobList";
    public static final String ERROR_KEY = "error";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private String jobId = "";
    private String title="";
    private String description="";
    private String pay = "";
    private String hoursPerWeek="";
    private String startDate="";
    private String location = "";
    private String isActive = "";
    private String ownerId = "";
    private String datePosted = "";
    private String picFileName = "";
    private String error = "";

    public Job(){

    }

    public Job(JSONObject object){
        try{
            this.jobId = object.getString(JOB_ID_KEY);
            this.title = object.getString(TITLE_KEY);
            this.description = object.getString(DESCRIPTION_KEY);
            this.hoursPerWeek = object.getString(HOURS_KEY);
            this.pay = object.getString(PAY_KEY);
            this.startDate = formatDate(object.getString(START_DATE_KEY));
            this.location = object.getString(LOCATION_KEY);
            this.isActive = object.getString(IS_ACTIVE_KEY);
            this.ownerId = object.getString(OWNER_ID_KEY);
            this.datePosted = object.getString(DATE_POSTED_KEY);
            this.picFileName = object.getString(PIC_FOLDER_NAME_KEY);

        } catch (JSONException e){
            this.error = e.toString();
        }
    }

    public Job(String jobId, String title, String description, String pay, String startDate, String location, String hours, String isActive, String ownerId, String datePosted, String picFileName) {
        this.jobId = jobId;
        this.title = title;
        this.description = description;
        this.pay = pay.replaceAll("$","");
        this.hoursPerWeek = hours;
        this.startDate = formatDate(startDate);
        this.location = location;
        this.isActive = isActive;
        this.ownerId = ownerId;
        this.datePosted = datePosted;
        this.picFileName = picFileName;
    }
    public String formatDate(String startDate){
       String newDate = startDate.substring(0,startDate.indexOf('/'))+ "-" + startDate.substring(startDate.indexOf('/')+1,startDate.lastIndexOf('/'))+"-20"+startDate.substring(startDate.lastIndexOf('/')+1, startDate.length());

        return newDate;
    }

    public void insert(final Job.JobRequestListener jobRequestListener){
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
                            Log.d("job", response.toString());
                            String error = response.getString(ERROR_KEY);
                            String ownerId = response.getString(OWNER_ID_KEY);
                            if(error.length() == 0) {

                                getPicFolderAfterInsert(ownerId, new Job.JobRequestListener() {
                                    @Override
                                    public void onResponse(Job newestJob) {
                                        jobRequestListener.onResponse(newestJob);
                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        Log.d("Job Insert Error", error.toString());
                                        jobRequestListener.onError(error);
                                    }
                                });
                            } else {
                                Job job = new Job(response);
                                job.setError(error);
                                jobRequestListener.onResponse(job);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        jobRequestListener.onError(anError);
                    }
                });
    }

    public void getPicFolderAfterInsert(String ownerId, final Job.JobRequestListener jobRequestListener){

        String selectUrl = Job.createSelectByOwnerIdUrl(ownerId, 1, 0);
        Log.d("selectUrl", selectUrl);

        NetworkManager.SHARED.requestFromUrl(selectUrl,
                null,
                null,
                null,
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("newest job", response.toString());

                        try {
                            JSONArray resultArray = response.getJSONArray(JOB_LIST_KEY);
                            JSONObject jobJSON = resultArray.getJSONObject(0);
                            Job newestJob = new Job(jobJSON);

                            jobRequestListener.onResponse(newestJob);
                        } catch(JSONException e){
                            Log.d("job error", e.toString());
                            ANError error = new ANError();
                            error.setErrorBody(e.toString());
                            jobRequestListener.onError(error);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        jobRequestListener.onError(anError);
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
        Job.UrlBuffer buffer = new Job.UrlBuffer(NetworkManager.Endpoint.MARKETPLACE.toString());
        buffer.append(INSERT_URL);
        buffer.append("&");

        if(title != null){
            buffer.urlArgAppend(TITLE_KEY, title);
        }

        if( description != null){
            buffer.urlArgAppend(DESCRIPTION_KEY, description);
        }
        if(pay != null) {
            buffer.urlArgAppend(PAY_KEY, pay);
        }

        if(isActive != null){
            buffer.urlArgAppend(IS_ACTIVE_KEY, isActive);
        }

        if(ownerId != null){
            buffer.urlArgAppend(OWNER_ID_KEY, ownerId);
        }
        if(startDate != null){
            buffer.urlArgAppend(START_DATE_KEY, startDate);
        }

        if(hoursPerWeek!=null){
            buffer.urlArgAppend(HOURS_KEY, hoursPerWeek);
        }
        if(location != null){
            buffer.urlArgAppend(LOCATION_KEY, location);
        }

        return buffer.toString();
    }

    public boolean isEmpty(){
        String allFields = jobId + title + description + pay + hoursPerWeek + isActive + ownerId + datePosted + picFileName + error;
        return (allFields.length() == 0);
    }

    public String getStartDate() {
        return startDate;
    }

    public String getLocation() {
        return location;
    }

    public void setStartDate(String startDate) {
        this.startDate = formatDate(startDate);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getHoursPerWeek() {return hoursPerWeek;}

    public void setHoursPerWeek(String hours) {this.hoursPerWeek = hours;}

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

    public interface JobRequestListener {
        void onResponse(Job job);
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
