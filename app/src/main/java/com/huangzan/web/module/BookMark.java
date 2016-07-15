package com.huangzan.web.module;

/**
 * Created by huangzan on 16/7/14.
 */
public class BookMark {
    private int id;
    private String name;
    private String url;

    public BookMark(){

    }
    public BookMark(int id,String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
