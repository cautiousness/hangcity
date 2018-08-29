package com.fuj.hangcity.activity.main.user;

/**
 * Created by fuj
 */
public class UserPresenter implements UserContract.Presenter {
    private UserContract.View mViews;

    public UserPresenter(UserContract.View views) {
        mViews = views;
        mViews.setPresenter(this);
    }

    public void start() {

    }
}
