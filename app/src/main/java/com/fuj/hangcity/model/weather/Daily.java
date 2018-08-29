package com.fuj.hangcity.model.weather;

/**
 * Created by fuj
 */
public class Daily {
    public String date;
    public Integer hum;
    public Float pcpn;
    public Integer pop;
    public Integer pres;
    public Integer vis;
    public Astro astro;
    public Cond cond;
    public Tmp tmp;
    public Wind wind;

    public class Astro {
        public String sr;
        public String ss;
    }

    public class Cond {
        public Integer code_d;
        public Integer code_n;
        public String txt_d;
        public String txt_n;
    }

    public class Tmp {
        public Integer max;
        public Integer min;
    }
}
