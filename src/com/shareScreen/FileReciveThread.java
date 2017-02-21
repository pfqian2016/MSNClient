package com.shareScreen;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class FileReciveThread extends Thread{
	private Socket socket;
	
	Handler handler;
	String name;
	String destination;
	public FileReciveThread(Socket s,Handler c)
	{
		this.socket=s;
		handler=c;
	}
	public void run()
	{
		DataInputStream in;
		Log.i("server", "recive file");
		try {
			in=new DataInputStream(socket.getInputStream());
			String tmp=in.readUTF();
			String[] datas=tmp.split(",");
			if(datas.length==2){
				name=datas[0];
				destination=datas[1];
			}else{
				Log.i("FileRecivieThread", "Packet 错误");
			}
			File f = new File("/sdcard/wifip2pshared/" + name);
			String dirsName=f.getParent();
			File dirs = new File(dirsName);
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            copyFile(in, new FileOutputStream(f));
            in.close();
            socket.close();
            Message msg=Message.obtain();
   		 	msg.what=1;
            Bundle bundle=new Bundle();
            bundle.putCharSequence("fileName", name);
            bundle.putCharSequence("dirsName", dirsName);
            msg.setData(bundle);
            handler.sendMessage(msg);
            //Toast.makeText(context, "成功接收文件："+name, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void storeFile(){
		if(!destination.equals(User.getInstance().getName())){	
           //将文件信息存储到数据库中
		}
	}

/***************************************************************************************/
 public boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
