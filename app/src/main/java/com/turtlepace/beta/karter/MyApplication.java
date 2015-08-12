package com.turtlepace.beta.karter;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by akumar on 10/08/15.
 */
public class MyApplication extends Application {
    private static MyApplication mInstance = null;
    private LatLng pickupLatLng =null;
    private LatLng dropLatLng =null;
    private LatLng destinationLatLng = null;
    public static MyApplication getInstance()
    {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public void setPickupLatLng(LatLng pickupLatLng) {
        this.pickupLatLng = pickupLatLng;
    }

    public LatLng getPickupLatLng(){
        return this.pickupLatLng;
    }

    public LatLng getDropLatLng() {
        return dropLatLng;
    }

    public void setDropLatLng(LatLng dropLatLng) {
        this.dropLatLng = dropLatLng;
    }
    boolean isValidPoints(){
        return (pickupLatLng !=null && dropLatLng != null);
    }
    public void setDropLatLng(String latLng){
        String lat = latLng.substring(0,latLng.indexOf(","));
        String lng = latLng.substring(latLng.indexOf(",") + 1, latLng.length());
        dropLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
    }
    public void setPickUpLatLng(String latLng){
        String lat = latLng.substring(0,latLng.indexOf(","));
        String lng = latLng.substring(latLng.indexOf(",") + 1, latLng.length());
        pickupLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
    }


    public LatLng getDestinationLatLng() {
        return destinationLatLng;
    }

    public void setPickUpAsDestination() {
        this.destinationLatLng = this.pickupLatLng;
    }
    public void setDropAsDestination() {
        this.destinationLatLng = this.dropLatLng;
    }

    public void clear(){
        this.pickupLatLng =null;
        this.dropLatLng =null;
        this.destinationLatLng = null;
    }
}

