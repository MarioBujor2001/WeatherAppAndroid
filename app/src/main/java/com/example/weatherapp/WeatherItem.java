package com.example.weatherapp;

public class WeatherItem {
    private String hour, temp, condition;

    public WeatherItem(String hour, String temp, String condition) {
        this.hour = hour;
        this.temp = temp;
        this.condition = condition;
    }

    public WeatherItem(){

    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
