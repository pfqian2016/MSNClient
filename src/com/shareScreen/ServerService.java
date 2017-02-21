package com.shareScreen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServerService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		ServerThread.getInstance().start();
		GetScreen.getInstance().start();
		return super.onStartCommand(intent, flags, startId);
	}
	

}
