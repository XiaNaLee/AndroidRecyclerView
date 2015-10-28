package com.lee.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;


/**
 * Created by lee on 15/10/28.
 * Email:lee131483@gmail.com
 */
public abstract class MfCommonRcvAdapter<T> extends CommonRcvAdapter<T> {

    private static final String TAG = "MfCommonRcvAdapter";

    public AdapterView.OnItemClickListener mOnItemClickListener;

    public AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    protected MfCommonRcvAdapter(List<T> data) {
        super(data);
    }

    private static final int TYPE_HEADER = 0;

    private static final int TYPE_FOOTER = 1;

    private static final int TYPE_ITEM = 2;

    private static final int TYPE_EMPTY = 3;

    public View mHeaderView;

    public View mFooterView;

    public View mEmptyView;

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mHeaderView != null) {
            return new SimpleViewHolder(mHeaderView);
        } else if (viewType == TYPE_FOOTER && mFooterView != null) {
            return new SimpleViewHolder(mFooterView);
        } else if (viewType == TYPE_EMPTY && mEmptyView != null) {
            return new SimpleViewHolder(mEmptyView);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemCount() {
        int size = super.getItemCount();
        if (size == 0 && null != mEmptyView) {
            size = 1;
        } else {
            if (null != mHeaderView)
                size++;
            if (null != mFooterView)
                size++;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        int size = super.getItemCount();
        if (size == 0 && null != mEmptyView) {
            return TYPE_EMPTY;
        } else if (position < getHeadViewSize()) {
            return TYPE_HEADER;
        } else if (position >= getHeadViewSize() + size) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    /**
     * 载入ViewHolder，这里仅仅处理header和footer视图的逻辑
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        if (super.getItemCount() == 0 && getItemCount() == 1 && null != mEmptyView && position == 0) {
            Log.d(TAG, "处理emptyView");
            //处理emptyView
        } else if (null != mHeaderView && position == 0) {
            //处理headView
            Log.d(TAG, "处理headView");
        } else if (null != mFooterView && position == getItemCount() - 1) {
            //处理footView
            Log.d(TAG, "处理footView");
        } else {
            Log.d(TAG, "处理其他");
            if (mHeaderView != null) {
                position--;
            }
            super.onBindViewHolder(viewHolder, position);

            final int pos = position;
            // 设置点击事件
            if (mOnItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(null, viewHolder.itemView, pos, pos);
                    }
                });
            }
            // 设置长按事件
            if (mOnItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return mOnItemLongClickListener.onItemLongClick(null, viewHolder.itemView, pos, pos);
                    }
                });
            }
        }
    }

    private int getHeadViewSize() {
        return mHeaderView == null ? 0 : 1;
    }

    private int getFooterViewSize() {
        return mFooterView == null ? 0 : 1;
    }


    private T getItem(int position) {
        return getDataList().get(position - getHeadViewSize());
    }


    //remove a header from the adapter
    public void removeHeader(View header) {
        notifyItemRemoved(0);
        mHeaderView = null;
    }

    //add datas
    public void addDatas(List<T> data) {
        getDataList().addAll(data);
        notifyDataSetChanged();
    }

    //add data
    public void addData(T data) {
        getDataList().add(data);
        notifyItemInserted(getHeadViewSize() + getDataList().size() - 1);
    }

    //refresh data
    public void refreshData(List<T> datas) {
        getDataList().clear();
        addDatas(datas);
    }


}
