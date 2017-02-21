package com.example.android.wifidirect.discovery;

import android.os.Handler;

public class Timer extends Thread {

	private Handler handler;
	public Timer(Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public void run() { 
		try {
			Timer.sleep(5000);
			handler.obtainMessage(0).sendToTarget();
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
	} 
}
