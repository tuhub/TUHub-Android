package edu.temple.tuhub.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by mangaramu on 4/2/2017.
 */

public class Marketitem { // should have everything I can obtain about the market
    //should discuss idea about having api call that allows for partial loading of database items.
    public String placeholderstring;
    public Bitmap Firstmarketimagescaled;
    public ArrayList<String> marketimagelinks = new ArrayList<>();
    public String price;
    public String pay;
    public String ownerid;
    public String dateposted;
    public String markettitle;
    public String description;
    public String markettype;
    public String location;
    public String picfolder;
    public String startdate;
    private String id;
    private String hoursPerWeek;
    private String isActive;

    public String getId() {
        return id;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(String hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPicfolder() {
        return picfolder;
    }

    public void setPicfolder(String picfolder) {
        this.picfolder = picfolder;
    }

    public String getPlaceholderstring() {
        return placeholderstring;
    }

    public void setPlaceholderstring(String placeholderstring) {
        this.placeholderstring = placeholderstring;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getDateposted() {
        return dateposted;
    }

    public void setDateposted(String dateposted) {
        this.dateposted = dateposted;
    }

    public String getMarkettitle() {
        return markettitle;
    }

    public void setMarkettitle(String markettitle) {
        this.markettitle = markettitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMarkettype() {
        return markettype;
    }

    public void setMarkettype(String markettype) {
        this.markettype = markettype;
    }


}
