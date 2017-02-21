package com.msn.wqt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.android.mobilesocietynetwork.client.info.ChatMsgEntity;
import com.android.mobilesocietynetwork.client.util.MyDate;
//import com.msn.servicediscovery.MainService;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class SendAPI {
	public static String HEAD_SYMBOL="#";
	
	public static void sendOfflineMessage(String message,ChatManager chatManager){
		if(chatManager!=null){
			String sendMessage=WqtConstants.ACTION_SEND_TXT_HEAD_START+message+WqtConstants.ACTION_SEND_TXT_HEAD_END;
			chatManager.write(sendMessage.getBytes());
			Log.d("sendOfflineMessage", "sent");
		}else{
			Log.d("sendOfflineMessage", "chatManager is null");
		}
	}
	//recommend
	public static void sendOfflineFile(Context context,Socket socket,File file){
		if(file!=null&&file.isFile()){
			String head=WqtConstants.ACTION_SEND_FILE_HEAD_START+file.getName()+HEAD_SYMBOL+file.length()+WqtConstants.ACTION_SEND_TXT_HEAD_END;
			FileInputStream fis=null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			FileTransferThread fileTransferThread=new FileTransferThread(context,socket, fis , head);
			new Thread(fileTransferThread).start();
		}else{
			Log.d("sendOfflineMessage", "file is null or is a dir");
		}
				
	}
	
	/*
	public static void sendOfflineFile(Context context,Socket socket,Uri uri){
		if(uri!=null){
			ContentResolver cr=context.getContentResolver();
			try {
				InputStream inputStream=cr.openInputStream(uri);
				String fileName=UriUtils.getFileName(UriUtils.getPath(context, uri));
				long fileSize=inputStream.available();
				String head=WqtConstants.ACTION_SEND_FILE_HEAD_START+fileName+HEAD_SYMBOL+fileSize+WqtConstants.ACTION_SEND_FILE_HEAD_END;
				FileTransferThread fileTransferThread=new FileTransferThread(context, socket, inputStream, head);
				new Thread(fileTransferThread).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Log.d("sendOfflineFile", "uri is null");
		}
	}
	*/
	
	/*
	public static void sendOfflineMessageWithoutLink(Context context,String message){
		if(message!=null&&message!=""){
			OfflineMsgEntity msgEntity=new OfflineMsgEntity("wqt","wqt",message,MyDate.getDateEN());	
			Intent intent=new Intent(context,MainService.class);
			Log.d("sendOfflineMessageWithoutLink",msgEntity.toString());
			intent.putExtra("message", msgEntity.toString());
			context.startService(intent);
		}
	
	}
	*/
	
	//use this
	public static void sendOfflineMessageWithoutLink(Context context,OfflineMsgEntity msgEntity){
		Intent intent=new Intent(context,OfflineMsgService.class);
		String offlineMsgString=WqtUtil.convertToOfflineMsgString(msgEntity);
		intent.putExtra("message", offlineMsgString);
		Log.d("sendOfflineMessageWithoutLink", offlineMsgString);
		context.startService(intent);
	}
	
	

}
