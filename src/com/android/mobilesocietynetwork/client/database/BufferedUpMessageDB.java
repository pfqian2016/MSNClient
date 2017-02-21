package com.android.mobilesocietynetwork.client.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.mobilesocietynetwork.client.info.BufferMsgEntity;
import com.android.mobilesocietynetwork.client.util.Constants;

public class BufferedUpMessageDB {
	private SQLiteDatabase db;

	public BufferedUpMessageDB(Context context)
	{
		db = context.openOrCreateDatabase(Constants.BUFFERMESSAGEDB,
				Context.MODE_PRIVATE, null);
	}

	public void insertMessage(String name, BufferMsgEntity entity)
	{
		db.execSQL("CREATE table IF NOT EXISTS "
				+ name
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, source TEXT"
				+ ", destination TEXT, startTime TEXT, throwID TEXT, life TEXT, data TEXT, pass TEXT)");
		db.execSQL(
				"insert into "
						+ name
						+ " (source, destination, startTime, throwID, life, data, pass) values(?,?,?,?,?,?,?)",
		               new Object[] {entity.getSource(),entity.getDestination(),entity.getDate(),entity.getThrowid(),
								entity.getLife(), entity.getMessage(), entity.getPassList()});
	}
	
	public void insertMessage(String name, String source, String destination, long startTime,
			int throwID, long life, String data, String pass)
	{
		db.execSQL("CREATE table IF NOT EXISTS "
				+ name
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, source TEXT"
				+ ", destination TEXT, startTime BIGINT, throwID INTEGER, life BIGINT, data TEXT, pass TEXT)");
		db.execSQL(
				"insert into "
						+ name
						+ " (source, destination, startTime, throwID, life, data, pass) values(?,?,?,?,?,?,?)",
				new Object[] { source, destination, startTime, throwID, life, data, pass });
	}

	
	public ArrayList<BufferMsgEntity> getData(String name)
	{
		ArrayList<BufferMsgEntity> list = new ArrayList<BufferMsgEntity>();
		db.execSQL("CREATE table IF NOT EXISTS "
				+ name
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, source TEXT"
				+ ", destination TEXT, startTime TEXT, throwID TEXT, life TEXT, data TEXT, pass TEXT)");
		Cursor c = db.rawQuery("SELECT * from " + name, null);
		while (c.moveToNext())
		{
				String pass = c.getString(c.getColumnIndex("pass"));
				String[] passes = pass.split("\\+");
				ArrayList<String>passList = new ArrayList<String>();
				for (int i = 0; i < passes.length; i++)
				{
					passList.add(passes[i]);
				}
				String throwid = c.getString(c.getColumnIndex("throwid"));
				String nameTo = c.getString(c.getColumnIndex("uname"));
				String nameFrom = c.getString(c.getColumnIndex("source"));
				String date = c.getString(c.getColumnIndex("date"));
				String message = c.getString(c.getColumnIndex("message"));
				String life = c.getString(c.getColumnIndex("life"));
				BufferMsgEntity entity = new BufferMsgEntity(throwid,nameTo,nameFrom, date, life,message,passList);			
				list.add(entity);
		}
		return list;
	}

	public boolean isNotEmpty(String name)
	{
		db.execSQL("CREATE table IF NOT EXISTS "
				+ name
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, source TEXT"
				+ ", destination TEXT, startTime TEXT, throwID TEXT, life TEXT, data TEXT, pass TEXT)");
		Cursor c = db.rawQuery("SELECT * from " + name, null);
		boolean b = c.moveToFirst();
		c.close();
		return b;
	}
	
	public void close()
	{
		if (db != null)
			db.close();
	}
	
	public void clear(String name)
	{
		db.execSQL("DELETE FROM _"+ name);
	}
	
}
