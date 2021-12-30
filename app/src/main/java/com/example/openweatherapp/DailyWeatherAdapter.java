package com.example.openweatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherViewHolder> {

    private final List<WeatherHourly> weatherDayList;
    private final MainActivity mainActivity;

    DailyWeatherAdapter(List<WeatherHourly> wDayList, MainActivity mActivity){
        this.weatherDayList  = wDayList;
        mainActivity   = mActivity;
    }



    @NonNull
    @Override
    public DailyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_weather_list_item, parent, false);

        return new DailyWeatherViewHolder(inflatedLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyWeatherViewHolder holder, int position) {
        WeatherHourly wItem = weatherDayList.get(position);

        holder.todayView.setText(wItem.getToday());
        holder.hourlyTimeView.setText(wItem.getTime());
        holder.hourlyTemp.setText(wItem.getTemp());
        holder.descriptionView.setText(wItem.getDescription());
        // get image resource
        String iconCode = wItem.getWeatherIcon();
        int iconResId = mainActivity.getResources().getIdentifier(iconCode, "drawable", mainActivity.getPackageName());
        holder.weatherIcon.setImageResource(iconResId);


    }

    @Override
    public int getItemCount() {
        return weatherDayList.size();
    }
}
