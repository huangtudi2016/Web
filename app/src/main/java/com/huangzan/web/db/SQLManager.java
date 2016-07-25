package com.huangzan.web.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.huangzan.web.module.BookMark;
import com.huangzan.web.module.HistoryWeb;

import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    private static final String DEG_TAG = "SQLManager";

    private DBHelper helper;
    private SQLiteDatabase db;
    List<BookMark> bookMarks = new ArrayList<BookMark>();
    List<HistoryWeb> historyWebs = new ArrayList<HistoryWeb>();

    public SQLManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * 添加书签
     *
     * @param bookMark 书签
     */
    public void addBookMark(BookMark bookMark) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", bookMark.getName());
            values.put("url", bookMark.getUrl());
            long rowid = db.insert("bookmark", null, values);
            Log.i(DEG_TAG,"rowid---------:"+rowid);
//            db.execSQL("INSERT INTO bookmark(name,url) VALUES( ?, ?)", new Object[]{bookMark.getName(), bookMark.getUrl()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    /**
     * 修改书签
     *
     * @param bookMark 要修改的书签
     * @return 修改成功与否 true 成功,flase 失败
     */
    public boolean updateBookMark(BookMark bookMark) {
        boolean flag = false;
        Log.i(DEG_TAG,"id:"+bookMark.getId());
        Log.i(DEG_TAG,"name:"+bookMark.getName());
        Log.i(DEG_TAG,"url:"+bookMark.getUrl());
        ContentValues cv = new ContentValues();
        cv.put("name", bookMark.getName());
        cv.put("url", bookMark.getUrl());
        int rowNum = db.update("bookmark", cv, "id=?", new String[]{String.valueOf(bookMark.getId())});
        Log.i(DEG_TAG,"rowNum:"+rowNum);
        if (rowNum > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 删除书签
     *
     * @param url 书签网址
     * @return 删除成功与否 true 成功,flase 失败
     */
    public boolean deleteBookMark(String url) {
        boolean flag = false;
        int result = db.delete("bookmark", "url=?", new String[]{url});
        if (result != -1) {
            flag = true;
        }
        return flag;
    }

    public List<BookMark> getAllBookMark() {

        Cursor cursor = queryTheCursor("bookmark");
        while (cursor.moveToNext()) {
            BookMark bookMark = new BookMark();
            Log.i(DEG_TAG,"cursorid:"+cursor.getInt(0));
            bookMark.setId(cursor.getInt(0));
            bookMark.setName(cursor.getString(1));
            bookMark.setUrl(cursor.getString(2));
            bookMarks.add(bookMark);
        }
        cursor.close();
        return bookMarks;
    }

    /**
     * query all bookmark, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor(String tableName) {
        Cursor c = db.rawQuery("SELECT * FROM "+tableName, null);
        return c;
    }

    /**
     * 判断网址是否已存为书签
     *
     * @param url 网址
     * @return true 是,false 否
     */
    public boolean isBookMarkExist(String url) {
        Cursor cursor = db.rawQuery("SELECT id FROM bookmark WHERE url = ?",new String[]{url});
        if (cursor.getCount()!=0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }

    /**
     * 添加历史记录
     *
     * @param history 历史纪录
     */
    public void addHistory(HistoryWeb history) {
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", history.getName());
            values.put("url", history.getUrl());
            values.put("date", history.getDate());
            long rowid = db.insert("history", null, values);
            Log.i(DEG_TAG,"rowid history--------:"+rowid);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public List<HistoryWeb> getAllHistory() {

        Cursor cursor = queryTheCursor("history");
        while (cursor.moveToNext()) {
            HistoryWeb historyWeb = new HistoryWeb();
            Log.i(DEG_TAG,"history cursorid:"+cursor.getInt(0));
            historyWeb.setId(cursor.getInt(0));
            historyWeb.setName(cursor.getString(1));
            historyWeb.setUrl(cursor.getString(2));
            historyWeb.setDate(cursor.getLong(3));
            historyWebs.add(historyWeb);
        }
        cursor.close();
        return historyWebs;
    }

    /**
     * 删除单个历史记录
     *
     * @param date 浏览时间
     * @return 删除成功与否 true 成功,flase 失败
     */
    public boolean deleteHistory(long date) {
        boolean flag = false;
        int result = db.delete("history", "date=?", new String[]{String.valueOf(date)});
        if (result != -1) {
            flag = true;
        }
        return flag;
    }

    /**
     * 删除所有历史记录
     *
     * @return 删除成功与否 true 成功,flase 失败
     */
    public boolean deleteAllHistory() {
        boolean flag = false;
        int result = db.delete("history", null, null);
//        db.execSQL("select * from sqlite_sequence");
//        db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = ‘history’;");
        if (result != -1) {
            flag = true;
        }
        return flag;
    }
    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
