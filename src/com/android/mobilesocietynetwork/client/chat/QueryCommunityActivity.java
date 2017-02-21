package com.android.mobilesocietynetwork.client.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.MainActivity;
import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.database.CommunityListDB;
import com.android.mobilesocietynetwork.client.database.MultiUserChatDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.tool.RecommendComTool;
import com.android.mobilesocietynetwork.client.tool.SearchTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.searchwidget.ExpandTabView;
import com.android.mobilesocietynetwork.searchwidget.ListViewAdapter;
import com.android.mobilesocietynetwork.searchwidget.ViewMiddle;
import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.chat.RecommendComActivity.ListClickListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class QueryCommunityActivity extends Activity {
	// private ListView listView;
	private static XMPPConnection connection;
	private SharePreferenceUtil util;
	private MultiUserChatDB mutiUserChatDB;
	// private List<String> list;
	private String clickedCom;

	private EditText edit_search;
	private ListView lv_search;
	private List<String> strs;
	private ArrayList<Map<String, Object>> listItems;
	private Map<String, Object> listItem;
	private ListViewAdapter adapter;
	//private SimpleAdapter simpleAdapter;
	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewMiddle viewMiddle;
	private ProgressDialog pd; 
	private RecommendComTool retool;
	private SimpleAdapter simpleAdapter;
	
	private CommunityListDB communityListDB;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_query_community);
		initView();
		initData();
		initControl();
	}

	private void initView() {
		// TODO Auto-generated method stub

		edit_search = (EditText) findViewById(R.id.edit_search);
		expandTabView = (ExpandTabView) findViewById(R.id.expandtab_view);
		viewMiddle = new ViewMiddle(this);
		lv_search = (ListView) findViewById(R.id.query_listview);
		

	}

	private void initData() {
		// TODO Auto-generated method stub
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		connection = XmppTool.getConnection();
		//NullPointerException error ,need to add the following statement
		mutiUserChatDB = MultiUserChatDB.getInstance();

		// ��ʼ��ɸѡ������
		mViewArray.add(viewMiddle);
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("Choose type");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewMiddle.getShowText(), 1);

		// ��ȡ�����������е��������ƣ�����ʾ���б���
		try {
			strs = getConferenceRoom();
			
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//������
		/*strs = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			strs.add("����"+i);
		}*/
		
	  listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < strs.size(); i++) {
		   listItem = new LinkedHashMap<String, Object>();
			listItem.put("name", strs.get(i));
			listItems.add(listItem);
		}
		
		//1104 modify
		/*adapter = new ListViewAdapter(strs, this);
		lv_search.setAdapter(adapter);
		lv_search.setOnItemClickListener(new ListClickListener());*/
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.list_view, new String[] { "name" },
				new int[] { R.id.item_community });
		lv_search.setAdapter(simpleAdapter);
		lv_search.setOnItemClickListener(new ListClickListener());
	}

	private void initControl() {
		// TODO Auto-generated method stub

		// ����������������ƽ��й���
		edit_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				adapter.getFilter().filter(s);
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		// ����ѡ����������ͽ��й�����ʾ
		viewMiddle.setOnSelectListener(new ViewMiddle.OnSelectListener() {

			@Override
			public void getValue(String showText) {

				onRefresh(viewMiddle, showText);

			}
		});

		// ���ÿ���������ƺ����Ӧ
		lv_search.setOnItemClickListener(new ListClickListener());
	}

	private void onRefresh(View view, String showText) {

		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		} 
		//showText��ѡ��Ĺ����������ͣ�����������������������ʾ�����͵�������������
		//1106 modify
		retool=new RecommendComTool("labelRecommend", showText);
		retool.RecommendComSendIQ();
		   pd = ProgressDialog.show(QueryCommunityActivity.this, "Hint", "loading");  
	        new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	            	if(receiveProcess())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
	                handler.sendEmptyMessage(1);  // ִ�к�ʱ�ķ���֮��������handler  
	            	else 
	            		handler.sendEmptyMessage(0); //û������ʱ��������Ϣ0
	            }  	  
	        }).start();  
		/*SearchTool setool = new SearchTool();
		setool.SendSeComIQ( showText);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		strs = setool.getSearchFriList();
		adapter = new ListViewAdapter(strs, this);
		lv_search.setAdapter(adapter);
		Toast.makeText(QueryCommunityActivity.this, "ɸѡ����Ϊ"+showText,
				Toast.LENGTH_SHORT).show();*/
	}

	//���߳���ִ�еĺ�ʱ����  
	   private boolean receiveProcess() {  
	 	  //����һ��List<noticeInfo>���͵Ļ�б�
	   	int count = 0;
			while(retool.getRecommendComList()==null&&count<20){
			try {
				Thread.sleep(500);
				count++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			strs = retool.getRecommendComList();
	 		return strs.size()!=0;
	   }  
	   
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
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
	           	 simpleAdapter = new SimpleAdapter(QueryCommunityActivity.this, listItems,
	     				R.layout.list_view, new String[] { "name" },
	     				new int[] { R.id.item_community });
	           	 listItems.clear();
	           		for (int i = 0; i < strs.size(); i++) {
	           			Map<String, Object> listItem = new LinkedHashMap<String, Object>();
	           			listItem.put("name", strs.get(i));
	           			listItems.add(listItem);
	           			
	           		}
	           		lv_search.setAdapter(simpleAdapter);
	           		lv_search.setOnItemClickListener(new ListClickListener());
	               break;  

	           case 0:  //���û������
	         	  pd.dismiss();// �ر�ProgressDialog 
	         	 simpleAdapter = new SimpleAdapter(QueryCommunityActivity.this, listItems,
	     				R.layout.list_view, new String[] { "name" },
	     				new int[] { R.id.item_community });
	     		lv_search.setAdapter(simpleAdapter);
	     		lv_search.setOnItemClickListener(new ListClickListener());
	          	Toast.makeText(QueryCommunityActivity.this,"no data", Toast.LENGTH_SHORT).show();
	             break;  
	           default:  
	               break;        
	           }  
	       }  
	   };
	
	class ListClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			// Toast.makeText(getApplicationContext(),
			// "������λ��Ϊ��"+list.get(arg2),Toast.LENGTH_SHORT).show();
			clickedCom = strs.get(arg2);
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
				MultiUserChat muc = joinMultiUserChat(util.getName(),
						clickedCom, passwordtext);
				if (muc != null) {
					//mutiUserChatDB.addMuc(muc);
					//1108 modify
					communityListDB = CommunityListDB.getInstance(QueryCommunityActivity.this);
					communityListDB.insertCommunity(util.getName(), clickedCom,passwordtext);
					//1104 modify
					/*Intent intent = new Intent(QueryCommunityActivity.this,
							MainActivity.class);
					startActivity(intent);*/
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
	public static List<String> getConferenceRoom() throws XMPPException {
		List<String> list = new ArrayList<String>();
		new ServiceDiscoveryManager(connection);
		if (!MultiUserChat.getHostedRooms(connection, connection.getServiceName()).isEmpty()) {
			for (HostedRoom k : MultiUserChat.getHostedRooms(connection, connection.getServiceName())) {
				for (HostedRoom j : MultiUserChat.getHostedRooms(connection, k.getJid())) {
					RoomInfo info2 = MultiUserChat.getRoomInfo(connection, j.getJid());
					if (j.getJid().indexOf("@") > 0) {
						list.add(j.getName());
					}
				}
			}
		}
		return list;
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
	public MultiUserChat joinMultiUserChat(String user, String roomsName,
			String password) {
		if (connection == null)
			return null;
		try {
			// ʹ��XMPPConnection����һ��MultiUserChat����
			MultiUserChat muc = new MultiUserChat(connection, roomsName
					+ "@conference." + connection.getServiceName());
			// �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// �û�����������
			muc.join(user, password, history,
					SmackConfiguration.getPacketReplyTimeout());
			Log.i("MultiUserChat", "room��" + roomsName + "��participate successfully........");
			Toast.makeText(getApplicationContext(),
					"room��" + roomsName + "��participate successfully", 3000).show();
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("MultiUserChat", "room��" + roomsName + "��participate failed........");
			Toast.makeText(getApplicationContext(),
					"room��" + roomsName + "��participate failed", 3000).show();
			return null;
		}
	}

}
