package com.fuj.hangcity.base;

import android.support.v7.app.ActionBar;

/**
 * Created by fuj
 */
public interface BaseView<T> {

    void setPresenter(T presenter);

    String getString(int resId);

    void showToast(int resId);

    void setTitle(String title);

    void setTitle(int resId);

    ActionBar getToolBar();
}
