package com.example.cpr.route;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DealOneDayContactLog implements Runnable {
	
	private Context context;
	private SQLiteDatabase db;
	private String timeSlot;
	private String contactLable;
	
	private Calendar calendar = Calendar.getInstance();
	private String today;
	private int hour;
	private static final String TS1 = "TS1";
	private static final String TS2 = "TS2";
	private static final String TS3 = "TS3";
	private static final String TS4 = "TS4";
	private static final String TS5 = "TS5";
	private static final String TS6 = "TS6";
	private static final String TS7 = "TS7";
	
	public DealOneDayContactLog(Context context, String today, String contactLable) {
		this.context = context;
		this.today = today;
		this.contactLable = contactLable;
	}
	
	public DealOneDayContactLog(Context context, Calendar calendar, String contactString) {
		this.context = context;
		this.calendar = calendar;
		this.contactLable = contactLable;
	}

	@Override
	public void run() { 
		
//		System.out.println("test DealOneDayContactLog contactLable:" + contactLable);
		int contactNum = 0;
		timeSlot = getTS();
		System.out.println("timeSlot:" + timeSlot);
		String contact = today + ".db";
		OneDayContactLog oneDay = new OneDayContactLog(context, contact);
		db = oneDay.getWritableDatabase();
		if (contactLable == null) {
			return;
		}
		Cursor cursor = db.rawQuery("select * from oneDayContactLogTable where contactLable = ?", 
									new String[] {contactLable});
//		System.out.println("test DealOneDayContactLog cursor.getCount():" + cursor.getCount());
		//未与LB group里的节点通信
		if (cursor.getCount() == 0) {
			db.execSQL("insert into oneDayContactLogTable " +
					"(contactLable, TS1, TS2, TS3, TS4, TS5, TS6, TS7) " +
					"values(?, ?, ?, ?, ?, ?, ?, ?)", 
					new Object[] {contactLable, 0, 0, 0, 0, 0, 0, 0});			
		} else {
			
			while (cursor.moveToNext()) {
				//取出该时段联系的次数
				contactNum = cursor.getInt(cursor.getColumnIndex(timeSlot));				
			}cursor.close();//yjy add
		}cursor.close();//yjy add
		cursor.close();//yjy add
		contactNum++;
		updateOneDayContactLog(contactLable, contactNum);
	}
	
	private String getTS() {
		
		hour = calendar.get(Calendar.HOUR_OF_DAY);		
		String TS = null;
		if(0 < hour && hour <= 6) {
			TS = TS1;
		} else if (6 < hour && hour <= 8) {
			TS = TS2;
		} else if (8 < hour && hour <= 12) {
			TS = TS3;
		} else if (12 < hour && hour <= 14) {
			TS = TS4;
		} else if (14 < hour && hour <= 18) {
			TS = TS5;
		} else if (18 < hour && hour <= 20) {
			TS = TS6;
		} else if (20 < hour && hour <= 23) {
			TS = TS7;
		}		
		return TS;
	}
	
	private void updateOneDayContactLog (String contactLable, int contactNum) {
		switch (timeSlot) {
			case TS1:			
				db.execSQL("update oneDayContactLogTable set TS1 = ? where contactLable = ?", 
							new Object[] {contactNum, contactLable});
				break;			
			case TS2:
				db.execSQL("update oneDayContactLogTable set TS2 = ? where contactLable = ?", 
							new Object[] {contactNum, contactLable});
				break;			 
			case TS3:
				db.execSQL("update oneDayContactLogTable set TS3 = ? where contactLable = ?",
							new Object[] {contactNum, contactLable});
				break;
			case TS4:
				db.execSQL("update oneDayContactLogTable set TS4 = ? where contactLable = ?", 
							new Object[] {contactNum, contactLable});
				break;
			case TS5:
				db.execSQL("update oneDayContactLogTable set TS5 = ? where contactLable = ?", 
							new Object[] {contactNum, contactLable});
				break;
			case TS6:
				db.execSQL("update oneDayContactLogTable set TS6 = ? where contactLable = ?", 
							new Object[] {contactNum, contactLable});
				break;
			case TS7:
				db.execSQL("update oneDayContactLogTable set TS7 = ? where contactLable = ?", 
							new Object[] {contactNum, contactLable});
				break;
			default:
				System.out.println("update oneDayContactLog error !");
				break;
		}
		
	}

}
