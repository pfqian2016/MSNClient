package com.msn.wqt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class FileTransferThread implements Runnable {

	private Socket socket;
	private Context context;
	private Uri uri;
	private String destAndSource;
	private InputStream inputStream;
	private String head;

	public FileTransferThread(Context context, Socket socket, Uri uri, String head) {
		this.context = context;
		this.socket = socket;
		this.uri = uri;
		this.head = head;
	}

	public FileTransferThread(Context context,Socket socket, InputStream inputStream, String head) {
		this.context=context;
		this.socket = socket;
		this.inputStream = inputStream;
		this.head = head;
	}

	@Override
	public void run() {

		System.out.println("test head:" + head);
		try {
			byte buf[] = new byte[1024 * 4];
			int len = 0;
			OutputStream out = socket.getOutputStream();
			out.write(head.getBytes());
			out.flush();

			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);
				out.flush();
			}

			inputStream.close();
			System.out.println("scf data trans over");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
