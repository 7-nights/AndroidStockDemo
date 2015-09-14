package com.edward.stock.event;

/**
 * Created by 朱凌峰 on 9-5.
 */
public class ListItemDataRemovingEvent {
    private String stockId;

    public ListItemDataRemovingEvent(String stockId) {
        this.stockId = stockId;
    }

    public String getStockId() {
        return stockId;
    }
}
