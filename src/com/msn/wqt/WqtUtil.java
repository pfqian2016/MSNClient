package com.msn.wqt;

import android.util.Log;

public class WqtUtil {
	public static String convertToOfflineMsgString(OfflineMsgEntity msgEntity) {
		return msgEntity.getSource() + WqtConstants.SYMBOL_SPILT + msgEntity.getDestnation() + WqtConstants.SYMBOL_SPILT
				+ msgEntity.getDate() + WqtConstants.SYMBOL_SPILT + msgEntity.getMsgContent();
	}
	
	public static String  getMessageFromOfflineMsgString(String offlineMsgString){
		String token[]=offlineMsgString.split(WqtConstants.SYMBOL_SPILT);
		return token[3];		
	}
	
	public static String getMsgHeader(String offlineMsgString){
		int index=offlineMsgString.lastIndexOf(WqtConstants.SYMBOL_SPILT);
		return offlineMsgString.substring(0, index);
	}
	
	public static String getDstName(String offlineMsgString){
		String token[]=offlineMsgString.split(WqtConstants.SYMBOL_SPILT);
		for(String tokk:token) Log.d("getDstName", tokk);
		return token[1];
	}
	
	public static String getSourceName(String offlineMsgString){
		String token[]=offlineMsgString.split(WqtConstants.SYMBOL_SPILT);
		return token[0];
	}
	
	public static OfflineMsgEntity convertToOfflineMsgEntity(String OfflineMsgString){
		OfflineMsgEntity msgEntity=new OfflineMsgEntity();
		String token[]=OfflineMsgString.split(WqtConstants.SYMBOL_SPILT);
		msgEntity.setSource(token[0]);
		msgEntity.setDestnation(token[1]);
		msgEntity.setDate(token[2]);
		msgEntity.setMsgContent(token[3]);
		//msgEntity.setImg(0);
		//msgEntity.setMsgType(false);
		return msgEntity;
	}

}
