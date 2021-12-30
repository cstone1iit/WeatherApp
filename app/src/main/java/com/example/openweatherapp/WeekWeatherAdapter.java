package com.example.openweatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherViewHolder> {

    private final List<WeatherWeekly> weatherWeekList;
    private final WeekForecastActivity weekForecastActivity;

    WeekWeatherAdapter(List<WeatherWeekly> wWeekList, WeekForecastActivity wForecast){
        this.weatherWeekList = wWeekList;
        weekForecastActivity = wForecast;
    }




    @NonNull
    @Override
    public WeekWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_weather_list_item, parent, false);

        return new WeekWeatherViewHolder(inflatedLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekWeatherViewHolder holder, int position) {
        WeatherWeekly wItem = weatherWeekList.get(position);

        holder.dayDateView.setText(wItem.getDayDate());
        holder.tempView.setText(wItem.getTemp());
        //holder.highTempView.setText(wItem.getHighTemp());
        holder.descriptionWeekView.setText(wItem.getDescription());
        holder.precipProbView.setText(wItem.getPrecipProb());
        holder.UVIndexView.setText(wItem.getUvIndex());
        holder.mornTempView.setText(wItem.getMornTemp());
        holder.dayTempView.setText(wItem.getDayTemp());
        holder.eveTempView.setText(wItem.getEveTemp());
        holder.nightTempView.setText(wItem.getNightTemp());

        String iconCode = wItem.getWeatherIcon();
        int iconResId = weekForecastActivity.getResources().getIdentifier(iconCode, "drawable", weekForecastActivity.getPackageName());
        holder.weatherIcon.setImageResource(iconResId);


    }

    @Override
    public int getItemCount() {
        return weatherWeekList.size();
    }
}
