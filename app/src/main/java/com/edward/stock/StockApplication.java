package com.edward.stock;

import android.content.Context;

import com.orhanobut.logger.Logger;

import org.litepal.LitePalApplication;

/**
 * Created by 朱凌峰 on 8-10.
 */
public class StockApplication extends LitePalApplication {
    private static Context context;

    public static Context getContextAnywhere() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init();
        context = getApplicationContext();
    }
}
