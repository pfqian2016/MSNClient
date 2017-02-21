package com.shareScreen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

public class ServerOutputThread extends Thread{
	private Socket socket;
	MyDeviceInfo info=MyDeviceInfo.getInstance();
	private boolean isStop=false;
	//private boolean is=false;
	DataOutputStream s_out;
	File file=new File("/sdcard/mypic.h264");
	public ServerOutputThread(Socket s) {
		// TODO Auto-generated constructor stub
		socket=s;
		try {
			s_out=new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} 
	public void close(){
		isStop=true;
	}
	public void setContinue() {
		// TODO Auto-generated method stub
		Log.i("server", "send file notify");
		synchronized (this) {
			notify();
		}	
	}
	public void run(){
		Log.i("server", "启动服务端输出线程");
		while(!isStop){
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.i("server", "send file");
			FileInputStream f_in;
			try {
/*				s_out.writeInt(info.getWidth());
				s_out.flush();
				s_out.writeInt(info.getHeight());
				s_out.flush();*/
				f_in = new FileInputStream(file);
				DataInputStream in=new DataInputStream(f_in);		
				int m=f_in.available();
				Log.i("11", "length="+m);
				s_out.writeInt(m);
				s_out.flush();
				copyFile(in, s_out);
				f_in.close();
				in.close();
				s_out.flush();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		try {
			s_out.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int m=0;
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
            	m+=len;
            	Log.i("11", "length="+len);
                out.write(buf, 0, len);
            }
            Log.i("11", "end="+m);
            out.flush();
            inputStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
