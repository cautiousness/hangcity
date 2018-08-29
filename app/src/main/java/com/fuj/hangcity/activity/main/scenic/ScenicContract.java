package com.fuj.hangcity.activity.main.scenic;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.fuj.hangcity.adapter.base.FootAdapter;
import com.fuj.hangcity.base.BasePresenter;
import com.fuj.hangcity.base.BaseView;
import com.fuj.hangcity.model.scenic.Scenic;

import java.util.List;


/**
 * Created by fuj
 */
public interface ScenicContract {
    interface View extends BaseView<Presenter> {

        void updateRecyclerView(List<Scenic> scenics);

        void setLoadingIndicator(boolean b);
    }

    interface Presenter extends BasePresenter {

        void getData(Context context);

        void onScroll(LinearLayoutManager layoutManager, FootAdapter<Scenic> adapter);
    }
}
