package edu.temple.tuhub.models;

import android.graphics.Bitmap;

/**
 * Created by mangaramu on 3/12/2017.
 */

public class Newsitem {
    public String newstitle, newssubtitle,newscontent,newsDate,newsimagelink, newsurl;
    public Bitmap newsimage;
    public Newsitem(String title, String subtitle, String imagelink, Bitmap image, String content, String Date , String url)
    {
        newstitle=title;
        newssubtitle=subtitle;
        newsimage=image;
        newscontent = content;
        newsDate = Date;
        newsimagelink = imagelink;
        newsurl=url;
    }
    public Newsitem() {
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    public void setNewstitle(String newstitle) {
        this.newstitle = newstitle;
    }

    public void setNewssubtitle(String newssubtitle) {
        this.newssubtitle = newssubtitle;
    }
    public void setNewsImageLink(String newsimagelink)
    {
        this.newsimagelink= newsimagelink;
    }

    public void setNewsimage(Bitmap newsimage) {
        this.newsimage = newsimage;
    }

    public void setNewscontent(String newscontent) {
        this.newscontent = newscontent;
    }

    public void setNewsurl(String newsurl) {
        this.newsurl = newsurl;
    }

    public String getNewstitle() {
        return newstitle;
    }

    public String getNewssubtitle() {
        return newssubtitle;
    }

    public String getNewscontent() {
        return newscontent;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public String getNewsimagelink() {
        return newsimagelink;
    }

    public String getNewsurl() {
        return newsurl;
    }

    public Bitmap getNewsimage() {
        return newsimage;
    }
}

