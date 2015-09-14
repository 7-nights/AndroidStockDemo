package com.edward.stock.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.edward.stock.event.ListItemNewestInfoEvent;
import com.edward.stock.event.RefreshEvent;
import com.edward.stock.model.ListItemNewestInfo;
import com.edward.stock.util.OkHttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class GetListStockInfoService extends Service implements Callback {
    private static final String SERVER_URL = "http://hq.sinajs.cn/list=";
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private String stockIds;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stockIds = intent.getStringExtra("stockIds");
        EventBus.getDefault().post(new RefreshEvent(true));
        getLastTimePrices(stockIds);
        return super.onStartCommand(intent, flags, startId);
    }

    public void getLastTimePrices(final String stockIds) {
        Request request = new Request.Builder()
                .url(SERVER_URL + stockIds)
                .build();
        OkHttpUtil.enqueue(request, this);
    }

    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) throws IOException {
        ArrayList<ListItemNewestInfo> infos = new ArrayList<>();
//        String str = response.toString();
        InputStream in = response.body().byteStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer sb = new StringBuffer();
        String l;
        while ((l = reader.readLine()) != null) {
            sb.append(l);
        }
        String[] lines = sb.toString().split(";");
        for (String line : lines) {
            String[] nums = line.split(",");
            float current = Float.parseFloat(nums[3]);
            float closeYesterday = Float.parseFloat(nums[2]);
            boolean isHalting = nums[1].equals("0.00");
            float diff = current - closeYesterday;
            Float rate = isHalting ? 0 : 100 * diff / closeYesterday;
            int status = rate > 0 ? 1 : rate < 0 ? -1 : 0;
            infos.add(new ListItemNewestInfo("", "", "", nums[3], decimalFormat.format(rate), isHalting, status));
        }
        EventBus.getDefault().post(new ListItemNewestInfoEvent(infos));
    }
}
