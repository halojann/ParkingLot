package com.example.user.parkinglot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean launch = true;//on app launch camera pans to current location
    private LocationManager locationManager;
    private LocationListener ll;
    private Location lcn = null;//holds last updated location
    int PLACE_PICKER_REQUEST = 1;//request id for manual place selection
    private static String radius = "1000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lcn = location;

                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);//current position indicated by a car icon
                MarkerOptions curr = new MarkerOptions();
                curr.position(loc);
                curr.icon(icon);

                if (launch) {//pan camera to location on launch
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                    launch = false;
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, ll);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(ll);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {//toolbar
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nearby://manually using place picker

                //place picker -  google maps android api
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d("place picker", "GooglePlayServicesRepairableException");
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException ex) {
                    Log.d("place picker", "GooglePlayServicesNotAvailableException");
                    ex.printStackTrace();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                String name = place.getName().toString();
                String lat = String.valueOf(place.getLatLng().latitude);
                String lon = String.valueOf(place.getLatLng().longitude);

                NotifySelection(name, lat, lon);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                if (!launch) {//only after initial pan to position
                    LatLng center = mMap.getCameraPosition().target;

                    String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" +
                            String.valueOf(center.latitude) + "," +
                            String.valueOf(center.longitude) +
                            "&radius=" + radius +
                            "&types=" + "parking" +
                            "&sensor=true" +
                            "&key=" + getResources().getString(R.string.google_maps_key);

                    Log.d("query URL: ", googlePlacesUrl);

                    //query
                    new getJSON().execute(googlePlacesUrl);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String lat = String.valueOf(marker.getPosition().latitude);
                String lon = String.valueOf(marker.getPosition().longitude);
                String name = String.valueOf(marker.getTitle());

                NotifySelection(name, lat, lon);
                return false;
            }
        });
    }

    private boolean NotifySelection(String lotname, String lat, String lon) {

        Log.d("Marker clicked :", lotname + "@" + lat + "," + lon);
        //todo send these to server
        return false;
    }

    private class getJSON extends AsyncTask<String, Void, JSONObject> {

        ProgressDialog pd;

        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    Log.d("Response: ", "> " + line);

                }
                return new JSONObject(buffer.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("IOException", "asynctask");
                e.printStackTrace();
            } catch (JSONException j) {
                Log.d("JSONexception", "asynctask");
                j.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            //if json is null
            if ((!result.has("result") && (!result.isNull("result"))))
                Log.d("JSON result", "Empty");
            else {//add markers to map
                try {
                    JSONArray res = result.getJSONArray("results");
                    if (!result.getString("status").equals("ZERO_RESULTS")) {

                        for (int i = 0; i < res.length(); i++) {

                            JSONObject pl = res.getJSONObject(i);

                            String lat = pl.getJSONObject("geometry").getJSONObject("location").getString("lat");
                            String lon = pl.getJSONObject("geometry").getJSONObject("location").getString("lng");
                            LatLng pos = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                            String name = pl.getString("name");

                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_nearby);

                            mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .icon(icon)
                                    .title(name));

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
