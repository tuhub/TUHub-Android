package edu.temple.tuhub.models;

import android.widget.ImageView;

/**
 * Created by mangaramu on 4/2/2017.
 */

public class MarketImageItem {
    Marketitem itemref;
    ImageView viewref;
    int osition;

    public MarketImageItem(ImageView x, Marketitem y, int z)
    {
        viewref=x;
        itemref=y;
        osition=z;
    }

    public int getOsition() {
        return osition;
    }

    public void setOsition(int osition) {
        this.osition = osition;
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
