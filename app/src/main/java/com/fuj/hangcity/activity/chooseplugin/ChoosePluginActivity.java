package com.fuj.hangcity.activity.chooseplugin;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fuj.hangcity.R;
import com.fuj.hangcity.adapter.ChoosePluginAdapter;
import com.fuj.hangcity.adapter.base.RVAdapter;
import com.fuj.hangcity.base.BaseActivity;
import com.fuj.hangcity.tools.Constant;
import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.DLUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.BMapManager.getContext;

/**
 * Created by fuj
 */
public class ChoosePluginActivity extends BaseActivity {
    private ChoosePluginAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseplugin);
        getToolBar().show();

        initAdapter();
        initView();
        listPlugins();
    }

    private void initAdapter() {
        adapter = new ChoosePluginAdapter(getContext(), new ArrayList<PluginItem>(), R.layout.item_plugin);
        adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener<PluginItem>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, PluginItem pluginItem, int position) {
                DLPluginManager pluginManager = DLPluginManager.getInstance(ChoosePluginActivity.this);
                pluginManager.startPluginActivity(ChoosePluginActivity.this,
                        new DLIntent(pluginItem.packageInfo.packageName, pluginItem.launcherActivityName));

                if (pluginItem.launcherServiceName != null) {
                    DLIntent intent = new DLIntent(pluginItem.packageInfo.packageName, pluginItem.launcherServiceName);
                    pluginManager.startPluginService(ChoosePluginActivity.this, intent);
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, PluginItem pluginItem, int position) {
                return false;
            }
        });
    }

    private void initView() {
        RecyclerView pluginRV = (RecyclerView) findViewById(R.id.choose_pluginRV);
        pluginRV.setLayoutManager(new LinearLayoutManager(getContext()));
        pluginRV.setAdapter(adapter);
    }

    private void listPlugins() {
        File file = new File(Constant.PLUGIN_DIR);
        File[] plugins = file.listFiles();
        if (plugins == null || plugins.length == 0) {
            return;
        }

        List<PluginItem> pluginList = new ArrayList<>();
        for (File plugin : plugins) {
            PluginItem item = new PluginItem();
            item.pluginPath = plugin.getAbsolutePath();
            item.packageInfo = DLUtils.getPackageInfo(this, item.pluginPath);
            if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0) {
                item.launcherActivityName = item.packageInfo.activities[0].name;
            }
            if (item.packageInfo.services != null && item.packageInfo.services.length > 0) {
                item.launcherServiceName = item.packageInfo.services[0].name;
            }
            pluginList.add(item);
            DLPluginManager.getInstance(this).loadApk(item.pluginPath);
        }

        adapter.updateRecyclerView(pluginList);
    }

    public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;
        public String launcherActivityName;
        public String launcherServiceName;
    }
}
