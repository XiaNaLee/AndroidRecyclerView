package com.rcv.lee.androidrecyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * AndroidRecyclerView
 * com.rcv.lee.androidrecyclerview
 * Created by lee on 15/8/28.
 * Email:lee131483@gmail.com
 */
public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public abstract T getItem(int position);
}