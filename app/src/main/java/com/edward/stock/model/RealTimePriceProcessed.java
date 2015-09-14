package com.edward.stock.model;

import java.util.ArrayList;

/**
 * Created by 朱凌峰 on 8-7.
 */
public class RealTimePriceProcessed {
    private int status;
    private ArrayList<Float> prices;
    private String currentPrice;
    private String openingToday;
    private String closeYesterday;
    private String currentDifference;
    private String currentDifferenceRate;
    private String totalVolume;
    private String turnoverRate;
    private String maxPrice;
    private String minPrice;
    private String turnover;
    private String amplitude;

    public RealTimePriceProcessed(int status, ArrayList<Float> prices, String currentPrice, String openingToday, String closeYesterday, String currentDifference, String currentDifferenceRate, String totalVolume, String turnoverRate, String maxPrice, String minPrice, String turnover, String amplitude) {
        this.status = status;
        this.prices = prices;
        this.currentPrice = currentPrice;
        this.openingToday = openingToday;
        this.closeYesterday = closeYesterday;
        this.currentDifference = currentDifference;
        this.currentDifferenceRate = currentDifferenceRate;
        this.totalVolume = totalVolume;
        this.turnoverRate = turnoverRate;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.turnover = turnover;
        this.amplitude = amplitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOpeningToday() {
        return openingToday;
    }

    public void setOpeningToday(String openingToday) {
        this.openingToday = openingToday;
    }

    public ArrayList<Float> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<Float> prices) {
        this.prices = prices;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCloseYesterday() {
        return closeYesterday;
    }

    public void setCloseYesterday(String closeYesterday) {
        this.closeYesterday = closeYesterday;
    }

    public String getCurrentDifference() {
        return currentDifference;
    }

    public void setCurrentDifference(String currentDifference) {
        this.currentDifference = currentDifference;
    }

    public String getCurrentDifferenceRate() {
        return currentDifferenceRate;
    }

    public void setCurrentDifferenceRate(String currentDifferenceRate) {
        this.currentDifferenceRate = currentDifferenceRate;
    }

    public String getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(String totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(String turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(String amplitude) {
        this.amplitude = amplitude;
    }


}
