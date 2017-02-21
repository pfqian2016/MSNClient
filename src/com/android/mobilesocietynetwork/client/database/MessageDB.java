package com.android.mobilesocietynetwork.client.database;

import java.util.ArrayList;
import java.util.List;









import com.android.mobilesocietynetwork.client.info.ChatMsgEntity;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*
 * 
 * 消息数据库，每个用户的id作为表名，存储用户的对话消息
 * 
 * */

	public class MessageDB
	{
		private SQLiteDatabase db;

		public MessageDB(Context context)
		{
			db = context.openOrCreateDatabase(Constants.MESSAGEDB, Context.MODE_PRIVATE, null);
		}

		public void saveMsg(String name, ChatMsgEntity entity)
		{
			initTable(name);
			int isCome = 0;
			if (entity.getMsgType())
			{
				isCome = 1;
			}
			db.execSQL(
					"insert into " + name + " (name,img,date,isCome,message) values(?,?,?,?,?)",
					new Object[] { entity.getName(), entity.getImg(), entity.getDate(), isCome,
							entity.getMessage() });
		}

		public List<ChatMsgEntity> getMsg(String myName, String toName)
		{
			List<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
			initTable(myName);
			Cursor c = db.rawQuery("SELECT * from " + myName + " where name=" + "\"" + toName + "\"" + " ORDER BY id DESC LIMIT 5", null);
			while (c.moveToNext())
			{
				int img = c.getInt(c.getColumnIndex("img"));
				String date = c.getString(c.getColumnIndex("date"));
				int isCome = c.getInt(c.getColumnIndex("isCome"));
				String message = c.getString(c.getColumnIndex("message"));
				boolean isComMsg = false;
				if (isCome == 1)
				{
					isComMsg = true;
				}
				ChatMsgEntity entity = new ChatMsgEntity(toName, date, message, img, isComMsg);
				list.add(entity);
			}
			c.close();
			return list;
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
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, img INTEGER,date TEXT,isCome INTEGER,message TEXT)");
		}
	}

