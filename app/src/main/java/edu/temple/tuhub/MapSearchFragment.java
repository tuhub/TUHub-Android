package edu.temple.tuhub;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.temple.tuhub.models.Building;
import edu.temple.tuhub.models.FoodTruck;

public class MapSearchFragment extends Fragment {

    public MapSearchFragment() {
    }

    ListView lv;
    Building[] buildings;
    FoodTruck[] foodTrucks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            buildings = (Building[]) bundle.getSerializable("Buildings");
            foodTrucks = (FoodTruck[]) bundle.getSerializable("FoodTrucks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_search, container, false);
        lv = (ListView) v.findViewById(R.id.mapSearchListView);
        List<Map<String, String>> data = new ArrayList<>();
        for (Building building : buildings) {
            Map<String, String> datum = new HashMap<>(2);
            datum.put("First Line", building.getName());
            datum.put("Second Line", "Building");
            data.add(datum);
            SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), data, android.R.layout.simple_list_item_2,
                    new String[]{"First Line", "Second Line"},
                    new int[]{android.R.id.text1, android.R.id.text2}) {

                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    text1.setTextColor(Color.BLACK);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    text2.setTextColor(Color.DKGRAY);
                    return view;
                }
            };
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (position > buildings.length - 1) {
                        activity2.loadFoodTruckDetails(foodTrucks[(position - buildings.length)].getName(),
                                foodTrucks[(position - buildings.length)].getRating(),
                                foodTrucks[(position - buildings.length)].getIsClosed(),
                                foodTrucks[(position - buildings.length)].getLatitude(),
                                foodTrucks[(position - buildings.length)].getLongitude(),
                                foodTrucks[(position - buildings.length)].getImageURL(),
                                foodTrucks[(position - buildings.length)].getPhone());
                    } else {
                        activity.loadBuildingDetails(buildings[position].getName(),
                                buildings[position].getImageUrl(),
                                buildings[position].getLatitude(),
                                buildings[position].getLongitude());
                    }
                }
            });
            adapter.notifyDataSetChanged();
        }
        for (FoodTruck foodTruck : foodTrucks) {
            Map<String, String> datum = new HashMap<>(2);
            datum.put("First Line", foodTruck.getName());
            datum.put("Second Line", "FoodTruck");
            data.add(datum);
            SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), data, android.R.layout.simple_list_item_2,
                    new String[]{"First Line", "Second Line"},
                    new int[]{android.R.id.text1, android.R.id.text2}) {

                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    text1.setTextColor(Color.BLACK);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    text2.setTextColor(Color.DKGRAY);
                    return view;
                }
            };
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (position > buildings.length - 1) {
                        activity2.loadFoodTruckDetails(foodTrucks[(position - buildings.length)].getName(),
                                foodTrucks[(position - buildings.length)].getRating(),
                                foodTrucks[(position - buildings.length)].getIsClosed(),
                                foodTrucks[(position - buildings.length)].getLatitude(),
                                foodTrucks[(position - buildings.length)].getLongitude(),
                                foodTrucks[(position - buildings.length)].getImageURL(),
                                foodTrucks[(position - buildings.length)].getPhone());
                    } else {
                        activity.loadBuildingDetails(buildings[position].getName(),
                                buildings[position].getImageUrl(),
                                buildings[position].getLatitude(),
                                buildings[position].getLongitude());
                    }
                }
            });
            adapter.notifyDataSetChanged();
        }
        return v;
    }

    MapsFragment.loadBuildingDetailsInterface activity;
    MapsFragment.loadFoodTruckDetailsInterface activity2;

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (MapsFragment.loadBuildingDetailsInterface) c;
        activity2 = (MapsFragment.loadFoodTruckDetailsInterface) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        activity2 = null;
    }
}
