package com.fuj.hangcity.listener;

/**
 * Created by whocare
 */
public interface OnDataListener<T> {

    void success(T t);

    void failed(Throwable t);

}
