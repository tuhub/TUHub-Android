package edu.temple.tuhub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.temple.tuhub.models.MarketImageItem;
import edu.temple.tuhub.models.Marketitem;

/**
 * Created by mangaramu on 4/2/2017.
 */

public class MarketAdapter extends ArrayAdapter<Marketitem> {

Bitmap noimage = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.no_photo);
    Handler setimage = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            MarketImageItem x = (MarketImageItem) msg.obj;
            if(x.getItemref().Firstmarketimagescaled ==null)
            {
                x.getItemref().Firstmarketimagescaled =noimage;
                x.getViewref().setImageBitmap(noimage);
            }
            else {
                x.getViewref().setImageBitmap(x.getItemref().Firstmarketimagescaled); // TODo need to fix
            }

        }
    };

    public MarketAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Marketitem> objects) {
        super(context, resource, objects);
    }

    public MarketAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Marketitem item = getItem(position);

        if (convertView ==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.marketperitem,parent,false);

        }

        TextView title = (TextView) convertView.findViewById(R.id.markettitle);// to be able to set the text of these fields later on!
        ImageView imgre = (ImageView) convertView.findViewById(R.id.marketimage);
        TextView date = (TextView) convertView.findViewById(R.id.marketdate) ;
        TextView price = (TextView) convertView.findViewById(R.id.marketprice) ;

         if (item.getMarkettype().equals("Product"))
        {
            title.setText(item.getMarkettitle());
            date.setText(item.getDateposted());
            price.setVisibility(View.VISIBLE);
            price.setText(getContext().getText(R.string.price) + ": " +item.getPrice());
        }

         else if(item.getMarkettype().equals("Job"))
         {
             title.setText(item.getMarkettitle());
             date.setText(item.getDateposted());
             if(item.getPay()!=null) {
                 price.setVisibility(View.VISIBLE);
                 price.setText(getContext().getText(R.string.hourlyrate) + ": " + item.getPay());
             }
             else // if there is no pay information hide the area to put the pay information
             {
                 price.setVisibility(View.GONE);
             }

         }

        else if(item.getMarkettype().equals("Personal"))
        {

            title.setText(item.getMarkettitle());
            date.setText(item.getDateposted());
            price.setVisibility(View.GONE);
        }





         if (item.Firstmarketimagescaled !=null )//if the image has already been loaded, We can just set the image from here.
        {
            imgre.setImageBitmap(item.Firstmarketimagescaled);
        }
        else
        {
            imgre.setImageBitmap(noimage);
            MarketImageloadThread imthread = new MarketImageloadThread(new MarketImageItem(imgre,item),setimage);
            imthread.start();

        }



        //the async non-ui thread based scheme for loading the image from the url link



        return convertView;
    }
}
