package com.example.cpr.route;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class OneWeekContactLog extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	 
	private String oneWeekContactLog = "create table if not exists oneWeekContactLogTable " +
			"( id integer primary key, " +
			" username varchar(20), " +	 
			" ownLable varchar(20), " + 		 
			" totalLB varchar(100)," +	 
			" n_dayago varchar(20))";
	public OneWeekContactLog(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version); 
	}
	
	public OneWeekContactLog(Context context, String name) {
		super(context, name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(oneWeekContactLog);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("update oneWeekContactLog");
	} 
	
}
