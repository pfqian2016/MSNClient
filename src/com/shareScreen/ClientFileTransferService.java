package com.shareScreen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ClientFileTransferService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("ClientFileTransferService", "FileCheckThread create...");
		FileCheckThread.getInstance().start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
//		Log.i("ClientFileTransferService", "ClientFileTransferService run...");
//		FileCheckThread.getInstance().setStart(true);
		return super.onStartCommand(intent, flags, startId);
	}
	
}
