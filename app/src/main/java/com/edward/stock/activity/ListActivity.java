package com.edward.stock.activity;

import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.edward.stock.MainActivity;
import com.edward.stock.R;
import com.edward.stock.StockService;
import com.edward.stock.adapter.ListAdapter;
import com.edward.stock.event.ListItemDataRemovingEvent;
import com.edward.stock.event.ListItemDataSavingEvent;
import com.edward.stock.event.ListItemNewestInfoEvent;
import com.edward.stock.event.RefreshEvent;
import com.edward.stock.event.SearchSuggestionEvent;
import com.edward.stock.model.BasicStockInfo;
import com.edward.stock.model.ListItemNewestInfo;
import com.edward.stock.service.GetListStockInfoService;
import com.edward.stock.util.StockUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class ListActivity extends AppCompatActivity implements ListAdapter.OnItemClickLitener {

    private Toolbar toolbar;
    private Menu menu;

    private SearchView searchView;
    private SimpleCursorAdapter suggestionAdapter;
    private ArrayList<BasicStockInfo> suggestions;
    private Timer timer;
    private TimerTask timerTask;

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;

    private List<ListItemNewestInfo> newestInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        EventBus.getDefault().register(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setSubtitle("峰峰自选股");
        timer = new Timer();
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newestInfos = new ArrayList<>();
        newestInfos = DataSupport.order("id desc").find(ListItemNewestInfo.class);
        listAdapter = new ListAdapter(this, newestInfos);
        listAdapter.setOnItemClickLitener(this);
        recyclerView.setAdapter(listAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (newestInfos.isEmpty()) {
            stopStockService();
        } else {
            startStockService(newestInfos);
        }
    }

    @Override
    protected void onStop() {
        for (ListItemNewestInfo info : newestInfos) {
            info.updateAll("stockId = ?", info.getStockId());
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopStockService();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_list, menu);
        final MenuItem menuItemSearch = menu.findItem(R.id.menuitem_search);
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
                menuItemSearch.collapseActionView();
                Intent i = new Intent(ListActivity.this, MainActivity.class);
                i.putExtra("market", itemInfo.getMarket());
                i.putExtra("id", itemInfo.getId());
                i.putExtra("name", itemInfo.getName());
                startActivity(i);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuitem_refresh:
                setRefreshActionButtonState(true);
                Intent i = new Intent(this, GetListStockInfoService.class);
                i.putExtra("stockIds", getStringFromInfos(newestInfos));
                i.putExtra("isOne", false);
                startService(i);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    private void startStockService(List<ListItemNewestInfo> newestInfos) {
        setRefreshActionButtonState(true);
        Intent i = new Intent(this, StockService.class);
        i.putExtra("stockIds", getStringFromInfos(newestInfos));
        i.putExtra("isOne", false);
        startService(i);
    }

    private void stopStockService() {
        setRefreshActionButtonState(false);
        Intent i = new Intent(this, StockService.class);
        stopService(i);
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

    public void onEventMainThread(ListItemNewestInfoEvent event) {
        ArrayList<ListItemNewestInfo> infos = event.getInfos();
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            ListItemNewestInfo oldInfo = newestInfos.get(i);
            ListItemNewestInfo newInfo = infos.get(i);
            oldInfo.setLastPrice(newInfo.getLastPrice());
            oldInfo.setLastDifferenceRate(newInfo.getLastDifferenceRate());
            oldInfo.setHalting(newInfo.isHalting());
            oldInfo.setStatus(newInfo.getStatus());
            listAdapter.notifyItemChanged(i);
        }
        setRefreshActionButtonState(false);
    }

    public void onEventMainThread(ListItemDataSavingEvent event) {
        ListItemNewestInfo itemNewestInfo = event.getInfo();
        newestInfos.add(0, itemNewestInfo);
        listAdapter.notifyItemInserted(0);
    }

    public void onEventMainThread(ListItemDataRemovingEvent event) {
        int size = newestInfos.size();
        for (int i = 0; i < size; i++) {
            if (newestInfos.get(i).getStockId().equals(event.getStockId())) {
                newestInfos.remove(i);
                listAdapter.notifyItemRemoved(i);
                return;
            }
        }
    }

    public void onEventMainThread(RefreshEvent event) {
        setRefreshActionButtonState(event.isRefreshing());
    }

    private void setRefreshActionButtonState(boolean refreshing) {
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

    private String getStringFromInfos(List<ListItemNewestInfo> infos) {
        String split = "";
        StringBuffer sb = new StringBuffer();
        for (ListItemNewestInfo info : infos) {
            sb.append(split + info.getMarket() + info.getStockId());
            split = ",";
        }
        return sb.toString();
    }

    @Override
    public void onItemClick(View view, int position) {
        ListItemNewestInfo info = newestInfos.get(position);
        Intent i = new Intent(ListActivity.this, MainActivity.class);
        i.putExtra("market", info.getMarket());
        i.putExtra("id", info.getStockId());
        i.putExtra("name", info.getName());
        startActivity(i);
    }
}
