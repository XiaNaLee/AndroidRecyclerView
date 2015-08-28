package com.rcv.lee.androidrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * AndroidRecyclerView
 * com.rcv.lee.androidrecyclerview
 * Created by lee on 15/8/28.
 * Email:lee131483@gmail.com
 */
public abstract class ArrayAdapter<T, VH extends RecyclerView.ViewHolder> extends BaseAdapter<T, VH> {
    private List<T> mItems;
    private Context mContext;
    private Object mLock = new Object();
    private boolean mNotifyOnChange = true;

    public ArrayAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    public ArrayAdapter(Context context, T... items) {
        this(context, Arrays.asList(items));
    }

    public ArrayAdapter(Context context, List<T> items) {
        mContext = context;
        mItems = items;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    public final void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    private final void notifyDataSetChangedIfNeed(){
        if(mNotifyOnChange) notifyDataSetChanged();
    }

    public void add(T item){
        synchronized (mLock) {
            mItems.add(item);
        }
        if(mNotifyOnChange) notifyItemInserted(mItems.lastIndexOf(item));
    }

    public void addAll(Collection<T> items){
        synchronized (mLock) {
            mItems.addAll(items);
        }
        notifyDataSetChangedIfNeed();
    }

    public void add(int position, T item){
        synchronized (mLock) {
            mItems.add(position, item);
        }
        if(mNotifyOnChange) notifyItemInserted(position);
    }

    public void remove(T item){
        synchronized (mLock) {
            mItems.remove(item);
        }
        notifyDataSetChangedIfNeed();
    }

    public void clear(){
        synchronized (mLock) {
            mItems.clear();
        }
        notifyDataSetChangedIfNeed();
    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }


}