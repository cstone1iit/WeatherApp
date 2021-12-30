package com.example.openweatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class DailyWeatherViewHolder extends RecyclerView.ViewHolder {

    TextView todayView;
    TextView hourlyTimeView;
    TextView hourlyTemp;
    TextView descriptionView;
    ImageView weatherIcon;


    public DailyWeatherViewHolder(View itemView){
        super(itemView);

        todayView = itemView.findViewById(R.id.todayDayView);
        hourlyTimeView = itemView.findViewById(R.id.hourlyTimeDayView);
        hourlyTemp = itemView.findViewById(R.id.hourlyDayTemp);
        descriptionView = itemView.findViewById(R.id.descriptionDayView);
        weatherIcon = itemView.findViewById(R.id.weatherIconDayView);


    }




}
