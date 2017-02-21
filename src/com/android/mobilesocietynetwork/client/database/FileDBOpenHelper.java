package com.android.mobilesocietynetwork.client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FileDBOpenHelper extends SQLiteOpenHelper {
	
	//文件列表
		public static final String CREATE_FILELIST = "CREATE TABLE filelist_table (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"fileName TEXT," +
				" destination TEXT," +
				"filePath TEXT)";

	public FileDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_FILELIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
