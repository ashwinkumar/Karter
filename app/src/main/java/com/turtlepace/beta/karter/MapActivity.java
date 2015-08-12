package com.turtlepace.beta.karter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;

import org.json.JSONObject;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;


public class MapActivity extends FragmentActivity implements LocationListener{

    GoogleMap mGoogleMap;
    ArrayList<LatLng> mMarkerPoints;

    LatLng mPickUp , mDrop;
    Location mLastCorrect;
    float mTravelDistance;

    List<LatLng> mFuture;//  route between origin and destination
    List<LatLng> mConstructedLatLng;

    private Button mPickUpButton;
    private Button mEndTripButton;
    private Button mNavigateButton;
    // TO-DO <remove>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMarkerPoints = new ArrayList<LatLng>();
        mTravelDistance=0.0F;
        setContentView(R.layout.activity_map);
        String sOrigin, sDestination;
        final MyApplication myApp = MyApplication.getInstance();
        if(!myApp.isValidPoints()){
            this.finish();
            return;
        }

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available

            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            mGoogleMap = fm.getMap();

            // Enable MyLocation Button in the Map
            mGoogleMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);

            mPickUp = myApp.getPickupLatLng();
            mLastCorrect = new Location("Track");
            mLastCorrect.setLongitude(mPickUp.longitude);
            mLastCorrect.setLatitude(mPickUp.latitude);
            mDrop =myApp.getDropLatLng();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mPickUp));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            drawMarker(mPickUp);
            drawMarker(mDrop);
            mConstructedLatLng = new ArrayList<>();
            mFuture = new ArrayList<>();
            String url = getDirectionsUrl(mPickUp,mDrop);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.setIsConstruct(false);
            downloadTask.execute(url);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 2000, 100, this);
            //onLocationChanged(location);
            mPickUpButton= (Button)findViewById(R.id.pickupbutton);
            mEndTripButton = (Button)findViewById(R.id.endTripbutton);
            mEndTripButton.setClickable(false);
            mNavigateButton = (Button)findViewById(R.id.navigatebutton);
            mPickUpButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Verify PickUp Code
                    myApp.setDropAsDestination();
                    // TODO Auto-generated method stub
                }
            });
            mEndTripButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //verify end
                    myApp.clear();
                    // TODO Auto-generated method stub
                }
            });
            mNavigateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+myApp.getDestinationLatLng().latitude+","+myApp.getDestinationLatLng().longitude+"&mode=d");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            });
        }


    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        //String alternatives = "alternatives=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Excpt:downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String>{
        private boolean m_bisConstruct;
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.setIsConstruct(m_bisConstruct);
            parserTask.execute(result);
        }
        boolean isConstruct(){
            return m_bisConstruct;
        }
        void setIsConstruct(boolean b){
            m_bisConstruct = b;
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<LatLng>> >{
        private boolean m_bisConstruct;

        // Parsing the data in non-ui thread
        @Override
        protected List<List<LatLng>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<LatLng> routes = null;
            DirectionWrapper parseResult = new DirectionWrapper();

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                parseResult = parser.parse(jObject);
                if(m_bisConstruct){
                    mTravelDistance=parseResult.distance;

                }

            }catch(Exception e){
                e.printStackTrace();
            }
            return parseResult.routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<LatLng>> result) {
            ArrayList<LatLng> constructedpoints, futurepoints = null;
            PolylineOptions constructedlineOptions,futurelineOptions = null;

            constructedpoints = new ArrayList<LatLng>();
            futurepoints = new ArrayList<LatLng>();
            constructedlineOptions = new PolylineOptions();
            futurelineOptions = new PolylineOptions();
            if(m_bisConstruct){
                mConstructedLatLng.addAll(result.get(0));

            }
            else {
                mFuture =result.get(0);
            }
            // Fetching all the points in i-th route

            for(int j=0;j<mConstructedLatLng.size();j++){
                LatLng point = mConstructedLatLng.get(j);
                constructedpoints.add(point);
            }
            if(mFuture != null) {
                for (int j = 0; j < mFuture.size(); j++) {
                    LatLng point = mFuture.get(j);
                    futurepoints.add(point);
                }
            }


            // Drawing polyline in the Google Map for the i-th route

            // Adding all the points in the route to LineOptions
            constructedlineOptions.addAll(constructedpoints);
            constructedlineOptions.width(10);
            constructedlineOptions.color(Color.BLUE);

            futurelineOptions.addAll(futurepoints);
            futurelineOptions.width(10);
            futurelineOptions.color(Color.GREEN);

            RefreshMap(constructedlineOptions,futurelineOptions);


        }
        boolean isConstruct(){
            return m_bisConstruct;
        }
        void setIsConstruct(boolean b){
            m_bisConstruct = b;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void drawMarker(LatLng point){
        mMarkerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if(mMarkerPoints.size()==1){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if(mMarkerPoints.size()==2){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mGoogleMap.addMarker(options);
    }

    @Override
    public void onLocationChanged(Location location) {

        double currlat = location.getLatitude();
        double currlong = location.getLongitude();
        LatLng point = new LatLng(currlat, currlong);
        LatLng lastpoint = new LatLng(mLastCorrect.getLatitude(),mLastCorrect.getLongitude());

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        if(mFuture != null  && !PolyUtil.isLocationOnPath(point, mFuture,false,100)){
            // Getting URL to the Google Directions API

            /*Location lastCorrectLocation = new Location("Temp");
            lastCorrectLocation.setLatitude(mLastCorrect.latitude);
            lastCorrectLocation.setLongitude(mLastCorrect.longitude);

            UpdateDistance(lastCorrectLocation,location);*/
            DownloadTask downloadTask = new DownloadTask();
            DownloadTask dowloadFutureTask = new DownloadTask();
            downloadTask.setIsConstruct(true);

            String urlConstruct = getDirectionsUrl(lastpoint,point);
            downloadTask.execute(urlConstruct);
            mLastCorrect.setLatitude(currlat);
            mLastCorrect.setLongitude(currlong);

            String url = getDirectionsUrl(point, mDestination);
            dowloadFutureTask.setIsConstruct(false);
            // Start downloading json data from Google Directions API
            dowloadFutureTask.execute(url);
        }

    }
    void RefreshMap(PolylineOptions constructed, PolylineOptions future){
        mGoogleMap.clear();
        mMarkerPoints.clear();
        drawMarker(mOrigin);
        drawMarker(mDestination);
        mGoogleMap.addPolyline(constructed);
        mGoogleMap.addPolyline(future);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    /*void UpdateDistance(Location pointA, Location pointB){
        this.mTravelDistance += pointA.distanceTo(pointB);
        return;
    }*/

}