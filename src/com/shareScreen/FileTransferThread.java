package com.shareScreen;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.android.mobilesocietynetwork.client.MyApplication;
import com.android.mobilesocietynetwork.client.database.FileListDB;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class FileTransferThread extends Thread{
	private Uri uri;
	private String fileName;
	private boolean isUri=true;
	Socket socket;	
	DataOutputStream s_out;
	Context context;
	private String destination;
	private String filePath;
	public FileTransferThread(Uri uri,String file,Context c){
		this.uri=uri;
		this.fileName=file;
		this.context=c;
		isUri=true;
	}
	public FileTransferThread(String fileName,String destination, String filePath){
		this.fileName=fileName;
		this.destination=destination;
		this.filePath=filePath;
		isUri=false;
	}
	public void run(){
		socket=new Socket();
		try {
			Log.i("client", "connect");
			socket.connect(new InetSocketAddress("192.168.49.1", 8225), 8000);
			s_out=new DataOutputStream(socket.getOutputStream());
			String tmp=fileName+","+destination;
			s_out.writeUTF(tmp);  //дid
			Log.i("client", "file name");
			s_out.flush();
			InputStream in=null;
			if(isUri){
				ContentResolver cr = context.getContentResolver();
				in=cr.openInputStream(uri);
			}else{
				File file=new File(filePath+fileName);
				in=new FileInputStream(file);
			}
			copyFile(in, s_out);
			in.close();
			s_out.close();
			socket.close();
			clearDatabase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.flush();
            inputStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
	public void clearDatabase(){
		FileListDB fileListDB=FileListDB.getInstance(MyApplication.getInstance());
		fileListDB.deletAllData();
	}
}
