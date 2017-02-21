package com.android.mobilesocietynetwork.client.database;

import java.util.ArrayList;

import com.android.mobilesocietynetwork.client.util.Constants;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class OfflineMessageDB {

		private SQLiteDatabase db;

		public OfflineMessageDB(Context context)
		{
			db = context.openOrCreateDatabase(Constants.OFFLINE_MESSAGE_DB, Context.MODE_PRIVATE, null);
		}

		public void insertMessage(String name, String source, String destination, long startTime,
				int throwID, long life, String data, String pass, long insertTime, String respInfo)
		{
			initTable(name);
			Constants.logPrint("经过初始化阶段。");
			System.out.println("public void insertMessage(String name, String source");
			db.execSQL("insert into " + name
					+ " (source, destination, startTime, throwID, life, data, pass,"
					+ " insertTime, respInfo) values(?,?,?,?,?,?,?,?,?)", new Object[] { source,
					destination, startTime, throwID, life, data, pass, insertTime, respInfo });
			Constants.logPrint("结束插入阶段。");
		}
		
		

		public boolean isNotEmpty(String name)
		{
			initTable(name);
			Cursor c = db.rawQuery("SELECT * from " + name, null);
			boolean b = c.moveToFirst();
			c.close();
			return b;
		}

		public boolean isIn(String name, int throwID, String destination)
		{
			initTable(name);
			Cursor c = db.rawQuery("SELECT * FROM " + name + " where throwID=" + throwID
					+ " AND destination="+ "\"" + destination+ "\"", null);
			boolean b = c.moveToFirst();
			c.close();
			return b;
		}
		
		public ArrayList<String> getBroadcastData(String name)
		{
			ArrayList<String> list = new ArrayList<String>();
			Cursor c = db.rawQuery("SELECT * from " + name, null);
			while (c.moveToNext())
			{
				list.add("U+" + c.getString(c.getColumnIndex("destination")) + "+"
						+ c.getString(c.getColumnIndex("data")).getBytes().length + "+"
						+ c.getString(c.getColumnIndex("throwID")));
			}
			c.close();
			return list;
		}

		public String getDestination(String name, int throwID)
		{
			String destination = null;
			Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
			while (c.moveToNext())
			{
				destination = c.getString(c.getColumnIndex("destination"));
			}
			c.close();
			return destination;
		}
		
		public String getPass(String name, int throwID)
		{
			String pass = null;
			Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
			while (c.moveToNext())
			{
				pass = c.getString(c.getColumnIndex("pass"));
			}
			c.close();
			return pass;
		}

		public String getSource(String name, int throwID)
		{
			String source = null;
			Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
			while (c.moveToNext())
			{
				source = c.getString(c.getColumnIndex("source"));
			}
			c.close();
			return source;
		}
		
		public long getStartTime(String name, int throwID)
		{
			long startTime = 0;
			Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
			while (c.moveToNext())
			{
				startTime = c.getLong(c.getColumnIndex("startTime"));
			}
			c.close();
			return startTime;
		}
		
		public long getLife(String name, int throwID)
		{
			long life = 0;
			Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
			while (c.moveToNext())
			{
				life = c.getLong(c.getColumnIndex("life"));
			}
			c.close();
			return life;
		}
		
		public String getData(String name, int throwID)
		{
			String data = null;
			Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
			while (c.moveToNext())
			{
				data = c.getString(c.getColumnIndex("data"));
			}
			c.close();
			return data;
		}

		public long getInsertTime(String name, int throwID)
		{
			long insertTime = 0;
			Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
			while (c.moveToNext())
			{
				insertTime = c.getLong(c.getColumnIndex("insertTime"));
			}
			c.close();
			return insertTime;
		}

		public String getRespInfo(String name, int throwID)
		{
			String respInfo = null;
			Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
			while (c.moveToNext())
			{
				respInfo = c.getString(c.getColumnIndex("respInfo"));
			}
			c.close();
			return respInfo;
		}

		public void updateRespInfo(String name, String respInfo, int throwID)
		{
			initTable(name);
			db.execSQL("UPDATE " + name + "SET respInfo=" + respInfo + "WHERE throwID =" + throwID);
		}

		public void deleteMessage(String name, int id)
		{
			db.execSQL("DELETE from " + name + " WHERE throwID =" + id);
		}

		public void deleteMessage(String name, String source, String destination, int throwID,
				long startTime)
		{
			db.execSQL("DELETE from " + name + " WHERE source="+ "\"" + source+ "\"" + " and destination="+ "\""
					+ destination+ "\"" + " and throwID=" + throwID + "and startTime=" + startTime);
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

		public void close()
		{
			if (db != null)
				db.close();
		}

		public void initTable(String name)
		{
			db.execSQL("CREATE table IF NOT EXISTS " + name
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, source TEXT"
					+ ", destination TEXT, startTime BIGINT, throwID INTEGER, life BIGINT, data TEXT,"
					+ " pass TEXT, insertTime BIGINT, respInfo TEXT)");
		}
}
