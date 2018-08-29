package com.fuj.hangcity.activity.main.weather;


import com.fuj.hangcity.listener.OnDataListener;
import com.fuj.hangcity.model.setting.AppSettings;
import com.fuj.hangcity.model.weather.WeatherEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by fuj
 */
public class WeatherPresenter implements WeatherContract.Presenter {
    private WeatherContract.View mView;

    public WeatherPresenter(WeatherContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    public void start() {
        getWeather();
    }

    public void getWeather() {
        AppSettings.getInstance().getWeather(new OnDataListener<Map<String, List<WeatherEntity>>>() {
            @Override
            public void success(Map<String, List<WeatherEntity>> map) {
                mView.setLoadingIndicator(false);
                for (String key : map.keySet()) {
                    WeatherEntity entity = map.get(key).get(0);
                    mView.refreshView(entity);
                }
            }

            @Override
            public void failed(Throwable t) {
                mView.setLoadingIndicator(false);
            }
        });
    }
}
