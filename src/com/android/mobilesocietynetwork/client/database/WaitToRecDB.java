package com.android.mobilesocietynetwork.client.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.android.mobilesocietynetwork.client.util.Constants;


public class WaitToRecDB
{
	private SQLiteDatabase db;

	public WaitToRecDB(Context context)
	{
		db = context.openOrCreateDatabase(Constants.WAIT_TO_REC_DB, context.MODE_PRIVATE, null);
	}

	public void insertData(String name, String destination, int throwID, long throwTime, int type)
	{
		initTable(name);
		db.execSQL("insert into " + name
				+ " (destination, throwID, throwTime, type) values(?, ?, ?, ?)", new Object[] {
				destination, throwID, throwTime, type });
	}

	public boolean isNotEmpty(String name)
	{
		initTable(name);
		Cursor c = db.rawQuery("SELECT * FROM " + name, null);
		boolean b = c.moveToFirst();
		c.close();
		return b;
	}

	public long getThrowTime(String name, int throwID, String destination)
	{
		long throwTime = 0;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID
				+ " and destination=" + "\"" + destination + "\"", null);
		while (c.moveToNext())
		{
			throwTime = c.getLong(c.getColumnIndex("throwTime"));
		}
		c.close();
		return throwTime;
	}
	
	public int getType(String name, int throwID, String destination)
	{
		int type = 2;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID=" + throwID
				+ " and destination=" + "\"" + destination + "\"", null);
		while (c.moveToNext())
		{
			type = c.getInt(c.getColumnIndex("type"));
		}
		c.close();
		return type;
	}

	public void initTable(String name)
	{
		db.execSQL("CREATE TABLE IF NOT EXISTS " + name
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, destination TEXT, throwID INTEGER, "
				+ "arriveTime BIGINT, throwTime BIGINT, type INTEGER)");
	}

	public void clearTable(String name)
	{
		initTable(name);
		db.execSQL("DELETE FROM " + name);
	}

	public void deleteTable(String name)
	{
		initTable(name);
		db.execSQL("DROP TABLE " + name);
	}

	public void deleteData(String name, int throwID, String destination, int type)
	{
		initTable(name);
		db.execSQL("DELETE FROM " + name + " where throwID=" + throwID + " AND destination="
				 + "\"" + destination + "\"" + " AND type=" + type);
	}
}
