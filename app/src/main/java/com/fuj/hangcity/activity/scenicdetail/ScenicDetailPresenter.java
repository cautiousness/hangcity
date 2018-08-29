package com.fuj.hangcity.activity.scenicdetail;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.fuj.hangcity.R;
import com.fuj.hangcity.map.DrivingRouteOverlay;
import com.fuj.hangcity.map.OverlayManager;
import com.fuj.hangcity.map.TransitRouteOverlay;
import com.fuj.hangcity.map.WalkingRouteOverlay;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by fuj
 */
public class ScenicDetailPresenter implements ScenicDetailContract.Presenter {
    private float curZoom = 18;

    private LatLng curLatLng;
    private LatLng endLatLng;
    private LocationClient mLocClient;
    private GeoCoder mSearch;
    private RoutePlanSearch routePlanSearch;
    private ScenicDetailContract.View mView;

    public MyLocationListenner myListener; //定位监听

    public ScenicDetailPresenter(ScenicDetailContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        initBaiduMap();
    }

    @Override
    public void initLocClient(LocationClient mLocClient) {
        this.mLocClient = mLocClient;
    }

    private void searchScenic(String addr) {
        mSearch.geocode(new GeoCodeOption().city(mView.getString(R.string.scenic_detail_city)).address(addr));
    }

    private void initBaiduMap() {
        myListener = new MyLocationListenner();
        setLocationListener();
        startLocate();
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    mView.showToast(R.string.toast_search_null);
                    return;
                }
                endLatLng = result.getLocation();
                mView.addPOIOverlay(endLatLng);
                mView.setMapCenterAndZoom(MapStatusUpdateFactory.newLatLngZoom(endLatLng, curZoom));
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR || result.getPoiList() == null) {
                    mView.showToast(R.string.toast_search_null);
                }
            }
        });

        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                getRouteResult(result);

            }

            public void onGetTransitRouteResult(TransitRouteResult result) {
                getRouteResult(result);
            }

            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                getRouteResult(result);
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {}
        });

        searchScenic(mView.getScenicAddr());
    }

    private <T extends SearchResult> void getRouteResult(T result) {
        if(result == null) {
            return;
        }
        mView.clearMap();
        mView.addOverlay(curLatLng);
        mView.showLine(result);
    }

    private void setLocationListener() {
        if(myListener == null) {
            mView.showToast(R.string.toast_locate_failed);
        } else {
            mLocClient.registerLocationListener(myListener);
        }
    }

    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location == null) { // MapView 销毁后不再处理新接收的位置
                return;
            }
            curLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    private void startLocate() {
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
        startLocateThread();
    }

    private void startLocateThread() {
        mLocClient.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(curLatLng == null) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mLocClient.stop();
                mView.addOverlay(curLatLng);
            }
        }).start();
    }

    @Override
    public void clickCar() {
        routePlanSearch.drivingSearch((new DrivingRoutePlanOption())
            .from(PlanNode.withLocation(curLatLng))
            .to(PlanNode.withLocation(endLatLng)));
    }

    @Override
    public void clickBus() {
        routePlanSearch.transitSearch((new TransitRoutePlanOption())
            .city(mView.getString(R.string.scenic_detail_city))
            .from(PlanNode.withLocation(curLatLng))
            .to(PlanNode.withLocation(endLatLng)));
    }

    @Override
    public void clickWalk() {
        routePlanSearch.walkingSearch((new WalkingRoutePlanOption())
            .from(PlanNode.withLocation(curLatLng))
            .to(PlanNode.withLocation(endLatLng)));
    }

    @Override
    public void showLine(SearchResult result, BaiduMap mBaiduMap) {
        OverlayManager overlay;
        List<? extends RouteLine> routeLines;
        if(result instanceof DrivingRouteResult) {
            overlay = new DrivingRouteOverlay(mBaiduMap);
            routeLines = ((DrivingRouteResult) result).getRouteLines();
            DrivingRouteLine routeLine = (DrivingRouteLine) routeLines.get(0);
            ((DrivingRouteOverlay)overlay).setData(routeLine);
            mView.updateStep(routeLine.getAllStep());
        } else if(result instanceof WalkingRouteResult) {
            overlay = new WalkingRouteOverlay(mBaiduMap);
            routeLines = ((WalkingRouteResult) result).getRouteLines();
            WalkingRouteLine routeLine = (WalkingRouteLine) routeLines.get(0);
            ((WalkingRouteOverlay)overlay).setData(routeLine);
            mView.updateStep(routeLine.getAllStep());
        } else {
            overlay = new TransitRouteOverlay(mBaiduMap);
            routeLines = ((TransitRouteResult) result).getRouteLines();
            TransitRouteLine routeLine = (TransitRouteLine) routeLines.get(0);
            ((TransitRouteOverlay)overlay).setData(routeLine);
            mView.updateStep(routeLine.getAllStep());
        }
        overlay.addToMap();
        overlay.zoomToSpan();
    }

    @Override
    public void destroy() {
        mSearch.destroy();
    }

    @Override
    public void setCurZoom(float zoom) {
        curZoom = zoom;
    }
}
