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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.temple.tuhub.models.ImageItem;
import edu.temple.tuhub.models.Newsitem;

// Created by mangaramu on 3/12/2017

class ArrayNewsAdapter extends ArrayAdapter<Newsitem> {
    private ListView par;

    private Bitmap noimage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_photo);
    private Handler setimage = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ImageItem x = (ImageItem) msg.obj;
            int firstpos;
            int lastpos;
            lastpos = par.getLastVisiblePosition();
            firstpos = par.getFirstVisiblePosition();
            if (x.getOsition() >= firstpos && x.getOsition() <= lastpos + 2) {
                x.getViewref().setImageBitmap(x.getItemref().newsimage);
            }
        }
    };

    ArrayNewsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Newsitem> objects) {
        super(context, resource, objects);
    }

    public ArrayNewsAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        par = (ListView) parent;
        Newsitem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.newsitem, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.newstitle);// to be able to set the text of these fields later on!
        TextView summary = (TextView) convertView.findViewById(R.id.newssummary);
        ImageView imgre = (ImageView) convertView.findViewById(R.id.newsimage);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        title.setText(item != null ? item.newstitle : null);
        summary.setText(item != null ? item.newssubtitle : null);
        date.setText(item != null ? item.newsDate : null);
        if (item.newsimage != null)//if the image has already been loaded, We can just set the image from here.
        {
            imgre.setImageBitmap(item.newsimage);
        } else {
            imgre.setImageBitmap(noimage);
            ImageLoadThread imthread = new ImageLoadThread(new ImageItem(imgre, item, position), setimage);
            imthread.start();
        }
        return convertView;
    }
}
