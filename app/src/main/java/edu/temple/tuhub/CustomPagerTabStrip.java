package edu.temple.tuhub;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;

// Created by Tom on 4/13/2017
public class CustomPagerTabStrip extends PagerTabStrip
{
    public CustomPagerTabStrip(Context context, AttributeSet attrsPagerTabStrip)
    {
        super(context, attrsPagerTabStrip);
        final TypedArray a = context.obtainStyledAttributes(attrsPagerTabStrip,
                R.styleable.CustomPagerTabStrip);
        setTabIndicatorColor(a.getColor(
                R.styleable.CustomPagerTabStrip_indicatorColor, getResources().getColor(R.color.colorPrimary)));
        a.recycle();
    }

}
