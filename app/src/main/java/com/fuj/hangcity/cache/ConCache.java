package com.fuj.hangcity.cache;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;

import com.fuj.hangcity.utils.DensityUtils;


/**
 * Created by fuj
 */
public class ConCache {
    public static final String GROUP_LOGO_WIDTH = "GROUP_LOGO_WIDTH";
    public static final String GROUP_LOGO_HEIGHT = "GROUP_LOGO_HEIGHT";
    public static final String GROUP_ICON_WIDTH = "GROUP_ICON_WIDTH";
    public static final String GROUP_SWIPEEDIT_WIDTH = "GROUP_SWIPEEDIT_WIDTH";
    public static final String GROUP_NAMEEDIT_WIDTH = "GROUP_NAMEEDIT_WIDTH";
    public static final String LOGIN_HEAD_WIDTH = "LOGIN_HEAD_WIDTH";
    public static final String MAP_DIALOG_WIDTH = "MAP_DIALOG_WIDTH";
    public static final String RADIO_PANEL_HEIGHT = "RADIO_PANEL_HEIGHT";
    public static final String RADIO_BUTTON_WIDTH = "RADIO_BUTTON_WIDTH";
    public static final String RADIO_MEMBER_HEAD_WIDTH = "RADIO_MEMBER_HEAD_WIDTH";
    public static final String WEATHER_TEMP_COLUMN_WIDTH = "WEATHER_TEMP_COLUMN_WIDTH";
    public static final String WEATHER_HOUR_COLUMN_WIDTH = "WEATHER_HOUR_COLUMN_WIDTH";
    public static final String WEATHER_TEMP_HEIGHT = "WEATHER_TEMP_HEIGHT";
    public static final String WEATHER_HOUR_HEIGHT = "WEATHER_HOUR_HEIGHT";
    public static final String WEATHER_TEMP_MINI_HEIGHT = "WEATHER_TEMP_MINI_HEIGHT";
    public static final String WEATHER_HOUR_MINI_HEIGHT = "WEATHER_HOUR_MINI_HEIGHT";
    public static final String REGIST_RESEND_WIDTH = "REGIST_RESEND_WIDTH";
    public static final String LOGIN_TEXT_SIZE = "LOGIN_TEXT_SIZE";
    public static final String DISPLAY_WIDTH = "DISPLAY_WIDTH";
    public static final String DISPLAY_HEIGHT = "DISPLAY_HEIGHT";
    public static final String SERVER_ADDR = "";

    private static ConCache instance;
    private CacheManager mCacheManager;

    public static ConCache getInstance() {
        if(instance == null) {
            instance = new ConCache();
        }
        return instance;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void init(Context context) {
        mCacheManager = CacheManager.getInstance(context);
        float display_width;
        float display_height;
        if (Build.VERSION.SDK_INT >= 13) {
            Point dims = new Point();
            ((Activity)context).getWindowManager().getDefaultDisplay().getSize(dims);
            display_width = DensityUtils.px2dp(context, dims.x);
            display_height = DensityUtils.px2dp(context, dims.y);
        } else {
            display_width = DensityUtils.px2dp(context, ((Activity)context).getWindowManager().getDefaultDisplay().getWidth());
            display_height = DensityUtils.px2dp(context, ((Activity)context).getWindowManager().getDefaultDisplay().getHeight());
        }

        mCacheManager.put(GROUP_LOGO_WIDTH, String.valueOf(dp2px(context, display_width)));
        mCacheManager.put(GROUP_LOGO_HEIGHT, String.valueOf(dp2px(context, display_width * 3 / 4)));
        mCacheManager.put(GROUP_ICON_WIDTH, String.valueOf(dp2px(context, display_width / 8)));
        mCacheManager.put(GROUP_SWIPEEDIT_WIDTH, String.valueOf(dp2px(context, display_width / 4)));
        mCacheManager.put(GROUP_NAMEEDIT_WIDTH, String.valueOf(dp2px(context, display_width * 2 / 3)));
        mCacheManager.put(LOGIN_HEAD_WIDTH, String.valueOf(dp2px(context, display_width / 3)));
        mCacheManager.put(MAP_DIALOG_WIDTH, String.valueOf(dp2px(context, display_width * 2 / 3)));
        mCacheManager.put(RADIO_PANEL_HEIGHT, String.valueOf(dp2px(context, display_height * 13 / 16)));
        mCacheManager.put(RADIO_BUTTON_WIDTH, String.valueOf(dp2px(context, display_width / 3)));
        mCacheManager.put(WEATHER_TEMP_COLUMN_WIDTH, String.valueOf(dp2px(context, display_width / 6)));
        mCacheManager.put(WEATHER_HOUR_COLUMN_WIDTH, String.valueOf(dp2px(context, display_width / 10)));
        mCacheManager.put(WEATHER_HOUR_HEIGHT, String.valueOf(dp2px(context, display_width / 3)));
        mCacheManager.put(WEATHER_TEMP_HEIGHT, String.valueOf(dp2px(context, display_width * 5 / 4)));
        mCacheManager.put(WEATHER_TEMP_MINI_HEIGHT, String.valueOf(dp2px(context, display_width * 5 / 400)));
        mCacheManager.put(WEATHER_HOUR_MINI_HEIGHT, String.valueOf(dp2px(context, display_width / 30)));
        mCacheManager.put(RADIO_MEMBER_HEAD_WIDTH, String.valueOf(dp2px(context, display_width / 7)));
        mCacheManager.put(REGIST_RESEND_WIDTH, String.valueOf(dp2px(context, display_width / 4)));

        mCacheManager.put(LOGIN_TEXT_SIZE, String.valueOf(display_width / 18));
        mCacheManager.put(DISPLAY_WIDTH, String.valueOf(display_width));
        mCacheManager.put(DISPLAY_HEIGHT, String.valueOf(display_height));
    }

    public Integer getInt(Context context, String key) {
        return (int) Float.parseFloat(getStr(context, key));
    }

    public float getFloat(Context context, String key) {
        return Float.parseFloat(getStr(context, key));
    }

    public String getStr(Context context, String key) {
        if(mCacheManager == null || mCacheManager.get(key) == null) {
            init(context);
        }

        String value = mCacheManager.get(key);
        return value == null ? "0" : value;
    }

    private int dp2px(Context context, float value) {
        return DensityUtils.dp2px(context, value);
    }
}
