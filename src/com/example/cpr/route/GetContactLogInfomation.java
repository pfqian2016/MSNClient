package com.example.cpr.route;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase; 

public class GetContactLogInfomation {
	
	private Context context;
	private SQLiteDatabase db;
	private StringBuilder broadcastInfo = new StringBuilder();
	public static final String USERINFO_TAG = "#&@";
	public static final String TS_TAG = "#@";
	public static final String DAY_TAG = "@@@";
	public static final String LB_TAG = "@&#";
	
	public GetContactLogInfomation(Context context) {
		this.context = context;
	}
	//username USERINFO_TAG ownlable DAY_TAG date_1 LB_TAG LB_1 n TS_TAG ã€‚ã?‚ã?‚ã?‚ã??
	//							     DAY_TAG date_2 LB_TAG LB_1 n TS_TAG ......
	//   							 DAY_TAG date_3 LB_TAG LB_1 n TS_TAG ......
	//	...............
	public String getLastWeek(){
		OneWeekContactLog oneWeekContact = new OneWeekContactLog(context, "oneWeek.db");
		db = oneWeekContact.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from oneWeekContactLogTable", null); 
		if (cursor.getCount() == 0) {
			return null;
		}
		boolean isfirst = true;
		
		if (cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				if ((cursor.getCount() - cursor.getPosition()) < 3) {
					if (isfirst) {
						broadcastInfo.append(cursor.getString(cursor.getColumnIndex("username")));
						broadcastInfo.append(USERINFO_TAG);
						broadcastInfo.append(cursor.getString(cursor.getColumnIndex("ownLable"))); 
						isfirst = false;
					}
					String date = cursor.getString(cursor.getColumnIndex("n_dayago"));
					broadcastInfo.append(DAY_TAG);
					broadcastInfo.append(cursor.getString(cursor.getColumnIndex("n_dayago")));
					getOneDay(date); 
				}
			}cursor.close();//yjy add
		}cursor.close();//yjy add
//		if (cursor.getCount() != 0 && cursor.getCount() < 8) { 
//			while (cursor.moveToNext()) {
//				
//				if (cursor.isFirst()) {
//					broadcastInfo.append(cursor.getString(cursor.getColumnIndex("username")));
//					broadcastInfo.append(USERINFO_TAG);
//					broadcastInfo.append(cursor.getString(cursor.getColumnIndex("ownLable")));
//					//broadcastInfo.append(USERINFO_TAG); 
//				}
//				String date = cursor.getString(cursor.getColumnIndex("n_dayago"));
//				broadcastInfo.append(DAY_TAG);
//				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("n_dayago")));
//				getOneDay(date); 
//			}
//		} else if (cursor.getCount() >= 8) {
//			while (cursor.moveToNext()) {
//				if ((cursor.getCount() - cursor.getPosition()) < 7) {
//					if (isfirst) {
//						broadcastInfo.append(cursor.getString(cursor.getColumnIndex("username")));
//						broadcastInfo.append(USERINFO_TAG);
//						broadcastInfo.append(cursor.getString(cursor.getColumnIndex("ownLable"))); 
//						isfirst = false;
//					}
//					String date = cursor.getString(cursor.getColumnIndex("n_dayago"));
//					broadcastInfo.append(DAY_TAG);
//					broadcastInfo.append(cursor.getString(cursor.getColumnIndex("n_dayago")));
//					getOneDay(date); 
//				}
//			}
//			cursor.getPosition();
//		}
		return broadcastInfo.toString();
		
		
		
	}
	
	private void getOneDay(String date) {
		 
//		System.out.println("test OneDayContactLog:" + date);
		OneDayContactLog oneDayContact = new OneDayContactLog(context, date + ".db");
		SQLiteDatabase db = oneDayContact.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from oneDayContactLogTable", null);
//		System.out.println("test cursor.getCount():" + cursor.getCount());
		if (cursor.getCount() == 0) {
			return;
		}
//		System.out.println("test private void getOneDay:" + date);
		if (cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				broadcastInfo.append(LB_TAG);
				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("contactLable")));
				broadcastInfo.append(TS_TAG);
				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("TS1")));
				broadcastInfo.append(TS_TAG);
				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("TS2")));
				broadcastInfo.append(TS_TAG);
				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("TS3")));
				broadcastInfo.append(TS_TAG);
				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("TS4")));
				broadcastInfo.append(TS_TAG);
				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("TS5")));
				broadcastInfo.append(TS_TAG);
				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("TS6")));
				broadcastInfo.append(TS_TAG);
				broadcastInfo.append(cursor.getString(cursor.getColumnIndex("TS7"))); 
			} cursor.close();//yjy add
		}cursor.close();//yjy add
	}

}
