package com.edward.stock.model;

/**
 * Created by 朱凌峰 on 8-17.
 */
public class BasicStockInfo {
    private String id;
    private String name;
    private String market;

    public BasicStockInfo(String id, String name, String market) {
        this.id = id;
        this.name = name;
        this.market = market;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }
}
