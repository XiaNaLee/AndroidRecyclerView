package com.rcv.lee.androidrecyclerview.item;

import android.view.View;
import android.widget.TextView;

import com.lee.recyclerview.adapter.AdapterItem;
import com.rcv.lee.androidrecyclerview.R;

/**
 * AndroidRecyclerView
 * com.rcv.lee.androidrecyclerview.item
 * Created by lee on 15/10/28.
 * Email:lee131483@gmail.com
 */
public class Item implements AdapterItem<String> {

    private TextView mTextView;

    @Override
    public int getLayoutResId() {
        return R.layout.recycler_item_view;
    }

    @Override
    public void onBindViews(View root) {
        mTextView = (TextView) root.findViewById(R.id.textView);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(String model, int position) {
        mTextView.setText(String.valueOf(position));
    }
}
