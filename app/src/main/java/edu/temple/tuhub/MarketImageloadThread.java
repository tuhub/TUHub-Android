package edu.temple.tuhub;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import edu.temple.tuhub.models.MarketImageItem;

/**
 * Created by mangaramu on 4/2/2017.
 */
//https://tumobilemarketplace.s3.amazonaws.com/?list-type=2&x-amz-date=20170413T030140Z&prefix= to get the image information
//http://tumobilemarketplace.s3.amazonaws.com/  to get the actual image
public class MarketImageloadThread extends Thread {

    MarketImageItem Imagei;
    Handler placetosend;

    private final String Marketstage1 = "https://tumobilemarketplace.s3.amazonaws.com/?list-type=2&x-amz-date=20170413T030140Z&prefix=";
    private final String Marketstage2 = "http://tumobilemarketplace.s3.amazonaws.com/";
    MarketImageloadThread(MarketImageItem x, Handler y )
    {
        Imagei = x;
        placetosend = y;

    }

    @Override
    public void run() {
        String html2="";// html2 will have the html code filled within it
        String htmlt="";


        try {
           String somelink= Marketstage1+Imagei.getItemref().getPicfolder();

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

            if(tmpstring.matches(".+?\\.[a-zA-Z]+"))//regex to see if the tmpstring between <Key> and </Key> matches an image filename or a path to an imagefilename
            {
                Imagei.getItemref().marketimagelinks.add(Marketstage2+tmpstring);
            }
            else
            {

            }
            tmpstart=html2.indexOf("<Key>",tmpend);
            tmpend=html2.indexOf("</Key>",tmpstart);
        }

        if(Imagei.getItemref().marketimagelinks.size()==0)// if there were no image links we add a default image in the adapter!
        {

        }
        else { // if there are image links we the first image

            BitmapFactory bitmafa;
            Bitmap tmpbitma;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled=true;

            try {

            tmpbitma= BitmapFactory.decodeStream(((URL) new URL(Imagei.getItemref().marketimagelinks.get(0))).openStream());

                //scaling the bitmap using the ratio of the smallest side over the biggest side
                final int height = tmpbitma.getHeight();
                final int width = tmpbitma.getWidth();

                double ratio= height/(double)width;
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


                }


                Imagei.getItemref().Firstmarketimagescaled =Bitmap.createScaledBitmap(tmpbitma,(int)newwidth,(int)newhight, false);



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
