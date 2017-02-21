package com.example.cpr.route;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class OneDayContactLog extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	//创建�?个数据库表，分别表示用户在分为七个时隙的�?天的时间中，每个时隙的�?�信状况 
	private String oneDayContactLogTable = "create table if not exists oneDayContactLogTable" +
			"( contactLable varchar(20) primary key, " +
			" TS1 integer, " +
			" TS2 integer, " +
			" TS3 integer, " +
			" TS4 integer, " +
			" TS5 integer, " +
			" TS6 integer, " +
			" TS7 integer)";	
	
	public OneDayContactLog(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version); 
	}
	
	public OneDayContactLog(Context context, String name) {
		super(context, name, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) { 
		db.execSQL(oneDayContactLogTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("update oneDayContactLogTable"); 
	}

}
