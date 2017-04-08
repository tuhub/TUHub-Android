package edu.temple.tuhub.models;

import android.widget.ImageView;

/**
 * Created by mangaramu on 4/2/2017.
 */

public class MarketImageItem {
    Marketitem itemref;
    ImageView viewref;

    public MarketImageItem(ImageView x, Marketitem y)
    {
        viewref=x;
        itemref=y;
    }

    public void setItemref(Marketitem itemref) {
        this.itemref = itemref;
    }

    public void setViewref(ImageView viewref) {
        this.viewref = viewref;
    }

    public Marketitem getItemref() {
        return itemref;
    }

    public ImageView getViewref() {
        return viewref;
    }
}
