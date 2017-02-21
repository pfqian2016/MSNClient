package com.android.mobilesocietynetwork.client.notice;


import java.util.ArrayList;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.chat.DialogActivity;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.packet.AskParticipantsPacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ParticipantNoticeActivity extends Activity{

	private ListView lvParticipants;
	private Button btBack;
	private String noticeID;
	private int count = 0;
	private ArrayList<String> participants = new ArrayList<String>();
	private SharePreferenceUtil util;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notice_participant);
		Intent intent = getIntent();
	    noticeID = intent.getStringExtra("NoticeID");  
	    initView();
	    initControl();
	    initData();
	}
	
	private void initView() {
		lvParticipants = (ListView) findViewById(R.id.lvparticipants);
		btBack = (Button)findViewById(R.id.btBack);
	}
	private void initControl(){
		btBack.setOnClickListener(new OnClickListener(){
	    	
	    	@Override
	    	public void onClick(View v){
	    		ParticipantNoticeActivity.this.finish();
	    	}});
		
		//点击每个子项的响应,获取名称，打开聊天窗口
		lvParticipants.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				User user = new User();
				user.setName(participants.get(position));
				Intent intend5 = new Intent(ParticipantNoticeActivity.this,DialogActivity.class);
				intend5.putExtra("user", user);
				startActivity(intend5);
			}
             
        });
	}
	
	private void initData(){
		
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		//获得该活动的评论内容
		ReceiveNoticeIQTool recieveTool = new ReceiveNoticeIQTool();
		recieveTool.init();
		AskParticipantsPacket askParticipantsIQ = new AskParticipantsPacket();
		askParticipantsIQ.setNoticeID(noticeID);
		XmppTool.getConnection().sendPacket(askParticipantsIQ);
	  //接收一个List<String>类型的活动列表，计数器是为了防止此时无参与者陷入死循环
		while(recieveTool.getParticipants().size()==0&&count<3){
		try {
			Thread.sleep(500);
			count++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		participants = recieveTool.getParticipants();	
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.list_simple_view, participants);
		lvParticipants.setAdapter(mAdapter);
	}
	
	
	

}
