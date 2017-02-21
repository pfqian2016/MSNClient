package com.android.mobilesocietynetwork.client.database;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.mobilesocietynetwork.client.util.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class FriendListDB {


	private SQLiteDatabase db;
	private static FriendListDB friendListDB;
	
	//构造方法私有化
	private FriendListDB(Context context){
		ClientDBOpenHelper dbhelper = new ClientDBOpenHelper(context,
				Constants.FRIENDDBNAME,null,1);
		db = dbhelper.getWritableDatabase();
	}
	//获取实例，保证全局只有一个
	public synchronized static FriendListDB getInstance(Context context){
		if(friendListDB == null){
			friendListDB = new FriendListDB(context);
		}
		return friendListDB;
	}
	
	//插入一个分组的数据(初始化获得服务器端的数据)
	public  void insertData(String userid,String groupname,ArrayList<String> list){
		if(list != null && list.size()>0){
			for(int i = 0; i<list.size();i++){
			String friendname	= list.get(i);
			ContentValues values = new ContentValues();
			values.put("userid", userid);
			values.put("groupname", groupname);
			values.put("friendname", friendname);
			db.insert("friendlist_table", null, values);
			}			
		}	
	}
	
	public void deletData(int noteid){
		
		
	}
	
	public void deletAllData(){
		db.delete("friendlist_table", null, null);
	}
	
	
	//查询某一个用户的分组
	public HashMap<String,ArrayList<String>> qureyGroup(String userid){
		HashMap<String,ArrayList<String>> friendlist = new HashMap<String,ArrayList<String>>();
		String group =null;
		String child =null;
		Cursor cursor = db.query( "friendlist_table", null, "userid=?", new String[]{userid} , null, null,null);
	//	Cursor cursor = db.query("data_table", null,null,null, null, null,null);
		if(cursor.moveToFirst()){
			do{
				group = cursor.getString(cursor.getColumnIndex("groupname"));
				child = cursor.getString(cursor.getColumnIndex("friendname"));
				if(friendlist.containsKey(group))
				{
					friendlist.get(group).add(child);
				}
				else{
					ArrayList<String> childlist = new ArrayList<String>();
					childlist.add(child);
					friendlist.put(group, childlist);
				}
			}while(cursor.moveToNext());
		}
		cursor.close();
		return friendlist;	
	}

	
}
