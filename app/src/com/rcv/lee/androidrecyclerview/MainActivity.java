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

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private TextView mFooterTv;

    private ProgressBar mFooterPb;

    private List<String> mShowDatas;

    private int mCurPageNo;

    private int MAX_PAGE_NUM=5;

    private RcvSupportHead$FooterViewAdapter mAdapter;

    private WeakHandler mWeakHandler;

    private static final int MSG_LOAD_MORE_DATA=0x01;

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
        mWeakHandler=new WeakHandler(this);
        mCurPageNo=1;
        for (int i = 0; i < 30; i++) {
            mShowDatas.add(String.valueOf(i));
        }
    }

    private void loadMoreData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mIsRefreshing=true;
                SystemClock.sleep(2000);
                int size=mShowDatas.size();
                List<String> data=new ArrayList<String>();
                for (int i = 1; i <= 30; i++) {
                    data.add(String.valueOf(size+i-1));
                }
                Message message=mWeakHandler.obtainMessage();
                message.what=MSG_LOAD_MORE_DATA;
                message.obj=data;
                mWeakHandler.sendMessage(message);
            }
        }).start();
    }

    private void setUpView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new OnRcvScrollListener(OnRcvScrollListener.LAYOUT_MANAGER_TYPE.LINEAR, new OnBottomListener() {
            @Override
            public void onBottom() {
                if(!mIsRefreshing)
                if(mCurPageNo<MAX_PAGE_NUM){
                    loadMoreData();
                    mCurPageNo++;
                }else{
                    updateFooterView();
                }
            }
        }));

        mAdapter = new RcvSupportHead$FooterViewAdapter(mShowDatas);
        mRecyclerView.setAdapter(mAdapter);

        View headerView = LayoutInflater.from(this)
                .inflate(R.layout.recycler_header_item, null);
        RecyclerView.LayoutParams headerLayoutParams=new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,200);
        headerView.setLayoutParams(headerLayoutParams);
        mAdapter.addHeader(headerView);

        View footerView = LayoutInflater.from(this)
                .inflate(R.layout.recycler_footer_item, null);
        mFooterTv=(TextView)footerView.findViewById(R.id.footer_item_text);
        mFooterPb=(ProgressBar)footerView.findViewById(R.id.footer_item_progressBar);
        RecyclerView.LayoutParams footerLayoutParams=new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,200);
        footerView.setLayoutParams(footerLayoutParams);
        mAdapter.addFooter(footerView);

    }

    private void updateFooterView(){
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
                        activity.updateView((List<String>)msg.obj);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void updateView(List<String> data) {
        mAdapter.addDatas(data);
        mIsRefreshing=false;
    }


}
