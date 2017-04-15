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

import com.androidnetworking.error.ANError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.tuhub.models.Building;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {

    private GoogleMap googleMap;
    private MapView mMapView;
    private String currentCampus = "MN";
    private Building[] Buildings;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
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
                    googleMap.addMarker(new MarkerOptions().position(templeUniversity).title("Temple University").snippet("Main Campus"));
                    // For zooming automatically to the location of the marker
                    Building.retrieveBuildings(currentCampus, new Building.BuildingRequestListener() {
                        @Override
                        public void onResponse(Building[] buildingResponse) {
                            System.out.println(buildingResponse[0].getName());
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
                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {

                                for(int i = 0; i<Buildings.length; i++){
                                    if(Buildings[i].getName().equals(marker.getTitle())){
                                        activity.loadBuildingDetails(Buildings[i].getName(),Buildings[i].getImageUrl(),Buildings[i].getLatitude(),Buildings[i].getLongitude());
                                    }
                                }


                        }
                    });
                }  }
        });

        return v;
    }
    public void onRequestPermissionsResult ( int requestCode, String[]
            permissions,int[] grantResults) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    loadBuildingDetails activity;

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (loadBuildingDetails) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public interface loadBuildingDetails{
        void loadBuildingDetails(String name, String imageUrl, String latitude, String longitude);
    }
}


