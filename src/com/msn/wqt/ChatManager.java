
package com.msn.wqt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class ChatManager implements Runnable {

	private Socket socket = null;
	private Handler handler;
	private InputStream inputStream;
	private OutputStream oStream;
	public static final String TAG = "ChatHandler";
	private Context context;

	public ChatManager(Context context, Socket socket, Handler handler) {
		this.socket = socket;
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void run() {

		try {
			inputStream = socket.getInputStream();
			oStream = socket.getOutputStream();

			while (true) {

				try {
					byte[] buffer = new byte[1024];
					int bytes = 0;
					bytes = inputStream.read(buffer);
					if (bytes == -1) {
						break;
					}
					String readMessage = new String(buffer, 0, bytes);
					if (readMessage.startsWith(WqtConstants.ACTION_SEND_TXT_HEAD_START)
							&& (readMessage.indexOf(WqtConstants.ACTION_SEND_TXT_HEAD_END) != -1)) {

						// send a broadcast
						int startIndex = WqtConstants.ACTION_SEND_TXT_HEAD_START.length();
						int endIndex = readMessage.indexOf(WqtConstants.ACTION_SEND_TXT_HEAD_END);
						String subMessage = readMessage.substring(startIndex, endIndex);
						Intent intent = new Intent();
						intent.setAction(WqtConstants.MsgOnRsvInLinkAction);
						intent.putExtra("message", subMessage);
						context.sendBroadcast(intent);
						
					} else if (readMessage.startsWith(WqtConstants.ACTION_SEND_FILE_HEAD_START)
							&& readMessage.indexOf(WqtConstants.ACTION_SEND_FILE_HEAD_END) != -1) {
						int start=WqtConstants.ACTION_SEND_FILE_HEAD_START.length();
						int end=readMessage.indexOf(WqtConstants.ACTION_SEND_FILE_HEAD_END);
						String headInfo=readMessage.substring(start,end);
						String[] tokens=headInfo.split(SendAPI.HEAD_SYMBOL);
						String fileName=tokens[0];
						long fileLength=Long.valueOf(tokens[1]);
						File outFile=new File(Environment.getExternalStorageDirectory()+"/MSN/"+fileName);
						File dir=new File(outFile.getParent());
						if(!dir.exists()){
							dir.mkdirs();
						}
						outFile.createNewFile();
						OutputStream fileOut=new FileOutputStream(outFile);
						String headString=readMessage.substring(0,end+WqtConstants.ACTION_SEND_FILE_HEAD_END.length());
						int headLength=headString.getBytes().length;
						long recvSize=0;
						if(headLength<bytes){
							fileOut.write(buffer, headLength, bytes-headLength);
							fileOut.flush();
							recvSize=recvSize+bytes-headLength;
						}
						while(recvSize<fileLength&&(bytes=inputStream.read(buffer))>0){
							recvSize=recvSize+bytes;
							if(recvSize>fileLength){
								fileOut.write(buffer,0,(int)(bytes-(recvSize-fileLength)));
							}
							else{
								fileOut.write(buffer, 0, bytes);
							}
							fileOut.flush();
						}
						Log.d("ChatManager", "file recv finish");
						Intent intent=new Intent();
						intent.setAction(WqtConstants.FileOnRsvInLinkAtion);
						context.sendBroadcast(intent);
						fileOut.close();
					}

				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				inputStream.close();
				oStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public void write(byte[] buffer) {

		try {
			oStream.write(buffer);
			oStream.flush();
		} catch (IOException e) {
			Log.e(TAG, "Exception during write", e);
		}
	}

	public Socket getSocket() {
		return socket;
	}
}
