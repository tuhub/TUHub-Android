package edu.temple.tuhub.models;

import android.widget.ImageView;

/**
 * Created by mangaramu on 3/20/2017.
 */

public class ImageItem {

    Newsitem itemref;
    ImageView viewref;

    public ImageItem(ImageView x, Newsitem y)
    {
        viewref=x;
        itemref=y;
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
