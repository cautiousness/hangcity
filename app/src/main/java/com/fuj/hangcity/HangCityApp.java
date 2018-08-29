package com.fuj.hangcity;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.fuj.hangcity.utils.LogUtils;

/**
 * Created by fuj
 */
public class HangCityApp extends Application {
    private static HangCityApp instance;

    public static HangCityApp getInstance() {
        if (instance == null) {
            instance = new HangCityApp();
        }
        return instance;
    }

    public HangCityApp getContext() {
        return getInstance();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        LogUtils.getInstance().init(this);
        SDKInitializer.initialize(this);
    }
}