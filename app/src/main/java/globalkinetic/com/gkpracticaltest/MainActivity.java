package globalkinetic.com.gkpracticaltest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import globalkinetic.com.gkpracticaltest.databinding.ActivityMainBinding;
import interfacelisteners.RefreshEventListener;
import model.Weather;
import util.ConnectionManager;
import viewmodel.WeatherViewModel;

public class MainActivity extends AppCompatActivity implements LocationListener, RefreshEventListener{

    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final String API_KEY = "53f9d8e4213222cf517d86dc406d67fc";

    private int latitude = 0;
    private int longitude =  0;
    private LocationManager locationManager;
    Location location = null;
    private String provider;

    private ActivityMainBinding mainBinding;
    WeatherViewModel weatherViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get ViewModel using ViewModelProviders and then tech data
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setLifecycleOwner(this);
        mainBinding.setEventListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
                    getLocationManager();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // case
        }
    }

    public void onClickRefresh(View view) {
        //checkForPermissions();
        Toast.makeText(MainActivity.this, "Cannot connect.\nPlease check your internet connection",
                Toast.LENGTH_LONG).show();
    }

    void checkForPermissions() {
        try {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission has already been granted
                // Get the location manager
                getLocationManager();
            } else {
                requestLocationPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Main Activity", "onCreate: " + e.getMessage());
        }
    }

    private void requestLocationPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location permission needed")
                        .setMessage("Device location needs to be on.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();

            } else {
                // No explanation needed; request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Main Activity", "requestLocationPermission: " + e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationManager() {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        locationManager.requestLocationUpdates(provider, 400, 1, this);
        location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
            makeHttpCall();
        }
    }

    public void makeHttpCall(){

        final ProgressDialog dialog;
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait");
        dialog.show();

        weatherViewModel.getModel(latitude, longitude, API_KEY).observe(this, weather ->{
            mainBinding.setMain(weather.getMain());
            mainBinding.setSys(weather.getSys());
            dialog.dismiss();
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        checkForPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (location != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = (int) (location.getLatitude());
        longitude = (int) (location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, provider + " enabled",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, provider + " disabled",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickRefresh() {
        // Check for internet connection
        if(ConnectionManager.canConnect(this)){
            checkForPermissions();
        }else{
            // we can't connect
            // show no connection layout
            Toast.makeText(MainActivity.this, "Cannot connect.\nPlease check your internet connection",
                    Toast.LENGTH_LONG).show();
        }
    }
}
