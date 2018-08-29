package com.fuj.hangcity.activity.main.weather;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fuj.hangcity.R;
import com.fuj.hangcity.adapter.base.ItemDivider;
import com.fuj.hangcity.adapter.base.RVAdapter;
import com.fuj.hangcity.adapter.base.RVHolder;
import com.fuj.hangcity.base.BaseFragment;
import com.fuj.hangcity.cache.ConCache;
import com.fuj.hangcity.model.weather.Suggestion;
import com.fuj.hangcity.model.weather.WeatherEntity;
import com.fuj.hangcity.widget.HourlyCurveView;
import com.fuj.hangcity.widget.ScrollChildSwipeRefreshLayout;
import com.fuj.hangcity.widget.TempertureCurveView;
import com.fuj.hangcity.widget.TranslucentScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuj
 */
public class WeatherFragment extends BaseFragment implements WeatherContract.View {
    private RelativeLayout weather_firstRL;
    private TextView weather_humidityTV;
    private TextView weather_updateTimeTV;
    private TextView weather_tempertureTV;
    private TextView weather_weatherTV;
    private TextView weather_windTV;
    private TempertureCurveView tempertureCV;
    private HourlyCurveView hourlyCV;
    private RVAdapter<Suggestion.Content> indexAdapter;
    private WeatherContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        indexAdapter = new RVAdapter<Suggestion.Content>(getContext(), new ArrayList<Suggestion.Content>(), R.layout.item_index) {
            @Override
            public void convert(RVHolder holder, Suggestion.Content content) {
                int tempPos = holder.getPosition();
                holder.setText(R.id.item_desTV, getIndexString(tempPos));
                holder.setText(R.id.item_contentTV, content.brf);
                holder.setImageResource(R.id.item_logoIV, getIndexImage(tempPos));
            }
        };
    }

    protected void findViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.view_weather, container, false);
        tempertureCV = (TempertureCurveView) findViewById(R.id.weather_tempertureCV);
        hourlyCV = (HourlyCurveView) findViewById(R.id.weather_hourlyCV);
        weather_humidityTV = (TextView) findViewById(R.id.weather_humidityTV);
        weather_updateTimeTV = (TextView) findViewById(R.id.weather_updateTimeTV);
        weather_tempertureTV = (TextView) findViewById(R.id.weather_tempertureTV);
        weather_weatherTV = (TextView) findViewById(R.id.weather_weatherTV);
        weather_windTV = (TextView) findViewById(R.id.weather_windTV);

        weather_firstRL = (RelativeLayout) findViewById(R.id.weather_firstRL);
        setWH(weather_firstRL, 0, ConCache.getInstance().getInt(getContext(), ConCache.RADIO_PANEL_HEIGHT));

        swipeRL = (ScrollChildSwipeRefreshLayout) findViewById(R.id.swipeRL);
        swipeRL.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setLoadingIndicator(true);
                mPresenter.getWeather();
            }
        });

        RecyclerView indexRV = (RecyclerView) findViewById(R.id.weather_indexRV);
        indexRV.setLayoutManager(new GridLayoutManager(getContext(), 2));
        indexRV.addItemDecoration(new ItemDivider(getContext(), ItemDivider.VERTICAL_LIST, 2, 2));
        indexRV.setAdapter(indexAdapter);

        TranslucentScrollView scrollView = (TranslucentScrollView) findViewById(R.id.weather_scrollview);
        scrollView.setOnScrollChangedListener(new TranslucentScrollView.OnScrollChangedListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
                int headerHeight = 600;
                int minHeaderHeight = 0;
                float scrollY = who.getScrollY();
                float headerBarOffsetY = headerHeight - minHeaderHeight; //Toolbar与header高度的差值
                float offset = 1 - Math.max((headerBarOffsetY - scrollY) / headerBarOffsetY, 0f);
                setToolbarBackground(offset);
                new AlphaChangedTask().execute(scrollY);
            }
        });
    }

    @Override
    protected void initTitle(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        title = inflater.inflate(R.layout.title_weather, container, false);
    }

    @Override
    public void setPresenter(WeatherContract.Presenter presenter) {
        mPresenter = presenter;
        mPresenter.start();
        setViewMargins(weather_firstRL, 0, getToolbarHeight(), 0, 0);
        setToolbarBackground(0);
        setCustomToolbar(title);
    }

    @Override
    public void refreshView(WeatherEntity weatherEntity) {
        weather_humidityTV.setText(getString(R.string.weather_humidityTV, weatherEntity.now.hum));
        weather_updateTimeTV.setText(getString(R.string.weather_updateTimeTV, weatherEntity.basic.update.loc));
        weather_tempertureTV.setText(getString(R.string.weather_tempertureTV, weatherEntity.now.tmp));
        weather_weatherTV.setText(getString(R.string.weather_weatherTV, weatherEntity.now.cond.txt));
        weather_windTV.setText(getString(R.string.weather_windTV, weatherEntity.now.wind.dir, weatherEntity.now.wind.sc));
        tempertureCV.refresh(weatherEntity);
        hourlyCV.refresh(weatherEntity);

        refreshIndex(weatherEntity.suggestion);
    }

    private void refreshIndex(Suggestion suggestion) {
        List<Suggestion.Content> list = new ArrayList<>();
        list.add(suggestion.comf);
        list.add(suggestion.cw);
        list.add(suggestion.drsg);
        list.add(suggestion.flu);
        list.add(suggestion.sport);
        list.add(suggestion.trav);
        list.add(suggestion.uv);
        indexAdapter.updateRecyclerView(list);
    }

    private int getIndexImage(int position) {
        int resId;
        switch (position) {
            case 0:
                resId = R.mipmap.ic_comfort;
                break;
            case 1:
                resId = R.mipmap.ic_car;
                break;
            case 2:
                resId = R.mipmap.ic_dress;
                break;
            case 3:
                resId = R.mipmap.ic_medicine;
                break;
            case 4:
                resId = R.mipmap.ic_sports;
                break;
            case 5:
                resId = R.mipmap.ic_travel;
                break;
            default:
                resId = R.mipmap.ic_rays;
                break;
        }

        return resId;
    }

    private String getIndexString(int position) {
        String des;
        switch (position) {
            case 0:
                des = "舒适度";
                break;
            case 1:
                des = "洗车指数";
                break;
            case 2:
                des = "穿衣指数";
                break;
            case 3:
                des = "感冒指数";
                break;
            case 4:
                des = "运动指数";
                break;
            case 5:
                des = "旅行指数";
                break;
            default:
                des = "紫外线";
                break;
        }

        return des;
    }

    class AlphaChangedTask extends AsyncTask<Float, Integer, Integer> {
        @Override
        protected Integer doInBackground(Float... scrollY) {
            int alpha = (int) (255 - (scrollY[0] / 5));
            if (alpha > 220) {
                alpha = 220;
            } else if (alpha < 30) {
                alpha = 30;
            }
            return alpha;
        }

        @Override
        protected void onPostExecute(Integer alpha) {
            swipeRL.getBackground().setAlpha(alpha);
        }
    }
}
