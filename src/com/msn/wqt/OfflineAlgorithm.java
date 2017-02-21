package com.msn.wqt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.android.mobilesocietynetwork.client.database.FriendDB;
import com.android.mobilesocietynetwork.client.database.FriendListDB;
import com.android.mobilesocietynetwork.client.info.User;

import android.content.Context;
import android.util.Log;

public class OfflineAlgorithm {
	
	public static boolean isMyMessage(String msgEntityString,String myUserName){
		String dstName=WqtUtil.getDstName(msgEntityString);		
		boolean result=myUserName.equals(dstName.trim());
		Log.d("isMyMessage", "result: "+result);
		return result;		
		//return false;
	}
	
	public static boolean isMyFriendMessage(Context context,String msgEntityString,String myUserName){
		FriendListDB friendDb=FriendListDB.getInstance(context);
		HashMap<String,ArrayList<String>> friendlist=friendDb.qureyGroup(myUserName);				
		String destName=WqtUtil.getDstName(msgEntityString);
		Set<String> keySet=friendlist.keySet();
		for(String groupName:keySet){
			if(friendlist.get(groupName).contains(destName))
				return true;
		}
		
		return false;
	}
	

}
