package com.example.openweatherapp;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class WeatherCurrent implements Serializable {

    private String location;
    private String currDate;
    private String currTemp;
    private String feelsLike;
    private String currHumidity;
    private String currUVI;
    private String mornTemp;
    private String dayTemp;
    private String eveTemp;
    private String nightTemp;
    private String sunrise;
    private String wDescription;
    private String currWindSpeed;
    private String currWindDeg;
    private String currWindGust;
    private String currVis;
    private String sunset;
    private String wind;
    private String icon;




    public WeatherCurrent(String location, String currDate, String currTemp, String feelsLike, String currHumidity,
                          String currUVI, String mornTemp, String dayTemp, String eveTemp, String nightTemp,
                          String sunrise, String wDescription, String wind, String currVis, String sunset, String icon){

        this.location = location;
        this.currDate = currDate;
        this.currTemp = currTemp;
        this.feelsLike = feelsLike;
        this.currHumidity = currHumidity;
        this.currUVI = currUVI;
        this.mornTemp = mornTemp;
        this.dayTemp = dayTemp;
        this.eveTemp = eveTemp;
        this.nightTemp = nightTemp;
        this.sunrise = sunrise;
        this.wDescription  = wDescription;
        this.wind = wind;
        this.currVis = currVis;
        this.sunset = sunset;
        this.icon = icon;
    }

    String getLocation(){return location;}
    String getCurrDate(){return currDate;}
    String getCurrTemp(){return currTemp;}
    String getFeelsLike(){return feelsLike;}
    String getCurrHumidity(){return currHumidity;}
    String getCurrUVI(){return currUVI;}
    String getMornTemp(){return mornTemp;}
    String getDayTemp(){return dayTemp;}
    String getEveTemp(){return eveTemp;}
    String getNightTemp(){return nightTemp;}
    String getSunrise(){return sunrise;}
    String getWDescription(){return wDescription;}
    //String getCurrWindSpeed(){return currWindSpeed;}
    //String getCurrWindDeg(){return currWindDeg;}
    //String getCurrWindGust(){return currWindGust;}
    String getCurrVis(){return currVis;}
    String getSunset(){return sunset;}
    String getWind(){return wind;}
    String getIcon(){return icon;}


    void setLocation(String location){this.location = location;}
    void setCurrDate(String currDate){this.currDate = currDate;}
    void setCurrTemp(String currTemp){this.currTemp = currTemp;}
    void setFeelsLike(String feelsLike){this.feelsLike = feelsLike;}
    void setCurrHumidity(String currHumidity){this.currHumidity = currHumidity;}
    void setCurrUVI(String currUVI){this.currUVI = currUVI;}
    void setMornTemp(String mornTemp){this.mornTemp = mornTemp;}
    void setDayTemp(String dayTemp){this.dayTemp = dayTemp;}
    void setEveTemp(String eveTemp){this.eveTemp = eveTemp;}
    void setNightTemp(String nightTemp){this.nightTemp = nightTemp;}
    void setSunrise(String sunrise){this.sunrise = sunrise;}
    void setWDescription(String wDescription){this.wDescription = wDescription;}
    //void setCurrWindSpeed(String currWindSpeed){this.currWindSpeed = currWindSpeed;}
    //void setCurrWindDeg(String currWindDeg){this.currWindDeg = currWindDeg;}
    //void setCurrWindGust(String currWindGust){this.currWindGust = currWindGust;}
    void setCurrVis(String currVis){this.currVis = currVis;}
    void setSunset(String sunset){this.sunset = sunset;}
    void setWind(String wind){this.wind = wind;}
    void setIcon(String icon){this.icon = icon;}


    @NonNull
    public String toString(){
        try{
            StringWriter sWriter = new StringWriter();
            JsonWriter jWriter = new JsonWriter(sWriter);
            jWriter.beginObject();

            jWriter.name("location").value(getLocation());
            jWriter.name("currDate").value(getCurrDate());
            jWriter.name("currTemp").value(getCurrTemp());
            jWriter.name("feelsLike").value(getFeelsLike());
            jWriter.name("currHumidity").value(getCurrHumidity());
            jWriter.name("currUVI").value(getCurrUVI());
            jWriter.name("mornTemp").value(getMornTemp());
            jWriter.name("dayTemp").value(getDayTemp());
            jWriter.name("eveTemp").value(getEveTemp());
            jWriter.name("nightTemp").value(getNightTemp());
            jWriter.name("sunrise").value(getSunrise());
            jWriter.name("wDescription").value(getWDescription());
            //jWriter.name("currWindSpeed").value(getCurrWindSpeed());
            //jWriter.name("currWindDeg").value(getCurrWindDeg());
            //jWriter.name("currWindGust").value(getCurrWindGust());
            jWriter.name("currVis").value(getCurrVis());
            jWriter.name("sunset").value(getSunset());
            jWriter.name("wind").value(getWind());

            jWriter.endObject();
            jWriter.close();
            return sWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

}
