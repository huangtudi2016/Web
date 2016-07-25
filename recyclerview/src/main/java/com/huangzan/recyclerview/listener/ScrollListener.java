package com.huangzan.recyclerview.listener;

import android.support.v7.widget.RecyclerView;


public abstract class ScrollListener extends HidingScrollListener {
    public ScrollListener(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
        loadMore = true;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    //加载更多
    public abstract void onLoadMore();
}
