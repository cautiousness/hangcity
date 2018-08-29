package com.fuj.hangcity.activity.scenicdetail;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.fuj.hangcity.R;
import com.fuj.hangcity.base.BaseActivity;


/**
 * Created by fuj
 */
public class ScenicDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenic_detail);
        getToolBar().hide();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ScenicDetailFragment scenicDetailFragment = new ScenicDetailFragment();
        scenicDetailFragment.setArguments(getIntent().getExtras());
        transaction.add(R.id.fragment, scenicDetailFragment);
        transaction.commit();
        new ScenicDetailPresenter(scenicDetailFragment);
    }
}
