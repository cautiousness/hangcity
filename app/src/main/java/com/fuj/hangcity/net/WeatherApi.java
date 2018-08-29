package com.fuj.hangcity.net;


import com.fuj.hangcity.model.weather.WeatherEntity;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by fuj
 */
public interface WeatherApi {

    @GET("x3/weather")
    Observable<Map<String, List<WeatherEntity>>> getWeather(@Query("cityid") String cityid, @Query("key") String key);
}
