package com.rcv.lee.androidrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MainActivity extends AppCompatActivity {

    private PtrClassicFrameLayout mPtrClassicFrameLayout;

    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private TextView mFooterTv;

    private ProgressBar mFooterPb;

    private List<String> mShowDatas;

    private int mCurPageNo;

    private int MAX_PAGE_NUM = 5;

    private RcvSupportHead$FooterViewAdapter mAdapter;

    private WeakHandler mWeakHandler;

    private static final int MSG_LOAD_MORE_DATA = 0x01;

    private static final int MSG_REFRESH_DATA = 0x02;

    private boolean mIsRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        setUpView();
    }

    private void initData() {
        mShowDatas = new ArrayList<>();
        mWeakHandler = new WeakHandler(this);
        mCurPageNo = 1;
    }

    private void loadMoreData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mIsRefreshing = true;
                SystemClock.sleep(2000);
                int size = mShowDatas.size();
                List<String> data = new ArrayList<String>();
                for (int i = 1; i <= 30; i++) {
                    data.add(String.valueOf(size + i - 1));
                }
                Message message = mWeakHandler.obtainMessage();
                message.what = MSG_LOAD_MORE_DATA;
                message.obj = data;
                mWeakHandler.sendMessage(message);
            }
        }).start();
    }

    private void refreshData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCurPageNo = 1;
                mIsRefreshing = true;
                SystemClock.sleep(2000);
                List<String> data = new ArrayList<String>();
                for (int i = 1; i <= 30; i++) {
                    data.add(String.valueOf(i - 1));
                }
                Message message = mWeakHandler.obtainMessage();
                message.what = MSG_REFRESH_DATA;
                message.obj = data;
                mWeakHandler.sendMessage(message);
            }
        }).start();
    }

    private void setUpView() {
        mPtrClassicFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.ptr_frame_id);

        mPtrClassicFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                refreshData();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new OnRcvScrollListener(OnRcvScrollListener.LAYOUT_MANAGER_TYPE.LINEAR, new OnBottomListener() {
            @Override
            public void onBottom() {
                if (!mIsRefreshing && !mShowDatas.isEmpty())
                    if (mCurPageNo < MAX_PAGE_NUM) {
                        loadMoreData();
                        mCurPageNo++;
                    } else {
                        updateFooterView();
                    }
            }
        }));

        mAdapter = new RcvSupportHead$FooterViewAdapter(mShowDatas);
        mRecyclerView.setAdapter(mAdapter);

        //post 方法为了获取mRecyclerView的高度
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View emptyView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.recycler_empty_item, null);
                int height = mRecyclerView.getHeight();
                RecyclerView.LayoutParams emptyViewLayoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, height);
                emptyView.setLayoutParams(emptyViewLayoutParams);
                mAdapter.setEmptyView(emptyView);

                View headerView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.recycler_header_item, null);
                RecyclerView.LayoutParams headerLayoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 200);
                headerView.setLayoutParams(headerLayoutParams);
                mAdapter.addHeader(headerView);

                View footerView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.recycler_footer_item, null);
                mFooterTv = (TextView) footerView.findViewById(R.id.footer_item_text);
                mFooterPb = (ProgressBar) footerView.findViewById(R.id.footer_item_progressBar);
                RecyclerView.LayoutParams footerLayoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 200);
                footerView.setLayoutParams(footerLayoutParams);
                mAdapter.addFooter(footerView);
            }
        });

       mWeakHandler.postDelayed(new Runnable() {
           @Override
           public void run() {
               mPtrClassicFrameLayout.autoRefresh();
           }
       },500);
    }

    private void updateFooterView() {
        mFooterPb.setVisibility(View.INVISIBLE);
        mFooterTv.setText(R.string.last_page);
    }

    private static class WeakHandler extends Handler {
        WeakReference<MainActivity> weakReference;

        public WeakHandler(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReference.get();
            if (null != activity) {
                switch (msg.what) {
                    case MSG_LOAD_MORE_DATA:
                        activity.updateView((List<String>) msg.obj);
                        break;
                    case MSG_REFRESH_DATA:
                        activity.refreshView((List<String>) msg.obj);
                    default:
                        break;
                }
            }
        }
    }

    private void updateView(List<String> data) {
        if(mPtrClassicFrameLayout.isRefreshing())
          mPtrClassicFrameLayout.refreshComplete();
        mAdapter.addDatas(data);
        mIsRefreshing = false;
    }

    private void refreshView(List<String> data) {
        if(mPtrClassicFrameLayout.isRefreshing())
            mPtrClassicFrameLayout.refreshComplete();
        mAdapter.refreshData(data);
        mIsRefreshing = false;
    }


}
