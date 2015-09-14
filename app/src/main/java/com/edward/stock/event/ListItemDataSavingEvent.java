package com.edward.stock.event;

import com.edward.stock.model.ListItemNewestInfo;

/**
 * Created by 朱凌峰 on 9-5.
 */
public class ListItemDataSavingEvent {
    private ListItemNewestInfo info;

    public ListItemDataSavingEvent(ListItemNewestInfo info) {
        this.info = info;
    }

    public ListItemNewestInfo getInfo() {
        return info;
    }
}
