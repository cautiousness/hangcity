package com.fuj.hangcity.net;

import android.content.Context;

import com.fuj.hangcity.model.info.InfoResult;
import com.fuj.hangcity.model.scenic.ScenicResult;
import com.fuj.hangcity.model.weather.WeatherEntity;
import com.fuj.hangcity.tools.JsonFactory;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by fuj
 */
public class NetworkHelper {

    private static final String BASE_URL_WEATHER = "https://free-api.heweather.com/";
    private static final String BASE_URL_INFO = "https://v.juhe.cn/toutiao/";
    private static final String KEY = "1d6bf37092ae43f7a7072302ed932c96";

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit weatherRetrofit;
    private Retrofit infoRetrofit;

    private NetworkHelper() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        OkHttpClient client = httpClientBuilder.build();

        weatherRetrofit = getSubRetrofit(client, BASE_URL_WEATHER);
        infoRetrofit = getSubRetrofit(client, BASE_URL_INFO);
    }

    private static class SingletonHolder{
        private static final NetworkHelper INSTANCE = new NetworkHelper();
    }

    public static NetworkHelper getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public Observable<Map<String, List<WeatherEntity>>> getWeather(String cityid, String key){
        WeatherApi weatherApi = weatherRetrofit.create(WeatherApi.class);
        return weatherApi.getWeather(cityid, key);
    }

    public Observable<InfoResult> getInfo(String type) {
        InfoApi infoApi = infoRetrofit.create(InfoApi.class);
        return infoApi.getInfo(type, KEY);
    }

    public Observable<ScenicResult> getScenic(int page, Context context) {
        ScenicResult scenicResult = new Gson()
                .fromJson(JsonFactory.getJsonString(page, context), ScenicResult.class);
        return Observable.just(scenicResult);
    }

    private Retrofit getSubRetrofit(OkHttpClient client, String url) {
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }
}
