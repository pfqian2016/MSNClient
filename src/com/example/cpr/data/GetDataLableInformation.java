package com.example.cpr.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GetDataLableInformation {
	
	private Context context;
	private String dataname;
	//private String dataLableInfo;
	public GetDataLableInformation(Context context, String dataname) {
		this.context = context;
		this.dataname = dataname;
	}
	
	public String getDataLableInfomation() {
		
		SCFdataLable scfdata = new SCFdataLable(context, "data.db");
		SQLiteDatabase db = scfdata.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from dataLableTable where dataName = ?", 
											new String[]{dataname});
		
		StringBuilder dataLableInfo = new StringBuilder();
		while (cursor.moveToNext()) {
			dataLableInfo.append(cursor.getString(cursor.getColumnIndex("destination")));
			dataLableInfo.append(DealSCFdataLableTable.TAG);
			dataLableInfo.append(cursor.getString(cursor.getColumnIndex("source")));
			dataLableInfo.append(DealSCFdataLableTable.TAG);
			dataLableInfo.append(cursor.getString(cursor.getColumnIndex("dataName")));
			dataLableInfo.append(DealSCFdataLableTable.TAG);
			dataLableInfo.append(cursor.getString(cursor.getColumnIndex("dataLable")));
		}
		return dataLableInfo.toString();
	}

}
