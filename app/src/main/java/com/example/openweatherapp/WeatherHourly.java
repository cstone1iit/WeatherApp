package com.example.openweatherapp;

import android.graphics.drawable.Icon;
import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class WeatherHourly implements Serializable {

    private String today;
    private String time;
    private String weatherIcon;
    private String temp;
    private String description;

    public WeatherHourly(String today, String time, String weatherIcon,
                         String temp, String description){
        this.today = today;
        this.time = time;
        this.weatherIcon  = weatherIcon;
        this.temp = temp;
        this.description = description;

    }

    String getToday(){return today;}
    String getTime(){return time;}
    String getWeatherIcon(){return weatherIcon;}
    String getTemp(){return temp;}
    String getDescription(){return description;}


    void setToday(String today){this.today = today;}
    void setTime(String time){this.time = time;}
    void setWeatherIcon(String weatherIcon){this.weatherIcon = weatherIcon;}
    void setTemp(String temp){this.temp = temp;}
    void setDescription(String description){this.description = description;}


    @NonNull
    public String toString(){
        try{
            StringWriter sWriter = new StringWriter();
            JsonWriter jWriter = new JsonWriter(sWriter);
            jWriter.beginObject();

            jWriter.name("time").value(getTime());
            jWriter.name("weatherIcon").value(getWeatherIcon());
            jWriter.name("temp").value(getTemp());
            jWriter.name("description").value(getDescription());


            jWriter.endObject();
            jWriter.close();
            return sWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }



}
