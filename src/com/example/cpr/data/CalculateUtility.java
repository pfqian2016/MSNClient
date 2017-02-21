//package com.example.cpr.data;
//
//import com.example.cpr.route.OneDayContactLog;
//import com.example.cpr.route.OneWeekContactLog;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//public class CalculateUtility {
//
//	private Context context;
//	private int lableNumber;
//	private int current;
//	private String TS;
//	private double utility[];
//	private double prob[][];
//	public static final int referenceWindowSize = 4;
//	public static final int TS_NUMBER = 7;
//	
//	private static final String TS1 = "TS1";
//	private static final String TS2 = "TS2";
//	private static final String TS3 = "TS3";
//	private static final String TS4 = "TS4";
//	private static final String TS5 = "TS5";
//	private static final String TS6 = "TS6";
//	private static final String TS7 = "TS7";
//	
//	public CalculateUtility(Context context, String TS) {
//		this.context = context;
//		this.TS = TS;
//	}
//	
//	public void calUtility() {
//		
//		init();
//		
//		convertTStocurrent(TS);
//		
//		for (int i = 1; i <= lableNumber; i++) {
//			for (int j = 1; j <= TS_NUMBER; j++) {
//				if (Math.abs(j - current) < 4) {
//					utility[i] = prob[i][j] / (Math.abs(j - current));
//				} 
//			}
//		}
//	}
//	
//	private void init() {
//		OneWeekContactLog oneWeek = new OneWeekContactLog(context, "oneWeek.db");
//		SQLiteDatabase db = oneWeek.getReadableDatabase();
//		Cursor weekCursor = db.rawQuery("select * from oneWeekContactLogTable", null);  
//		
//		while (weekCursor.moveToNext()) {
//			String date = weekCursor.getString(weekCursor.getColumnIndex("n_dayago"));
//			OneDayContactLog oneDay = new OneDayContactLog(context, date + ".db");
//			SQLiteDatabase dbDatabase = oneDay.getReadableDatabase();
//			Cursor dayCursor = dbDatabase.rawQuery("select * from oneDayContactLogTable", null);
//			lableNumber = dayCursor.getCount(); 
//			utility = new double[lableNumber];
//		}
//	}
//	
//	private void convertTStocurrent(String TS) {
//		
//		switch (TS) {
//			case TS1:
//				current = 1;
//				break;
//			case TS2:
//				current = 2;
//				break;
//			case TS3:
//				current = 3;
//				break;
//			case TS4:
//				current = 4;
//				break;
//			case TS5:
//				current = 5;
//				break;
//			case TS6:
//				current = 6;
//				break;
//			case TS7:
//				current = 7;
//				break;
//			default:
//				System.out.println("");
//				break;
//		}		
//	}
//	
//}
