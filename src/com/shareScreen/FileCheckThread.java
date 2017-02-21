package com.shareScreen;

import java.util.ArrayList;

import com.android.mobilesocietynetwork.client.MyApplication;
import com.android.mobilesocietynetwork.client.database.FileListDB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class FileCheckThread extends Thread{
	private boolean isStart=true;
	private boolean isClose=false;
	private FileListDB fileListDB;
	private ArrayList<String> fileList;
	private static FileCheckThread instance;
	private FileCheckThread(){}
	public static FileCheckThread getInstance(){
		if(instance==null){
			instance=new FileCheckThread();
		}
		return instance;
	}
	public void setStart(boolean isStart){
		this.isStart=isStart;
		Log.i("FileCheckThread", "FileCheckThread set start");
		synchronized (this) {
			notify();
		}
	}
	public void close(){
		isClose=true;
		synchronized (this) {
			notify();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		Log.i("FileCheckThread", "FileCheckThread start...");
		while(isStart){
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(isClose){
				return;
			}
			
			/*SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase("qqfile.db", null);
			Cursor cursor=db.rawQuery("select * from filelist_table", null);
			String fileName=cursor.getString(0);
			String destination=cursor.getString(1);
			String filePath=cursor.getString(2);
			cursor.close();
			db.close();*/
			fileListDB=FileListDB.getInstance(MyApplication.getInstance());
			String name;
			String dest;
			String path;
			fileList=fileListDB.queryData();
			name=fileList.get(0);
			dest=fileList.get(1);
			path=fileList.get(2);
			
			new FileTransferThread(name, dest, path).start();
		}
		
	}
	
}
