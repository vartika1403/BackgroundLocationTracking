package com.example.vartikasharma.backgroundlocationtracking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
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

    @BindView(R.id.start_shift)
    public Button startShift;
    @BindView(R.id.stop_shift)
    public Button stopShift;

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
}
