package com.edward.stock;

import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.edward.stock.event.ListItemDataRemovingEvent;
import com.edward.stock.event.ListItemDataSavingEvent;
import com.edward.stock.event.RealTimePriceProcessedEvent;
import com.edward.stock.event.RefreshEvent;
import com.edward.stock.event.SearchSuggestionEvent;
import com.edward.stock.model.BasicStockInfo;
import com.edward.stock.model.ListItemNewestInfo;
import com.edward.stock.model.RealTimePriceProcessed;
import com.edward.stock.util.StockUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private Intent fromIntent;

    private Toolbar toolbar;
    private Menu menu;
    private SearchView searchView;
    private SimpleCursorAdapter suggestionAdapter;
    private ArrayList<BasicStockInfo> suggestions;
    private Timer timer;
    private TimerTask timerTask;
    private View viewOpeningToday, viewCloseYesterday, viewTotalVolume, viewTurnoverRate, viewMaxPrice, viewMinPrice, viewTurnover, viewAmplitude;
    private TextView tvCurrentPrice, tvCurrentDifference, tvCurrentDifferenceRate, tvOpeningToday, tvCloseYesterday, tvTotalVolume, tvTurnoverRate, tvMaxPrice, tvMinPrice, tvTurnover, tvAmplitude;
    private String str;
    private Gson gson;
    private LineChart lineChart;
    private YAxis leftAxis, rightAxis;
    private ArrayList<String> xVals;
    private LimitLine llOpeningToday;
    private DecimalFormat dfAxis = new DecimalFormat("#0.00");

    private boolean isInDatabase;
    private String market, stockId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        fromIntent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(fromIntent.getStringExtra("name"));
        toolbar.setSubtitle(fromIntent.getStringExtra("id"));
        timer = new Timer();
        setSupportActionBar(toolbar);

        initViews();
        initXVals();

        Intent i = new Intent(this, StockService.class);
        market = fromIntent.getStringExtra("market");
        stockId = fromIntent.getStringExtra("id");
        i.putExtra("marketId", market + stockId);
        i.putExtra("isOne", true);
        startService(i);

        lineChart = (LineChart) findViewById(R.id.line_chart);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");
        lineChart.setHighlightEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);

        leftAxis = lineChart.getAxisLeft();
        leftAxis.setStartAtZero(false);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return dfAxis.format(value);
            }
        });
        leftAxis.setLabelCount(5, true);
        leftAxis.setTextColor(getResources().getColor(R.color.grey_dark));
        rightAxis = lineChart.getAxisRight();
        rightAxis.setStartAtZero(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(5, true);
        rightAxis.setTextColor(getResources().getColor(R.color.grey_dark));
//        rightAxis.setEnabled(false);
        XAxis bottomAxis = lineChart.getXAxis();
        bottomAxis.setLabelsToSkip(59);
        bottomAxis.setTextColor(getResources().getColor(R.color.grey_dark));
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED.BOTTOM);
//        leftAxis.set

        str = null;
        isInDatabase = !DataSupport.where("stockId = ?", fromIntent.getStringExtra("id")).find(ListItemNewestInfo.class).isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItemSearch = menu.findItem(R.id.menuitem_search);
        final MenuItem menuItemEdit = menu.findItem(R.id.menuitem_edit);
        if (isInDatabase) {
            menuItemEdit.setIcon(getResources().getDrawable(R.mipmap.ic_close));
        }
        searchView = (SearchView) MenuItemCompat.getActionView(menuItemSearch);
        suggestionAdapter = new SimpleCursorAdapter(this, R.layout.item_search_suggestion, null, new String[]{"name", "id"}, new int[]{R.id.tv_suggestion_name, R.id.tv_suggestion_id}, 0);
        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                BasicStockInfo itemInfo = suggestions.get(position);
                searchView.clearFocus();
                setRefreshActionButtonState(true);
                market = itemInfo.getMarket();
                stockId = itemInfo.getId();
                toolbar.setTitle(itemInfo.getName());
                toolbar.setSubtitle(stockId);
                menuItemSearch.collapseActionView();
                isInDatabase = !DataSupport.where("stockId = ?", stockId).find(ListItemNewestInfo.class).isEmpty();
                if (isInDatabase) {
                    menuItemEdit.setIcon(getResources().getDrawable(R.mipmap.ic_close));
                } else {
                    menuItemEdit.setIcon(getResources().getDrawable(R.mipmap.ic_add));
                }
                menuItemEdit.setTitle(isInDatabase ? "remove" : "add");

                Intent i = new Intent(MainActivity.this, StockService.class);
                i.putExtra("marketId", market + stockId);
                i.putExtra("isOne", true);
                startService(i);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                if (timerTask != null) {
//                    timerTask.cancel();
//                }
//                StockUtil.searchStock(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (timerTask != null) {
                    timerTask.cancel();
                }
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        StockUtil.searchStock(newText);
                    }
                };
                timer.schedule(timerTask, 500);
                return false;
            }
        });

        menuItemEdit.setTitle(isInDatabase ? "remove" : "add");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuitem_refresh:
                setRefreshActionButtonState(true);
//                Intent i = new Intent(this, GetOneStockInfoService.class);
//                i.putExtra("marketId", market + stockId);
//                i.putExtra("isOne", true);
//                startService(i);
                break;
            case R.id.menuitem_edit:
                if (isInDatabase) {
                    if (DataSupport.deleteAll(ListItemNewestInfo.class, "stockId = ?", stockId) > 0) {
                        EventBus.getDefault().post(new ListItemDataRemovingEvent(stockId));
                        isInDatabase = false;
                        item.setIcon(getResources().getDrawable(R.mipmap.ic_add));
                        Toast.makeText(this, getResources().getText(R.string.text_remove_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getResources().getText(R.string.text_remove_failed), Toast.LENGTH_SHORT).show();
                    }
                    isInDatabase = false;
                } else {
                    ListItemNewestInfo itemNewestInfo = new ListItemNewestInfo(stockId, market, toolbar.getTitle().toString(), "0.00", "0.00 %", false, 0);
                    if (itemNewestInfo.save()) {
                        EventBus.getDefault().post(new ListItemDataSavingEvent(itemNewestInfo));
                        isInDatabase = true;
                        item.setIcon(getResources().getDrawable(R.mipmap.ic_close));
                        Toast.makeText(this, getResources().getText(R.string.text_add_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getResources().getText(R.string.text_add_failed), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (menu == null) {
            return;
        }
        final MenuItem refreshItem = menu.findItem(R.id.menuitem_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                MenuItemCompat.setActionView(refreshItem, R.layout.toolbar_indeterminate_refresh);
            } else {
                MenuItemCompat.setActionView(refreshItem, null);
            }
        }
    }

    public void onEventMainThread(RealTimePriceProcessedEvent event) {
        setNumbers(event.getRealTimePriceProcessed());
        ArrayList<Float> p = event.getRealTimePriceProcessed().getPrices();

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        int count = p.size();
        for (int i = 0; i < count; i++) {
            yVals.add(new Entry(p.get(i), i));
        }
        LineDataSet set = new LineDataSet(yVals, "DataSet 1");
        set.disableDashedLine();
        set.setColor(getResources().getColor(R.color.blue_light));
        set.setLineWidth(1f);
        set.setFillAlpha(65);
        set.setFillColor(Color.BLACK);
        set.setDrawValues(false);
        set.setDrawFilled(true);
        set.setDrawCircles(false);
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set);
        LineData data = new LineData(xVals, dataSets);

        lineChart.setData(data);
//        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        setRefreshActionButtonState(false);
    }

    public void onEventMainThread(SearchSuggestionEvent event) {
        suggestions = event.getSearchSuggeestion();
        String[] columns = {
                BaseColumns._ID, "name", "id"
        };
        MatrixCursor cursor = new MatrixCursor(columns);
        for (int i = 0; i < suggestions.size(); i++) {
            String[] tmp = {Integer.toString(i), suggestions.get(i).getName(), suggestions.get(i).getId()};
            cursor.addRow(tmp);
        }
        suggestionAdapter.swapCursor(cursor);
    }

    public void onEventMainThread(RefreshEvent event) {
        setRefreshActionButtonState(event.isRefreshing());
    }

    private void setColors(int status) {
        int id;
        switch (status) {
            case -1:
                id = getResources().getColor(R.color.green);
                break;
            case 1:
                id = getResources().getColor(R.color.red);
                break;
            default:
                id = getResources().getColor(R.color.white);
        }
        tvCurrentPrice.setTextColor(id);
        tvCurrentDifferenceRate.setTextColor(id);
        tvCurrentDifference.setTextColor(id);
    }

    private void setNumbers(RealTimePriceProcessed r) {
        final float floatCloseYesterday = Float.parseFloat(r.getCloseYesterday());
        if (llOpeningToday == null) {
            llOpeningToday = new LimitLine(floatCloseYesterday);
            llOpeningToday.setLabel(r.getCloseYesterday());
            llOpeningToday.setLabelPosition(LimitLine.LimitLabelPosition.POS_LEFT);
            llOpeningToday.enableDashedLine(10, 10, 5);
            llOpeningToday.setTextColor(getResources().getColor(R.color.grey_dark));
            llOpeningToday.setLineColor(getResources().getColor(R.color.green_grey));
            leftAxis.addLimitLine(llOpeningToday);
            rightAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return dfAxis.format((value - floatCloseYesterday) / floatCloseYesterday * 100) + "%";
                }
            });
        }
        setColors(r.getStatus());
        tvCurrentPrice.setText(r.getCurrentPrice());
        tvCurrentDifference.setText(r.getCurrentDifference());
        tvCurrentDifferenceRate.setText(r.getCurrentDifferenceRate());
        tvOpeningToday.setText(r.getOpeningToday());
        tvCloseYesterday.setText(r.getCloseYesterday());
        tvTotalVolume.setText(r.getTotalVolume());
        tvTurnoverRate.setText(r.getTurnoverRate());
        tvMaxPrice.setText(r.getMaxPrice());
        tvMinPrice.setText(r.getMinPrice());
        tvTurnover.setText(r.getTurnover());
        tvAmplitude.setText(r.getAmplitude());
    }

    private void initViews() {
        viewOpeningToday = findViewById(R.id.rl_opening_today);
        viewCloseYesterday = findViewById(R.id.rl_close_yesterday);
        viewTotalVolume = findViewById(R.id.rl_total_volume);
        viewTurnoverRate = findViewById(R.id.rl_turnover_rate);
        viewMaxPrice = findViewById(R.id.rl_max_price);
        viewMinPrice = findViewById(R.id.rl_min_price);
        viewTurnover = findViewById(R.id.rl_turnover);
        viewAmplitude = findViewById(R.id.rl_amplitude);

        ((TextView) viewOpeningToday.findViewById(R.id.tv_title)).setText("今开");
        ((TextView) viewCloseYesterday.findViewById(R.id.tv_title)).setText("昨收");
        ((TextView) viewTotalVolume.findViewById(R.id.tv_title)).setText("成交量");
        ((TextView) viewTurnoverRate.findViewById(R.id.tv_title)).setText("换手率");
        ((TextView) viewMaxPrice.findViewById(R.id.tv_title)).setText("最高");
        ((TextView) viewMinPrice.findViewById(R.id.tv_title)).setText("最低");
        ((TextView) viewTurnover.findViewById(R.id.tv_title)).setText("成交额");
        ((TextView) viewAmplitude.findViewById(R.id.tv_title)).setText("振幅");

        tvCurrentPrice = (TextView) findViewById(R.id.tv_current_price);
        tvCurrentDifference = (TextView) findViewById(R.id.tv_current_difference);
        tvCurrentDifferenceRate = (TextView) findViewById(R.id.tv_current_difference_rate);
        tvOpeningToday = (TextView) viewOpeningToday.findViewById(R.id.tv_number);
        tvCloseYesterday = (TextView) viewCloseYesterday.findViewById(R.id.tv_number);
        tvTotalVolume = (TextView) viewTotalVolume.findViewById(R.id.tv_number);
        tvTurnoverRate = (TextView) viewTurnoverRate.findViewById(R.id.tv_number);
        tvMaxPrice = (TextView) viewMaxPrice.findViewById(R.id.tv_number);
        tvMinPrice = (TextView) viewMinPrice.findViewById(R.id.tv_number);
        tvTurnover = (TextView) viewTurnover.findViewById(R.id.tv_number);
        tvAmplitude = (TextView) viewAmplitude.findViewById(R.id.tv_number);
    }

    private void initXVals() {
        xVals = new ArrayList<String>();
        for (int i = 30; i < 60; i++) {
            xVals.add("09:" + i);
        }
        for (int i = 0; i < 10; i++) {
            xVals.add("10:0" + i);
        }
        for (int i = 10; i < 60; i++) {
            xVals.add("10:" + i);
        }
        for (int i = 0; i < 10; i++) {
            xVals.add("11:0" + i);
        }
        for (int i = 10; i < 31; i++) {
            xVals.add("11:" + i);
        }
        for (int i = 0; i < 10; i++) {
            xVals.add("13:0" + i);
        }
        for (int i = 10; i < 60; i++) {
            xVals.add("13:" + i);
        }
        for (int i = 0; i < 10; i++) {
            xVals.add("14:0" + i);
        }
        for (int i = 10; i < 60; i++) {
            xVals.add("14:" + i);
        }
        xVals.add("15:00");
    }

}
