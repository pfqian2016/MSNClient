package com.example.cpr.route;

import java.util.Calendar;

import android.R.bool;
import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DealOneWeekContactLog extends Thread {

	private Context context;
	private SQLiteDatabase db;
	private int year;
	private int month;
	private int day;
	private String today;
	private String ownLable;
	private String contactLable;
	private String username;
	private String firstCursorLables; 
	private final Calendar mCalendar = Calendar.getInstance();
	public static final String LB_TAG = "$%#";
	public DealOneWeekContactLog(Context context, String ownUsername, String ownLable, String contactLable) {
		this.context = context;
		this.username = ownUsername;
		this.ownLable = ownLable;
		this.contactLable = contactLable;	//与本节点通信的LB�?
	}
	
	@Override
	public void run() { 
		
		init();
//		System.out.println("test DealOneWeekContactLog username:" + username + " ownLable:" + ownLable + " contactLable:" + contactLable);
		OneWeekContactLog oneWeekContact = new OneWeekContactLog(context, "oneWeek.db");
		db = oneWeekContact.getWritableDatabase();		
		Cursor cursor = db.rawQuery("select * from oneWeekContactLogTable",null);
//		System.out.println("cursor.getCount():" + cursor.getCount()); 
		if (cursor.getCount() == 0) {// 
			db.execSQL("insert into oneWeekContactLogTable (id, username, ownLable, totalLB, n_dayago)" +
					" values(null, ?, ?, ?, ?)", new Object[] {username, ownLable, contactLable, today});
		} else if (cursor.getCount() == 1) { // 
			cursor.moveToFirst(); 
			String totalLable = cursor.getString(cursor.getColumnIndex("totalLB"));// 
			String date = cursor.getString(cursor.getColumnIndex("n_dayago"));// 
			
			if (!haveExistedLB(totalLable)) {
				totalLable = totalLable + LB_TAG + contactLable;
				db.execSQL("update oneWeekContactLogTable set totalLB = ? where n_dayago = ?" , 
						new Object[] {totalLable, date});					 
			}
			 			
			if (!date.equals(today)) {
				db.execSQL("insert into oneWeekContactLogTable (id, username, ownLable, totalLB, n_dayago)" +
						" values(null, ?, ?, ?, ?)", new Object[] {username, contactLable, totalLable, today});
			}  
			 
		} else { // 
			while (cursor.moveToNext()) {
				
				String totalLable = cursor.getString(cursor.getColumnIndex("totalLB"));
				String date = cursor.getString(cursor.getColumnIndex("n_dayago"));
				
				if (cursor.isFirst()) {// 
					
					if (!haveExistedLB(totalLable)) {
						firstCursorLables = totalLable + LB_TAG + contactLable;
						db.execSQL("update oneWeekContactLogTable set totalLB = ? where n_dayago = ?" , 
								new Object[] {firstCursorLables, date});
						
					} else {
						firstCursorLables = totalLable;
					}
					
				} else if (cursor.isLast()) {
					
					db.execSQL("update oneWeekContactLogTable set totalLB = ? where n_dayago = ?" , 
							new Object[] {firstCursorLables, date});
					
					if (!date.equals(today)){// 
						db.execSQL("insert into oneWeekContactLogTable (id, username, ownLable, totalLB, n_dayago)" +
								" values(null, ?, ?, ?, ?)", new Object[] {username, contactLable, firstCursorLables, today});
					}  
					 
				} else {
					db.execSQL("update oneWeekContactLogTable set totalLB = ? where n_dayago = ?" , 
							new Object[] {firstCursorLables, date});
				} 
			}	
		}
		cursor.close(); 

//		System.out.println("test DealOneDayContactLog:" + today + " " +contactLable);
		DealOneDayContactLog oneDay = new DealOneDayContactLog(context, today, contactLable);
		Thread thread = new Thread(oneDay);
		thread.start(); 
	}
	
	private boolean haveExistedLB(String lableInfo) {
		String[] lables = lableInfo.split(LB_TAG);
		for (String lab : lables) {
			if (lab.equals(contactLable)) {
				return true;
			}
		}
		return false;
	}
	
	private void init() {
		year = mCalendar.get(Calendar.YEAR);
		month = mCalendar.get(Calendar.MONTH) + 1;
		day = mCalendar.get(Calendar.DAY_OF_MONTH) + 9;
		today = year + "_" + month + "_" + day;
	} 
}
