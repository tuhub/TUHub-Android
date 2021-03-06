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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.temple.tuhub.models.MarketImageItem;
import edu.temple.tuhub.models.Marketitem;

// Created by mangaramu on 4/2/2017

class MarketAdapter extends ArrayAdapter<Marketitem> {

    private GridView par;
    private String currentList;
    private EditItemListener listener;

private Bitmap noimage = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.no_photo);

    private Handler setimage = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            MarketImageItem x = (MarketImageItem) msg.obj;
            int firstpos;
            int lastpos;
            lastpos=par.getLastVisiblePosition();
            firstpos= par.getFirstVisiblePosition();
            if(x.getOsition()>=firstpos && x.getOsition()<=lastpos+2) {
                if(x.getItemref().Firstmarketimagescaled ==null)
                {
                    x.getItemref().Firstmarketimagescaled =noimage;
                    x.getViewref().setImageBitmap(noimage);
                }
                else {
                    x.getViewref().setImageBitmap(x.getItemref().Firstmarketimagescaled); // TODo need to fix
                }
            }
        }
    };

     MarketAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Marketitem> objects, String currentList, EditItemListener listener) {
        super(context, resource, objects);
        this.currentList = currentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        par=(GridView)parent;
        final Marketitem item = getItem(position);

        if (convertView ==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.marketperitem,parent,false);

        }

        TextView title = (TextView) convertView.findViewById(R.id.markettitle);// to be able to set the text of these fields later on!
        ImageView imgre = (ImageView) convertView.findViewById(R.id.marketimage);
        TextView price = (TextView) convertView.findViewById(R.id.marketprice) ;
        assert item != null;
        switch (item.getMarkettype()) {
            case "Product":
                title.setText(item.getMarkettitle());
                price.setVisibility(View.VISIBLE);
                price.setText(getContext().getText(R.string.price) + ": " + item.getPrice());
                break;
            case "Job":
                title.setText(item.getMarkettitle());
                if (item.getPay() != null) {
                    price.setVisibility(View.VISIBLE);
                    price.setText(getContext().getText(R.string.hourlyrate) + ": " + item.getPay());
                } else // if there is no pay information hide the area to put the pay information
                {
                    price.setVisibility(View.GONE);
                }
                break;
            case "Personal":
                title.setText(item.getMarkettitle());
                price.setVisibility(View.GONE);
                break;
        }


         if (item.Firstmarketimagescaled !=null )//if the image has already been loaded, We can just set the image from here.
        {
            imgre.setImageBitmap(item.Firstmarketimagescaled);
        }
        else
        {
            imgre.setImageBitmap(noimage);
            MarketImageloadThread imthread = new MarketImageloadThread(new MarketImageItem(imgre,item,position),setimage);
            imthread.start();

        }

        if(currentList.equals(MarketTableFragment.MY_JOBS) || currentList.equals(MarketTableFragment.MY_PERSONALS)
                || currentList.equals(MarketTableFragment.MY_PRODUCTS)){
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.editItem(item);
                }
            });

        } else convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.showListingDetails(item);
            }
        });



        //the async non-ui thread based scheme for loading the image from the url link



        return convertView;
    }

    interface EditItemListener{
        void editItem(Marketitem item);
        void showListingDetails(Marketitem item);
    }
}
