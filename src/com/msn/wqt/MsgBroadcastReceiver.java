package com.msn.wqt;


import com.android.mobilesocietynetwork.client.database.MessageDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.info.ChatMsgEntity;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

public class MsgBroadcastReceiver extends BroadcastReceiver {
	
	Context context;
	SharePreferenceUtil util;
	MessageDB messageDB;
	public MsgBroadcastReceiver(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
		util= new SharePreferenceUtil(this.context, Constants.SAVE_USER);
		messageDB=new MessageDB(this.context);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		OfflineMsgEntity msgEntity=(OfflineMsgEntity) intent.getSerializableExtra("message");
		Log.d("WqtMsgBroadcastReceiver", msgEntity.toString());
		//Looper.prepare();  	
		Toast.makeText(this.context, msgEntity.toString(), Toast.LENGTH_LONG).show();
		//Looper.loop();
		ChatMsgEntity chatMsgEntity=new  ChatMsgEntity();
		chatMsgEntity.setName(msgEntity.getSource());
		chatMsgEntity.setDate(msgEntity.getDate());
		chatMsgEntity.setImg(util.getImg());
		chatMsgEntity.setMsgType(true);
		chatMsgEntity.setMessage(msgEntity.getMsgContent());
		messageDB.saveMsg(util.getName(), chatMsgEntity);
		Intent broadCast = new Intent();
		broadCast.setAction(Constants.NORMAL_ACTION);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.NORMAL_MSGKEY, chatMsgEntity);
		broadCast.putExtras(bundle);
		context.sendBroadcast(broadCast);// ���յ�����Ϣ�ѹ㲥����ʽ���ͳ�ȥ
	}

}
