package edu.temple.tuhub;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidnetworking.error.ANError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.tuhub.models.Building;
import edu.temple.tuhub.models.FoodTruck;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {

    private GoogleMap googleMap;
    private MapView mMapView;
    private String currentCampus = "MN";
    private Building[] Buildings;
    private FoodTruck[] FoodTrucks;
    private Button detailBtn;
    private Marker currentMarker;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        detailBtn = (Button) v.findViewById(R.id.mapDetailsButton);
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDetails();
            }
        });
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my location button
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                    } else {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                1);

                    }
                } else {
                    //default map
                    // For dropping a marker at a point on the Map
                    LatLng templeUniversity = new LatLng(39.9794501, -75.1565292);
                    FoodTruck.retrieveFoodTrucks(new FoodTruck.FoodTruckRequestListener() {
                        @Override
                        public void onResponse(FoodTruck[] foodTrucks) {
                            FoodTrucks = new FoodTruck[foodTrucks.length];
                            for(int i = 0; i<foodTrucks.length; i++){
                                FoodTrucks[i] = foodTrucks[i];
                                if(foodTrucks[i]!=null) {
                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(foodTrucks[i].getLatitude()),Double.parseDouble(foodTrucks[i].getLongitude()))).title(foodTrucks[i].getName()));
                                }
                            }
                        }

                        @Override
                        public void onError(ANError error) {

                        }
                    });
                    // For zooming automatically to the location of the marker
                    Building.retrieveBuildings(currentCampus, new Building.BuildingRequestListener() {
                        @Override
                        public void onResponse(Building[] buildingResponse) {
                            Buildings = new Building[buildingResponse.length];
                            for(int i = 0; i<buildingResponse.length; i++){
                                Buildings[i] = buildingResponse[i];
                                if(buildingResponse[i]!=null){
                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(buildingResponse[i].getLatitude()),Double.parseDouble(buildingResponse[i].getLongitude()))).title(buildingResponse[i].getName()));
                                }
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                        }
                    });
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(templeUniversity).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.showInfoWindow();
                            currentMarker = marker;
                            if(detailBtn!=null)
                                detailBtn.setVisibility(View.VISIBLE);
                            return false;
                        }
                    });
                    googleMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                        @Override
                        public void onInfoWindowClose(Marker marker) {
                            currentMarker = null;
                            if(detailBtn!=null)
                                detailBtn.setVisibility(View.INVISIBLE);
                        }
                    });

                } }


        });


        return v;
    }
    public void onRequestPermissionsResult ( int requestCode, String[]
            permissions,int[] grantResults) {
    }
    private void loadDetails(){
        if(currentMarker!=null) {
            for (edu.temple.tuhub.models.Building Building : Buildings) {
                if (Building.getName().equals(currentMarker.getTitle())) {
                    activity.loadBuildingDetails(Building.getName(), Building.getImageUrl(), Building.getLatitude(), Building.getLongitude());
                }
            }
            for(edu.temple.tuhub.models.FoodTruck FoodTruck : FoodTrucks){
                if(FoodTruck.getName().equals(currentMarker.getTitle())){
                    //activity2
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(mMapView!=null)
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMapView!=null)
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMapView!=null)
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mMapView!=null)
        mMapView.onLowMemory();
    }


    loadBuildingDetails activity;
    loadFoodTruckDetails activity2;

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (loadBuildingDetails) c;
        activity2 = (loadFoodTruckDetails) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        activity2 = null;
    }

    public interface loadBuildingDetails{
        void loadBuildingDetails(String name, String imageUrl, String latitude, String longitude);
    }
    public interface loadFoodTruckDetails{
        //loadFoodTruckDetails
    }
}


