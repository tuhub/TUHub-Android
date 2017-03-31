package edu.temple.tuhub;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by mangaramu on 11/24/2016.
 */

public class networkClass {

    networkgo go =null;//async task object
    //String gothtml=""; // for saving the obtained hmtl
    //String goturl=""; //for saving the url if there is anything
    boolean bolcancel= false;
    urlandstring pageandurl=new urlandstring("","");


    public class urlandstring extends Object//object that takes a url string and an html string
    {
        String url1="";
        String html1="";

        urlandstring(String url, String html)
        {
            url1=url;
            html1=html;
        }
        public String getUrl1()//gets url1
        {
            return url1;
        }
        public String getHtml1()//gets html1
        {
            return html1;
        }

        public void setUrl1(String url1) {
            this.url1 = url1;
        }

        public void setHtml1(String html1) {
            this.html1 = html1;
        }
    }



    public class networkgo extends AsyncTask<Object,String,String> // this will do the network task i assign, since it is a class.. it is an object!
    {
        String html;// url that we will use to get the html crap
        String urll;


        @Override
        protected String doInBackground(Object... perams)
        {// is not within the UI thread!
            urll=perams[0].toString();//perams is an array of the vribles or things passed
            //place for html to go within scope of doinbackground
            String html2="";// html2 will have the html code filled within it
            String htmlt="";


            try {


                InputStreamReader haha= new InputStreamReader(((URL)perams[0]).openStream());//input stream reader takes in an input stream!
                BufferedReader read= new BufferedReader(haha);// put the IO stream into the buffer for conversion from bytes to chars
                while((htmlt=read.readLine())!= null ) {

                    if(isCancelled())
                    {
                        html2="";
                        break;

                    }
                    html2= html2+htmlt;

                }



                read.close();//closes the buffered stream
                urlandstring urlstr=new urlandstring(urll,html2);//creates a new urlandstringobject
                Message msg= Message.obtain();//creates and saves a new message instance
                msg.obj=urlstr;//puts the html variable into an object within the message
                msg.setTarget((Handler) perams[1]);//sets target to basic handler
                msg.sendToTarget(); //sends to target

                return html2;

            }
            catch (IOException e) {
                e.printStackTrace();
               // toast.toastpush("1",urll);
                // Toast err= Toast.makeText(getApplicationContext(),"ERROR cant open IO stream",Toast.LENGTH_SHORT);//error message for if the io stream does not work
                //  err.show();
                // Looper.loop();//for the correct implementation of thread message handling!
                return "nope";
            }

        }




    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


    public Boolean hashttp(String s)// checks if url entered has http:// on it
    {
        if(s.contains("http://"))
        {
            return true;
        } else if (s.contains("https://"))
        {
            return true;
        }
        else return false;

    }

    public Boolean hasend(String s)// checks if there is a com at the end of your url if you don't have http://
    {
        // Log.d("From hasend", end);
        if ( s.contains(".com")|| s.contains(".net")|| s.contains(".edu")|| s.contains(".gov")|| s.contains(".co")|| s.contains(".jp")|| s.contains(".info"))return true;
        else return false;
    }

    public void clickload (String url, Handler handler)// with a string we test if the url is properly formatted then call an async task to load a webpage for us
    {
        bolcancel=false;// we are into the fragment loading the view... variable representing a cancel should be false

        URL uurl=null;//URL object

        if (url.equals("")) {//Url handling .. if the url isnt properly formated... does a google search

            return;
        }
        else if(hashttp(url)&& hasend(url))// if hashttp we open an asyc task and do someting cool!
        {

            try{// URL throws an exception if it is not foratted properly! so must try-catch!
                uurl = new URL(url.trim());//URL object

            }

            catch (Exception e)// need to create handlers for messages!
            {
                e.printStackTrace();

                //toast.toastpush("1",url);

            }
            //toast.toastpush("7",url);



            pageandurl.setUrl1(uurl.toString());


            go=new networkgo();// new network go async task object!
            if(bolcancel)
            {

            }
            else {
                go.execute(uurl, handler);// executes websearching!
            }
        }
        else if(hasend(url)) // we have the http:// now do we have .com?
        {
            url="http://"+url;


            try{// URL throws an exception if it is not foratted properly! so must try-catch!
                uurl = new URL(url.trim());//URL object

            }

            catch (Exception e)// need to create handlers for messages!
            {
                e.printStackTrace();

                //toast.toastpush("1",url);

            }
            //toast.toastpush("7",url);



            pageandurl.setUrl1(uurl.toString());


            go=new networkgo();// new network go async task object!
            if(bolcancel)
            {

            }
            else {
                go.execute(uurl, handler);// executes websearching!
            }


        }
        else
        {

            url=url.replace("."," ");
            url="https://www.google.com/search?q="+url; // tries to follow the syntax of google quiries.
            url=url.replace(" ","%20");

            try{// URL throws an exception if it is not foratted properly! so must try-catch!
                uurl = new URL(url.trim());//URL object

            }

            catch (Exception e)// need to create handlers for messages!
            {
                e.printStackTrace();

              //  toast.toastpush("1",url);

            }
            //toast.toastpush("7",url);

            /*if(text!=null)// for if we needed to call clickload before the views for it were created
            {
                text.setText(uurl.toString());
            }
            else
            {
                pageandurl.setUrl1(uurl.toString());
            }*/

            pageandurl.setUrl1(uurl.toString());

            go=new networkgo();// new network go async task object!
            if(bolcancel)
            {

            }
            else {
                go.execute(uurl, handler);// executes websearching!
            }
        }


    }

    public void Cancel()
    {

        if(go!=null)
        {
            go.cancel(true);
        }
        else {
            bolcancel = true;
        }
    }
    public urlandstring getnewUrlString()
    {
        return new urlandstring("","");
    }

}
