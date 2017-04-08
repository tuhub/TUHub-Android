package edu.temple.tuhub;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.URL;

import edu.temple.tuhub.models.ImageItem;
import edu.temple.tuhub.models.MarketImageItem;

/**
 * Created by mangaramu on 4/2/2017.
 */

public class MarketImageloadThread extends Thread {
    MarketImageItem Imagei;
    Handler placetosend;

    MarketImageloadThread(MarketImageItem x, Handler y)
    {
        Imagei = x;
        placetosend = y;
    }

    @Override
    public void run() {


       /* try {
            Imagei.getItemref().setNewsimage(BitmapFactory.decodeStream(((URL)new URL(Imagei.getItemref().newsimagelink)).openStream())); //TODO fix this so that item can be obtained!
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/

        Message m = Message.obtain();
        m.obj=Imagei;
        m.setTarget(placetosend);
        m.sendToTarget();
    }
}
