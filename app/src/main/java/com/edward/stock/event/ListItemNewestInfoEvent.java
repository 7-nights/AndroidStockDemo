package com.edward.stock.event;

import com.edward.stock.model.ListItemNewestInfo;

import java.util.ArrayList;

/**
 * Created by 朱凌峰 on 9-6.
 */
public class ListItemNewestInfoEvent {
    private ArrayList<ListItemNewestInfo> infos;

    public ListItemNewestInfoEvent(ArrayList<ListItemNewestInfo> infos) {
        this.infos = infos;
    }

    public ArrayList<ListItemNewestInfo> getInfos() {
        return infos;
    }
}
