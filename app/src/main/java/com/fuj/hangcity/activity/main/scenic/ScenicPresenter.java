package com.fuj.hangcity.activity.main.scenic;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;

import com.fuj.hangcity.adapter.base.FootAdapter;
import com.fuj.hangcity.listener.OnDataListener;
import com.fuj.hangcity.model.scenic.Scenic;
import com.fuj.hangcity.model.setting.AppSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuj
 */
public class ScenicPresenter implements ScenicContract.Presenter {
    private boolean isLoading;
    private Context mContext;
    private Handler handler = new Handler();
    private List<Scenic> mList;
    private AppSettings settings;
    private ScenicContract.View mViews;

    private static int currPage = 0;

    public ScenicPresenter(ScenicContract.View views) {
        mViews = views;
        mViews.setPresenter(this);
    }

    public void start() {
        settings = AppSettings.getInstance();
        mList = new ArrayList<>();
    }

    @Override
    public void getData(Context context) {
        mContext = context;
        currPage = 0;
        mList.clear();
        startGetData();
    }

    private void startGetData() {
        settings.getScenic(currPage++, mContext, new OnDataListener<List<Scenic>>() {
            @Override
            public void success(List<Scenic> scenics) {
                mViews.setLoadingIndicator(false);
                isLoading = false;
                mList.addAll(scenics);
                mViews.updateRecyclerView(mList);
            }

            @Override
            public void failed(Throwable t) {
                mViews.setLoadingIndicator(false);
                isLoading = false;
                mViews.setLoadingIndicator(false);
            }
        });
    }

    @Override
    public void onScroll(LinearLayoutManager layoutManager, FootAdapter<Scenic> adapter) {
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
            if (!isLoading) {
                isLoading = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGetData();
                    }
                }, 1000);
            }
        }
    }
}
