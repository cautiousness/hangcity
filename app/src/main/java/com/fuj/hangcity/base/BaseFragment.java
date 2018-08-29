package com.fuj.hangcity.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuj.hangcity.cache.ConCache;
import com.fuj.hangcity.tools.LoadingDialog;
import com.fuj.hangcity.utils.LogUtils;
import com.fuj.hangcity.utils.PreferenceUtils;
import com.fuj.hangcity.utils.ToastUtils;
import com.fuj.hangcity.widget.ScrollChildSwipeRefreshLayout;

/**
 * Created by fuj
 */
public abstract class BaseFragment extends Fragment {
    protected ScrollChildSwipeRefreshLayout swipeRL;
    protected ViewGroup root;
    protected View title;
    private ConCache cache;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.i(getClass().getSimpleName());
        cache = ConCache.getInstance();
        findViews(inflater, container, savedInstanceState);
        initTitle(inflater, container, savedInstanceState);
        return root;
    }

    protected void initTitle(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i(getClass().getSimpleName());
        setListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i(getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.i(getClass().getSimpleName());
    }

    protected abstract void findViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    protected View findTitle(int viewId) {
        return title.findViewById(viewId);
    }

    protected View findViewById(int viewId) {
        return root.findViewById(viewId);
    }

    public Drawable getResDrawable(int drawableId) {
        return getResources().getDrawable(drawableId);
    }

    public int getResColor(int colorId) {
        return getResources().getColor(colorId);
    }

    public void showToast(String msg) {
        ToastUtils.showToast(getActivity(), msg);
    }

    public void showToast(int resId) {
        ToastUtils.showToast(getActivity(), getString(resId));
    }

    protected void setWH(View view, int w, int h) {
        if(w != 0) {
            view.getLayoutParams().width = w;
        }

        if(h != 0) {
            view.getLayoutParams().height = h;
        }
    }

    protected void setViewMargins(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams paramTest2 = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        paramTest2.setMargins(left, top, right, bottom);
        view.requestLayout();
    }

    public void showActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(getContext(), clazz);
        startActivity(intent);
    }

    protected void showActivityResult(Class<? extends Activity> clazz, int requestCode) {
        Intent intent = new Intent(getContext(), clazz);
        startActivityForResult(intent, requestCode);
    }

    public void showActivity(Class<? extends Activity> clazz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public String getServer() {
        return cache.getStr(getContext(), ConCache.SERVER_ADDR);
    }

    public ActionBar getToolBar() {
        return ((BaseActivity)getActivity()).getToolBar();
    }

    public int getToolbarHeight() {
        return ((BaseActivity)getActivity()).getToolbarHeight(getContext());
    }

    public int getStatusBarHeight() {
        return ((BaseActivity)getActivity()).getStatusBarHeight(getContext());
    }

    public void setPreBool(String file, String key, boolean b) {
        PreferenceUtils.write(getContext(), file, key, b);
    }

    public boolean getPreBool(String config, String configBussiness) {
        return PreferenceUtils.readBoolean(getContext(), config, configBussiness);
    }

    public void setPreInt(String file, String key, int i) {
        PreferenceUtils.write(getContext(), file, key, i);
    }

    public void setPreString(String file, String key, String s) {
        PreferenceUtils.write(getContext(), file, key, s);
    }

    protected void setListeners() {}

    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    public void setTitle(String title) {
        ((BaseActivity)getActivity()).setTitle(title);
    }

    public void setToolbarBackground(float offset) {
        ((BaseActivity)getActivity()).setToolbarBackground(offset);
    }

    public void setCustomToolbar(View view) {
        ((BaseActivity)getActivity()).setCustomToolbar(view);
    }

    public void showLoading(int resId) {
        LoadingDialog.getInstance().showLoadingDialog(getContext(), getString(resId));
    }

    public void hideLoading() {
        LoadingDialog.getInstance().hideLoadingDialog();
    }

    public void setLoadingIndicator(final boolean isRefresh) {
        if (getView() == null) {
            return;
        }

        swipeRL.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRL.setRefreshing(isRefresh);
            }
        }, 500);
    }
}
