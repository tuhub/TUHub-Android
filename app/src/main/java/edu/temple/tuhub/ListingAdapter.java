package edu.temple.tuhub;

import android.content.Context;
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
 * Created by rob s on 4/23/2017.
 */

public class ListingAdapter extends ArrayAdapter<Marketitem> {


//    Handler setimage = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg) {
//            MarketImageItem x = (MarketImageItem) msg.obj;
//            x.getViewref().setImageBitmap(x.getItemref().getMarketimage()); // TODo need to fix
//
//        }
//    };

    public ListingAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Marketitem> objects) {
        super(context, resource, objects);
    }

    public ListingAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Marketitem item = getItem(position);

        if (convertView ==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_listing_details,parent,false);

        }

        TextView title = (TextView) convertView.findViewById(R.id.title_view);// to be able to set the text of these fields later on!
        ImageView img = (ImageView) convertView.findViewById(R.id.image_view);
        TextView date = (TextView) convertView.findViewById(R.id.start_input);
        TextView price = (TextView) convertView.findViewById(R.id.price_input);
        TextView description = (TextView) convertView.findViewById(R.id.description_input);
        TextView location = (TextView) convertView.findViewById(R.id.location_input);
        TextView hours = (TextView) convertView.findViewById(R.id.hours_input);
        TextView pay = (TextView) convertView.findViewById(R.id.pay_input);
        TextView seller = (TextView) convertView.findViewById(R.id.seller_input);

         if (item.getMarkettype().equals("Product"))
        {
            title.setText(item.getMarkettitle());
            description.setText(item.getDescription());
            price.setVisibility(View.VISIBLE);
            price.setText(item.getPrice());
            seller.setVisibility(View.VISIBLE);
            seller.setText(item.getOwnerid());
            location.setVisibility(View.GONE);
            hours.setVisibility(View.GONE);
            pay.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
        }

         else if(item.getMarkettype().equals("Job"))
         {
             //however to do img's for each one
//             if(item.getMarketimage()!=null) {
//                 img.setVisibility(View.VISIBLE);
//                 img.setImageResource(item.getMarketimage());
//             }
//             else // if there is no pay information hide the area to put the pay information
//             {
//                 price.setVisibility(View.GONE);
//             }

             title.setText(item.getMarkettitle());
             date.setVisibility(View.VISIBLE);
             date.setText(item.getStartdate());
             if(item.getPay()!=null) {
                 price.setVisibility(View.VISIBLE);
                 price.setText(getContext().getText(R.string.hourlyrate) + ": " + item.getPay());
             }
             else // if there is no pay information hide the area to put the pay information
             {
                 price.setVisibility(View.GONE);
             }
             location.setText(item.getLocation());
             hours.setText(item.getHoursPerWeek());
             description.setText(item.getDescription());
         }

        else if(item.getMarkettype().equals("Personal"))
        {
            title.setText(item.getMarkettitle());
            date.setText(item.getDateposted());
            location.setText(item.getLocation());
            description.setText(item.getDescription());
            price.setVisibility(View.GONE);
            hours.setVisibility(View.GONE);
            seller.setVisibility(View.GONE);
            pay.setVisibility(View.GONE);
        }

        return convertView;
    }
}
