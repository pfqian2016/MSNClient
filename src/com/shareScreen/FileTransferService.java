package com.shareScreen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FileTransferService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("server","file transfer service start");
		//new FileServerThread(getApplicationContext()).start();
		return super.onStartCommand(intent, flags, startId);
	}

}
