package com.android.mobilesocietynetwork.client.database;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;

import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.WeightCompute;



public class OfflineThrowTimeDB
{
	private SQLiteDatabase db;

	public OfflineThrowTimeDB(Context context)
	{
		db = context.openOrCreateDatabase(Constants.OFFLINE_THROWTIME_DB, Context.MODE_PRIVATE,
				null);
	}

	public void insertData(String name, String other, long time)
	{
		initTable(name);

		Cursor c = db.rawQuery("SELECT * from " + name + " where uname=" + "\"" + other + "\"",
				null);
		if (c.moveToFirst())
		{
			long[] timeArray = new long[5];
			timeArray[0] = c.getLong(c.getColumnIndex("last4"));
			timeArray[1] = c.getLong(c.getColumnIndex("last3"));
			timeArray[2] = c.getLong(c.getColumnIndex("last2"));
			timeArray[3] = c.getLong(c.getColumnIndex("last1"));
			timeArray[4] = time;
			WeightCompute wc = new WeightCompute(timeArray);

			db.execSQL("UPDATE " + name + " set uname =" + other + ",set last1=" + timeArray[4]
					+ ",set last2=" + timeArray[3] + ",set last3=" + timeArray[2] + ",set last4="
					+ timeArray[1] + ",set last5=" + timeArray[0] + ",set predict=" + wc
					+ "where uname =" + "\"" + other + "\"");

		}
		else
		{
			db.execSQL("insert into " + name
					+ " (uname ,last1, last2, last3, last4,last5,predict) values(?,?,?,?,?,?,?)",
					new Object[] { other, time, 3600000, 3600000, 3600000, 3600000,
							4 * time / 3 + 3600000 / 5 + 3600000 * 2 / 15 + 3600000 / 15 });
		}
		c.close();
	}

	public void initData(String name, String other)
	{
		initTable(name);
		db.execSQL("insert into " + name
				+ " (uname ,last1, last2, last3, last4,last5,predict) values(?,?,?,?,?,?,?)",
				new Object[] { other, 3600000, 3600000, 3600000, 3600000, 3600000, 3600000 });
	}

	public long getWeightTime(String name, String uname)
	{
		long l = 0;
		 initTable(name);
		Cursor c = db.rawQuery("SELECT * from " + name + " where uname=" + "\"" + uname + "\"",
				null);
		if (c.moveToFirst())
		{
			l = c.getLong(c.getColumnIndex("predict"));
		}
		else
		{
			//Constants.logPrint("数据库问题。");
		}
		c.close();
		return l;
	}

	public void close()
	{
		if (db != null)
			db.close();
	}

	public void initTable(String name)
	{
		db.execSQL("CREATE table IF NOT EXISTS "
				+ name
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, uname TEXT, last1 BIGINT, last2 BIGINT, last3 BIGINT,"
				+ " last4 BIGINT, last5 BIGINT, predict BIGINT)");
	}
}

