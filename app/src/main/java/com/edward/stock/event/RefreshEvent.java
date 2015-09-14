package com.edward.stock.event;

/**
 * Created by 朱凌峰 on 9-2.
 */
public class RefreshEvent {
    private boolean isRefreshing;

    public RefreshEvent(boolean isRefreshing) {

        this.isRefreshing = isRefreshing;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }
}
