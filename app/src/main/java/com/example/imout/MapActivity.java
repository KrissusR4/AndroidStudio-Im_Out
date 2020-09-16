package com.example.imout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;


import android.support.annotation.NonNull;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.app.AlertDialog;
import android.widget.Spinner;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener,
        OnMarkerClickListener, OnMyLocationButtonClickListener, OnMyLocationClickListener,OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gMap;
    private EditText mEditTextSearch;
    private Button searchButton;
    public JSONObject receiveData;
    private Button buttonFilterts;
    public String mInputDisplayType = "";
    public String mInputDisplayMusic = "";

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = MapActivity.class.getSimpleName();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    private int count = -1;                 //Times search button clicked
    private int ID;



    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        buttonFilterts = findViewById(R.id.buttonFilters);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mapView = findViewById(R.id.map_view);

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        mEditTextSearch = findViewById(R.id.edit_Text_Search);
        searchButton = findViewById(R.id.buttonSearch);


        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getLocations(mInputDisplayType, mInputDisplayMusic, mEditTextSearch.getText().toString());
            }
        });

        buttonFilterts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Open Dialog");
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.filter_layout, null);
                mBuilder.setTitle("Chose prefered Filters");

                final Spinner spinnerObjectType = (Spinner) mView.findViewById(R.id.spinnerObjectType);
                final Spinner spinnerObjectMusic = (Spinner) mView.findViewById(R.id.spinnerObjectMusic);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapActivity.this,
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.arrayObjectType));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerObjectType.setAdapter(adapter);


                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(MapActivity.this,
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.arrayObjectMusic));
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerObjectMusic.setAdapter(adapter2);


                mBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                mBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!spinnerObjectType.getSelectedItem().toString().equalsIgnoreCase("Choose object type"))
                            mInputDisplayType = spinnerObjectType.getSelectedItem().toString();
                        else
                            mInputDisplayType = "";

                        if (!spinnerObjectMusic.getSelectedItem().toString().equalsIgnoreCase("Object music"))
                            mInputDisplayMusic = spinnerObjectMusic.getSelectedItem().toString();
                        else
                            mInputDisplayMusic = "";

                        dialog.dismiss();
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
        getLocations("", "", "");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMinZoomPreference(14.0f);
        gMap.setMaxZoomPreference(19.0f);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        gMap.setMyLocationEnabled(true);
        gMap.setOnMyLocationButtonClickListener(this);
        gMap.setOnMyLocationClickListener(this);
        gMap.setOnMarkerClickListener(this);
        gMap.setOnInfoWindowClickListener(this);

        LatLng Nis = new LatLng(43.316872, 21.894501);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(Nis));
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker m) {
        gMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
        m.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker m) {
        Intent intentObject = new Intent(MapActivity.this, ObjectActivity.class);
        Bundle b = new Bundle();
        b.putInt("ID", (Integer) m.getTag());//Your id za object
        intentObject.putExtras(b);
        startActivity(intentObject);
    }


    protected void getLocations(String t, String m, String n) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", n);
            postData.put("objectType", t);
            postData.put("objectMusic", m);
            Requests task = new Requests(new Requests.AsyncResponse() {
                @Override
                public void processFinish(String output) { //Sta da se uradi sa povratnim podacima
                    try {
                        receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");
                        gMap.clear();
                        if (message.equals("Success")) {
                            JSONArray locations = receiveData.getJSONArray("locations");
                            for (int i = 0; i < locations.length(); i++) {
                                JSONObject location = locations.getJSONObject(i);
                                Marker markersSearch[];
                                markersSearch = new Marker[locations.length()];

                                String ime = location.getString("Ime");
                                double x = location.getDouble("LokacijaX");
                                double y = location.getDouble("LokacijaY");
                                ID = location.getInt("idObjekta");

                                LatLng latLng = new LatLng(x, y);
                                Marker markerStart = gMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(ime));
                                markerStart.setVisible(true);
                                markerStart.setTag(ID);
                                markersSearch[i] = markerStart;

                                if(i == 0)
                                    gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                task.execute("locationMarkers.php", postData.toString());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch(Exception e){
        e.printStackTrace();
        }
    }
}