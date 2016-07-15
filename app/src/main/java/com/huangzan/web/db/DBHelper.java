package com.huangzan.web.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{


	private static final String DATABASE_NAME = "browser.db";
	private static final int DATABASE_VERSION = 1;

	public static final String CREATE_TABLE_BOOKMARKS =
			"CREATE TABLE IF NOT EXISTS bookmark (id INTEGER PRIMARY KEY, name TEXT NOT NULL, url TEXT NOT NULL)";
	public static final String CREATE_TABLE_HISTORY =
			"CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY, name TEXT NOT NULL, url TEXT NOT NULL, date LONG NOT NULL)";

	public DBHelper(Context context){
		//CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_TABLE_BOOKMARKS);
		sqLiteDatabase.execSQL(CREATE_TABLE_HISTORY);
	}


	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		sqLiteDatabase.execSQL("ALTER TABLE bookmark ADD COLUMN other STRING");
	}
}
