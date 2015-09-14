package com.edward.stock.cursor;

import android.database.AbstractCursor;

import com.edward.stock.model.BasicStockInfo;

import java.util.ArrayList;

/**
 * Created by 朱凌峰 on 8-17.
 */
public class SearchSuggestionsCursor extends AbstractCursor {
    private ArrayList<BasicStockInfo> results;
    private String[] columnNames = {"_id", "id", "name", "market"};

    public SearchSuggestionsCursor(ArrayList<BasicStockInfo> results) {
        this.results = results;
    }

    @Override
    public int getCount() {
        return results == null ? 0 : results.size();
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public long getLong(int column) {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public String getString(int column) {
        switch (column) {
            case 0:
                return results.get(mPos).getId();
            case 1:
                return results.get(mPos).getName();
            case 2:
                return results.get(mPos).getMarket();
            default:
                return null;
        }
    }

    @Override
    public short getShort(int column) {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public int getInt(int column) {
        return 0;
    }

    @Override
    public float getFloat(int column) {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public double getDouble(int column) {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public boolean isNull(int column) {
        return false;
    }

}
