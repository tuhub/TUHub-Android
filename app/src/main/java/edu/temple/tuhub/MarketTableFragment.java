package edu.temple.tuhub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import edu.temple.tuhub.models.Marketitem;
import edu.temple.tuhub.models.User;


/**
 * Created by mangaramu on 4/2/2017.
 */

public class MarketTableFragment extends Fragment {
    GridView marketgrid;
    GetMarketDataThread mark;//TODO needs to be changed
    ArrayList<Marketitem> Marketitems;
    TextView whichone;
    newListingInterface activity;

    int selected=0;
    //http://tuhubapi-env.us-east-1.elasticbeanstalk.com/select_all_products.jsp?activeOnly=true
    //http://tuhubapi-env.us-east-1.elasticbeanstalk.com/select_all_jobs.jsp?activeOnly=true
    //http://tuhubapi-env.us-east-1.elasticbeanstalk.com/select_all_personals.jsp?activeOnly=true
    String[] marketfeeds = {
            "/select_all_products.jsp?activeOnly=true", // = “Products”
            "/select_all_jobs.jsp?activeOnly=true", // = “Jobs”
            "/select_all_personals.jsp?activeOnly=true", // = “Personals”
            "/find_products_by_user_id.jsp?userId=" + User.CURRENT.getUsername(), // = “Products”
            "/find_jobs_by_user_id.jsp?userId=" +  User.CURRENT.getUsername(), // = “Jobs”
            "/find_personals_by_user_id.jsp?userId=" + User.CURRENT.getUsername() // = “Personals”
              };
    String[] marketsearchfeeds = {
            "/search_active_product_titles.jsp?title=", // = “Productssearch”
            "/search_active_job_titles.jsp?title=", // = “Jobssearch”
            "/search_active_personal_titles.jsp?title=", // = “Personalssearch”
            "/find_products_by_user_id.jsp?userId=" + User.CURRENT.getUsername(), // = “Products”
            "/find_jobs_by_user_id.jsp?userId=" +  User.CURRENT.getUsername(), // = “Jobs”
            "/find_personals_by_user_id.jsp?userId=" + User.CURRENT.getUsername() // = “Personals”
    };

    String baselink = "http://tuhubapi-env.us-east-1.elasticbeanstalk.com";

    String finallink ;

    String defaultlink = baselink+marketfeeds[0];

    networkClass net = new networkClass();

    //networkClass net = new networkClass();

    MarketAdapter arraymarket;
    public MarketTableFragment() {
        super();
    }

    Handler MarketDataHandle = new Handler(){ // gets an arraylist to give to the Arrayadapter that gets set to the listview.
        @Override
        public void handleMessage(Message msg) {
            Marketitems=(ArrayList<Marketitem>) msg.obj;

            if(getActivity()!=null) {
                arraymarket = new MarketAdapter(getActivity().getApplicationContext(), R.layout.marketperitem, Marketitems);
            }
            marketgrid.setAdapter(arraymarket);
        }
    };

    Handler RawMarket = new Handler(){
        @Override
        public void handleMessage(Message msg) {//gets the data supplied by the network classa to be parsed by the getmarketdata thread
            JSONObject marketJSON;
            try {
                marketJSON = new JSONObject(((networkClass.urlandstring)msg.obj).getHtml1());
                mark= new GetMarketDataThread(MarketDataHandle,marketJSON); //TODO need neew getting data thread
                mark.start();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (newListingInterface) c;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        marketgrid = (GridView) getActivity().findViewById(R.id.marketgrid);

        if(Marketitems==null  || Marketitems.size()==0 || (finallink!=null && !finallink.equals(defaultlink)))
        {
            loadmarket();
        }
        else
        {

        }

        setRetainInstance(true);
        return inflater.inflate(R.layout.marketplacefrag,container,false);
    }

//TODO have it so the data doesent reload .. unless to try and swipe down ... or you choose diffrent items from the selection menu!


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        marketgrid = (GridView) getActivity().findViewById(R.id.marketgrid);
        whichone = (TextView) getActivity().findViewById(R.id.Whichmarket);
        marketgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MarketTableFragment.marketshow)getActivity()).showmarket(Marketitems.get(position));

            }

        });



        if(Marketitems==null)
        {

        }
        else
        {
            arraymarket=new MarketAdapter(getActivity().getApplicationContext(),R.layout.marketperitem,Marketitems);
            marketgrid.setAdapter(arraymarket);
        }

        switch (selected) {
            case 0 :
                whichone.setText(R.string.Products);
                break;
            case 1 :
                whichone.setText(R.string.Jobs);
                break;
            case 2 :
                whichone.setText(R.string.Personals);
                break;
            case 3 :
                whichone.setText(R.string.userProducts);
                break;
            case 4 :
                whichone.setText(R.string.userJobs);
                break;
            case 5 :
                whichone.setText(R.string.userPersonals);
                break;

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
        activity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void loadmarket(){ //TODO needs fixing
        if (finallink==null)
        {
            net.clickload(defaultlink,RawMarket);
        }
        else {
            net.clickload(finallink, RawMarket);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.marketmenu,menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.marketsearch).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {// hopefully searches just based on text obtained form the searchview
                String [] tmp;
                finallink=baselink+marketsearchfeeds[selected];
                tmp=s.split(" ");
                for(int x=0; x<tmp.length; x++)
                {
                    if(x>0 && !tmp[x].equals(""))
                    {
                        finallink= finallink + "%20" + tmp[x];
                    }
                    else if (tmp[x].equals(""))
                    {

                    }
                    else if (x==0)
                    {
                        finallink= finallink + tmp[x];
                    }
                }
                loadmarket();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//is used do an action if the button we want exists.
        switch (item.getItemId()) {
            case R.id.NewListing:
                String[] ListingType = {"Product", "Job", "Personal"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select New Listing Type")
                        .setItems(ListingType, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                activity.newListing(which);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            case R.id.Productbutt:
                finallink = baselink;
                finallink= finallink+marketfeeds[0];
                selected=0;
                loadmarket();
                whichone.setText(R.string.Products);
                return true;
            case R.id.Jobbutt:
                finallink = baselink;
                finallink= finallink+marketfeeds[1];
                selected=1;
                loadmarket();
                whichone.setText(R.string.Jobs);
                return true;
            case R.id.Personalsbutt:
                finallink = baselink;
                finallink=finallink+marketfeeds[2];
                selected=2;
                loadmarket();
                whichone.setText(R.string.Personals);
                return true;
            case R.id.UsersProductsButt:
                finallink = baselink;
                finallink=finallink+marketfeeds[3];
                selected=3;
                loadmarket();
                whichone.setText(R.string.userProducts);
                return true;
            case R.id.UsersJobsButt:
                finallink = baselink;
                finallink=finallink+marketfeeds[4];
                selected=4;
                loadmarket();
                whichone.setText(R.string.userJobs);
                return true;
            case R.id.UsersPersonalsButt:
                finallink = baselink;
                finallink=finallink+marketfeeds[5];
                selected=5;
                loadmarket();
                whichone.setText(R.string.userPersonals);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] ListingType = {"Product", "Job", "Personal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select New Listing Type")
                .setItems(ListingType, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }

    public interface marketshow
    {
        public void showmarket(Marketitem t);// for possibly showing the market item

    }

    public interface newListingInterface{
        public void newListing(int i);
    }



}
