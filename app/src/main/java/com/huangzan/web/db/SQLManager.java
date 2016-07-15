package com.huangzan.web.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huangzan.web.module.BookMark;

import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    private DBHelper helper;
    private SQLiteDatabase db;

    private boolean flag = false;


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
            db.execSQL("INSERT INTO bookmark VALUES(null, ?, ?)", new Object[]{bookMark.getName(), bookMark.getUrl()});
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
        ContentValues cv = new ContentValues();
        cv.put("name", bookMark.getName());
        cv.put("url", bookMark.getUrl());
        int rowNum = db.update("bookmark", cv, "id=?", new String[]{String.valueOf(bookMark.getId())});
        if (rowNum != 0) {
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
        int result = db.delete("bookmark", "url=?", new String[]{url});
        if (result != -1) {
            flag = true;
        }
        return flag;
    }

    public List<BookMark> getAllBookMark() {
        ArrayList<BookMark> bookMarks = new ArrayList<>();
        Cursor cursor = queryTheCursor();
        while (cursor.moveToNext()) {
            BookMark bookMark = new BookMark();
            bookMark.setId(Long.valueOf(cursor.getColumnIndex("_id")));
            bookMark.setName(cursor.getString(cursor.getColumnIndex("name")));
            bookMark.setUrl(cursor.getString(cursor.getColumnIndex("url")));
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
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM bookmark", null);
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
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
