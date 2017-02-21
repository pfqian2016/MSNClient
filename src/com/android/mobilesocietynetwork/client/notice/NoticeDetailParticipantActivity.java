package com.android.mobilesocietynetwork.client.notice;


import java.util.ArrayList;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.packet.AskParticipantsPacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NoticeDetailParticipantActivity extends Activity{

	private ListView lvParticipants;
	private String noticeID;
	private ArrayAdapter<String> mAdapter;
	private int count = 0;
	private ArrayList<String> participants = new ArrayList<String>();
	private ReceiveNoticeIQTool ReceiveNoticeIQTool = new ReceiveNoticeIQTool();
    private ProgressDialog pd;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notice_detail_participant);
		Intent intent = getIntent();
	    noticeID = intent.getStringExtra("NoticeID");  
		lvParticipants = (ListView) findViewById(R.id.lvparticipants);
	    initData();
	}
	
	private void initData(){
		
		//获得该活动的参与者
		ReceiveNoticeIQTool.init();
    	ReceiveNoticeIQTool.resetIsReceive();
		AskParticipantsPacket askParticipantsIQ = new AskParticipantsPacket();
		askParticipantsIQ.setNoticeID(noticeID);
		XmppTool.getConnection().sendPacket(askParticipantsIQ);
		//开启接收线程
        pd = ProgressDialog.show(NoticeDetailParticipantActivity.this, "Hint", "loading……");  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	if(receiveParticipants()) 
            		handler.sendEmptyMessage(1);
            	else
            		{
            		handler.sendEmptyMessage(0); 
            		}
            }  	  
        }).start();  
	}
    private boolean receiveParticipants() {  
  	  //接收一个参与者列表
    	count = 0;
  		while((!ReceiveNoticeIQTool.getIsReceive())&&count<20){
  		//while(participants.size()==0&&count<10){
  		try {
  			Thread.sleep(500);
  			count++;
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		}	
  		participants = ReceiveNoticeIQTool.getParticipants();	
  		ReceiveNoticeIQTool.resetIsReceive();
  		return participants.size()!=0;
    }  
    
    Handler handler=new Handler()  
    {   
    	@Override  
        public void handleMessage(Message msg)  // handler接收到消息后就会执行此方法 
        {     
    		super.handleMessage(msg);
             switch(msg.what)  
            {  
            case 1:  //如果查看
            	 pd.dismiss();
            	 mAdapter = new ArrayAdapter<String>(NoticeDetailParticipantActivity.this, R.layout.list_simple_view, participants);
         		lvParticipants.setAdapter(mAdapter);
                break;  

            case 0:  //如果没有数据
          	  pd.dismiss();// 关闭ProgressDialog 
          	 mAdapter = new ArrayAdapter<String>(NoticeDetailParticipantActivity.this, R.layout.list_simple_view, participants);
    		lvParticipants.setAdapter(mAdapter);
           	Toast.makeText(NoticeDetailParticipantActivity.this,"no data", Toast.LENGTH_SHORT).show();
              break;  
            default:  
                break;        
            }  
        }  
    };



}
