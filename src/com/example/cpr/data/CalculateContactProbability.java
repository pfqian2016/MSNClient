package com.example.cpr.data;

import com.example.cpr.route.DealOneWeekContactLog;
import com.example.cpr.route.OneDayContactLog;
import com.example.cpr.route.OneWeekContactLog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CalculateContactProbability {

	private Context context;
	//private int contactNum = 0;
	private int lableNumber = 0; 
	private double Prob[][];
	private int contactNumber[][];
	private double contactWeight[][];
	private Cursor weekCursor;
	private Cursor dayCursor;
	public static final int TS_NUMBER = 7;
	public static final double[] w = {0, 0.2, 0.12, 0.12, 0.12, 0.12, 0.12, 0.2}; 
	
	public CalculateContactProbability(Context context) {
		this.context = context;
	}
	
	public void init() {
		OneWeekContactLog oneWeek = new OneWeekContactLog(context, "oneWeek.db");
		SQLiteDatabase db = oneWeek.getReadableDatabase();
		weekCursor = db.rawQuery("select * from oneWeekContactLogTable", null);  
		
		
		weekCursor.moveToFirst();
		String lables = weekCursor.getString(weekCursor.getColumnIndex("totalLB"));
		String[] lable = lables.split(DealOneWeekContactLog.LB_TAG);
		lableNumber = lable.length;
		/*
		String date = weekCursor.getString(weekCursor.getColumnIndex("n_dayago"));		
		OneDayContactLog oneDay = new OneDayContactLog(context, date + ".db");
		SQLiteDatabase dbDatabase = oneDay.getReadableDatabase();
		dayCursor = dbDatabase.rawQuery("select * from oneDayContactLogTable", null);
		lableNumber = dayCursor.getCount();*/
		Prob = new double[lableNumber][TS_NUMBER];
		contactNumber = new int[lableNumber][TS_NUMBER];
		contactWeight = new double[lableNumber][TS_NUMBER];
		 
	}
	
	public void calContactProbability(){
		
		for (int i = 1; i <= lableNumber; i++) {
			
			for (int j = 1; j < TS_NUMBER; j++) {
				
				Prob[i][j] = contactWeight[i][j]/contactNumber[i][j];
			}
		}
	}
	 
	public void calContactValue() {
		int k = 1;
		while (weekCursor.moveToNext()) {
			
			String date = weekCursor.getString(weekCursor.getColumnIndex("n_dayago"));
			
			OneDayContactLog oneDay = new OneDayContactLog(context, date + ".db");
			SQLiteDatabase dbDatabase = oneDay.getReadableDatabase();
			dayCursor = dbDatabase.rawQuery("select * from oneDayContactLogTable", null);
			int i = 1;
			 
			while (dayCursor.moveToNext()) {
				 
				for (int j = 1; j <= TS_NUMBER; j++) {
					contactNumber[i][j] = dayCursor.getInt(j);		
					contactWeight[i][j] = w[k] * dayCursor.getInt(j);
				}				
				i++;
			}
			
			k++;
		} 
	}
	 
}
