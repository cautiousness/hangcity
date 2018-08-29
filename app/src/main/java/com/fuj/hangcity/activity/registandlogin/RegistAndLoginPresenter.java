package com.fuj.hangcity.activity.registandlogin;

/**
 * Created by fuj
 */
public class RegistAndLoginPresenter implements RegistAndLoginContract.Presenter {
    private RegistAndLoginContract.View mView;

    public RegistAndLoginPresenter(RegistAndLoginContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
