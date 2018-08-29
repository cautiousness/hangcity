package com.fuj.hangcity.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuj.hangcity.R;
import com.fuj.hangcity.utils.LogUtils;

import java.util.List;

public abstract class FootAdapter<T> extends RecyclerView.Adapter<RVHolder> {
    protected int mLayoutId;
    private boolean isEnd = false;

    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;
    private final static int TYPE_ITEM = 0;
    private final static int TYPE_FOOT = 1;

    private OnItemClickListener mOnItemClickListener;

    public FootAdapter(Context context, List<T> datas, int layoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public RVHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            RVHolder rvHolder = RVHolder.get(mContext, null, parent, mLayoutId, -1);
            setListener(parent, rvHolder, viewType);
            return rvHolder;
        } else if(viewType == TYPE_FOOT){
            return RVHolder.get(mContext, null, parent, R.layout.item_foot, -1);
        }
        return null;
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
        if(position + 1 != getItemCount()) {
            holder.updatePosition(position);
            convert(holder, mDatas.get(position));
        } else {
            holder.setVisible(R.id.progressBar, !isEnd);
            holder.setText(R.id.content, isEnd ? "无更多数据" : "正在加载...");
        }
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
    public int getItemViewType(int position) {
        return position + 1 == getItemCount() ? TYPE_FOOT : TYPE_ITEM;
    }

    public void setFootVisible(boolean isShow) {
        isEnd = getItemViewType(getItemCount() - 1) == TYPE_FOOT && isShow;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas.size() == 0 ? 0 : mDatas.size() + 1;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(ViewGroup parent, View view, T t, int position);
        boolean onItemLongClick(ViewGroup parent, View view, T t, int position);
    }
}
