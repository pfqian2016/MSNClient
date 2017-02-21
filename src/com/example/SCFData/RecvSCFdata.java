package com.example.SCFData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.android.wifidirect.discovery.ChatManager;
import com.example.android.wifidirect.discovery.WiFiChatFragment;

public class RecvSCFdata {

	private InputStream input;
	private OutputStream out;
	private int fileSize;
	private int recvSize;
	private Activity context;

	public RecvSCFdata(Activity context, InputStream inputStream,
			OutputStream outputStream, int fileSize, int recvSize) {
		this.context = context;
		this.input = inputStream;
		this.out = outputStream;
		this.fileSize = fileSize;
		this.recvSize = recvSize;
	}

	public void dealSCFdata() {
		try {
			byte[] bufFile = new byte[1024 * 8];
			int len = 0;
			while (recvSize < fileSize && (len = input.read(bufFile)) > 0) {
				try {
					recvSize += len;
					if (recvSize > fileSize) {
						out.write(bufFile, 0, len - (recvSize - fileSize));
						String endFile = new String(bufFile, 0, len);
						if (endFile
								.endsWith(WiFiChatFragment.ACTION_SEND_FILE_END)) {
							out.close();
							Log.d(ChatManager.TAG,
									"outputStream close, the recvsize is  : "
											+ recvSize + " ,filse Size : "
											+ fileSize);
						}
					} else {
						out.write(bufFile, 0, len);
					}
				} catch (IOException e) {
					Log.d(ChatManager.TAG, "Save File Failer");
					e.printStackTrace();
				}
			}
			Log.d("#######", "file recv finish");
			// Toast.makeText(context, "Download succeeded",
			// Toast.LENGTH_SHORT).show();
			Thread UIUpdateThread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					context.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(context, "File received!",
									Toast.LENGTH_LONG).show();
							
							
						}

					});
				}

			});
			UIUpdateThread.start();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}
