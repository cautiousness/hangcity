package com.fuj.hangcity.model.weather;

/**
 * Created by fuj
 */
public class Aqi {
    public City city;

    private class City {
        public Integer aqi;
        public Integer co;
        public Integer no2;
        public Integer o3;
        public Integer pm10;
        public Integer pm25;
        public Integer so2;
        public String qlty;
    }
}
