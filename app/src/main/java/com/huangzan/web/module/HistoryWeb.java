package com.huangzan.web.module;

import java.util.Date;

/**
 * Created by huangzan on 16/7/14.
 */
public class HistoryWeb {
    private long id;
    private String name;
    private String url;
    private long date;

    public HistoryWeb() {
    }

    public HistoryWeb(long id, String name, String url, long date) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
