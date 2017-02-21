package com.android.mobilesocietynetwork.client.chat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.xmlpull.v1.XmlPullParser;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.MainActivity;
import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.packet.RecommendPacket;
import com.android.mobilesocietynetwork.client.tool.RecommendComTool;
import com.android.mobilesocietynetwork.client.tool.RecommendFriTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.database.CommunityListDB;
import com.android.mobilesocietynetwork.client.database.MultiUserChatDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RecommendComActivity extends Activity {

	private ListView mListView;
	private ArrayList<Map<String, Object>> listItems= new ArrayList<Map<String, Object>>();
	private ArrayList<String> list  = new ArrayList<String>();
	private SimpleAdapter simpleAdapter;
	private static XMPPConnection connection;
	private RecommendComTool retool = new RecommendComTool("recommend");
    private ProgressDialog pd;  
    private String clickedCom;
	private SharePreferenceUtil util;
	private MultiUserChatDB mutiUserChatDB;
	
	private CommunityListDB communityListDB;
	
	// ������ĳ�Ա�������ڽ���xmlʱ���Ͱ�Recommendlist��ֵ

	// private XMPPConnection con;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_recommend_com);
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(RecommendComActivity.this);
		mListView = (ListView) findViewById(R.id.listView_recommend_com);
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		connection = XmppTool.getConnection();
		mutiUserChatDB=MultiUserChatDB.getInstance();
		retool.RecommendComSendIQ();
		   pd = ProgressDialog.show(RecommendComActivity.this, "Hint", "loading");  
	        new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	            	if(receiveProcess())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
	                handler.sendEmptyMessage(1);  // ִ�к�ʱ�ķ���֮��������handler  
	            	else 
	            		handler.sendEmptyMessage(0); //û������ʱ��������Ϣ0
	            }  	  
	        }).start();  
}
	
	//���߳���ִ�еĺ�ʱ����  
   private boolean receiveProcess() {  
 	  //����һ��List<noticeInfo>���͵Ļ�б�
   	int count = 0;
		while(retool.getRecommendComList()==null&&count<20){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		list = retool.getRecommendComList();
 		return list.size()!=0;
   }  
   
   Handler handler=new Handler()  
   {   
   	@Override  
       public void handleMessage(Message msg)  // handler���յ���Ϣ��ͻ�ִ�д˷��� 
       {     
   		super.handleMessage(msg);
            switch(msg.what)  
           {  
           case 1:  //������Ƽ��
           	  pd.dismiss();// �ر�ProgressDialog 
           	 simpleAdapter = new SimpleAdapter(RecommendComActivity.this, listItems,
     				R.layout.list_view, new String[] { "name" },
     				new int[] { R.id.item_community });
           		for (int i = 0; i < list.size(); i++) {
           			Map<String, Object> listItem = new LinkedHashMap<String, Object>();
           			listItem.put("name", list.get(i));
           			listItems.add(listItem);
           			
           		}
           		mListView.setAdapter(simpleAdapter);
           		mListView.setOnItemClickListener(new ListClickListener());
               break;  

           case 0:  //���û������
         	  pd.dismiss();// �ر�ProgressDialog 
         	 simpleAdapter = new SimpleAdapter(RecommendComActivity.this, listItems,
     				R.layout.list_view, new String[] { "name" },
     				new int[] { R.id.item_community });
     		mListView.setAdapter(simpleAdapter);
     		mListView.setOnItemClickListener(new ListClickListener());
          	Toast.makeText(RecommendComActivity.this,"no data", Toast.LENGTH_SHORT).show();
             break;  
           default:  
               break;        
           }  
       }  
   };
   class ListClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			TextView tv = (TextView) arg1.findViewById(R.id.item_text);
			// Toast.makeText(getApplicationContext(),
			// "������λ��Ϊ��"+list.get(arg2),Toast.LENGTH_SHORT).show();
			clickedCom = list.get(arg2);
			showDialog();
		}
	}

	public void showDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final EditText passwordEditText = new EditText(this);
		builder.setIcon(R.drawable.icon);
		builder.setTitle("Please enter password");
		builder.setView(passwordEditText);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String passwordtext = passwordEditText.getText().toString();
				MultiUserChat muc = joinMultiUserChat(util.getName(), clickedCom, passwordtext);
				if (muc != null) {
					//mutiUserChatDB.addMuc(muc);//null object reference
					//11-08 modify
					communityListDB = CommunityListDB.getInstance(RecommendComActivity.this);
					communityListDB.insertCommunity(util.getName(),clickedCom, passwordtext);
					Intent intentBroadcast = new Intent("com.android.mobilesocietynetwork.client.CREATE_COMMUNITY_SUCCESSFULLY");
					sendBroadcast(intentBroadcast);
					finish();
				}
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		builder.create().show();
	}

	/**
	 * ���������
	 * 
	 * @param user
	 *            �ǳ�
	 * @param password
	 *            ����������
	 * @param roomsName
	 *            ��������
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomsName, String password) {
		if (connection == null)
			return null;
		try {
			// ʹ��XMPPConnection����һ��MultiUserChat����
			MultiUserChat muc = new MultiUserChat(connection, roomsName + "@conference." + connection.getServiceName());
			// �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// �û�����������
			muc.join(user, password, history, SmackConfiguration.getPacketReplyTimeout());
			Log.i("MultiUserChat", "room��" + roomsName + "��participate successfully........");
			Toast.makeText(getApplicationContext(), "room��" + roomsName + "��participate successfully", 3000).show();
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("MultiUserChat", "room��" + roomsName + "��participate failed........");
			Toast.makeText(getApplicationContext(), "room��" + roomsName + "��participate failed", 3000).show();
			return null;
		}
	}

}
