package com.example.checkup;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceTask  extends AsyncTask<String,Integer,String> {

    private GoogleMap map;

    public PlaceTask(GoogleMap map)
    {
        this.map = map;
    }
    @Override
    protected String doInBackground(String... strings) {
        String data = null;
        try{
            data = downloadURL(strings[0]);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("JSON",s);
        new ParserTask(map).execute(s);
    }

    private String downloadURL(String string) throws IOException
    {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while((line = reader.readLine()) != null)
        {
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }
}
