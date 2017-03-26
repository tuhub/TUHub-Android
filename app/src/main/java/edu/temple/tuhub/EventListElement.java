package edu.temple.tuhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ben on 3/26/2017.
 */

public class EventListElement extends LinearLayout {

    @BindView(R.id.event_text)
    TextView eventText;


    public EventListElement(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rootView =  inflater.inflate(R.layout.event_list_element, this);

        ButterKnife.bind(this, rootView);
    }

    public void setEventText(String text){
        eventText.setText(text);
    }
}
