package com.fuj.hangcity.model.weather;

/**
 * Created by fuj
 */
public class Basic {
    public String city;
    public String cnty;
    public String id;
    public String lat;
    public String lon;
    public Update update;

    public class Update {
        public String loc;
        public String utc;
    }
}
