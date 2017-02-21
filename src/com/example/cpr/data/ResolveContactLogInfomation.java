package com.example.cpr.data;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ResolveContactLogInfomation {

	private SQLiteDatabase db;
	private Map<String, String> record = new HashMap<>();	
	private String nodeLableInfo;
	public static final String TAG = "@&#";
	public static final String NODE_TAG = "#&@";
	
	public ResolveContactLogInfomation(SQLiteDatabase db, Map<String, String> record) {
		this.db = db;
		this.record = record;
	}



	public boolean isContainLB() {
		parseRecord();
		Cursor cursor = db.rawQuery("select * from dataLableTable", null);
		while (cursor.moveToNext()) {
			String lables = cursor.getString(cursor.getColumnIndex("lable"));
			String[] LB = lables.split(TAG);
			for (String lable : LB) {
				if (nodeLableInfo.equals(lable)) {
					System.out.println("邻居节点LB包含在SCF数据LBs里面");
					break;
				}
			}
		}
		return false;
	}
	
	private void parseRecord() {
		 String recvInfo = record.get("");
		 System.out.println(recvInfo);
		 String[] contactLogInfo = recvInfo.split(NODE_TAG);
		 nodeLableInfo = contactLogInfo[1];
	}
}
