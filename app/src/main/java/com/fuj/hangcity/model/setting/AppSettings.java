package com.fuj.hangcity.model.setting;

import android.content.Context;

import com.fuj.hangcity.listener.OnDataListener;
import com.fuj.hangcity.model.scenic.Scenic;
import com.fuj.hangcity.model.scenic.ScenicResult;
import com.fuj.hangcity.model.weather.WeatherEntity;
import com.fuj.hangcity.net.NetworkHelper;

import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by whocare
 */
public class AppSettings {
    private static final String cityId = "CN101210101";
    private static final String key = "b53a87a631af47dabd0f0b61cb944181";

    private AppSettings() {}

    public static AppSettings getInstance() {
        return SingleTon.INSTANCE;
    }

    private static class SingleTon {
        private static AppSettings INSTANCE = new AppSettings();
    }

    public void getWeather(final OnDataListener<Map<String, List<WeatherEntity>>> dataListener) {
        NetworkHelper.getInstance().getWeather(cityId, key)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Map<String, List<WeatherEntity>>>() {
                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable e) {
                    dataListener.failed(e);
                }

                @Override
                public void onNext(Map<String, List<WeatherEntity>> map) {
                    dataListener.success(map);
                }
            });
    }

    public void getScenic(int currPage, Context context, final OnDataListener<List<Scenic>> dataListener) {
        NetworkHelper.getInstance().getScenic(currPage, context)
                .map(new Func1<ScenicResult, List<Scenic>>() {
                    @Override
                    public List<Scenic> call(ScenicResult scenicResult) {
                        return scenicResult.result;
                    }
                })
                .subscribe(new Subscriber<List<Scenic>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        dataListener.failed(e);
                    }

                    @Override
                    public void onNext(List<Scenic> scenics) {
                        dataListener.success(scenics);
                    }
                });
    }
}
