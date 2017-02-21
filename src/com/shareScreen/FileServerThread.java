package com.shareScreen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class FileServerThread extends Thread{
	ServerSocket server;
	Handler handler;
	public FileServerThread(Handler handler) {
		// TODO Auto-generated constructor stub
		this.handler=handler;
	}
	public void run(){
		try {
			Log.i("server","wait for file transfer");
			server=new ServerSocket(8225);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			try {
				Socket s=server.accept();
				Log.i("server", "start to recive file");
				new FileReciveThread(s, handler).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
