package edu.temple.tuhub;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.temple.tuhub.models.Marketitem;
import edu.temple.tuhub.models.marketplace.Job;
import edu.temple.tuhub.models.marketplace.Personal;
import edu.temple.tuhub.models.marketplace.Product;

// Created by mangaramu on 4/6/2017

//title is 45 characters!
/** personalList
 * "personalId":"3",
 "ownerId":"tue94788",
 "title":"Climbing Competition!",
 "description":"Temple\u0027s annual climbing competition is April 15th! Register at the rock wall.",
 "location":"Pearson and McGonagle Hall",
 "datePosted":"2017-04-01T19:39:14Z",
 "isActive":"true",
 "picFolder":"29",
 "error":""*/

/**productList
 * "productId":"22",
 "title":"utg",
 "description":"huf",
 "price":"$58.00",
 "isActive":"true",
 "ownerId":"tue94788",
 "datePosted":"2017-04-06T17:54:59.000Z",
 "picFolder":"46",
 "error":""*/

/**jobList
 "jobId":"5",
 "ownerId":"tue94788",
 "datePosted":"2017-04-01T16:24:22Z",
 "location":"Wachman Hall",
 "hoursPerWeek":3,
 "description":"Monitor the computer labs",
 "title":"Lab Monitor",
 "pay":"$7.25",
 "startDate":"2017-05-10T00:00:00Z",
 "isActive":"true",
 "picFolder":"20",
 "error":""",
*/

/**
 *public String placeholderstring;
 public Bitmap marketimage;
 public String price;
 public String ownerid;
 public String dateposted;
 public String markettitle;
 public String description;
 public String markettype;
 public String location;
 * */
public class GetMarketDataThread extends Thread {

    networkClass net = new networkClass();
    Handler handle;
    private JSONObject marketJSON;
    ArrayList<Marketitem> t=new ArrayList<>();

    GetMarketDataThread(Handler x, JSONObject y)
    {
        handle = x;
        marketJSON = y;
    }
    @Override
    public void run() { //TODO maybe have cancel/interrupt functionality to save cpu cycles?

        try {
            JSONArray marketList;
            JSONObject tmp;
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat startDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat startDateEndFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat destFormat = new SimpleDateFormat("MMM d, yyyy hh:mm:ss a"); //here 'a' for AM/PM

            if (marketJSON.has("productList")) {
                marketList=(JSONArray)marketJSON.get("productList");



                Date date = null;


                for(int x=0; x<marketList.length();x++)
                {


                    Marketitem pow = new Marketitem();
                    String tmptime;
                    String formattedDate;
                    String tmptitle;
                    String tmpimageurl;
                    String tmpdescription;
                    String tmpprice;
                    String tmpownerid;
                    String tmplocation;
                    String tmppicfolder;

                    tmp=(JSONObject)marketList.get(x);
                    tmptime = (String)tmp.get("datePosted");

                    try {
                        date = sourceFormat.parse(tmptime);
                    }
                    catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                    formattedDate = destFormat.format(date);
                    pow.setDateposted(formattedDate);//Setting the formated date
                    tmptitle = (String)tmp.get("title");
                    pow.setMarkettitle(tmptitle);
                    tmpdescription = (String)tmp.get("description");
                    pow.setDescription(tmpdescription);
                    tmpprice = (String)tmp.get("price");
                    pow.setPrice(tmpprice);
                    tmpownerid = (String)tmp.get("ownerId");
                    pow.setOwnerid(tmpownerid);
                    tmppicfolder = (String)tmp.get("picFolder");
                    pow.setPicfolder(tmppicfolder);
                    pow.setMarkettype("Product");
                    pow.setId(tmp.getString(Product.PRODUCT_ID_KEY));
                    pow.setIsActive(tmp.getString(Product.IS_ACTIVE_KEY));



                    t.add(pow);

                }
            }
            else if (marketJSON.has("jobList"))
            {
                marketList=(JSONArray)marketJSON.get("jobList");

                Date date = null;


                for(int x=0; x<marketList.length();x++) {

                    Marketitem pow = new Marketitem();
                    String tmptime;
                    String formattedDate;
                    String tmptitle;
                    String tmpimageurl;
                    String tmpdescription;
                    String tmpprice;
                    String tmpownerid;
                    String tmplocation;
                    String tmppicfolder;
                    String tmppay;
                    String tmpstartdate;


                    tmp = (JSONObject) marketList.get(x);
                    tmptime = (String) tmp.get("datePosted");

                    try {
                        date = sourceFormat.parse(tmptime);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                    formattedDate = destFormat.format(date);
                    pow.setDateposted(formattedDate);//Setting the formated date


                    tmpstartdate=(String) tmp.get("startDate");

                   if(tmpstartdate!=null) { // a job may not have a start date? maybe
                       try {
                           date = startDateFormat.parse(tmpstartdate);
                       } catch (java.text.ParseException e) {
                           date = null;
                           e.printStackTrace();
                       }
                       if(date != null) {
                           formattedDate = startDateEndFormat.format(date);
                           pow.setStartdate(formattedDate);//Setting the formated date
                       } else {
                           pow.setStartdate("");
                       }
                   }

                    tmptitle = (String) tmp.get("title");
                    pow.setMarkettitle(tmptitle);
                    tmpdescription = (String) tmp.get("description");
                    pow.setDescription(tmpdescription);
                    tmpownerid = (String) tmp.get("ownerId");
                    pow.setOwnerid(tmpownerid);
                    tmppay = (String) tmp.get("pay");
                    pow.setPay(tmppay);
                    tmplocation = (String) tmp.get("location");
                    pow.setLocation(tmplocation);
                    tmppicfolder = (String) tmp.get("picFolder");
                    pow.setPicfolder(tmppicfolder);
                    pow.setMarkettype("Job");
                    pow.setId(tmp.getString(Job.JOB_ID_KEY));
                    pow.setHoursPerWeek(tmp.getString(Job.HOURS_KEY));
                    pow.setIsActive(tmp.getString(Job.IS_ACTIVE_KEY));


                    t.add(pow);
                }
            }
            else if(marketJSON.has("personalList"))
            {

                marketList=(JSONArray)marketJSON.get("personalList");

                Date date = null;


                for(int x=0; x<marketList.length();x++)
                {

                    Marketitem pow = new Marketitem();
                    String tmptime;
                    String formattedDate;
                    String tmptitle;
                    String tmpimageurl;
                    String tmpdescription;
                    String tmpprice;
                    String tmpownerid;
                    String tmplocation;
                    String tmppicfolder;

                    tmp=(JSONObject)marketList.get(x);
                    tmptime = (String)tmp.get("datePosted");

                    try {
                        date = sourceFormat.parse(tmptime);
                    }
                    catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                    formattedDate = destFormat.format(date);
                    pow.setDateposted(formattedDate);//Setting the formated date
                    tmptitle = (String)tmp.get("title");
                    pow.setMarkettitle(tmptitle);
                    tmpdescription = (String)tmp.get("description");
                    pow.setDescription(tmpdescription);
                    tmpownerid = (String)tmp.get("ownerId");
                    pow.setOwnerid(tmpownerid);
                    tmplocation = (String)tmp.get("location");
                    pow.setLocation(tmplocation);
                    tmppicfolder = (String)tmp.get("picFolder");
                    pow.setPicfolder(tmppicfolder);
                    pow.setMarkettype("Personal");
                    pow.setId(tmp.getString(Personal.PERSONAL_ID_KEY));
                    pow.setIsActive(tmp.getString(Personal.IS_ACTIVE_KEY));



                    t.add(pow);
                    //if is interrupted break!
                }

            }

            Message marketitems = Message.obtain();
            marketitems.obj=t;
            marketitems.setTarget(handle);
            marketitems.sendToTarget();

        } catch (JSONException e) {
            e.printStackTrace();
        }












    }
}
