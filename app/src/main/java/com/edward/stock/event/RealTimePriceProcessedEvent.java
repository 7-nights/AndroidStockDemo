package com.edward.stock.event;

import com.edward.stock.model.RealTimePriceProcessed;

/**
 * Created by 朱凌峰 on 8-11.
 */
public class RealTimePriceProcessedEvent {
    private RealTimePriceProcessed realTimePriceProcessed;

    public RealTimePriceProcessedEvent(RealTimePriceProcessed realTimePriceProcessed) {
        this.realTimePriceProcessed = realTimePriceProcessed;
    }

    public RealTimePriceProcessed getRealTimePriceProcessed() {
        return realTimePriceProcessed;
    }
}
