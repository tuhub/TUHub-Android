package edu.temple.tuhub;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

;import edu.temple.tuhub.models.Newsitem;

/**
 * Created by mangaramu on 3/8/2017.
 */

public class NewsTableFragment extends Fragment {
    ListView newslist;
    Context appcontext;
    GetNewsDataThread news;
    ArrayList<Newsitem> newsitems;
    boolean buttonstate=true;

    String[] newsfeeds = {"feed1383143213597", // = “Temple News: Arts & Culture” //make a selector to be able to select diffrent feeds to show.
            // TODO -maybe save selections in saved prefrences
            "feed1383143223191", // = “Temple News: Athletics”
            "feed1383143236812", // = “Temple News: Campus News”
            "feed1383143243155", // = “Temple News: Community Engagement”
            "feed1383143253860", // = “Temple News: Global Temple”
            "feed1383143263909", // = “Temple News: Research”
            "feed1383143274415", // = “Temple News: Staff & Faculty”
            "feed1383143285101", // = “Temple News: Student Success”
            "feed1383143312318", // = “Temple News: Sustainability”
            "feed1383143507786", // = “Temple News: Temple 20/20”

    };

    String baselink = "https://prd-mobile.temple.edu/banner-mobileserver/rest/1.2/feed?namekeys=";
    String feedslinks = newsfeeds[4];
    String finallink;
    String defaultlink=baselink+feedslinks;
    networkClass net = new networkClass();

    ArrayNewsAdapter arraynews;
    public NewsTableFragment() {
        super();
    }

    Handler NewsDataHandle = new Handler(){ // gets an arraylist to give to the Arrayadapter that gets set to the listview.
        @Override
        public void handleMessage(Message msg) {
            newsitems=(ArrayList<Newsitem>) msg.obj;
            arraynews=new ArrayNewsAdapter(appcontext,R.layout.newsitem,newsitems);
            newslist.setAdapter(arraynews);
        }
    };

    Handler RawNews = new Handler(){
        @Override
        public void handleMessage(Message msg) {//gets the data supplied by the network classa to be parsed by the getnewsdata thread
            JSONObject newsJSON;
            try {
                newsJSON = new JSONObject(((networkClass.urlandstring)msg.obj).getHtml1());
                news= new GetNewsDataThread(NewsDataHandle,newsJSON);
                news.start();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onAttach(Context context) {
        appcontext=context;
        super.onAttach(context);
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        newslist = (ListView) getActivity().findViewById(R.id.newslist);

        if(newsitems==null  || (finallink!=null && !finallink.equals(baselink)))
        {
            loadnews();
        }
        else
        {

        }

        setRetainInstance(true);
        return inflater.inflate(R.layout.newstable,container,false);
    }

//TODO have it so the data doesent reload .. unless to try and swipe down ... or you choose diffrent items from the selection menu!
    //TODO make an actionbar that has a dropdown with selectors of diffrent items based on the newsfeeds array.

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        newslist= (ListView) getActivity().findViewById(R.id.newslist);
        newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((newsshow)getActivity()).shownews(newsitems.get(position));

            }

    });



        if(newsitems==null)
        {

        }
        else
        {
            arraynews=new ArrayNewsAdapter(appcontext,R.layout.newsitem,newsitems);
            newslist.setAdapter(arraynews);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void loadnews()//does not take in any information. Checks if finallink is null. If it is then we default to default link, else we load the finallink.
    {
        if (finallink==null)
        {
            net.clickload(defaultlink,RawNews);
        }
        else {
            net.clickload(finallink, RawNews);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.filterbutton,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//is used do an action if the button we want exists.
        switch (item.getItemId()) {
            case R.id.filterbutton:
                ((filterbutton)getActivity()).filterbuttonpresseed();
                changefilterbuttonstate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface newsshow
    {
        public void shownews(Newsitem t);

    }
    public interface filterbutton
    {
        public void filterbuttonpresseed();
    }

    public void changefilterbuttonstate()
    {
        if (getActivity().findViewById(R.id.filterbutton).isEnabled()) {
            getActivity().findViewById(R.id.filterbutton).setEnabled(false);
            buttonstate=false;
        }
        else
        {
            getActivity().findViewById(R.id.filterbutton).setEnabled(true);
            buttonstate=true;
        }
    }
}
