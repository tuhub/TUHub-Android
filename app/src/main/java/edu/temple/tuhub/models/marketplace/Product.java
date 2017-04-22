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
 * Created by Ben on 4/5/2017.
 */

public class Product extends Listing{
    public static final String INSERT_URL = "/insert_product.jsp?";
    public static final String UPDATE_URL = "/update_product.jsp?";
    public static final String SELECT_BY_OWNER_URL = "/find_products_by_user_id.jsp?";
    public static final String LIMIT_KEY = "limit";
    public static final String OFFSET_KEY = "offset";
    public static final String TITLE_KEY = "title";
    public static final String PRODUCT_ID_KEY = "productId";
    public static final String DESCRIPTION_KEY = "description";
    public static final String PRICE_KEY = "price";
    public static final String OWNER_ID_KEY = "ownerId";
    public static final String USER_ID_KEY = "userId";
    public static final String PIC_FOLDER_NAME_KEY = "picFolder";
    public static final String DATE_POSTED_KEY = "datePosted";
    public static final String PRODUCT_LIST_KEY = "productList";
    public static final String ERROR_KEY = "error";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String PRICE = "Price";

    public static final int FIELD_COUNT = 8;

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

    public Product(JSONObject object){
        try{
            this.productId = object.getString(PRODUCT_ID_KEY);
            this.title = object.getString(TITLE_KEY);
            this.description = object.getString(DESCRIPTION_KEY);
            this.price = object.getString(PRICE_KEY);
            this.isActive = object.getString(IS_ACTIVE_KEY);
            this.ownerId = object.getString(OWNER_ID_KEY);
            this.datePosted = object.getString(DATE_POSTED_KEY);
            this.picFileName = object.getString(PIC_FOLDER_NAME_KEY);

        } catch (JSONException e){
            this.error = e.toString();
        }
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

    @Override
    public String toString(){
        return "ID: " + productId + " title: " + title + " description: " + description + " price" + price + " isActive" + isActive
                + " owner: " + ownerId + " date posted: " + datePosted + " picFileName: " + picFileName + " error: " + error;
    }

    public void insert(final ProductRequestListener productRequestListener){
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
                            Log.d("product", response.toString());
                            String error = response.getString(ERROR_KEY);
                            String ownerId = response.getString(OWNER_ID_KEY);
                            if(error.length() == 0) {

                                getPicFolderAfterInsert(ownerId, new ProductRequestListener() {
                                    @Override
                                    public void onResponse(Product newestProduct) {
                                        productRequestListener.onResponse(newestProduct);
                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        Log.d("Product Insert Error", error.toString());
                                        productRequestListener.onError(error);
                                    }
                                });
                            } else {
                                Product product = new Product(response);
                                product.setError(error);
                                productRequestListener.onResponse(product);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        productRequestListener.onError(anError);
                    }
                });
    }

    public void getPicFolderAfterInsert(String ownerId, final ProductRequestListener productRequestListener){

        String selectUrl = Product.createSelectByOwnerIdUrl(ownerId, 1, 0);
        Log.d("selectUrl", selectUrl);

        NetworkManager.SHARED.requestFromUrl(selectUrl,
                null,
                null,
                null,
                new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("newest product", response.toString());

                        try {
                            JSONArray resultArray = response.getJSONArray(PRODUCT_LIST_KEY);
                            JSONObject productJSON = resultArray.getJSONObject(0);
                            Product newestProduct = new Product(productJSON);

                            productRequestListener.onResponse(newestProduct);
                        } catch(JSONException e){
                            Log.d("product error", e.toString());
                            ANError error = new ANError();
                            error.setErrorBody(e.toString());
                            productRequestListener.onError(error);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        productRequestListener.onError(anError);
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
        UrlBuffer buffer = new UrlBuffer(NetworkManager.Endpoint.MARKETPLACE.toString());
        buffer.append(INSERT_URL);
        buffer.append("&");

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

    /*
   Create the url for the update API call. Checks to see which arguments are not null and appends their values to the GET url
    */
    public String createUpdateUrl(){
        UrlBuffer buffer = new UrlBuffer(NetworkManager.Endpoint.MARKETPLACE.toString());
        buffer.append(UPDATE_URL);
        String string = "productId=[id]&title=[nullable]&price=[nullable]&description=[nullable]&isActive=[nullable]";


        if(productId != null){
            buffer.urlArgAppend(PRODUCT_ID_KEY, productId);
        }
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

        return buffer.toString();
    }

    public boolean isEmpty(){
        String allFields = productId + title + description + price + isActive + ownerId + datePosted + picFileName + error;
        return (allFields.length() == 0);
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public String getId(){
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
    @Override
    public String getPicFolderName(){
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

        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
        fieldMap.put(PRODUCT_ID_KEY, productId);
        fieldMap.put(TITLE, title);
        fieldMap.put(DESCRIPTION, description);
        fieldMap.put(PRICE, price);
        fieldMap.put(IS_ACTIVE_KEY, isActive);
        fieldMap.put(OWNER, ownerId);
        fieldMap.put(DATE_POSTED, datePosted);
        fieldMap.put(PIC_FOLDER_NAME_KEY, picFileName);

        return fieldMap;
    }

    @Override
    public Listing fromMap(LinkedHashMap<String, String> fieldMap) {
        if(fieldMap.size() != FIELD_COUNT) {
            return null;
        }
        Product product = new Product();
        product.setProductId(fieldMap.get(PRODUCT_ID_KEY));
        product.setTitle(fieldMap.get(TITLE));
        product.setDescription(fieldMap.get(DESCRIPTION));
        String price = fieldMap.get(PRICE);
        if(price.charAt(0) == '$'){
            price = price.substring(1);
        }
        product.setPrice(price);
        product.setIsActive(fieldMap.get(IS_ACTIVE_KEY));
        product.setOwnerId(fieldMap.get(OWNER));
        product.setDatePosted(fieldMap.get(DATE_POSTED));
        product.setPicFileName(fieldMap.get(PIC_FOLDER_NAME_KEY));

        return product;
    }

    @Override
    public boolean validateFields(ArrayList<EditListingFragment.InputAndKey> inputs) {
        boolean noErrors = true;
        for(int i = 0; i<inputs.size(); i++){
            EditListingFragment.InputAndKey field = inputs.get(i);
            String value = field.editText.getText().toString();
            switch (field.key){
                case TITLE:
                    if(value.length() > 45){
                        noErrors = false;
                        field.editText.setError("Must be less than 45 characters");
                    } else if (value == null || value.length() == 0) {
                        noErrors = false;
                        field.editText.setError("Required Field");
                    }
                    break;
                case PRICE:
                    if (value == null || value.length() == 0) {
                        noErrors = false;
                        field.editText.setError("Required Field");
                    } else {
                        if (value.charAt(0) == '$') {
                            value = value.substring(1);
                        }
                        if (!value.matches(DOLLAR_REGEX)) {
                            noErrors = false;
                            field.editText.setError("Must be digits only and have two decimal places");
                        }
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
                            Log.d("product", response.toString());
                            String error = response.getString(ERROR_KEY);
                            if(error.length() == 0) {

                                listener.onResponse(true);

                            } else {
                                Product product = new Product(response);
                                product.setError(error);
                                ListingError productError = new ListingError();
                                productError.body = product.toString();
                                listener.onError(productError);
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
    }

    public interface ProductRequestListener {
        void onResponse(Product product);
        void onError(ANError error);
    }

}



