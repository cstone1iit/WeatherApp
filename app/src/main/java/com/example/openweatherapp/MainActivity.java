package com.example.openweatherapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<WeatherHourly> weatherHourlyList = new ArrayList<>();
    private final ArrayList<WeatherWeekly> weatherWeeklyList = new ArrayList<>();
    private RecyclerView dayRecyclerView;
    private DailyWeatherAdapter dayAdapter;
    private boolean connection;

    private ActivityResultLauncher<Intent> activityResultLauncher;


    private boolean fahrenheit = true;
    //private double [] location = {41.8675766,-87.616232};
    private String location = "Chicago,US";
    TextView currDate;
    Menu mainMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),this::handleActivityResult);

        dayRecyclerView = findViewById(R.id.dayRecyclerView);
        dayAdapter = new DailyWeatherAdapter(weatherHourlyList, this);
        dayRecyclerView.setAdapter(dayAdapter);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        currDate = findViewById(R.id.dateTime);



        //grabData();

        connection = hasNetworkConnection();
        if (connection == true){
            Toast.makeText(this, "Network connection established.", Toast.LENGTH_LONG).show();
            grabData();
        }else{
            currDate.setText("NO NETWORK CONNECTION");
            Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("LOCATION",location);
        outState.putBoolean("UNIT", fahrenheit);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        location = savedInstanceState.getString("LOCATION");
        fahrenheit = savedInstanceState.getBoolean("UNIT");

        //grabData();


        connection = hasNetworkConnection();
        if (connection == false){
            currDate.setText("NO NETWORK CONNECTION");
            Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show();
        }else {
            grabData();
        }


    }




    public void grabData(){
        //connection = hasNetworkConnection();
        if(location == null){

            return;
        }else {

            //location = locationLatLon(locationName(locationET.getText().toString().trim().replaceAll(", ", ",")));

            //Grab data for hourly recycler view

            HourlyWeatherRunnable hourlyWeatherRunnable = new HourlyWeatherRunnable(this, location, fahrenheit);
            new Thread(hourlyWeatherRunnable).start();

            //Grab data for current weather view

            CurrentWeatherRunnable currentWeatherRunnable = new CurrentWeatherRunnable(this, location, fahrenheit);
            new Thread(currentWeatherRunnable).start();
        }

    }

    public void grabWeekData(){
        //connection = hasNetworkConnection();
        if(location == null){

            return;
        }else {

            WeeklyWeatherRunnable weeklyWeatherRunnable = new WeeklyWeatherRunnable(this, location, fahrenheit);
            new Thread(weeklyWeatherRunnable).start();
        }
    }

    private String locationName (String loc){
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> address = geocoder.getFromLocationName(loc, 1);
            if (address == null || address.isEmpty()) {
                Toast.makeText(this, "Location: ' " + loc.toString() + " ' could not be resolved", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Location: ' " + loc.toString() + " ' could not be resolved", Toast.LENGTH_LONG).show();
            return null;
        }
    }


    private double [] locationLatLon(String loc){
        if (loc == null){
            return null;
        }
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> address = geocoder.getFromLocationName(loc, 1);
            if (address == null || address.isEmpty()) {
                Toast.makeText(this, "Location: ' " + loc.toString() + " ' could not be resolved", Toast.LENGTH_SHORT).show();
                return null;
            }
            double lat = address.get(0).getLatitude();
            double lon = address.get(0).getLongitude();

            return new double[] {lat, lon};
        } catch (IOException e) {
            //Failed
            Toast.makeText(this, " Location: ' " + loc.toString() + " ' could not be resolved", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void errorHandle(String errorMsg){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setTitle("Problem with Data").setMessage(errorMsg)
                .setPositiveButton("OKAY", (dialogInterface, i) -> {}).create().show();

    }

    public void dataUpdateHourly(ArrayList<WeatherHourly> weatherList){
        weatherHourlyList.clear();
        weatherHourlyList.addAll(weatherList);
        dayAdapter.notifyItemChanged(0,weatherList.size());

    }

    public void dataUpdateWeekly(ArrayList<WeatherWeekly> weatherList){
        //Toast.makeText(this,"Weekly weather list returned. Size: " + weatherList.size(), Toast.LENGTH_LONG).show();
        weatherWeeklyList.clear();
        weatherWeeklyList.addAll(weatherList);
        openWeekForecastActivity(weatherWeeklyList);
    }

    public void openWeekForecastActivity(ArrayList<WeatherWeekly> weatherList){
        Intent intent = new Intent(this, WeekForecastActivity.class);
        intent.putParcelableArrayListExtra("WEATHER", weatherList);
        intent.putExtra("LOCATION", location);
        activityResultLauncher.launch(intent);
    }

    public void dataUpdateCurrent(WeatherCurrent weatherCurrent){
        TextView currLocation = findViewById(R.id.location);
        currLocation.setText(weatherCurrent.getLocation());

        //TextView currDate = findViewById(R.id.dateTime);
        currDate.setText(weatherCurrent.getCurrDate());

        TextView currTemp = findViewById(R.id.currTemp);
        currTemp.setText(weatherCurrent.getCurrTemp());

        TextView feelsLike = findViewById(R.id.feelsLike);
        feelsLike.setText("Feels Like: " + weatherCurrent.getFeelsLike());

        TextView currHumidity = findViewById(R.id.humidity);
        currHumidity.setText("Humidity: " + weatherCurrent.getCurrHumidity());

        TextView currUVI = findViewById(R.id.uvIndex);
        currUVI.setText("UV Index: " + weatherCurrent.getCurrUVI());

        TextView mornTemp = findViewById(R.id.morningTemp);
        mornTemp.setText(weatherCurrent.getMornTemp());

        TextView dayTemp = findViewById(R.id.dayTemp);
        dayTemp.setText(weatherCurrent.getDayTemp());

        TextView eveTemp = findViewById(R.id.eveTemp);
        eveTemp.setText(weatherCurrent.getEveTemp());

        TextView nightTemp = findViewById(R.id.nightTemp);
        nightTemp.setText(weatherCurrent.getNightTemp());

        TextView sunriseTime = findViewById(R.id.sunriseView);
        sunriseTime.setText("Sunrise: " + weatherCurrent.getSunrise());

        TextView sunsetTime = findViewById(R.id.sunsetView);
        sunsetTime.setText("Sunset: " + weatherCurrent.getSunset());

        TextView description = findViewById(R.id.description);
        description.setText(weatherCurrent.getWDescription());

        TextView winds = findViewById(R.id.wind);
        winds.setText(weatherCurrent.getWind());

        TextView currVisibility = findViewById(R.id.visibility);
        currVisibility.setText("Visibility: " + weatherCurrent.getCurrVis());

        ImageView currIcon = findViewById(R.id.currIcon);
        String iconCode = weatherCurrent.getIcon();
        int iconResId = this.getResources().getIdentifier(iconCode, "drawable", this.getPackageName());
        currIcon.setImageResource(iconResId);






    }


    public void handleActivityResult(ActivityResult result){
        //Toast.makeText(this,"Weekly Forecast Loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        getMenuInflater().inflate(R.menu.mainmenu,  menu);
        if (fahrenheit == true){
            menu.findItem(R.id.changeUnits).setIcon(R.drawable.units_f);
        }else{
            menu.findItem(R.id.changeUnits).setIcon(R.drawable.units_c);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.weekForecast){
            connection = hasNetworkConnection();
            if (connection == false){
                currDate.setText("NO NETWORK CONNECTION");
                Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show();
            }else {
                grabWeekData();
            }
        }
        if(item.getItemId() == R.id.locationChange){
            connection = hasNetworkConnection();
            if (connection == false){
                currDate.setText("NO NETWORK CONNECTION");
                Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show();
            }else {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
                adBuilder.setTitle("Enter a Location");
                adBuilder.setMessage("For US locations enter as 'City', or 'City,State'" + "\n" + "\n" + "For international locations enter as 'City,Country'");
                final EditText locationInput = new EditText(MainActivity.this);
                locationInput.setInputType(InputType.TYPE_CLASS_TEXT);
                adBuilder.setView(locationInput);

                adBuilder.setPositiveButton("OK", (dialog, id) -> { //grab city name from edit text, run API call to get JSON data
                    String city = locationInput.getText().toString().trim().replaceAll(", ", ",");
                    location = locationName(city);
                    connection = hasNetworkConnection();
                    if (connection == false) {
                        currDate.setText("NO NETWORK CONNECTION");
                        Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show();
                    } else {
                        grabData();
                    }
                });

                adBuilder.setNegativeButton("CANCEL", (dialog, id) -> {
                }); // do nothing
                AlertDialog locationDialog = adBuilder.create();
                locationDialog.show();
                return true;
            }

        }
        if(item.getItemId() == R.id.changeUnits){

            connection = hasNetworkConnection();
            if (connection == false){
                currDate.setText("NO NETWORK CONNECTION");
                Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show();
            }else {
                boolean temp = !fahrenheit;
                fahrenheit = temp;
                if (fahrenheit == true) {
                    MenuItem imperialMetric = item;
                    imperialMetric.setIcon(R.drawable.units_f);

                } else {
                    MenuItem imperialMetric = item;
                    imperialMetric.setIcon(R.drawable.units_c);
                }
                grabData();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void downloadFail(){
        weatherHourlyList.clear();
        dayAdapter.notifyItemChanged(0,weatherHourlyList.size());

    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}