package com.shareScreen;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class ServerInputTread extends Thread{
	private Socket socket;
	private boolean isStop=false;
	private boolean isShare=false;
	private Handler handler;
	ServerDB db=ServerDB.getInstance();
	public ServerInputTread(Socket s){
		this.socket=s;
	}
//	private void setIsStop(boolean stop) {
//		isStop=stop;
//	}
//	private void setIsShare(boolean share) {
//		isShare=share;
//		notify();
//	}
//	public void  setHandler(Handler handler) {
//		this.handler=handler;
//	}
	public void run(){
		Log.i("server", "启动服务端接收线程");
		DataInputStream inputStream;
		try {
			inputStream = new DataInputStream(socket.getInputStream());
			while(!isStop){
				String msg=inputStream.readUTF();
				Log.i("server", msg);
				String[] strings=msg.split(" ");
				if(strings[0].equals("id")){
					ServerOutputThread out=new ServerOutputThread(socket);
					db.getAll().put(strings[strings.length-1], out);
					out.start();
				}else if(strings[strings.length-1].equals("continue")){					
					GetScreen.getInstance().addNum();
				}else if(strings[strings.length-1].equals("stop")){
					ServerOutputThread out=db.getAll().get(strings[0]);
					out.close();
					db.getAll().remove(strings[0]);
					isStop=true;
					if(db.getAll().size()==0){
						GetScreen.getInstance().setStop(true);
					}
				}	
			}
			inputStream.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
