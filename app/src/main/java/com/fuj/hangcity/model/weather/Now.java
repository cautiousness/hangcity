package com.fuj.hangcity.model.weather;

/**
 * Created by fuj
 */
public class Now {
    public Integer fl;
    public Integer hum;
    public Float pcpn;
    public Integer pres;
    public Integer tmp;
    public Integer vis;
    public Cond cond;
    public Wind wind;

    public class Cond {
        public Integer code;
        public String txt;
    }
}
