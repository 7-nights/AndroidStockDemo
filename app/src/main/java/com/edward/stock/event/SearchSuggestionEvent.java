package com.edward.stock.event;

import com.edward.stock.model.BasicStockInfo;

import java.util.ArrayList;

/**
 * Created by 朱凌峰 on 8-18.
 */
public class SearchSuggestionEvent {
    private ArrayList<BasicStockInfo> searchSuggeestion;

    public SearchSuggestionEvent(ArrayList<BasicStockInfo> searchSuggeestion) {
        this.searchSuggeestion = searchSuggeestion;
    }

    public ArrayList<BasicStockInfo> getSearchSuggeestion() {
        return searchSuggeestion;
    }
}
