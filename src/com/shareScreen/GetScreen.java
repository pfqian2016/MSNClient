package com.shareScreen;

import android.util.Log;

public class GetScreen extends Thread{
	private int num=0;
	private boolean isStop=false;
	CODE code=new CODE();
	ServerDB db=ServerDB.getInstance();
	private static GetScreen intstance=new GetScreen();
	private GetScreen(){}
	public static GetScreen getInstance(){
		return intstance;
	}
	static{
		System.loadLibrary("avutil-54");
		System.loadLibrary("avcodec-56");
		System.loadLibrary("swresample-1");
		System.loadLibrary("avformat-56");
		System.loadLibrary("swscale-3");
		System.loadLibrary("avfilter-5");
		System.loadLibrary("avdevice-56");
		System.loadLibrary("GetPicUsingJni");	
		
	}
	public synchronized void addNum(){
		num++;
		Log.i("server", " "+num);
		synchronized (this) {
			notify();
		}
	}
	public void setStop(boolean Stop){
		isStop=Stop;
		synchronized (this) {
			notify();
		}
	}
	public void run(){
		Log.i("server", "启动服务端获取屏幕线程");
		while(!isStop){
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.i("server"," "+db.getAll().size() );
			while(num==db.getAll().size()&&num!=0){
		       int width = MyDeviceInfo.getInstance().getWidth();  
		       int height = MyDeviceInfo.getInstance().getHeight(); 
		       Log.i("server", "width="+width+" height="+height);
			   code.encode(width, height);
			   Log.i("server", "had get screen");
			   for(ServerOutputThread out:db.getAll().values()){
				   out.setContinue();
			   }
			   num=0;
			}
		}
	}
}

