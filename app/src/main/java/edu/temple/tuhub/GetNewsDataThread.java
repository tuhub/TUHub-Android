package edu.temple.tuhub;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.temple.tuhub.models.Newsitem;

/**
 * Created by mangaramu on 3/19/2017.
 */

public class GetNewsDataThread extends Thread {
    networkClass net = new networkClass();
    Handler handle;
    JSONObject newsJSON;
    ArrayList<Newsitem> t=new ArrayList<>();

    GetNewsDataThread(Handler x, JSONObject y)
    {
     handle = x;
        newsJSON = y;
    }
    @Override
    public void run() { //TODO maybe have cancel/interrupt functionality to save cpu cycles?

        try {
            JSONArray entries;
            entries=(JSONArray)newsJSON.get("entries");
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat destFormat = new SimpleDateFormat("MMM d, yyyy hh:mm:ss a"); //here 'a' for AM/PM


            Date date = null;


            for(int x=0; x<entries.length();x++)
            {
                JSONObject tmp;
                Newsitem pow = new Newsitem();
                String tmptime;
                String formattedDate;
                String tmptitle;
                String tmpimageurl;
                String tmpcontent;
                String tmpsubtitle;
                String tmpnewsurl;

                String html1= "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<title>Some title</title>" +
                        "</head>"+ "<body>";
                String html2="</body></html>";

                tmp=(JSONObject)entries.get(x);
                tmptime = (String)tmp.get("postDate");

                try {
                    date = sourceFormat.parse(tmptime);
                }
                catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

               formattedDate = destFormat.format(date);
                pow.setNewsDate(formattedDate);//Setting the formated date
                tmptitle = (String)tmp.get("title");
                pow.setNewstitle(tmptitle);
                tmpnewsurl=(String)((JSONArray)tmp.get("link")).get(0);
                pow.setNewsurl(tmpnewsurl);
                tmpimageurl = (String) tmp.get("logo");
                pow.setNewsImageLink(tmpimageurl);
                tmpcontent = html1 + ((String) tmp.get("content")) + html2;
                tmpcontent=tmpcontent.replaceAll("u002f","");//replaces the weird u002f in some tags with nothing
                pow.setNewscontent(tmpcontent);
                tmpsubtitle = tmpcontent.substring(tmpcontent.indexOf(">",tmpcontent.indexOf("<p"))+1,tmpcontent.indexOf("</p>",tmpcontent.indexOf(">",tmpcontent.indexOf("<p"))));//tries to obtain substring composed of the subtitle within the html content code. Uses knowledge of how the html is structured.
                tmpsubtitle=tmpsubtitle.replaceAll("<.+?>","");//takes out all the brace tags using regular expressions http://www.regular-expressions.info/repeat.html
                if(tmpsubtitle.length()>120)
                {

                    pow.setNewssubtitle(tmpsubtitle.substring(0,119)+"...");
                }
                else
                {
                    pow.setNewssubtitle(tmpsubtitle);
                }

                t.add(pow);
                //if is interrupted break!
            }
            Message newsitems = Message.obtain();
            newsitems.obj=t;
            newsitems.setTarget(handle);
            newsitems.sendToTarget();

        } catch (JSONException e) {
            e.printStackTrace();
        }












    }

}
