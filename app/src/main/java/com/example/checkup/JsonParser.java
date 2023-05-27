package com.example.checkup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    private HashMap<String,String> parseJsonObject(JSONObject object)
    {
        HashMap<String,String> dataList = new HashMap<>();
        try {
            String name = object.getString("name");

            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location")
                    .getString("lat");

            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location")
                    .getString("lng");

            String id = object.getString("place_id");

            dataList.put("name",name);
            dataList.put("lat",latitude);
            dataList.put("lng",longitude);
            dataList.put("id",id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return dataList;
    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray)
    {
        List<HashMap<String,String>> dataList = new ArrayList<>();
        for(int i = 0;i<jsonArray.length();i++)
        {
            try {
                HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return dataList;
    }

    public List<HashMap<String,String>> parseResult(JSONObject object)
    {
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return parseJsonArray(jsonArray);
    }
}