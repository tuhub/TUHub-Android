package edu.temple.tuhub.models.marketplace;

import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import edu.temple.tuhub.models.NetworkManager;

/**
 * Created by Ben on 4/5/2017.
 */

public class Product {
    private final String INSERT = "/insert_product.jsp?";
    private final String TITLE_KEY = "title";
    private final String DESCRIPTION_KEY = "description";
    private final String PRICE_KEY = "price";
    private final String IS_ACTIVE_KEY = "isActive";
    private final String OWNER_ID_KEY = "ownerId";

    private String productId = "";
    private String title="";
    private String description="";
    private String price = "";
    private String isActive = "";
    private String ownerId = "";
    private String datePosted = "";
    private String picFileName = "";
    private String error = "";

    public Product(){

    }

    public Product(String productId, String title, String description, String price, String isActive, String ownerId, String datePosted, String picFileName) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.isActive = isActive;
        this.ownerId = ownerId;
        this.datePosted = datePosted;
        this.picFileName = picFileName;
    }



    /*
    Create the url for the insert API call. Checks to see which arguments are not null and appends their values to the GET url
     */
    public String createInsertUrl(){
       UrlBuffer buffer = new UrlBuffer(INSERT);
        buffer.append("?");

        if(title != null){
            buffer.urlArgAppend(TITLE_KEY, title);
        }

        if( description != null){
            buffer.urlArgAppend(DESCRIPTION_KEY, description);
        }

        if(price != null){
            buffer.urlArgAppend(PRICE_KEY, price);
        }

        if(isActive != null){
            buffer.urlArgAppend(IS_ACTIVE_KEY, isActive);
        }

        if(ownerId != null){
            buffer.urlArgAppend(OWNER_ID_KEY, ownerId);
        }

        return buffer.toString();
    }

    public boolean isEmpty(){
        String allFields = productId + title + description + price + isActive + ownerId + datePosted + picFileName + error;
        return (allFields.length() == 0);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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



