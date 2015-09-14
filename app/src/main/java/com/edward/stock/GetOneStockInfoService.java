package com.edward.stock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.edward.stock.event.RealTimePriceProcessedEvent;
import com.edward.stock.event.RefreshEvent;
import com.edward.stock.model.RealTimeInfo;
import com.edward.stock.model.RealTimePriceProcessed;
import com.edward.stock.util.OkHttpUtil;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class GetOneStockInfoService extends Service implements Callback {
    private static final String SERVER_URL = "http://ifzq.gtimg.cn/appstock/app/minute/query?code=";
    private static Gson gson = new Gson();
    private String stockId;
    private DecimalFormat dfTotalVolume = new DecimalFormat("#0.00万手"), dfTurnover = new DecimalFormat("#0.00亿");

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stockId = intent.getStringExtra("marketId");
        EventBus.getDefault().post(new RefreshEvent(true));
        getRealTimePrices(stockId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getRealTimePrices(final String stockId) {
        Request request = new Request.Builder()
                .url(SERVER_URL + stockId)
                .build();
        OkHttpUtil.enqueue(request, this);
    }

    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) throws IOException {
        ArrayList<Float> p = new ArrayList<Float>();
        String str = response.body().string();
        RealTimeInfo realTimeInfo = gson.fromJson(str, RealTimeInfo.class);
        ArrayList<String> infos = (ArrayList<String>) realTimeInfo.getData().get(stockId).get("data").get("data");
        for (String info : infos) {
            String[] parts = info.split(" ");
            p.add(Float.parseFloat(parts[1]));
        }
        infos = (ArrayList<String>) realTimeInfo.getData().get(stockId).get("qt").get(stockId);
        int status;
        float diff = Float.parseFloat(infos.get(31));
        if (diff < 0) {
            status = -1;
        } else if (diff == 0) {
            status = 0;
        } else {
            status = 1;
        }
        String currentPrice = infos.get(3);
        String openingToday = infos.get(5);
        String closeYesterday = infos.get(4);
        String currentDifference = status == 1 ? ("+" + infos.get(31)) : infos.get(31);
        String currentDifferenceRate = status == 1 ? ("+" + infos.get(32) + "%") : (infos.get(32) + "%");
        String totalVolume = dfTotalVolume.format(Float.parseFloat(infos.get(36)) / 10000);
        String turnoverRate = infos.get(38) + "%";
        String maxPrice = infos.get(41);
        String minPrice = infos.get(42);
        String turnover = dfTurnover.format(Float.parseFloat(infos.get(37)) / 10000);
        String amplitude = infos.get(43) + "%";
        EventBus.getDefault().post(new RealTimePriceProcessedEvent(new RealTimePriceProcessed(status, p, currentPrice, openingToday, closeYesterday, currentDifference, currentDifferenceRate, totalVolume, turnoverRate, maxPrice, minPrice, turnover, amplitude)));
    }
}
