package com.example.openweatherapp;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class WeeklyWeatherRunnable implements Runnable{

    private final MainActivity mainActivity;
    private final String location;
    private final String lat;
    private final String lon;
    private final boolean fahrenheit;


    private static final String URL = "https://api.openweathermap.org/data/2.5/onecall";

    private static final String myAPIKey = "1d4f42878c0d532eb134808f43173981";


    WeeklyWeatherRunnable(MainActivity mainActivity, String location, boolean fahrenheit){
        this.mainActivity = mainActivity;
        this.location = locationName(location);
        double [] latLon = locationLatLon(this.location);
        this.lat = String.valueOf(latLon[0]);
        this.lon = String.valueOf(latLon[1]);
        this.fahrenheit = fahrenheit;
    }




    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(URL).buildUpon();

        buildURL.appendQueryParameter("lat", lat);
        buildURL.appendQueryParameter("lon", lon);
        buildURL.appendQueryParameter("units", (fahrenheit ? "imperial" : "metric")); //true - use F, false - use C
        buildURL.appendQueryParameter("appid", myAPIKey);
        String urlUseable = buildURL.build().toString();

        StringBuilder stringBuilder = new StringBuilder();
        try {
            java.net.URL url = new URL(urlUseable);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) { //connection not ok, error
                InputStream inputStream = connection.getErrorStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
                errorHandle(stringBuilder.toString());
                return;
            }

            InputStream inputStream = connection.getInputStream(); //connection ok
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }


        } catch (Exception exception) {
            resultHandle(null);
            return;
        }
        resultHandle(stringBuilder.toString());

    }



    public void errorHandle(String string){
        String message = "Error: ";
        try{
            JSONObject jsonObject = new JSONObject(string);
            message += jsonObject.getString("message");

        } catch (JSONException exception){
            message += exception.getMessage();
        }
        String errorMessage = String.format("%s (%s)", message, " lat: ", lat, " lon: ", lon);
        mainActivity.runOnUiThread(() -> mainActivity.errorHandle(errorMessage));
    }


    public void resultHandle(final String jString){
        if (jString == null){
            mainActivity.runOnUiThread(mainActivity::downloadFail);
            return;
        }
        final ArrayList<WeatherWeekly> weatherList = parseJson(jString);
        mainActivity.runOnUiThread(() -> {
            if(weatherList != null){
                Toast.makeText(mainActivity, "Loaded " + weatherList.size() + " weather entries", Toast.LENGTH_SHORT).show();
            }
            mainActivity.dataUpdateWeekly(weatherList);
        });

    }

    private ArrayList<WeatherWeekly> parseJson(String string){

        ArrayList<WeatherWeekly> weatherList = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(string);

            //Get hourly array
            for(int i = 0; i < 7; i++) { // Create 7 daily objects
                JSONArray dailyArray = jsonObject.getJSONArray("daily");

                JSONObject daily = (JSONObject) dailyArray.get(i);
                long dt = daily.getLong("dt");
                String date = new SimpleDateFormat("EEEEEEEEE, d/MM",Locale.getDefault()).format(new Date(dt*1000));

                JSONObject tempObj = daily.getJSONObject("temp");

                String lowTemp = convertTemp(tempObj.getString("min"), fahrenheit);
                String highTemp = convertTemp(tempObj.getString("max"), fahrenheit);
                String highLowTemp = highTemp + "/" + lowTemp;

                JSONArray weatherArray = daily.getJSONArray("weather");
                JSONObject weather = (JSONObject) weatherArray.get(0);

                String description = convertTitleCase(weather.getString("description"));
                String icon = weather.getString("icon");

                String precipProb = daily.getString("pop");
                String precipProbString = "(" + precipProb + "% precip.)";
                String UVI = daily.getString("uvi");

                String mornTemp = convertTemp(tempObj.getString("morn"), fahrenheit);
                String dayTemp = convertTemp(tempObj.getString("day"), fahrenheit);
                String eveTemp = convertTemp(tempObj.getString("eve"), fahrenheit);
                String nightTemp = convertTemp(tempObj.getString("night"), fahrenheit);

                weatherList.add(new WeatherWeekly(date, highLowTemp, description, precipProbString, "UV Index: " + UVI,
                        mornTemp, dayTemp, eveTemp, nightTemp, "_" + icon));
            }
            return weatherList;

        }catch(Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    public static String convertTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    private String locationName (String loc){
        Geocoder geocoder = new Geocoder(mainActivity);
        try {
            List<Address> address = geocoder.getFromLocationName(loc, 1);
            if (address == null || address.isEmpty()) {
                Toast.makeText(mainActivity, "Location: ' " + loc.toString() + " ' could not be resolved", Toast.LENGTH_SHORT).show();
                return null;
            }
            String country = address.get(0).getCountryCode();
            String p1 = "";
            String p2 = "";
            if (country.equals("US")) {
                p1 = address.get(0).getLocality();
                p2 = address.get(0).getAdminArea();
            } else {
                p1 = address.get(0).getLocality();
                if (p1 == null)
                    p1 = address.get(0).getSubAdminArea();
                p2 = address.get(0).getCountryName();
            }
            return p1 + ", " + p2;
        } catch (IOException e) {
            // Failed
            Toast.makeText(mainActivity, "Location: ' " + loc.toString() + " ' could not be resolved", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    private double [] locationLatLon(String loc){
        if (loc == null){
            return null;
        }
        Geocoder geocoder = new Geocoder(mainActivity);
        try {
            List<Address> address = geocoder.getFromLocationName(loc, 1);
            if (address == null || address.isEmpty()) {
                Toast.makeText(mainActivity, "Location: ' " + loc.toString() + " ' could not be resolved", Toast.LENGTH_SHORT).show();
                return null;
            }
            double lat = address.get(0).getLatitude();
            double lon = address.get(0).getLongitude();

            return new double[] {lat, lon};
        } catch (IOException e) {
            //Failed
            Toast.makeText(mainActivity, " Location: ' " + loc.toString() + " ' could not be resolved", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String convertTemp (String temp, Boolean fahrenheit){
        String sign;
        if (fahrenheit == true){
            sign = "°F";
        }else{
            sign = "°C";
        }
        int tempInt = Math.round(Float.valueOf(temp));
        String tempString = String.valueOf(tempInt) + sign;

        return tempString;
    }


}
