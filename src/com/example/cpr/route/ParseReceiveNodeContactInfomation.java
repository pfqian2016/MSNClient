package com.example.cpr.route;

import java.util.HashMap;
import java.util.Map;

import com.example.cpr.data.SCFdataLable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ParseReceiveNodeContactInfomation {
	
	private Context context;
	private Map<String, String> record = new HashMap<>();	
	private String userLable;
	private String username;
	private String contactInfomation;
	private String recvInfomation; 
	
	public static final String LABLE_TAG = "@&#"; 
	
	public ParseReceiveNodeContactInfomation(Context context, Map<String, String> record) {
		this.context = context;
		this.record = record;	 
		parseRecordInformation();
//		initDateLableInfomation();
	}
	
	public boolean isContainsLB() { 
//		System.out.println("!!!!!!!!!!!!!!!!!!" + "cursor.getCount:" + cursor.getCount());
		SCFdataLable scFdataLable = new SCFdataLable(context, "data.db");
		SQLiteDatabase db = scFdataLable.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from dataLableTable", null);
		while (cursor.moveToNext()) {
			//System.out.println("11111111111111" + "cursor.getCount:" + cursor.getCount());
			String lables = cursor.getString(cursor.getColumnIndex("dataLable")); 
			Log.d("test dataLable:", lables);
			System.out.println("test userlable:" + userLable);
			String[] lable = lables.split("#");
			for (String LB : lable) {
				 if (userLable.equals(LB)) {
					return true;
				}
			}			
		}		
		return false;
	}
	
	public boolean isDestination() {
		
		//initDateLableInfomation();
		SCFdataLable scFdataLable = new SCFdataLable(context, "data.db");
		SQLiteDatabase db = scFdataLable.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from dataLableTable", null);
		while (cursor.moveToNext()) {
			String destination = cursor.getString(cursor.getColumnIndex("destination"));
//			System.out.println("test destination:" + destination);
			if (destination.equals(username)) {
				return true;
			}
		}
		return false;
	}
 
	private void parseRecordInformation() {
		
		recvInfomation = record.get("");
		if (recvInfomation == null) {
			return;
		}
		System.out.println(recvInfomation);
		String[] userInfo = recvInfomation.split(GetContactLogInfomation.DAY_TAG);
		username = userInfo[0].split(GetContactLogInfomation.USERINFO_TAG)[0];
		userLable = userInfo[0].split(GetContactLogInfomation.USERINFO_TAG)[1]; 
//		System.out.println("username:" + username + " userLable:" + userLable);
	}
	
	//返回过去�?周详细的通信纪录
	public String getLastContactInformation() {
		return recvInfomation;
	}

	public String getContactLB(){
		return userLable;
	}
	
	public String getOtherUsername() {
		return username;
	}

}
