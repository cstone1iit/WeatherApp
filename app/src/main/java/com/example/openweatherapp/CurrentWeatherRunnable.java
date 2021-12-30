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

public class CurrentWeatherRunnable implements Runnable{

    private final MainActivity mainActivity;
    private final String location;
    private final String lat;
    private final String lon;
    private final boolean fahrenheit;


    private static final String URL = "https://api.openweathermap.org/data/2.5/onecall";

    private static final String myAPIKey = "1d4f42878c0d532eb134808f43173981";


    CurrentWeatherRunnable(MainActivity mainActivity, String location, boolean fahrenheit){
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
        final WeatherCurrent weatherCurrent = parseJson(jString);
        mainActivity.runOnUiThread(() -> {
            if(weatherCurrent != null){
                Toast.makeText(mainActivity, "Loaded current weather entry", Toast.LENGTH_LONG).show();
            }
            mainActivity.dataUpdateCurrent(weatherCurrent);
        });

    }


    private WeatherCurrent parseJson(String string){

        WeatherCurrent weatherCurrent;



        try{
            JSONObject jsonObject = new JSONObject(string);

            //Get Current array

            JSONObject current = jsonObject.getJSONObject("current");

            // Get date

            long dt = current.getLong("dt");
            String date = new SimpleDateFormat("EEE MMM d h:mm a, yyyy", Locale.getDefault()).format(new Date(dt * 1000));
            //long timeZoneOffset = jsonObject.getLong("timezone_offset");

            //Get temp
            String temp = convertTemp(current.getString("temp"),fahrenheit);

            //Get feels like
            String feelsLike = convertTemp(current.getString("feels_like"),fahrenheit);

            //Get Humidity
            String humidity = current.getString("humidity");
            int humidityInt = Math.round(Float.valueOf(humidity));
            String humidityString = String.valueOf(humidityInt) + "%";

            //Get UVI
            String UVI = current.getString("uvi");

            //Get Daily array
            JSONArray dailyArray = jsonObject.getJSONArray("daily");
            JSONObject daily = (JSONObject) dailyArray.get(0);
            JSONObject tempObj = (JSONObject) daily.getJSONObject("temp");

            //Get Morning Temp
            String mornTemp = convertTemp(tempObj.getString("morn"),fahrenheit);

            //Get Day Temp
            String dayTemp = convertTemp(tempObj.getString("day"),fahrenheit);

            //Get Evening Temp
            String eveTemp = convertTemp(tempObj.getString("eve"),fahrenheit);

            //Get Night Temp
            String nightTemp = convertTemp(tempObj.getString("night"),fahrenheit);


            //Get Sunrise
            long sunrise = current.getLong("sunrise");
            String sunriseTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(sunrise*1000));

            //Get Sunset
            long sunset = current.getLong("sunset");
            String sunsetTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(sunset*1000));

            //Get Weather array/object

            JSONArray weatherArray = current.getJSONArray("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);

            //Get icon
            String icon = weather.getString("icon");

            //Get description
            String description = convertTitleCase(weather.getString("description"));

            //Get Wind

            String windUnits;
            if (fahrenheit == true){
                windUnits = " mph";
            }else{
                windUnits = " m/s";
            }

            Integer windSpeedInt = current.getInt("wind_speed");
            String windSpeed = String.valueOf(Math.round(windSpeedInt)) + windUnits;

            double windDeg = current.getDouble("wind_deg");
            String windDegree = String.valueOf(getDirection(windDeg));

            String wind;
            if (current.has("wind_Gust")){

                Integer windGustInt = current.getInt("wind_gust");
                String windGust = String.valueOf(Math.round(windGustInt)) + windUnits;
                wind = "Winds: " + windDegree + " at " + windSpeed +
                        '\n' + " with gusts of " + windGust;
            }else{
                wind = "Winds: " + windDegree + " at " + windSpeed;
            }

            //Get visibility
            String visibility;
            double visibilityDouble = current.getDouble("visibility");
            if (fahrenheit == true){
                double visMiles = Math.round(((visibilityDouble/1000)/1.609)*10)/10; //convert meters to km to miles
                visibility = String.valueOf(visMiles) + " mi";
            }else{
                double visMeters = Math.round((visibilityDouble/1000)*10)/10;
                visibility = String.valueOf(visMeters) + " km";
            }

            weatherCurrent = new WeatherCurrent(this.location, date, temp, feelsLike, humidityString, UVI,
                    mornTemp, dayTemp, eveTemp, nightTemp, sunriseTime, description, wind, visibility, sunsetTime, "_" + icon);

            return weatherCurrent;

        }catch(Exception exception){
            exception.printStackTrace();
        }
        return null;
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

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
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



}
