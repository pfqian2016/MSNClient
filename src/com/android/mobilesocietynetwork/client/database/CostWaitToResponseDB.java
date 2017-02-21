package com.android.mobilesocietynetwork.client.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.mobilesocietynetwork.client.util.Constants;


/**
 * 在收到offline广播请求后的cost暂存db，最终在req发起方发起连接时读取并清除，或者定时清除，
 * 初定和BroadcastReqGotRecordDB同时进行清除。
 * 格式：typeBussiness+type+forwarder+destination+ID+deviceFro+cost。
 * 其中type是具体投递方式，有4种，分别是bufferUp投递，线下投递
 * ，线上direct和forwarder（当类型为非4时，forwarder取值“null”），对应1~4取值。当2~4还没确定时（因为在等responseOnlineCost），暂取0。
 * 其中typeBussiness分别使用0和1表示聊天内容和分发内容。
 * 
 * @author LLR_sunshine
 * 
 */
public class CostWaitToResponseDB
{
	private SQLiteDatabase db;

	public CostWaitToResponseDB(Context context)
	{
		db = context.openOrCreateDatabase(Constants.COST_WAIT_TO_RESPONSE_DB, context.MODE_PRIVATE,
				null);
	}

	public void insertData(String name, byte typeBussiness, byte type, String forwarder,
			String destination, int throwID, String deviceFro, double cost)
	{
		initTable(name);
		db.execSQL(
				"insert into "
						+ name
						+ "(typeBussiness, type, forwarder, destination, throwID, deviceFro, cost) values(?, ?, ?, ?, ?, ?, ?)",
				new Object[] { typeBussiness, type, forwarder, destination, throwID, deviceFro,
						cost });
	}

	public void deleteData(String name, byte typeBussiness, String destination, int throwID,
			String deviceFro)
	{
		initTable(name);
		db.execSQL("DELETE FROM " + name + " where throwID=" + throwID + " AND destination=" + "\""
				+ destination + "\"" + " AND typeBussiness=" + typeBussiness + " AND destination="
				+ "\"" + destination + "\"" + " AND deviceFro=" + "\"" + deviceFro + "\"");
	}

	// 将最终确定的cost及对应的方式填入。 ——LLR_sunshine
	public void updateData(String name, byte typeBussiness, String destination, int throwID,
			byte type, double cost, String forwarder)
	{
		db.execSQL("UPDATE " + name + " set type =" + type + ",set cost=" + cost
				+ ",set forwarder=" + "\"" + forwarder + "\"" + "where typeBussiness ="
				+ typeBussiness + " AND destination=" + "\"" + destination + "\"" + " AND throwID="
				+ throwID);
	}

	public boolean isIn(String name, int throwID, String destination, byte type)
	{
		initTable(name);
		Cursor c = db.rawQuery("SELECT * FROM " + name + " where throwID=" + throwID + "\""
				+ " AND destination=" + "\"" + destination + "\"" + " AND type=" + type, null);
		boolean b = c.moveToFirst();
		c.close();
		return b;
	}

	public boolean isNotEmpty(String name)
	{
		initTable(name);
		Cursor c = db.rawQuery("SELECT * from " + name, null);
		boolean b = c.moveToFirst();
		c.close();
		return b;
	}

	public String getDeviceName(String name, int throwID, String destination, byte type)
	{
		String deviceName = "null";
		initTable(name);
		Cursor c = db.rawQuery("SELECT * FROM " + name + " where throwID=" + throwID
				+ " AND destination=" + "\"" + destination + "\"" + " AND type=" + type, null);
		while (c.moveToNext())
		{
			deviceName = c.getString(c.getColumnIndex("deviceFro"));
		}
		c.close();
		return deviceName;
	}

	public void initTable(String name)
	{
		db.execSQL("CREATE TABLE IF NOT EXISTS " + name
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, typeBussiness TINYINT, type TINYINT, "
				+ "forwarder TEXT, destination TEXT, throwID INTEGER, deviceFro TEXT, cost DOUBLE)");
	}
}
