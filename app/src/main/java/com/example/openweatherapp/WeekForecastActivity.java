package com.example.openweatherapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class WeekForecastActivity extends AppCompatActivity {

    private final ArrayList<WeatherWeekly> weatherWeeklyList = new ArrayList<>();
    private RecyclerView weekRecyclerView;
    private WeekWeatherAdapter weekAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);


        weekRecyclerView = findViewById(R.id.weekRecyclerView);
        weekAdapter = new WeekWeatherAdapter(weatherWeeklyList,WeekForecastActivity.this);
        weekRecyclerView.setAdapter(weekAdapter);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        Intent intent = getIntent();

        if (intent.hasExtra("WEATHER")){
            ArrayList<WeatherWeekly> weatherList = intent.getParcelableArrayListExtra("WEATHER");
            String location = intent.getStringExtra("LOCATION");
            this.setTitle(location);
            weatherWeeklyList.clear();
            weatherWeeklyList.addAll(weatherList);
            weekAdapter.notifyItemChanged(0,weatherList.size());
        }



    }


}