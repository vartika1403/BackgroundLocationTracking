package com.example.vartikasharma.backgroundlocationtracking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    // Google Map
    private GoogleMap googleMap;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    //instance of LocationManager
    protected LocationTracker locationTracker;

    //Connection detector class
    protected ConnectionDetector connectionDetector;

    //flag for internet connection status
    boolean isInternetPresent = false;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private Location startLocation;
    private LatLng destinationLatLng;
    private LocationRequest locationRequest;

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 100;
    private List<LatLng> latLngList;
    private MarkerOptions markerOption;
    private double latitudeValue = 0.0;
    private double longitudeValue = 0.0;

    private Date start_date;
    private Date stop_date;
    @BindView(R.id.start_shift)
    public Button startShift;
    @BindView(R.id.stop_shift)
    public Button stopShift;
    @BindView(R.id.total_shift_time_value)
    public TextView totalShiftTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        connectionDetector = new ConnectionDetector(this);

        // check is internet present
        isInternetPresent = connectionDetector.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API).build();
        }

        locationRequest = createLocationRequest();

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        locationTracker = new LocationTracker(this);

        //check if GPS Location can get
        if (locationTracker.canGetLocation()) {
            Log.d(LOG_TAG, "latitute: " + locationTracker.getLatitude() + ",longitute: " + locationTracker.getLongitude());
        }


    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @OnClick(R.id.start_shift)
    public void startShift() {
        if (locationTracker.canGetLocation()) {
            Log.d(LOG_TAG, "latitute: " + locationTracker.getLatitude() + ",longitute: " + locationTracker.getLongitude());
        }
        start_date = new Date(System.currentTimeMillis());
        Log.d(LOG_TAG, "start current date, " + start_date);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        startLocation = lastLocation;
        Log.d(LOG_TAG, "startlastLocation," + lastLocation);
        assignLocationValues(lastLocation, "StartLocation");
        setDefaultMarkerOption(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
       // setDefaultMarkerOption(new LatLng(12.9589, 77.6492));
    }

    @OnClick(R.id.stop_shift)
    public void stopShift() {
        if (locationTracker.canGetLocation()) {
            Log.d(LOG_TAG, "latitute: " + locationTracker.getLatitude() + ",longitute: " + locationTracker.getLongitude());
        }
//        stop_date = new Date(System.currentTimeMillis());
        //      Log.d(LOG_TAG, "stop current date, " + stop_date);
//        long startDate = start_date.getTime();
        // long stopDate = stop_date.getTime();
        //  long totalTime = stopDate - startDate;
        // Log.d(LOG_TAG, "duration, " + totalTime/60);
        //totalShiftTime.setText(" " + totalTime);
        // setDefaultMarkerOption(new LatLng(locationTracker.getLatitude(), locationTracker.getLongitude()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        Log.i(LOG_TAG, "lastLocation, "+ lastLocation);
        Log.i(LOG_TAG,"lastLocationLatitute, " + lastLocation.getLatitude() +" "+ lastLocation.getLongitude());
        totalShiftTime.setText(" "+ lastLocation.getLatitude());
        assignLocationValues(lastLocation, "Destination");
        setDefaultMarkerOption(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));

        getDestinationLatLong(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
        //getDestinationLatLong(latLng);
    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }
    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if (status != null)
            // Setting alert dialog icon
            //  alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(LOG_TAG, "Connection method has been called");
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), 
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                            Log.d(LOG_TAG, "lastLocation," + lastLocation);
                            assignLocationValues(lastLocation, "StartLocation");
                            setDefaultMarkerOption(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                        }else{
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void setDefaultMarkerOption(LatLng latLng) {
        if(markerOption == null){
            Log.d(LOG_TAG, "default marker location, " + latLng);
            markerOption = new MarkerOptions();
        }
        Log.d(LOG_TAG, "default marker location, "+ latLng);
        markerOption.position(latLng);
    }

    private void assignLocationValues(Location currentLocation, String locationTitle) {
        if ( currentLocation != null) {
            latitudeValue = currentLocation.getLatitude();
            longitudeValue = currentLocation.getLongitude();
            Log.d(LOG_TAG, "Latitude: " + latitudeValue + " Longitude: " + longitudeValue);
            markStartingLocationOnMap(googleMap, new LatLng(latitudeValue, longitudeValue), locationTitle);
            addCameraToMap(new LatLng(latitudeValue, longitudeValue));
        }
    }

    private void markStartingLocationOnMap(GoogleMap googleMap, LatLng latLng, String locationTitle) {
        googleMap.addMarker(new MarkerOptions().position(latLng).title(locationTitle));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void addCameraToMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (latLngList.size() > 0) {
            refreshMap(googleMap);
            latLngList.clear();
        }
        latLngList.add(latLng);
        Log.d(LOG_TAG, "Marker number " + latLngList.size());
        getDestinationLatLong(latLng);
        googleMap.addMarker(markerOption);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        LatLng defaultLocation = markerOption.getPosition();
        destinationLatLng = latLng;
      /*  //use Google Direction API to get the route between these Locations
        String directionApiPath = Helper.getUrl(String.valueOf(defaultLocation.latitude), String.valueOf(defaultLocation.longitude),
                String.valueOf(destinationLatLng.latitude), String.valueOf(destinationLatLng.longitude));
        Log.d(LOG_TAG, "Path " + directionApiPath);
        getDirectionFromDirectionApiServer(directionApiPath);*/
    }

    private void getDestinationLatLong(LatLng latLng) {
        googleMap.addMarker(markerOption);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        LatLng defaultLocation = markerOption.getPosition();
        //LatLng startLocation = new LatLng(12.953952, 77.648277);
        Log.i(LOG_TAG, "defaultLocation, " + defaultLocation.latitude + " ," + defaultLocation.longitude);
        destinationLatLng = new LatLng(12.9655, 77.6418);
        markStartingLocationOnMap(googleMap, destinationLatLng, "Destination");
        //use Google Direction API to get the route between these Locations
        String directionApiPath = Helper.getUrl(String.valueOf(startLocation.getLatitude()), String.valueOf(startLocation.getLongitude()),
                String.valueOf(destinationLatLng.latitude), String.valueOf(destinationLatLng.longitude));
        Log.d(LOG_TAG, "Path " + directionApiPath);
        getDirectionFromDirectionApiServer(directionApiPath);
    }

    private void getDirectionFromDirectionApiServer(String directionApiPath) {
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                directionApiPath,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(serverRequest);
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    private Response.Listener<DirectionObject> createRequestSuccessListener() {
        return new Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    if(response.getStatus().equals("OK")){
                        List<LatLng> directions = getDirectionPolylines(response.getRoutes());
                        drawRouteOnMap(googleMap, directions);
                    }else{
                        Toast.makeText(MainActivity.this, "error in server", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<LatLng>();
        for(RouteObject route : routes){
            List<LegsObject> legs = route.getLegs();
            for(LegsObject leg : legs){
                List<StepsObject> steps = leg.getSteps();
                for(StepsObject step : steps){
                    PolyLineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline){
                        directionList.add(direction);
                    }
                }
            }
        }
        return directionList;
    }

    private List<LatLng> decodePoly(String points) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = points.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = points.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = points.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                .zoom(17)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void refreshMap(GoogleMap googleMap) {
        googleMap.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(LOG_TAG,"totalShiftTime, " + location.getLatitude() +" "+ location.getLongitude());
        totalShiftTime.setText(" "+ location.getLatitude());
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
}
