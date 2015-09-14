package com.edward.stock.model;

import java.util.Map;

/**
 * Created by 朱凌峰 on 8-7.
 */
public class RealTimeInfo {
    private int code;
    private String msg;
    private Map<String, Map<String, Map<String, Object>>> data;

    public Map<String, Map<String, Map<String, Object>>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, Map<String, Object>>> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
