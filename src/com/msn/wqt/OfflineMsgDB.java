package com.msn.wqt;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OfflineMsgDB {
	
	public static String TABLE_NAME="offlineMsgTable";

	private SQLiteDatabase db;

	public OfflineMsgDB(Context context) {
		db = context.openOrCreateDatabase(WqtConstants.OFFLINE_MESSAGE_DB, Context.MODE_PRIVATE, null);
	}

	public void insertMessage(String name, String source, String destination, String date,String data) {
		initTable(name);

		System.out.println("public void insertMessage(String name, String source");
		db.execSQL("insert into " + name + " (source, destination, date, data" + ") values(?,?,?,?)",
				new Object[] { source, destination, date, data });
	}

	public boolean isNotEmpty(String name) {
		initTable(name);
		Cursor c = db.rawQuery("SELECT * from " + name, null);
		boolean b = c.moveToFirst();
		c.close();
		return b;
	}

	public boolean isIn(String name, String source, String destination, String date) {
		initTable(name);
		Cursor c = db.rawQuery("SELECT * FROM " + name + " where source=" + "\"" + source + "\"" + " AND destination="
				+ "\"" + destination + "\"" + " AND date=" + "\"" + date + "\"", null);
		boolean b = c.moveToFirst();
		c.close();
		return b;
	}

	public ArrayList<String> getBroadcastData(String name) {
		ArrayList<String> list = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT * from " + name, null);
		while (c.moveToNext()) {
			list.add("U+" + c.getString(c.getColumnIndex("destination")) + "+"
					+ c.getString(c.getColumnIndex("data")).getBytes().length + "+"
					+ c.getString(c.getColumnIndex("throwID")));
		}
		c.close();
		return list;
	}
	/*
	public ArrayList<String> getOfflineMsg(String name){
		ArrayList<String> list = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT * from " + name, null);
		while (c.moveToNext()) {
			list.add(c.getString(c.getColumnIndex("source")) + OfflineMsgEntity.SYMBOL_SPILT
					+ c.getString(c.getColumnIndex("destination")) + OfflineMsgEntity.SYMBOL_SPILT
					+ c.getString(c.getColumnIndex("date")) + OfflineMsgEntity.SYMBOL_SPILT
					+c.getString(c.getColumnIndex("data")) );
		}
		c.close();
		return list;
	}
	*/

	public String getDestination(String name, int throwID) {
		String destination = null;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
		while (c.moveToNext()) {
			destination = c.getString(c.getColumnIndex("destination"));
		}
		c.close();
		return destination;
	}

	public String getPass(String name, int throwID) {
		String pass = null;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
		while (c.moveToNext()) {
			pass = c.getString(c.getColumnIndex("pass"));
		}
		c.close();
		return pass;
	}

	public String getSource(String name, int throwID) {
		String source = null;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
		while (c.moveToNext()) {
			source = c.getString(c.getColumnIndex("source"));
		}
		c.close();
		return source;
	}

	public long getStartTime(String name, int throwID) {
		long startTime = 0;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
		while (c.moveToNext()) {
			startTime = c.getLong(c.getColumnIndex("startTime"));
		}
		c.close();
		return startTime;
	}

	public long getLife(String name, int throwID) {
		long life = 0;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
		while (c.moveToNext()) {
			life = c.getLong(c.getColumnIndex("life"));
		}
		c.close();
		return life;
	}

	public String getData(String name, int throwID) {
		String data = null;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
		while (c.moveToNext()) {
			data = c.getString(c.getColumnIndex("data"));
		}
		c.close();
		return data;
	}

	public long getInsertTime(String name, int throwID) {
		long insertTime = 0;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
		while (c.moveToNext()) {
			insertTime = c.getLong(c.getColumnIndex("insertTime"));
		}
		c.close();
		return insertTime;
	}

	public String getRespInfo(String name, int throwID) {
		String respInfo = null;
		Cursor c = db.rawQuery("SELECT * from " + name + " WHERE throwID =" + throwID, null);
		while (c.moveToNext()) {
			respInfo = c.getString(c.getColumnIndex("respInfo"));
		}
		c.close();
		return respInfo;
	}

	public void updateRespInfo(String name, String respInfo, int throwID) {
		initTable(name);
		db.execSQL("UPDATE " + name + "SET respInfo=" + respInfo + "WHERE throwID =" + throwID);
	}

	public void deleteMessage(String name, int id) {
		db.execSQL("DELETE from " + name + " WHERE throwID =" + id);
	}

	public void deleteMessage(String name, String source, String destination, String date) {
		db.execSQL("DELETE from " + name + " WHERE source=" + "\"" + source + "\"" + " and destination=" + "\""
				+ destination + "\"" + " and date=" + "\""+ date + "\"");
	}

	public void clearTable(String name) {
		initTable(name);
		db.execSQL("DELETE FROM " + name);
	}

	public void deleteTable(String name) {
		initTable(name);
		db.execSQL("DROP TABLE " + name);
	}

	public void close() {
		if (db != null)
			db.close();
	}

	public void initTable(String name) {
		db.execSQL("CREATE table IF NOT EXISTS " + name + " (id INTEGER PRIMARY KEY AUTOINCREMENT, source TEXT"
				+ ", destination TEXT,date TEXT, data TEXT" + " )");
	}
}
