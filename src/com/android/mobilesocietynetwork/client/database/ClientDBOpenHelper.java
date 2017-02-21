package com.android.mobilesocietynetwork.client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * ���ڴ��������б������б����ݿ�
 * @author shijie
 *
 */
public class ClientDBOpenHelper extends SQLiteOpenHelper {
	
	//�����б�
	public static final String CREATE_FRIENDLIST = "CREATE TABLE friendlist_table (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"userid TEXT," +
			"groupname TEXT," +
			"friendname TEXT)";
	//�����б�
	public static final String CREATE_COMMUNITYLIST = "CREATE TABLE communitylist_table  (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		    "userid TEXT," +
			"communityname TEXT," +
			"password TEXT)";
	
	public ClientDBOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_FRIENDLIST);
		db.execSQL(CREATE_COMMUNITYLIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}

