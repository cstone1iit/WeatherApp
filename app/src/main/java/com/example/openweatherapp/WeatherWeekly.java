package com.example.openweatherapp;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class WeatherWeekly implements Parcelable {

    private String dayDate;
    //private String highTemp;
    private String highLowTemp;
    private String description;
    private String precipProb;
    private String uvIndex;
    private String mornTemp;
    private String dayTemp;
    private String eveTemp;
    private String nightTemp;
    private String weatherIcon;

    public WeatherWeekly(String dayDate, String highLowTemp, String description, String precipProb,
                         String uvIndex, String mornTemp, String dayTemp, String eveTemp, String nightTemp, String weatherIcon){

        this.dayDate = dayDate;
        this.highLowTemp = highLowTemp;
        //this.lowTemp = lowTemp;
        this.description = description;
        this.precipProb = precipProb;
        this.uvIndex = uvIndex;
        this.mornTemp =mornTemp;
        this.dayTemp = dayTemp;
        this.eveTemp = eveTemp;
        this.nightTemp = nightTemp;
        this.weatherIcon = weatherIcon;
    }

    protected WeatherWeekly(Parcel in) {
        dayDate = in.readString();
        highLowTemp = in.readString();
        //lowTemp = in.readString();
        description = in.readString();
        precipProb = in.readString();
        uvIndex = in.readString();
        mornTemp = in.readString();
        dayTemp = in.readString();
        eveTemp = in.readString();
        nightTemp = in.readString();
        weatherIcon = in.readString();
    }

    public static final Creator<WeatherWeekly> CREATOR = new Creator<WeatherWeekly>() {
        @Override
        public WeatherWeekly createFromParcel(Parcel in) {
            return new WeatherWeekly(in);
        }

        @Override
        public WeatherWeekly[] newArray(int size) {
            return new WeatherWeekly[size];
        }
    };

    String getDayDate(){return dayDate;}
    //String getHighTemp(){return highTemp;}
    String getTemp(){return highLowTemp;}
    String getDescription(){return description;}
    String getPrecipProb(){return precipProb;}
    String getUvIndex(){return uvIndex;}
    String getMornTemp(){return mornTemp;}
    String getDayTemp(){return dayTemp;}
    String getEveTemp(){return eveTemp;}
    String getNightTemp(){return nightTemp;}
    String getWeatherIcon(){return weatherIcon;}


    void setDayDate(String dayDate){this.dayDate = dayDate;}
    void setTemp(String higTemp){this.highLowTemp = higTemp;}
    //void setLowTemp(String lowTemp){this.lowTemp = lowTemp;}
    void setDescription(String description){this.description = description;}
    void setPrecipProb(String precipProb){this.precipProb = precipProb;}
    void setUvIndex(String uvIndex){this.uvIndex = uvIndex;}
    void setMornTemp(String mornTemp){this.mornTemp = mornTemp;}
    void setDayTemp(String dayTemp){this.dayTemp = dayTemp;}
    void setEveTemp(String eveTemp){this.eveTemp = eveTemp;}
    void setNightTemp(String nightTemp){this.nightTemp = nightTemp;}
    void setWeatherIcon(String weatherIcon){this.weatherIcon = weatherIcon;}


    @NonNull
    public String toString(){
        try{
            StringWriter sWriter = new StringWriter();
            JsonWriter jWriter = new JsonWriter(sWriter);
            jWriter.beginObject();

            jWriter.name("dayDate").value(getDayDate());
            jWriter.name("highLowTemp").value(getTemp());
            //jWriter.name("lowTemp").value(getLowTemp());
            jWriter.name("description").value(getDescription());
            jWriter.name("precipProb").value(getPrecipProb());
            jWriter.name("uvIndex").value(getUvIndex());
            jWriter.name("mornTemp").value(getMornTemp());
            jWriter.name("dayTemp").value(getDayTemp());
            jWriter.name("eveTemp").value(getEveTemp());
            jWriter.name("nightTemp").value(getNightTemp());
            jWriter.name("weatherIcon").value(getWeatherIcon());


            jWriter.endObject();
            jWriter.close();
            return sWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dayDate);
        parcel.writeString(highLowTemp);
        //parcel.writeString(lowTemp);
        parcel.writeString(description);
        parcel.writeString(precipProb);
        parcel.writeString(uvIndex);
        parcel.writeString(mornTemp);
        parcel.writeString(dayTemp);
        parcel.writeString(eveTemp);
        parcel.writeString(nightTemp);
        parcel.writeString(weatherIcon);
    }
}

