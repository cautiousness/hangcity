package com.fuj.hangcity.activity.registandlogin;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.fuj.hangcity.R;
import com.fuj.hangcity.activity.scenicdetail.ScenicDetailFragment;
import com.fuj.hangcity.activity.scenicdetail.ScenicDetailPresenter;
import com.fuj.hangcity.base.BaseActivity;

/**
 * Created by fuj
 */
public class RegistAndLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registandlogin);
        getToolBar().hide();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        RegistAndLoginFragment fragment = new RegistAndLoginFragment();
        fragment.setArguments(getIntent().getExtras());
        transaction.add(R.id.fragment, fragment);
        transaction.commit();
        new RegistAndLoginPresenter(fragment);
    }
}
