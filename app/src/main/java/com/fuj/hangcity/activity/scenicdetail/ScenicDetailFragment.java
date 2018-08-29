package com.fuj.hangcity.activity.scenicdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.SearchResult;
import com.fuj.hangcity.R;
import com.fuj.hangcity.adapter.ScenicStepAdapter;
import com.fuj.hangcity.adapter.base.RVAdapter;
import com.fuj.hangcity.base.BaseFragment;
import com.fuj.hangcity.widget.MapControlButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuj
 */
public class ScenicDetailFragment extends BaseFragment implements ScenicDetailContract.View {
    private String addr;
    private BaiduMap mBaiduMap;
    private RVAdapter adapter;

    private ImageView bottomIV;
    private RecyclerView stepRV;
    private BitmapDescriptor mark = BitmapDescriptorFactory.fromResource(R.mipmap.map_location_gz);
    private BitmapDescriptor markerPic = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka);

    private ScenicDetailContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(getString(R.string.scenic_bundle))) {
            addr = (String) getArguments().get(getString(R.string.scenic_bundle));
        }

        adapter = new ScenicStepAdapter(getContext(), new ArrayList<RouteStep>(0), R.layout.item_scenic_step);
    }

    protected void findViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.view_scenic_detail, container, false);

        MapView mMapView = (MapView) findViewById(R.id.map_mapView);
        mMapView.showZoomControls(false);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true); //定位相关
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                mPresenter.setCurZoom(mapStatus.zoom);
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {}

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {}
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showRecyclerView(false);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        final RelativeLayout panelRL = (RelativeLayout) findViewById(R.id.scenic_detail_pannel);
        final ImageView unfoldedIV = (ImageView) findViewById(R.id.scenic_detail_unfolded);
        unfoldedIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfoldedIV.setVisibility(View.GONE);
                panelRL.setVisibility(View.VISIBLE);
                TranslateAnimation animation = new TranslateAnimation(
                        0, 0, -panelRL.getHeight() + unfoldedIV.getHeight(), 0);
                animation.setDuration(500);
                panelRL.startAnimation(animation);
            }
        });

        final ImageView foldedIV = (ImageView) findViewById(R.id.scenic_detail_folded);
        foldedIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateAnimation animation = new TranslateAnimation(
                        0, 0, 0, -panelRL.getHeight() + foldedIV.getHeight());
                animation.setDuration(500);
                panelRL.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        panelRL.setVisibility(View.GONE);
                        unfoldedIV.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        MapControlButton mapControlButton = (MapControlButton) findViewById(R.id.map_mapControlBTN);
        mapControlButton.setMapView(mMapView);

        RelativeLayout topRL = (RelativeLayout) findViewById(R.id.topRL);
        setViewMargins(topRL, 0, getStatusBarHeight(), 0, 0);

        EditText fromET = (EditText) findViewById(R.id.scenic_detail_fromET);
        fromET.setText(getString(R.string.scenic_detail_myaddr));

        EditText toET = (EditText) findViewById(R.id.scenic_detail_toET);
        toET.setText(addr);

        final Button carBTN = (Button) findViewById(R.id.scenic_detail_car);
        final Button busBTN = (Button) findViewById(R.id.scenic_detail_bus);
        final Button walkBTN = (Button) findViewById(R.id.scenic_detail_walk);
        carBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clickCar();
                setBackgroundColor(carBTN, busBTN, walkBTN);
            }
        });

        busBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clickBus();
                setBackgroundColor(busBTN, carBTN, walkBTN);
            }
        });

        walkBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clickWalk();
                setBackgroundColor(walkBTN, carBTN, busBTN);
            }
        });

        mPresenter.initLocClient(new LocationClient(getContext()));
        mPresenter.start();

        stepRV = (RecyclerView) findViewById(R.id.scenic_detail_step);
        stepRV.setLayoutManager(new LinearLayoutManager(getContext()));
        stepRV.setAdapter(adapter);

        bottomIV = (ImageView) findViewById(R.id.scenic_detail_bottom);
        bottomIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecyclerView(true);
            }
        });
    }

    @Override
    public void setPresenter(ScenicDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setMapCenterAndZoom(MapStatusUpdate update) {
        mBaiduMap.animateMapStatus(update);
    }

    @Override
    public void addOverlay(LatLng latLng) {
        mBaiduMap.addOverlay(new MarkerOptions().position(latLng).icon(mark).anchor(0.5f, 0.5f));
    }

    @Override
    public void addPOIOverlay(LatLng addr) {
        mBaiduMap.addOverlay(new MarkerOptions().position(addr).icon(markerPic).anchor(0.5f, 0.5f));
    }

    @Override
    public String getScenicAddr() {
        return addr;
    }

    @Override
    public void showLine(SearchResult result) {
        mPresenter.showLine(result, mBaiduMap);
    }

    private void setBackgroundColor(Button blueBTN, Button whiteBTN1, Button whiteBTN2) {
        blueBTN.setBackgroundColor(getResColor(R.color.blue));
        whiteBTN1.setBackgroundColor(getResColor(R.color.white));
        whiteBTN2.setBackgroundColor(getResColor(R.color.white));
    }

    @Override
    public void clearMap() {
        mBaiduMap.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    public void updateStep(List<? extends RouteStep> steps) {
        adapter.updateRecyclerView(steps);
        showRecyclerView(true);
    }

    private void showRecyclerView(boolean isShow) {
        stepRV.setVisibility(isShow ? View.VISIBLE : View.GONE);
        bottomIV.setVisibility(isShow? View.GONE : View.VISIBLE);
    }
}
