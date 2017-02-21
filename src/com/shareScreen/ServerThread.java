package com.shareScreen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class ServerThread extends Thread{
	private boolean isStop=false;
	private static ServerThread sThread=new ServerThread();
	private ServerThread(){
		
	}
	public static ServerThread getInstance(){
		return sThread;
	}
	public void setIsStop(boolean isStop){
		this.isStop=isStop;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Log.i("server", "start to server");
			ServerSocket server=new ServerSocket(8123);
			while(!isStop){
				Socket s=server.accept();
//				new ServerRecvThread(s).start();
				Log.i("server", "start to cleint");
				new ServerInputTread(s).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.run();
	}

}
