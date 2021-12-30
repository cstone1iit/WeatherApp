package com.example.openweatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class WeekWeatherViewHolder extends RecyclerView.ViewHolder {

    TextView dayDateView;
    TextView tempView;
    //TextView highTempView;
    TextView descriptionWeekView;
    TextView precipProbView;
    TextView UVIndexView;
    TextView mornTempView;
    TextView dayTempView;
    TextView eveTempView;
    TextView nightTempView;
    ImageView weatherIcon;


    public WeekWeatherViewHolder(View itemView) {
        super(itemView);

        dayDateView = itemView.findViewById(R.id.dayDateWeekView);
        tempView = itemView.findViewById(R.id.tempWeekView);
        //highTempView = itemView.findViewById(R.id.highTempWeekView);
        descriptionWeekView = itemView.findViewById(R.id.descriptionWeekView);
        precipProbView = itemView.findViewById(R.id.precipProbWeekView);
        UVIndexView = itemView.findViewById(R.id.UVIndexWeekView);
        mornTempView = itemView.findViewById(R.id.mornTempWeekView);
        dayTempView = itemView.findViewById(R.id.dayTempWeekView);
        eveTempView = itemView.findViewById(R.id.eveTempWeekView);
        nightTempView = itemView.findViewById(R.id.nightTempWeekView);
        weatherIcon = itemView.findViewById(R.id.iconWeekView);

    }




}
