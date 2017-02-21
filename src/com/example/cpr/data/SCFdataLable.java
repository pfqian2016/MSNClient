package com.example.cpr.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SCFdataLable extends SQLiteOpenHelper{

	private static final int VERSION = 1;
	private final String dataLableTable = "create table if not exists dataLableTable" +
			"(id integer primary key, " +
			"destination varchar(20), " +
			"source varchar(20), " +
			"dataName varchar(20), " + 
			"dataLable varchar(100))";
	public SCFdataLable(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version); 
	}
	
	public SCFdataLable(Context context, String name) {
		super(context, name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(dataLableTable);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("update datalable");
	}

}
