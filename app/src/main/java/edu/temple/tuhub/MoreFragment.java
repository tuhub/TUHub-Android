package edu.temple.tuhub;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.temple.tuhub.models.User;

public class MoreFragment extends Fragment {

    String[] Links = {"https://tumail.temple.edu", "https://tuportal4.temple.edu", "http://esff.temple.edu/", "https://learn.temple.edu",
            "https://directory.temple.edu", "https://tapride-temple.herokuapp.com/ride", "https://temple.edu/diamonddollars", "https://temple.edu/studenthealth",
            "https://prd-mobile.temple.edu/campussafety", "https://computerservices.temple.edu/system-status",
            "https://galaxy.adminsvc.temple.edu/web/m.php?c=823","https://apps.temple.edu/tumobile/tudininghours/",
            "https://apps.temple.edu/TUmobile/TUdiningFeedback/", "https://apps.temple.edu/TUmobile/TUdiningIFeelLikeEating/"};
    String [] Names = {"TUmail", "TUportal", "ESFF", "Blackboard", "Cherry & White Directory", "Flight", "Diamond Dollars",
            "Student Health Services", "Campus Safety", "System Status", "FAQ", "Hours","Feedback", "What to Eat?"};

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_more, container, false);
        TextView tv = (TextView) v.findViewById(R.id.moreTUIDtext);
        if((User.CURRENT != null && !User.CURRENT.getTuID().equals(""))){
            tv.setText(User.CURRENT.getTuID());
        }
        else{
            tv.setText("No TUID found.");
        }
        ListView lv = (ListView) v.findViewById(R.id.moreListView);
        lv.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, Names) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = Links[i];
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        return v;
    }
}
