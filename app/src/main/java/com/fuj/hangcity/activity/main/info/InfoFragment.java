package com.fuj.hangcity.activity.main.info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fuj.hangcity.R;
import com.fuj.hangcity.base.BaseFragment;
import com.fuj.hangcity.tools.Constant;

/**
 * Created by fuj
 */
public class InfoFragment extends BaseFragment implements InfoContract.View {

    protected void findViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.view_info, container, false);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        setViewMargins(mTabLayout, 0, getStatusBarHeight(), 0, 0);

        String[] mTitles = getResources().getStringArray(R.array.info_item);
        String[] mTypes = getResources().getStringArray(R.array.info_type);
        InfoAdapter adapter = new InfoAdapter(getActivity().getSupportFragmentManager(), mTitles, mTypes);
        mViewPager.setAdapter(adapter);

        for(String title : mTitles) {
            mTabLayout.addTab(mTabLayout.newTab().setText(title));
        }
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void setPresenter(InfoContract.Presenter presenter) {}

    private static class InfoAdapter extends FragmentStatePagerAdapter {
        private String[] mTitles;
        private String[] mTypes;

        public InfoAdapter(FragmentManager fm, String[] titles, String[] types) {
            super(fm);
            mTitles = titles;
            mTypes = types;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.BUNDLE_INFO_ITEM_TYPE, mTypes[position]);
            InfoItemFragment itemFragment = new InfoItemFragment();
            itemFragment.setArguments(bundle);
            return itemFragment;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}