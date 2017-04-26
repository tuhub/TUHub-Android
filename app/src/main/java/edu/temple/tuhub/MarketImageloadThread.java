package edu.temple.tuhub;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import edu.temple.tuhub.models.MarketImageItem;

// Created by mangaramu on 4/2/2017

//https://tumobilemarketplace.s3.amazonaws.com/?list-type=2&x-amz-date=20170413T030140Z&prefix= to get the image information
//http://tumobilemarketplace.s3.amazonaws.com/  to get the actual image
class MarketImageloadThread extends Thread {

    private MarketImageItem Imagei;
    private Handler placetosend;

    MarketImageloadThread(MarketImageItem x, Handler y )
    {
        Imagei = x;
        placetosend = y;

    }

    @Override
    public void run() {
        String html2="";// html2 will have the html code filled within it
        String htmlt;


        try {
            String marketstage1 = "https://tumobilemarketplace.s3.amazonaws.com/?list-type=2&x-amz-date=20170413T030140Z&prefix=";
            String somelink= marketstage1 +Imagei.getItemref().getPicfolder();

            InputStreamReader haha = new InputStreamReader((new URL(somelink)).openStream());//input stream reader takes in an input stream!
            BufferedReader read = new BufferedReader(haha);// put the IO stream into the buffer for conversion from bytes to chars
            while ((htmlt = read.readLine()) != null) {


                html2 = html2 + htmlt;

            }


            read.close();//closes the buffered stream
        }
        catch (IOException e) {
            e.printStackTrace();
            // toast.toastpush("1",urll);
            // Toast err= Toast.makeText(getApplicationContext(),"ERROR cant open IO stream",Toast.LENGTH_SHORT);//error message for if the io stream does not work
            //  err.show();
            // Looper.loop();//for the correct implementation of thread message handling!

        }

        int tmpstart= html2.indexOf("<Key>");
        int tmpend= html2.indexOf("</Key>");

        while(tmpstart!=-1)
        {//add 6 to the index of </key> to try and get next key..
            String tmpstring;

            tmpstring= html2.substring(tmpstart+5,tmpend);

            if(tmpstring.matches("[a-zA-Z0-9]+/[a-zA-Z0-9.]+"))//regex to see if the tmpstring between <Key> and </Key> matches an image filename or a path to an imagefilename
            {
                String marketstage2 = "http://tumobilemarketplace.s3.amazonaws.com/";
                Imagei.getItemref().marketimagelinks.add(marketstage2 +tmpstring);
            }
            tmpstart=html2.indexOf("<Key>",tmpend);
            tmpend=html2.indexOf("</Key>",tmpstart);
        }
        if (Imagei.getItemref().marketimagelinks.size() != 0)// if there were no image links we add a default image in the adapter!
        { // if there are image links we the first image

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            try {
                int reqHeight=400;
                int reqWidth=400;
            BitmapFactory.decodeStream(new URL(Imagei.getItemref().marketimagelinks.get(0)).openStream(),null,options);


                final int height = options.outHeight;
                final int width = options.outWidth;

                int inSampleSize = 1;

                if (height > reqHeight || width > reqWidth) {

                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                    // height and width larger than the requested height and width.
                    while ((halfHeight / inSampleSize) >= reqHeight
                            && (halfWidth / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                }

                /*double ratio= height/(double)width;
                double newhight=height;
                double newwidth=width;

                if(ratio>1) //flips the ratio if it is found to be bigger than one
                {
                    ratio = 1/ratio;

                }
                else if(ratio==1)
                {
                    newhight=500;
                    newwidth=500;
                }

                while (newhight > 500 || newwidth > 500) {
                    //will keep multiplying by the small side over the big side
                    newhight = newhight * ratio;
                    newwidth = newwidth * ratio;


                }*/

                options.inJustDecodeBounds = false;
                options.inSampleSize=inSampleSize;
                Imagei.getItemref().Firstmarketimagescaled =BitmapFactory.decodeStream(new URL(Imagei.getItemref().marketimagelinks.get(0)).openStream(),null,options);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        Message m = Message.obtain();
        m.obj = Imagei;
        m.setTarget(placetosend);
        m.sendToTarget();



    }
}
