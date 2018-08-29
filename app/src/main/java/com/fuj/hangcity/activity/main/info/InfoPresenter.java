package com.fuj.hangcity.activity.main.info;

/**
 * Created by fuj
 */
public class InfoPresenter implements InfoContract.Presenter {
    private InfoContract.View mViews;

    public InfoPresenter(InfoContract.View view) {
        mViews = view;
        mViews.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
