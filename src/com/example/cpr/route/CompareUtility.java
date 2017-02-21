package com.example.cpr.route;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cpr.data.SCFdataLable;

public class CompareUtility {
	
	private Context context; 
	private String lastWeekContactInfo;
	private String[] dataLables;
	public CompareUtility(Context context, String lastWeekContactInfo) {
		this.context = context;
		this.lastWeekContactInfo = lastWeekContactInfo;
	}
	
	private void getDataLable() {
		SCFdataLable scFdataLable = new SCFdataLable(context, "data.db");
		SQLiteDatabase db = scFdataLable.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from dataLableTable", null);
		while (cursor.moveToNext()) {
			String lables = cursor.getString(cursor.getColumnIndex("dataLable"));
			dataLables = lables.split("#");	// 
			break; 
		} 
	}
	
	public boolean isBetterNode(int current) {
//		ConvertTimetoCurrentTS convert = new ConvertTimetoCurrentTS(Calendar.getInstance());
//		int currentTS = convert.getCurrentTS();
		getDataLable();
		CalculateOwnUtility own = new CalculateOwnUtility(context);
		CalculateOtherNodeUtility otherNode = new CalculateOtherNodeUtility(context, lastWeekContactInfo);
		Map<String, Double> ownUtilityMap = own.getOwnUtility(current);
		Map<String, Double> otherUtilityMap = otherNode.getUtility(current);
		double ownUtility = 0.0;
		if (ownUtilityMap.size() == 0) {
			System.out.println("ownUtilityMap is null !");
			return true;
		}
		System.out.println("ownUtilityMap size:" + ownUtilityMap.size());
		for (String lable : ownUtilityMap.keySet()) {
			for (String dataLable : dataLables) {
//				System.out.println("dataLable:" + dataLable);
//				System.out.println("own utility:" + ownUtilityMap.get(lable));
				if (lable.equals(dataLable)) {
					ownUtility += ownUtilityMap.get(lable);
				}
			}
		}
		
		double otherUtility = 0.0;
		if (otherUtilityMap.size() == 0) {
			System.out.println("otherUtilityMap is null !");
			return false;
		}
		System.out.println("otherUtilityMap size:" + otherUtilityMap.size());
		for (String lable : otherUtilityMap.keySet()) {
			for (String dataLable : dataLables) {
//				System.out.println("dataLable:" + dataLable);
//				System.out.println("other utility:" + ownUtilityMap.get(lable));
				if (lable.equals(dataLable)) {
					otherUtility += otherUtilityMap.get(lable);
				}
			}
		}
		System.out.println("ownUtility:" + ownUtility + " otherUtility:" + otherUtility);
		if (ownUtility > otherUtility) { 
			return false;
		}  
		return true;  
	}

}
