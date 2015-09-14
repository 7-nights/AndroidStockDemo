package com.edward.stock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.edward.stock.service.GetListStockInfoService;

public class StockService extends Service {
    private AlarmManager am;
    private Intent i;
    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (pendingIntent != null) {
            am.cancel(pendingIntent);
        }
        if (intent.getBooleanExtra("isOne", true)) {
            i = new Intent(this, GetOneStockInfoService.class);
            i.putExtra("marketId", intent.getStringExtra("marketId"));
        } else {
            i = new Intent(this, GetListStockInfoService.class);
            i.putExtra("stockIds", intent.getStringExtra("stockIds"));
        }
        pendingIntent = PendingIntent.getService(this, 1, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC, 0, 10000, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}
