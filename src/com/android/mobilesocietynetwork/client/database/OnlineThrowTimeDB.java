package com.android.mobilesocietynetwork.client.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.WeightCompute;

public class OnlineThrowTimeDB
{
	private SQLiteDatabase db;

	public OnlineThrowTimeDB(Context context)
	{
		db = context.openOrCreateDatabase(Constants.ONLINE_THROWTIME_DB, Context.MODE_PRIVATE, null);
	}

	public void insertData(String name, String other, long time)
	{
		//Constants.logPrint("准备初始化onlineHDB。");
		initTable(name);
		//Constants.logPrint("初始化完onlineHDB。");
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

			//Constants.logPrint("UPDATE了。");

			db.execSQL("UPDATE " + name
					+ " set last1=?,last2=?,last3=?,last4=?,last5=?,predict=? where uname=?",
					new Object[] { timeArray[4], timeArray[3], timeArray[2], timeArray[1], timeArray[0], wc.getResult(),
							other});
			//Constants.logPrint("UPDATE完了。");

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

	public long getWeightTime(String name, String uname)
	{
		initTable(name);
		long l =0;
		Cursor c = db.rawQuery("SELECT * from " + name + " where uname=" + "\"" + uname + "\"",
				null);
		while (c.moveToNext())
		{
		l = c.getLong(c.getColumnIndex("predict"));
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

