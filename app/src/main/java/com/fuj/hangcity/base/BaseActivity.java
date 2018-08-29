package com.fuj.hangcity.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fuj.hangcity.R;
import com.fuj.hangcity.cache.ConCache;
import com.fuj.hangcity.utils.ActivityUtils;
import com.fuj.hangcity.utils.DensityUtils;
import com.fuj.hangcity.utils.LogUtils;


/**
 * Created by fuj
 */
public abstract class BaseActivity extends AppCompatActivity {
    private TextView titleTV;
    private ActionBar ab;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.i(getClass().getSimpleName());
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTheme(R.style.AppTheme_NoActionBar);
        ActivityUtils.addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup container = (ViewGroup) View.inflate(this, R.layout.activity_toolbar, null);
        View mUserView = LayoutInflater.from(this).inflate(layoutResID, null);
        mToolBar = (Toolbar) container.findViewById(R.id.id_tool_bar);
        titleTV = (TextView) mToolBar.findViewById(R.id.toolbar_title);
        titleTV.getLayoutParams().width = DensityUtils.dp2px(this, 2 * ConCache.getInstance().getFloat(this, ConCache.LOGIN_HEAD_WIDTH));
        container.addView(mUserView);

        setContentView(container);
        setImmerseLayout(container);
        setSupportActionBar(mToolBar);
        onCreateCustomToolBar(mToolBar);

        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        titleTV.setVisibility(View.GONE);
    }

    /**
     * 设置状态栏透明效果
     * @param view 根视图
     */
    public void setImmerseLayout(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 获取状态栏高度
     * @param context 上下文
     * @return 状态栏高度
     */
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if(resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onResume() {
        LogUtils.i(getClass().getSimpleName());
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i(getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i(getClass().getSimpleName());
    }

    private void onCreateCustomToolBar(Toolbar toolbar){
        toolbar.showOverflowMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    public ActionBar getToolBar() {
        return ab;
    }

    public int getToolbarHeight(Context context) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight + getStatusBarHeight(context);
    }

    public void setToolbarBackground(float offset) {
        mToolBar.setBackgroundColor(Color.argb((int) (offset * 255), 63, 81, 181));
    }

    protected void setViewMargins(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams paramTest2 = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        paramTest2.setMargins(left, top, right, bottom);
        view.requestLayout();
    }

    public void setCustomToolbar(View view) {
        ab.setCustomView(view);
    }

    public void setBackToolbar() {
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void setTitle(String title) {
        titleTV.setText(title);
    }

    public void setTitle(int resId) {
        titleTV.setText(getString(resId));
    }

    public void setCustomView(View view) {
        ab.setCustomView(view);
    }
}