package com.fuj.hangcity.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuj.hangcity.utils.LogUtils;

import java.util.List;

public abstract class RVAdapter<T> extends RecyclerView.Adapter<RVHolder> {
    protected int mLayoutId;

    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;

    public RVAdapter(Context context, List<T> datas, int layoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public RVHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        RVHolder rvHolder = RVHolder.get(mContext, null, parent, mLayoutId, -1);
        setListener(parent, rvHolder, viewType);
        return rvHolder;
    }

    protected int getPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition();
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    protected void setListener(final ViewGroup parent, final RVHolder RVHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        RVHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = getPosition(RVHolder);
                    mOnItemClickListener.onItemClick(parent, v, mDatas.get(position), position);
                    LogUtils.i("** click item ** -> position = " + position + ", count = " + getItemCount() + ", " + mDatas.get(position));
                }
            }
        });

        RVHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = getPosition(RVHolder);
                    LogUtils.i("** long click item ** -> position = " + position + ", count = " + getItemCount() + ", " + mDatas.get(position));
                    return mOnItemClickListener.onItemLongClick(parent, v, mDatas.get(position), position);
                }
                return false;
            }
        });
    }


    @Override
    public void onBindViewHolder(RVHolder holder, int position) {
        holder.updatePosition(position);
        convert(holder, mDatas.get(position));
    }

    public abstract void convert(RVHolder holder, T t);

    public void updateRecyclerView(List<T> list){
        mDatas = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(ViewGroup parent, View view, T t, int position);
        boolean onItemLongClick(ViewGroup parent, View view, T t, int position);
    }
}
