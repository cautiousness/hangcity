package com.fuj.hangcity.activity.main.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fuj.hangcity.R;
import com.fuj.hangcity.activity.infodetail.InfoDetailActivity;
import com.fuj.hangcity.adapter.base.RVAdapter;
import com.fuj.hangcity.adapter.base.RVHolder;
import com.fuj.hangcity.model.info.Info;
import com.fuj.hangcity.model.info.InfoResult;
import com.fuj.hangcity.net.NetworkHelper;
import com.fuj.hangcity.tools.Constant;
import com.fuj.hangcity.utils.ToastUtils;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fuj
 */
public class InfoItemFragment extends Fragment {
    private boolean isVisible;
    private boolean isPrepared;
    private boolean isFirst = true;
    private String type;
    private SwipeRefreshLayout swipeRL;
    private RVAdapter<Info> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(Constant.BUNDLE_INFO_ITEM_TYPE)) {
            type = (String) getArguments().get(Constant.BUNDLE_INFO_ITEM_TYPE);
        }

        adapter = new RVAdapter<Info>(getContext(), new ArrayList<Info>(0), R.layout.item_info) {
            @Override
            public void convert(RVHolder vHolder, final Info info) {
                Glide.with(getActivity()).load(info.thumbnail_pic_s03)
                    .placeholder(R.mipmap.bg_default).into((ImageView) vHolder.getView(R.id.item_previewIV));
                vHolder.setText(R.id.item_titleTV, getString(R.string.info_title, info.title));
                vHolder.setText(R.id.item_typeTV, info.realtype != null ? info.realtype : info.category);
                vHolder.setText(R.id.item_timeTV, info.date);
                vHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InfoItemFragment.this.getActivity(), InfoDetailActivity.class);
                        intent.putExtra(Constant.BUNDLE_INFO_EXTRA_URL, info.url);
                        startActivity(intent);
                    }
                });
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_info_item, container, false);

        RecyclerView infoRV = (RecyclerView) view.findViewById(R.id.commRV);
        infoRV.setLayoutManager(new LinearLayoutManager(getContext()));
        infoRV.setAdapter(adapter);

        swipeRL = (SwipeRefreshLayout) view.findViewById(R.id.swipeRL);
        swipeRL.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    private void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        initData();
        isFirst = false;
    }

    protected void onInvisible() {}

    public void initData() {
        setLoadingIndicator(true);
        NetworkHelper.getInstance().getInfo(type)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<InfoResult>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                setLoadingIndicator(false);
                ToastUtils.showToast(getContext(), getString(R.string.error_load_failed));
            }

            @Override
            public void onNext(InfoResult infoResult) {
                setLoadingIndicator(false);
                if(infoResult != null && infoResult.result != null) {
                    adapter.updateRecyclerView(infoResult.result.data);
                }
            }
        });
    }

    private void setLoadingIndicator(final boolean isRefresh) {
        if (getView() == null) {
            return;
        }

        swipeRL.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRL.setRefreshing(isRefresh);
            }
        }, 500);
    }
}
