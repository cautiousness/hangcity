package com.fuj.hangcity.model.weather;

import java.util.List;

/**
 * Created by fuj
 */
public class WeatherEntity {
    public Aqi aqi;
    public Basic basic;
    public List<Daily> daily_forecast;
    public List<Hourly> hourly_forecast;
    public Now now;
    public String status;
    public Suggestion suggestion;
}
