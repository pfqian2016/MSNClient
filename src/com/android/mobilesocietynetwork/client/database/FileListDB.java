package com.android.mobilesocietynetwork.client.database;

import java.util.ArrayList;

import com.android.mobilesocietynetwork.client.util.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FileListDB {
	private SQLiteDatabase db;
	private static FileListDB fileListDB;
	
	private FileListDB(Context context){
		FileDBOpenHelper dbhelper = new FileDBOpenHelper(context,
				Constants.FILEDBNAME,null,1);
		db = dbhelper.getWritableDatabase();
	}
	
	public synchronized static FileListDB getInstance(Context context){
		if(fileListDB==null){
			fileListDB=new FileListDB(context);
		}
		return fileListDB;
	}
	/**
	 * �������� 
	 * @param fileName �ļ���
	 * @param destination Ŀ�Ľڵ�
	 * @param filePath�ļ�·��
	 */
	public void insertData(String fileName,String destination,String filePath){
		ContentValues value=new ContentValues();
		value.put("fileName", fileName);
		value.put("destination", destination);
		value.put("filePath", filePath);
		db.insert("filelist_table", null, value);
	}
	
	//ɾ������
	public void deletAllData(){
		db.delete("filelist_table", null, null);
	}
	
	//��ѯ���ݿ� ��ȡһ�������б����fileName,destination��filePath
	public ArrayList<String> queryData(){
		ArrayList<String> list=new ArrayList<>();
		String name=null;
		String dest=null;
		String path=null;
		Cursor cursor=db.query("filelist_table", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				name=cursor.getString(cursor.getColumnIndex("fileName"));
				dest=cursor.getString(cursor.getColumnIndex("destination"));
				path=cursor.getString(cursor.getColumnIndex("filePath"));
				list.add(name);
				list.add(dest);
				list.add(path);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
}
