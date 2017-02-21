package com.android.mobilesocietynetwork.client.database;

import com.android.mobilesocietynetwork.client.util.Constants;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



/**
 * ���յ��Ĺ㲥����Ӧ���ֵļ�¼��ֻ��һ��Сʱ�Ĵ���ڣ��ᶨʱ���м������
 * ��ʽ(4)��type+comeFrom+throwID+insertTime������type�ֱ�ʹ��0��1��ʾ�������ݺͷַ����ݡ�
 * 
 * @author LLR_sunshine
 * 
 */
public class BroadcastResponseGotRecordDB
{
	private SQLiteDatabase db;

	public BroadcastResponseGotRecordDB(Context context)
	{
		db = context.openOrCreateDatabase(Constants.BROADCAST_RESPONSE_GOT_RECORD_DB,
				context.MODE_PRIVATE, null);
	}

	public void insertData(String name, byte type, String from, int throwID, long insertTime)
	{
		initTable(name);
		db.execSQL("insert into " + name + "(type, comeFrom, throwID, insertTime) values(?, ?, ?, ?)",
				new Object[] { type, from, throwID, insertTime });
	}

	public void deleteData(String name, String from, int throwID, int type)
	{
		initTable(name);
		db.execSQL("DELETE FROM " + name + " where throwID=" + throwID + " AND type=" + type
				+ " AND comeFrom=" + "\"" + from + "\"");
	}

	public boolean isIn(String name, String from, int throwID, byte type)
	{
		initTable(name);
		Cursor c = db.rawQuery("SELECT * FROM " + name + " where throwID=" + throwID + " AND type=" + type
				+ " AND comeFrom=" + "\"" + from + "\"", null);
		boolean b = c.moveToFirst();
		c.close();
		return b;
	}

	public void initTable(String name)
	{
		db.execSQL("CREATE TABLE IF NOT EXISTS " + name
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, type TINYINT, comeFrom TEXT, "
				+ "throwID INTEGER, insertTime BIGINT)");
	}
}
