package com.grocus.grocustotalservice.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/**
 * Created by DEVMAC04 on 21/07/16.
 */
public class LocationServiceImpl implements LocationListener {
    private static final String CLASSNAME = LocationServiceImpl.class.getSimpleName();

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;

    private static LocationServiceImpl instance = null;

    private LocationManager locationManager;
    public Location location;
    public double longitude;
    public double latitude;

    public boolean isGPSEnabled;
    public boolean isNetworkEnabled;
    public boolean locationServiceAvailable;


    /**
     * Singleton implementation
     * @return
     */
    public static LocationServiceImpl getLocationManager(Context context)     {
        if (instance == null) {
            instance = new LocationServiceImpl(context);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    private LocationServiceImpl(Context context )     {

        initLocationService(context);
        Log.d( CLASSNAME , "LocationServiceImpl created");
    }



    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService(Context context) {


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try   {
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (forceNetwork) isGPSEnabled = false;

            if (!isNetworkEnabled && !isGPSEnabled)    {
                // cannot get location
                this.locationServiceAvailable = false;
            }
            //else
            {
                this.locationServiceAvailable = true;

                if (isNetworkEnabled) {
                    Log.d( CLASSNAME , "isNetworkEnabled..confire" );
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null)   {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateCoordinates();
                    }
                }//end if

                if (isGPSEnabled && location == null)  {
                    Log.d( CLASSNAME , "isGPSEnabled..confire" );
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)  {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        updateCoordinates();
                    }
                }
            }
        } catch (Exception ex)  {
            Log.d( CLASSNAME , "Error creating location service: " + ex.getMessage() , ex );

        }
    }

    private void updateCoordinates() {
        if( location != null ){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

    }

    private void updateCoordinates( Location location ) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }


    @Override
    public void onLocationChanged(Location location)     {
        Log.d( CLASSNAME , "La ubicación ha cambiado..." + location);
        this.updateCoordinates( location );
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
