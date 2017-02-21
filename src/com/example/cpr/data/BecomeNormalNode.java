package com.example.cpr.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BecomeNormalNode extends Thread {
	
	private String dataName;
	private Context context;
	public BecomeNormalNode(Context context, String dataName) {
		this.context = context;
		this.dataName = dataName;
	}

	@Override
	public void run() {
		 SCFdataLable scf = new SCFdataLable(context, "data.db");
         SQLiteDatabase db = scf.getWritableDatabase();
         Cursor cursor = db.rawQuery("select * from dataLableTable where dataName = ? ", 
 				new String[]{dataName});
         
        if (cursor.getCount() == 0) {
			System.out.println("delete scf data failer!");
        	return;
		}
		while (cursor.moveToNext()) {
			db.execSQL("delete from dataLableTable where dataName = ? ", new String[]{dataName}); 
		}
	} 
}
