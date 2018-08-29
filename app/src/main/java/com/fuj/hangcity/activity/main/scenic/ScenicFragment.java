package com.fuj.hangcity.activity.main.scenic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fuj.hangcity.R;
import com.fuj.hangcity.activity.main.MainActivity;
import com.fuj.hangcity.activity.scenicdetail.ScenicDetailActivity;
import com.fuj.hangcity.adapter.base.FootAdapter;
import com.fuj.hangcity.adapter.base.RVHolder;
import com.fuj.hangcity.base.BaseFragment;
import com.fuj.hangcity.model.scenic.Scenic;
import com.fuj.hangcity.widget.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuj
 */
public class ScenicFragment extends BaseFragment implements ScenicContract.View {
    private FootAdapter<Scenic> adapter;
    private ScenicContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FootAdapter<Scenic>(getContext(), new ArrayList<Scenic>(), R.layout.item_scenic) {
            @Override
            public void convert(RVHolder holder, final Scenic scenic) {
                holder.setText(R.id.item_titleTV, scenic.title);
                Glide.with(getActivity()).load(scenic.imgurl).placeholder(R.mipmap.bg_default)
                        .into((ImageView) holder.getView(R.id.item_previewIV));
                holder.setText(R.id.item_addr, getString(R.string.scenic_addr, scenic.address));
                holder.setText(R.id.item_gradeTV, getString(R.string.scenic_grade ,scenic.grade));
                holder.setText(R.id.item_priceTV, getString(R.string.scenic_price, scenic.price_min));
                holder.setOnClickListener(R.id.item_goIV, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ScenicFragment.this.getActivity(), ScenicDetailActivity.class);
                        intent.putExtra(getString(R.string.scenic_bundle), scenic.title);
                        startActivity(intent);
                    }
                });
            }
        };
    }

    protected void findViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.view_scenic, container, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setViewMargins(toolbar, 0, getStatusBarHeight(), 0, 0);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("杭城美景");

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView scenicRV = (RecyclerView) findViewById(R.id.commRV);
        scenicRV.setAdapter(adapter);
        scenicRV.setLayoutManager(layoutManager);
        scenicRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPresenter.onScroll(layoutManager, adapter);
            }
        });

        swipeRL = (ScrollChildSwipeRefreshLayout) findViewById(R.id.swipeRL);
        swipeRL.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setLoadingIndicator(true);
                mPresenter.getData(getContext());
            }
        });

        loadBackdrop();
    }

    @Override
    public void setPresenter(ScenicContract.Presenter presenter) {
        mPresenter = presenter;
        mPresenter.start();
        mPresenter.getData(getContext());
    }

    private void loadBackdrop() {
        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(R.mipmap.bg_scenic).centerCrop().into(imageView);
    }

    @Override
    public void updateRecyclerView(List<Scenic> scenics) {
        adapter.updateRecyclerView(scenics);
    }
}
