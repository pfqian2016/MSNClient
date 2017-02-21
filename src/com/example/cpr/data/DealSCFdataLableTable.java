package com.example.cpr.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DealSCFdataLableTable implements Runnable { 
	
	private Context context;	
	private String dataInfomation;
	private String username; //表示本节点的名称
	private String destination;
	private String source;
	private String dataname;
	private String dataLable;
	
	public static final String TAG = "@@";  
	
	public DealSCFdataLableTable(Context context, String dataInfomation) {
		this.context = context;
		this.dataInfomation = dataInfomation;
	}

	@Override
	public void run() { 

       
		initInfo(); 
		SCFdataLable scFdataLable = new SCFdataLable(context, "data.db");
		SQLiteDatabase db = scFdataLable.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from dataLableTable where destination = ? ", 
				new String[]{destination}); 
		if (cursor.getCount() != 0) {
			//不做任何处理
		} else {
			db.execSQL("insert into dataLableTable (id, destination, source, dataName, dataLable)" +
					"values(null, ?, ?, ?, ?)", new Object[] {destination, source, dataname, dataLable});
			System.out.println("insert into dataLableTable success");
		}
	}
	
	private void initInfo() {
 
		destination = dataInfomation.split(TAG)[0];
		source = dataInfomation.split(TAG)[1];
		dataname = dataInfomation.split(TAG)[2];
		dataLable = dataInfomation.split(TAG)[3];
	}

}
