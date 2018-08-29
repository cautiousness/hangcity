package com.fuj.hangcity.activity.main;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.fuj.hangcity.R;
import com.fuj.hangcity.activity.main.info.InfoFragment;
import com.fuj.hangcity.activity.main.info.InfoPresenter;
import com.fuj.hangcity.activity.main.scenic.ScenicFragment;
import com.fuj.hangcity.activity.main.scenic.ScenicPresenter;
import com.fuj.hangcity.activity.main.user.UserFragment;
import com.fuj.hangcity.activity.main.user.UserPresenter;
import com.fuj.hangcity.activity.main.weather.WeatherFragment;
import com.fuj.hangcity.activity.main.weather.WeatherPresenter;
import com.fuj.hangcity.base.BaseActivity;
import com.fuj.hangcity.utils.LogUtils;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

/**
 * @author fuj
 */
public class MainActivity extends BaseActivity {
    private Fragment[] fragments;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.d("app is start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();

        BottomBar mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.noTopOffset();
        mBottomBar.noNavBarGoodness();
        mBottomBar.noTabletGoodness();
        mBottomBar.setMaxFixedTabs(4);
        mBottomBar.setActiveTabColor(getResources().getColor(R.color.colorPrimary));
        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.bb_menu_weather:
                        showFragment(fragments[0]);
                        setTitle(R.string.title_weather);
                        getToolBar().show();
                        break;
                    case R.id.bb_menu_info:
                        showFragment(fragments[1]);
                        getToolBar().hide();
                        break;
                    case R.id.bb_menu_scenic:
                        showFragment(fragments[2]);
                        setTitle(R.string.title_scenic);
                        getToolBar().hide();
                        break;
                    case R.id.bb_menu_user:
                        showFragment(fragments[3]);
                        getToolBar().hide();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {}
        });

        getWindow().setBackgroundDrawable(null);
    }

    private void initFragments() {
        fragments = new Fragment[4];
        fragments[0] = getSupportFragmentManager().findFragmentById(R.id.weather);
        fragments[1] = getSupportFragmentManager().findFragmentById(R.id.info);
        fragments[2] = getSupportFragmentManager().findFragmentById(R.id.scenic);
        fragments[3] = getSupportFragmentManager().findFragmentById(R.id.user);
        new WeatherPresenter((WeatherFragment) fragments[0]);
        new InfoPresenter((InfoFragment) fragments[1]);
        new ScenicPresenter((ScenicFragment) fragments[2]);
        new UserPresenter((UserFragment) fragments[3]);
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for(Fragment temp : fragments) {
            transaction.hide(temp);
        }
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager audioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
        int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(current > 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current - 1, 0);
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(current < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current + 1, 0);
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                showExitSystem(this);
                return false;
            default:
                break;
        }
        return true;
    }

    /**
     * 退出应用
     * @param context 上下文
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void showExitSystem(Context context) {
        Builder builder = new Builder(context, AlertDialog.BUTTON_POSITIVE);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("是否退出应用").setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}