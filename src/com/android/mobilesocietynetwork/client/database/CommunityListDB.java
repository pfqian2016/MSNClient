package com.android.mobilesocietynetwork.client.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.mobilesocietynetwork.client.util.Constants;

public class CommunityListDB {


	private SQLiteDatabase db;
	private static CommunityListDB communityListDB;
	
	//���췽��˽�л�
	private CommunityListDB(Context context){
		ClientDBOpenHelper dbhelper = new ClientDBOpenHelper(context,
				Constants.COMMUNITYDB,null,1);
		db = dbhelper.getWritableDatabase();
	}
	//��ȡʵ������֤ȫ��ֻ��һ��
	public synchronized static CommunityListDB getInstance(Context context){
		if(communityListDB == null){
			communityListDB = new CommunityListDB(context);
		}
		return communityListDB;
	}
	
	//��������(��ʼ����÷������˵�����)
	public  void insertCommunity(String userid,String communityname,String password){

			ContentValues values = new ContentValues();
			values.put("userid", userid);
			values.put("communityname", communityname);
			values.put("password", password);
			db.insert("communitylist_table", null, values);
		}	

	
	public void deletData(int noteid){
		
		
	}
	
	public void deletAllData(){
		db.delete("communitylist_table", null, null);
	}
	
	
	//��ѯĳһ���û�������
	public ArrayList<String> qureyCommunity(String userid){
		ArrayList<String> communitylist = new ArrayList<String>();
		String community =null;
		Cursor cursor = db.query("communitylist_table", null, "userid=?", new String[]{userid} , null, null,null);
		if(cursor.moveToFirst()){
			do{
				communitylist.add(cursor.getString(cursor.getColumnIndex("communityname")));
				}while(cursor.moveToNext());
			}
		cursor.close();
		return communitylist;	
	}
	
	//��ѯĳһ���û�ĳһ���ŵ�����
		public String qureyPassword(String userid,String communityname){
			String password =null;
			Cursor cursor = db.query
					("communitylist_table", null, "userid=? and communityname=?", 
							new String[]{userid,communityname} , null, null,null);
			if(cursor.moveToFirst()){
			password =  cursor.getString(cursor.getColumnIndex("password"));
			}
			cursor.close();
			return password;	
		}

	
}

