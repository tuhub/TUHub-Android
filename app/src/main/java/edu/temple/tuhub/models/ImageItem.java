package edu.temple.tuhub.models;

import android.widget.ImageView;

/**
 * Created by mangaramu on 3/20/2017.
 */

public class ImageItem {

    Newsitem itemref;
    ImageView viewref;
    int osition;

    public ImageItem(ImageView x, Newsitem y, int z)
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

    public void setItemref(Newsitem itemref) {
        this.itemref = itemref;
    }

    public void setViewref(ImageView viewref) {
        this.viewref = viewref;
    }

    public Newsitem getItemref() {
        return itemref;
    }

    public ImageView getViewref() {
        return viewref;
    }
}
