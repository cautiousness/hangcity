package com.fuj.hangcity.adapter;

import android.content.Context;

import com.fuj.hangcity.R;
import com.fuj.hangcity.activity.chooseplugin.ChoosePluginActivity;
import com.fuj.hangcity.adapter.base.RVAdapter;
import com.fuj.hangcity.adapter.base.RVHolder;
import com.ryg.utils.DLUtils;

import java.io.File;
import java.util.List;

/**
 * Created by fuj
 */
public class ChoosePluginAdapter extends RVAdapter<ChoosePluginActivity.PluginItem> {
    private Context mContext;

    public ChoosePluginAdapter(Context context, List<ChoosePluginActivity.PluginItem> datas, int layoutId) {
        super(context, datas, layoutId);
        mContext = context;
    }

    @Override
    public void convert(RVHolder holder, ChoosePluginActivity.PluginItem pluginItem) {
        holder.setImageDrawable(R.id.item_plugin_icon, DLUtils.getAppIcon(mContext, pluginItem.pluginPath));
        holder.setText(R.id.item_plugin_name, DLUtils.getAppLabel(mContext, pluginItem.pluginPath));
        holder.setText(R.id.item_plugin_apk, pluginItem.pluginPath.substring(pluginItem.pluginPath.lastIndexOf(File.separatorChar) + 1));
        holder.setText(R.id.item_plugin_package, pluginItem.packageInfo.applicationInfo.packageName + "\n" +
                pluginItem.launcherActivityName + "\n" +
                pluginItem.launcherServiceName);
    }
}
