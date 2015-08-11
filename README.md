# AndroidRecyclerView
Android RecyclerView support addHeaderView,addFooterView and loadMore.

![RecyclerView logo](http://raw.github.com/DukeLee1989/AndroidRecyclerView/master/recyclerview_support_head_footer_v3.gif)

##代码设置

 **添加headView**
`RcvSupportHead$FooterViewAdapter mAdapter`  
`mAdapter.addHeader(headerView)`

 **添加footerView** 
`mAdapter.addFooter(footerView)`

 **设置emptyView**  
`mAdapter.setEmptyView(emptyView)`

**RecyclerView滑动到bottom**  
`mRecyclerView.addOnScrollListener(new
OnRcvScrollListener(OnRcvScrollListener.LAYOUT_MANAGERTYPE.LINEAR, new OnBottomListener() {
            @Override
            public void onBottom() {
            //TODO 
            }
        }));`