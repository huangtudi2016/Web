package com.huangzan.web.module;

/**
 * Created by huangzan on 16/7/14.
 */
public class BookMark {
    private long id;
    private String name;
    private String url;

    public BookMark(){

    }
    public BookMark(long id,String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
