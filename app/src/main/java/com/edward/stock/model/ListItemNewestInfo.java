package com.edward.stock.model;

import org.litepal.crud.DataSupport;

/**
 * Created by 朱凌峰 on 9-3.
 */
public class ListItemNewestInfo extends DataSupport {
    private String stockId;
    private String market;
    private String name;
    private String lastPrice;
    private String lastDifferenceRate;
    private boolean isHalting;
    private int status;

    public ListItemNewestInfo() {
    }

    public ListItemNewestInfo(String stockId, String market, String name, String lastPrice, String lastDifferenceRate, boolean isHalting, int status) {
        this.stockId = stockId;
        this.market = market;
        this.name = name;
        this.lastPrice = lastPrice;
        this.lastDifferenceRate = lastDifferenceRate;
        this.isHalting = isHalting;
        this.status = status;
    }

    public boolean isHalting() {
        return isHalting;
    }

    public void setHalting(boolean isHalting) {
        this.isHalting = isHalting;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getLastDifferenceRate() {
        return lastDifferenceRate;
    }

    public void setLastDifferenceRate(String lastDifferenceRate) {
        this.lastDifferenceRate = lastDifferenceRate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
