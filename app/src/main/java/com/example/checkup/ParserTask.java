package com.example.checkup;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {

    private GoogleMap map;

    public ParserTask(GoogleMap map)
    {
        this.map = map;
    }
    @Override
    protected List<HashMap<String, String>> doInBackground(String... strings) {
        JsonParser jsonParser = new JsonParser();
        List<HashMap<String,String>> mapList = null;
        JSONObject object = null;
        try {
            object = new JSONObject(strings[0]);
            mapList = jsonParser.parseResult(object);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return mapList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
        //map.clear();
        for(int i = 0;i<hashMaps.size();i++)
        {
            HashMap<String,String> hashMapList = hashMaps.get(i);
            double lat = Double.parseDouble(hashMapList.get("lat"));
            double lng = Double.parseDouble(hashMapList.get("lng"));
            String name = hashMapList.get("name");
            LatLng latLng = new LatLng(lat,lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.snippet(hashMapList.get("id"));
            markerOptions.title(name);
            map.addMarker(markerOptions);
        }
    }
}
