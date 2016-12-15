package com.algonquinlive.lu000094.doorsopenottawa;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import com.algonquinlive.lu000094.doorsopenottawa.model.Building;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class DetailActivity extends Activity implements OnMapReadyCallback{

    private Building building;
    private String fullAddress;
    MapView mapView;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        building = (Building) getIntent().getSerializableExtra(MainActivity.BUILDING_EXTRA);
        TextView tv = (TextView) findViewById(R.id.textViewName);
        tv.setText(building.getName());
        tv = (TextView) findViewById(R.id.textViewDescription);
        tv.setText(building.getDescription());
        tv = (TextView) findViewById(R.id.textViewOpenHours);
        StringBuilder sb = new StringBuilder();
        sb.append("Open hours:\n");
        for (int i = 0; i < building.getOpenHours().size(); i++) {
            sb.append(" - " + building.getOpenHours().get(i) + "\n");
        }
        tv.setText(sb.toString());

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        fullAddress = building.getAddress() + " ottawa";
    }

    public void getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size() <= 0) {
                return;
            }
            Address location = address.get(0);

            p1 = new LatLng((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));
            // Show the current location in Google Map
            map.addMarker(new MarkerOptions().position(p1).title(fullAddress));
            map.moveCamera(CameraUpdateFactory.newLatLng(p1));
            // Zoom in the Google Map
            map.animateCamera(CameraUpdateFactory.zoomTo(17));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        this.map = map;
        getLocationFromAddress(fullAddress);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public final void onDestroy()
    {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public final void onLowMemory()
    {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public final void onPause()
    {
        mapView.onPause();
        super.onPause();
    }
}
