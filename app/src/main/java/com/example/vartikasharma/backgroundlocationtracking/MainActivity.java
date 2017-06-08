package com.example.vartikasharma.backgroundlocationtracking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback,LocationListener,
        GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks{
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
            googleApiClient= new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API).build();
        }

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

    @OnClick(R.id.start_shift)
    public void startShift() {
        if (locationTracker.canGetLocation()) {
            Log.d(LOG_TAG, "latitute: " + locationTracker.getLatitude() + ",longitute: " + locationTracker.getLongitude());
        }
        start_date = new Date(System.currentTimeMillis());
        Log.d(LOG_TAG, "start current date, " + start_date);
    }

    @OnClick(R.id.stop_shift)
    public void stopShift() {
        if (locationTracker.canGetLocation()) {
            Log.d(LOG_TAG, "latitute: " + locationTracker.getLatitude() + ",longitute: " + locationTracker.getLongitude());
        }
        stop_date = new Date(System.currentTimeMillis());
        Log.d(LOG_TAG, "stop current date, " + stop_date);
        long startDate = start_date.getTime();
        long stopDate = stop_date.getTime();
        long totalTime = stopDate - startDate;
        Log.d(LOG_TAG, "duration, " + totalTime/60);
        totalShiftTime.setText(" " + totalTime);

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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onLocationChanged(Location location) {

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
