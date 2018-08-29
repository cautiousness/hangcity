package com.fuj.hangcity.activity.scenicdetail;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.SearchResult;
import com.fuj.hangcity.base.BasePresenter;
import com.fuj.hangcity.base.BaseView;

import java.util.List;

/**
 * Created by fuj
 */
public interface ScenicDetailContract {
    interface View extends BaseView<Presenter> {

        void setMapCenterAndZoom(MapStatusUpdate mapStatusUpdate);

        void addOverlay(LatLng curLatLng);

        void addPOIOverlay(LatLng addr);

        String getScenicAddr();

        void showLine(SearchResult result);

        void clearMap();

        void updateStep(List<? extends RouteStep> steps);

    }

    interface Presenter extends BasePresenter {

        void setCurZoom(float zoom);

        void initLocClient(LocationClient mLocClient);

        void clickCar();

        void destroy();

        void clickBus();

        void clickWalk();

        void showLine(SearchResult result, BaiduMap mBaiduMap);
    }
}
