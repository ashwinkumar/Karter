package com.turtlepace.beta.karter;
/**
 * Created by akumar on 04/08/15.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
public class DirectionsJSONParser{
    /** Receives a JSONObject and returns a list containing latitude and longitude */
    public List<List<LatLng>> parse(JSONObject jObject){

        List<List<LatLng>> routes =  new ArrayList<List<LatLng>>()  ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject jDistance = null;
        JSONObject jDuration = null;
        Float calc_distance = 0F;

        try {

            jRoutes = jObject.getJSONArray("routes");


            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<LatLng>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    /** Getting distance from the json data */
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    String sDistance = jDistance.getString("text");
                    //calc_distance+=Float.valueOf(jDistance.getString("text"));
                    sDistance=sDistance.substring(0, sDistance.indexOf(' '));
                    calc_distance += Float.valueOf(sDistance);
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        path.addAll(PolyUtil.decode(polyline));

                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
    }

}


