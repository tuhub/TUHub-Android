package edu.temple.tuhub;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by mangaramu on 3/22/2017.
 */

public class FilterMenuFrag extends Fragment {

    ListView selectionitems;

    String[] newsfeeds = {//"feed1383143213597",feed has nothing // = “Temple News: Arts & Culture” //make a selector to be able to select diffrent feeds to show.
            // TODO -maybe save selections in saved prefrences
            "feed1383143223191", // = “Temple News: Athletics”
            "feed1383143236812", // = “Temple News: Campus News”
            "feed1383143243155", // = “Temple News: Community Engagement”
            "feed1383143253860", // = “Temple News: Global Temple”
            "feed1383143263909", // = “Temple News: Research”
            "feed1383143274415", // = “Temple News: Staff & Faculty”
            "feed1383143285101", // = “Temple News: Student Success”
            "feed1383143312318", // = “Temple News: Sustainability”
            "feed1383143507786" // = “Temple News: Temple 20/20”
    };
    String baselink = "https://prd-mobile.temple.edu/banner-mobileserver/rest/1.2/feed?namekeys=";

    String finallink = "https://prd-mobile.temple.edu/banner-mobileserver/rest/1.2/feed?namekeys=";

    String defaultlink = baselink+newsfeeds[4];

    String[] newswords = {
            //"Arts & Culture",
            "Athletics",
            "Campus News",
            "Community Engagement",
            "Global Temple",
            "Research",
            "Staff & Faculty",
            "Student Success",
            "Sustainability",
            "Temple 20/20"
    };

   static Boolean[] selected = {
            //false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
    };

   static Boolean[] tempoary = {
            //false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
    };




    CheckBox[] boxes = new CheckBox[9];
    RelativeLayout ok,greyarea;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // setRetainInstance(true);
        return inflater.inflate(R.layout.filltermenu,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ok = (RelativeLayout) getActivity().findViewById(R.id.utton);
        //greyarea = (RelativeLayout) getActivity().findViewById(R.id.greyarea);
        boxes[0]=(CheckBox) getActivity().findViewById(R.id.Athleticsbox);
        boxes[1]= (CheckBox) getActivity().findViewById(R.id.CampusNewsbox);
        boxes[2]= (CheckBox) getActivity().findViewById(R.id.CommunityEngagementbox);
        boxes[3]= (CheckBox) getActivity().findViewById(R.id.GlobalTemplebox);
        boxes[4]= (CheckBox) getActivity().findViewById(R.id.Researchbox);
        boxes[5]= (CheckBox) getActivity().findViewById(R.id.StaffandFacultybox);
        boxes[6]= (CheckBox) getActivity().findViewById(R.id.StudentSuccessbox);
        boxes[7]= (CheckBox) getActivity().findViewById(R.id.Sustainabilitybox);
        boxes[8]= (CheckBox) getActivity().findViewById(R.id.Temple2020box);


        for (int x = 0; x < newsfeeds.length; x++) {
            final int finalX = x;
            boxes[x].setOnCheckedChangeListener(null);
        }

        for (int x = 0; x < newsfeeds.length; x++) {
            if (selected[x]) {
                boxes[x].setChecked(true);
            } else {
                boxes[x].setChecked(false);
            }
        }

        for (int x = 0; x < newsfeeds.length; x++) {
            final int finalX = x;
            boxes[x].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//need to set this to null when changing the checked values.
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    {
                        tempoary[finalX] = true;
                    }
                    else
                    {
                        tempoary[finalX] = false;
                    }
                }
            });

        }

       ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               boolean first = true;

               for (int x = 0; x < selected.length; x++) {
                   selected[x]=tempoary[x];
                   if (first) {
                       if (selected[x] == true) {
                           finallink = finallink + newsfeeds[x];
                           first = false;
                       }
                   } else if (selected[x] == true) {
                       finallink = finallink + ',' + newsfeeds[x];
                   }
               }
               ((selectorinterface)getActivity()).newslink(finallink);
               finallink="https://prd-mobile.temple.edu/banner-mobileserver/rest/1.2/feed?namekeys=";
           }
       });
        /*greyarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean first = true;
                for (int x = 0; x < selected.length; x++) {
                    if (first) {
                        if (selected[x] == true) {
                            finallink = finallink + newsfeeds[x];
                            first = false;
                        }
                    } else if (selected[x] == true) {
                        finallink = finallink + ',' + newsfeeds[x];
                    }
                }
                ((selectorinterface)getActivity()).newslink(finallink);
                finallink="https://prd-mobile.temple.edu/banner-mobileserver/rest/1.2/feed?namekeys=";
            }
        });*/


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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        for (int y = 0; y < selected.length;y++)
        {
            tempoary[y]=selected[y];
        }

        for (int x = 0; x < newsfeeds.length; x++) {
            final int finalX = x;
            boxes[x].setOnCheckedChangeListener(null);
        }

        for (int x = 0; x < newsfeeds.length; x++) {
            if (selected[x]) {
                boxes[x].setChecked(true);
            } else {
                boxes[x].setChecked(false);
            }
        }
        super.onDetach();
    }

    public interface selectorinterface {
        public void newslink(String x);
    }
}
