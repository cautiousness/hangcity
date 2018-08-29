package com.fuj.hangcity.adapter;

import android.content.Context;

import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.fuj.hangcity.R;
import com.fuj.hangcity.adapter.base.RVAdapter;
import com.fuj.hangcity.adapter.base.RVHolder;

import java.util.List;

/**
 * Created by fuj
 */
public class ScenicStepAdapter extends RVAdapter<RouteStep> {

    public ScenicStepAdapter(Context context, List<RouteStep> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(RVHolder holder, RouteStep routeStep) {
        if(routeStep instanceof WalkingRouteLine.WalkingStep) {
            holder.setImageResource(R.id.item_stepIV, R.mipmap.ic_walk);
            holder.setText(R.id.item_stepTV, ((WalkingRouteLine.WalkingStep) routeStep).getInstructions());
        } else if(routeStep instanceof TransitRouteLine.TransitStep) {
            switch (((TransitRouteLine.TransitStep) routeStep).getStepType().name()) {
                case "WAKLING":
                    holder.setImageResource(R.id.item_stepIV, R.mipmap.ic_walk);
                    break;
                case "BUSLINE":
                    holder.setImageResource(R.id.item_stepIV, R.mipmap.ic_transit);
                    break;
                case "SUBWAY":
                    holder.setImageResource(R.id.item_stepIV, R.mipmap.ic_subway);
                    break;
                default:
                    break;
            }
            holder.setText(R.id.item_stepTV, ((TransitRouteLine.TransitStep) routeStep).getInstructions());
        } else {
            holder.setImageResource(R.id.item_stepIV, R.mipmap.ic_drive);
            holder.setText(R.id.item_stepTV, ((DrivingRouteLine.DrivingStep) routeStep).getInstructions());
        }
    }
}
