package com.fuj.hangcity.activity.main.weather;


import com.fuj.hangcity.base.BasePresenter;
import com.fuj.hangcity.base.BaseView;
import com.fuj.hangcity.model.weather.WeatherEntity;

/**
 * Created by fuj
 */
public interface WeatherContract {
    interface View extends BaseView<Presenter> {

        void refreshView(WeatherEntity movieEntity);

        void setLoadingIndicator(boolean b);
    }

    interface Presenter extends BasePresenter {

        void getWeather();
    }
}
