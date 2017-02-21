package com.android.mobilesocietynetwork.client.chat;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 好友功能界面
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.BaseActivity;
import com.android.mobilesocietynetwork.client.MainActivity;
import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.RecommendNoticeService;
import com.android.mobilesocietynetwork.client.database.BufferedUpMessageDB;
import com.android.mobilesocietynetwork.client.database.FileListDB;
import com.android.mobilesocietynetwork.client.database.FriendDB;
import com.android.mobilesocietynetwork.client.database.FriendListDB;
import com.android.mobilesocietynetwork.client.database.MessageDB;
import com.android.mobilesocietynetwork.client.database.OfflineMessageDB;
import com.android.mobilesocietynetwork.client.database.OfflineThrowTimeDB;
import com.android.mobilesocietynetwork.client.database.OnlineThrowTimeDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.database.WaitToRecDB;
import com.android.mobilesocietynetwork.client.info.BufferMsgEntity;
import com.android.mobilesocietynetwork.client.info.ChatMsgEntity;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.info.hybrid.AcceptNoticeInfo;
import com.android.mobilesocietynetwork.client.info.hybrid.BeginAskOnlineInfo;
import com.android.mobilesocietynetwork.client.info.hybrid.ScfInfo;
import com.android.mobilesocietynetwork.client.notice.NoticeActivity;
import com.android.mobilesocietynetwork.client.packet.hybrid.BufferedUpPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.HalfAskOnlinePacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.NetworkSpeedPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.NoticeAcceptPacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveFileTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.FileHelper;
import com.android.mobilesocietynetwork.client.util.GetData;
import com.android.mobilesocietynetwork.client.util.MyDate;
import com.msn.wqt.MsgBroadcastReceiver;
import com.msn.wqt.OfflineMsgEntity;
import com.msn.wqt.OfflineMsgService;
import com.msn.wqt.SendAPI;
import com.msn.wqt.WqtConstants;
import com.shareScreen.FileCheckThread;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import org.jivesoftware.smack.RosterGroup;
public class FriendActivity extends BaseActivity {
	private SharePreferenceUtil util;
	List<String> ListUid = new ArrayList<String>();
	String mName;
	User u;
	FriendDB friendDB;
	private MessageDB messageDB;
	private BufferedUpMessageDB bufferDB;
	private OfflineThrowTimeDB offlineThrowTimeDB;
	private WaitToRecDB waitToRecDB;
	private OnlineThrowTimeDB onlineThrowTimeDB;
	private OfflineMessageDB offlineMessageDB;

	/* 1120 add */
	private int count = 0;
	private String file=null;
	private Timer mTimer;
	private TimerTask mTimerTask;

	/* 1120 */

	/* 1121 add */
	private String[] sub = new String[10];
	private String[] buf=new String[10];
	private String suffix;
	private String current;
	private String total;
	private String destination;
	private int receivedPacket=0;
	/* 1121 */
	private ArrayList<String> fileList;
	String searchId = new String();
	String searchName = new String();
	String searchComId = new String();
	ArrayList<String> groupList = new ArrayList<String>();
	ArrayList<ArrayList<String>> childList = new ArrayList<ArrayList<String>>();
	GetData getData;
	MyExpandableListAdapter myAdapter;
	ExpandableListView expandListView;
	TextView textview;
	String reqFriId;
	String reqFriName;
	Roster roster;
	EditText newGroupTypeinEditText;
	EditText newFriendTypein;
	EditText groupNewFriendTo;
	String searchedFri;
	String groupFriTo;
	String Pfrom;
	// XMPPConnection con;

	// private long lastTotalRxBytes = 0;
	// private long lastTimeStamp = 0;

	private LinearLayout reFri;
	private LinearLayout seFri;
	private LinearLayout addFri;
	private LinearLayout addGroup;
	private IntentFilter mIntentFilter;
	//private RecommendNoticeReceiver mRecommendNoticeReceiver;
	private FriendListDB friendListDB;
	private FileListDB fileListDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_friend);
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(FriendActivity.this);

		// register recommend notice receiver
		/*mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.android.mobilesocietynetwork.RECEIVE_NEW_NOTICE");
		mRecommendNoticeReceiver = new RecommendNoticeReceiver();
		registerReceiver(mRecommendNoticeReceiver, mIntentFilter);
		
		// start recommend notice service
		Intent startIntent = new Intent(FriendActivity.this, RecommendNoticeService.class);
		startService(startIntent);*/
		
		initData();
		initView();
		initControl();
		if (XmppTool.getConnection().isConnected()) {
			dealReq(myHandler);
			initChatManager();
		}
	}

	private void initData() {
		// TODO Auto-generated method stub
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		mName = util.getName();
		friendDB = new FriendDB(this);
		messageDB = new MessageDB(this);
		bufferDB = new BufferedUpMessageDB(this);
		offlineThrowTimeDB = new OfflineThrowTimeDB(this);
		waitToRecDB = new WaitToRecDB(this);
		onlineThrowTimeDB = new OnlineThrowTimeDB(this);
		offlineThrowTimeDB = new OfflineThrowTimeDB(this);
		getData = new GetData(friendDB, mName);
		// 11-3 如果没有连接服务器，显示本地数据库中的数据
		if (!XmppTool.getConnection().isConnected()) {
			friendListDB = FriendListDB.getInstance(FriendActivity.this);
			HashMap<String, ArrayList<String>> friendMap = friendListDB.qureyGroup(util.getName());
			for (String group : friendMap.keySet()) {
				groupList.add(group);
				ArrayList<String> friends = new ArrayList<String>();
				friends = friendMap.get(group);
				childList.add(friends);
			}
		} else {// 连接服务器，数据从服务器处获取
			roster = XmppTool.getConnection().getRoster();
			groupList = getGroups(roster);
			// 如果分组为空，显示所有的好友列表
			if (groupList.isEmpty()) {
				groupList.add("Friends");
				ArrayList<String> friends = new ArrayList<String>();
				Collection<RosterEntry> it = roster.getEntries();
				for (RosterEntry rosterEnter : it) {
					friends.add(rosterEnter.getUser().split("@")[0]);
					// friends.add(rosterEnter.getUser());
				}
				childList.add(friends);
				friendListDB = FriendListDB.getInstance(FriendActivity.this);
				friendListDB.deletAllData();
				friendListDB.insertData(util.getName(), "Friends", friends);
				/**********1220add****************/
				fileListDB=FileListDB.getInstance(FriendActivity.this);
				//fileListDB.deletAllData();
				/**********1220add****************/
			} else {
				childList = getEntries(roster, groupList);
			}
		}
		myAdapter = new MyExpandableListAdapter(groupList, childList);

		/* 1120 add */

		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {	
				/***********1220监听数据库*****************/
				fileListDB=FileListDB.getInstance(FriendActivity.this);
				if(fileListDB.queryData().size()!=0){
					handler.sendEmptyMessage(2);//数据库中有文件
				}
				/***********1220监听数据库*****************/
				ReceiveFileTool.init();
				ReceiveFileTool.resetIsReceive();
				while(true){
				if(receiveFile()) {
					handler.sendEmptyMessage(1);//成功接收文件
					logPrint("test2");
					
					break;
					}
				ReceiveFileTool.resetIsReceive();
				logPrint("test");
				}
			
			}
		};
		
		//1130 modify
		/*ReceiveFileTool.init();
		ReceiveFileTool.resetIsReceive();
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					if(receiveFile()) {
						handler.sendEmptyMessage(1);
						logPrint("test2");
						break;
						}
					ReceiveFileTool.resetIsReceive();
					logPrint("test");
					}
			}
			
		}).start();*/
	
	}

	private boolean receiveFile() {
		//count = 0;
		while (!ReceiveFileTool.getIsReceive()) {
			try {
				Thread.sleep(30);
				//count++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// file=ReceiveFileTool.getFile();
		suffix = ReceiveFileTool.getSuffix();
		current = ReceiveFileTool.getCurrent();
		total = ReceiveFileTool.getTotal();
		destination=ReceiveFileTool.getDestination();
		sub[Integer.parseInt(current)] = ReceiveFileTool.getFile();
		file=composeFile(sub[Integer.parseInt(current)],current,total);
		ReceiveFileTool.resetIsReceive();
		return file != null;
	}
	
	public String composeFile(String s,String current,String total){
		int mCurrent=Integer.parseInt(current);
		int mTotal=Integer.parseInt(total);
		//String[] buf=new String[mTotal];
		//String file=null;
		buf[mCurrent]=s;
		receivedPacket++;
		if(receivedPacket==mTotal){
			file="";
			for(int i=0;i<mTotal;i++){
				
				file+=buf[i];
			}
			//reset
			receivedPacket=0;
			buf=new String[10];
			logPrint(file);
			return file;
		}else {
			return null;
		}
	}
	/* handle file */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				try {
					byte[] testByte = FileHelper.String2bytes(file);
					Date date=new Date();
					SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String fileName=dateFormat.format(date);
					FileHelper.byte2File(testByte, "/storage/emulated/0/MSN/", fileName+"\\."+suffix);
					/*********1220 add save to database******/
					if(destination!=util.getName()){
						fileListDB.getInstance(FriendActivity.this);
						fileListDB.insertData(fileName+"\\."+suffix, destination, "/storage/emulated/0/MSN/");
						//offline监听
						if(FileCheckThread.getInstance().isAlive()) FileCheckThread.getInstance().start();
						/*********1220 add save to database******/
					}
					
					else if(destination==util.getName()){
					Toast.makeText(FriendActivity.this, "Receive a file located in /storage/emulated/0/MSN/ ",
							Toast.LENGTH_LONG).show();
					//reset file
					file=null;
					destination=null;
					}
					//mTimer.cancel();
					//mTimer.schedule(mTimerTask, 0);
					/*receivedPacket=0;
					buf=new String[10];*/
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//mTimer.schedule(mTimerTask, 0);
			}
			/************1220从数据库中读取文件路径************/
			else if(msg.what==2){
				Log.d("com.android.mobilesocietynetwork.client", "Check database");
				String name;
				String dest;
				String path;
				fileList=fileListDB.queryData();
				name=fileList.get(0);
				dest=fileList.get(1);
				path=fileList.get(2);
				
				if(dest!=util.getName())
				/*offline传输*/
				Toast.makeText(FriendActivity.this, "name: "+name+"dest: "+dest+"path: "+path, Toast.LENGTH_LONG).show();
				/*offline传输*/
			}
			/************1220从数据库中读取文件路径************/
		}
		
	};
	
	
	private void initView() {
		// TODO Auto-generated method stub
		expandListView = (ExpandableListView) findViewById(R.id.list);
		expandListView.setAdapter(myAdapter);
		addFri = (LinearLayout) findViewById(R.id.addFri);
		addGroup = (LinearLayout) findViewById(R.id.addGroup);
		reFri = (LinearLayout) findViewById(R.id.recommendFri);
		seFri = (LinearLayout) findViewById(R.id.searchFri);
	}

	private void initControl() {
		// TODO Auto-generated method stub

		// 设置点击推荐好友的响应
		reFri.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 11-3 没网情况下，提示无法连接服务器
				if (XmppTool.getConnection().isConnected()) {
					Intent intent = new Intent(FriendActivity.this, RecommendFriActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(FriendActivity.this, "Service unavailable", Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 设置点击搜索好友的响应
		seFri.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 11-3 没网情况下，提示无法连接服务器
				if (XmppTool.getConnection().isConnected()) {
					Intent intent = new Intent(FriendActivity.this, SearchFriActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(FriendActivity.this, "Service unavailable", Toast.LENGTH_SHORT).show();
				}

			}
		});
		// 设置添加好友的响应
		addFri.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (XmppTool.getConnection().isConnected()) {
					final LinearLayout wannaAddNewFriend = (LinearLayout) getLayoutInflater()
							.inflate(R.layout.wanna_add_friend, null);
					new AlertDialog.Builder(FriendActivity.this).setTitle("Add friend").setView(wannaAddNewFriend)
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									newFriendTypein = (EditText) wannaAddNewFriend
											.findViewById(R.id.typeInWannaedNewFri);
									searchedFri = newFriendTypein.getText().toString();
									/*
									 * try { roster.createEntry(searchedFri +
									 * "@" + Constants.SERVER_NAME, searchedFri,
									 * null); } catch (XMPPException e) { //
									 * TODO Auto-generated catch block
									 * e.printStackTrace(); }
									 */
									Presence subscription = new Presence(Presence.Type.subscribe);
									subscription.setTo(searchedFri + "@" + Constants.SERVER_NAME);
									subscription.setFrom(util.getName() + "@" + Constants.SERVER_NAME);
									subscription.setStatus("wanner to add");
									XmppTool.getConnection().sendPacket(subscription);

								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							}).create().show();
				} else {
					Toast.makeText(FriendActivity.this, "Service unavailable", Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 设置添加分组的响应
		addGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (XmppTool.getConnection().isConnected()) {
					final LinearLayout newGroupTypein = (LinearLayout) getLayoutInflater()
							.inflate(R.layout.type_in_newgroup, null);
					new AlertDialog.Builder(FriendActivity.this).setTitle("Create group").setMessage("Please enter group name")
							.setView(newGroupTypein).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									newGroupTypeinEditText = (EditText) newGroupTypein
											.findViewById(R.id.newGroupTypein);
									String gName = newGroupTypeinEditText.getText().toString();
									if (addGro(roster, gName))
										toastShow("Succeed");
									toastShow("Failed");
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							}).create().show();
				} else {
					Toast.makeText(FriendActivity.this, "Service unavailable", Toast.LENGTH_SHORT).show();
				}
			}
		});

		/* 1120 add */
		// mTimer.schedule(mTimerTask, 0, 10000);
		mTimer.schedule(mTimerTask, 1000,60000);
		//mTimer.schedule(mTimerTask, 0);
	}

	/**
	 * 接收到不同的packet进行不同的处理 1、接收到available的presence包，加好友请求 2、接收到
	 * 
	 * @param myHandler
	 */
	public void dealReq(Handler myHandler) {
		// 被动添加，接收到添加好友请求
		AndFilter presence_wannaAdd_filter = new AndFilter(new PacketTypeFilter(Presence.class), new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				Presence p = (Presence) packet;
				if (p.getType().equals(Presence.Type.subscribe) && p.getStatus().equals("wanner to add")
						&& p.getTo().equals(util.getName() + "@" + Constants.SERVER_NAME))
					return true;
				return false;
			}
		});
		addPacketListenerWithType(presence_wannaAdd_filter, 0x1233, myHandler);
		// 拒绝加好友（好友请求中的拒绝或是删除）
		AndFilter presence_refuseAdd_filter = new AndFilter(new PacketTypeFilter(Presence.class), new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				Presence p = (Presence) packet;
				if (p.getType().equals(Presence.Type.unsubscribed)
						&& p.getTo().equals(util.getName() + "@" + Constants.SERVER_NAME))
					return true;
				return false;
			}
		});
		addPacketListenerWithType(presence_refuseAdd_filter, 0x1234, myHandler);
		// 同意加好友
		AndFilter presence_passAdd_filter = new AndFilter(new PacketTypeFilter(Presence.class), new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				Presence p = (Presence) packet;
				if (p.getType().equals(Presence.Type.subscribed)
						&& p.getTo().equals(util.getName() + "@" + Constants.SERVER_NAME) && p.getStatus().equals("ok"))
					return true;
				return false;
			}
		});
		addPacketListenerWithType(presence_passAdd_filter, 0x1235, myHandler);
		// 主动添加，被对方请求添加好友
		AndFilter presence_wannaAdd_filter2 = new AndFilter(new PacketTypeFilter(Presence.class), new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				Presence p = (Presence) packet;
				if (p.getType().equals(Presence.Type.subscribe) && p.getStatus().equals("wanner to add too")
						&& p.getTo().equals(util.getName() + "@" + Constants.SERVER_NAME))
					return true;
				return false;
			}
		});
		addPacketListenerWithType(presence_wannaAdd_filter2, 0x1236, myHandler);
	}

	public void addPacketListenerWithType(AndFilter filter, final int what, final Handler myHandler) {
		XmppTool.getConnection().addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				Pfrom = packet.getFrom();
				myHandler.sendEmptyMessage(what);
			}
		}, filter);
	}

	// 对接收到Message消息的处理,
	// 接收到message之后将收到massage中内容广播给不同的Activity
	protected void initChatManager() {
		ChatManager c = XmppTool.getConnection().getChatManager();
		c.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener() {
					@Override
					public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
						String name = message.getFrom().split("@")[0];
						Log.d("name", name);
						if (Constants.SERVER_NAME.equals(name)) {
							// 如果接收到的是辅助信息，把massage内容广播给baseActivity，每个Activity都会收到此广播
							/*
							 * Intent broadCast = new Intent();
							 * broadCast.putExtra(Constants.MSGKEY,
							 * message.getBody());
							 * broadCast.setAction(Constants.ACTION);
							 * sendBroadcast(broadCast);
							 */
							String msgBody = message.getBody();
							processMsgBody(msgBody);
						} else {
							// 如果是正常的消息
							// 取出message.getBody()中的类型和消息内容

							ChatMsgEntity entity = new ChatMsgEntity(name, MyDate.getDateEN(), message.getBody(),
									util.getImg(), true);
							if (message.getBody() != null) {
								messageDB.saveMsg(util.getName(), entity);// 保存到数据库
								// 并广播给Dialog页面
								Intent broadCast = new Intent();
								broadCast.setAction(Constants.NORMAL_ACTION);
								Bundle bundle = new Bundle();
								bundle.putSerializable(Constants.NORMAL_MSGKEY, entity);
								broadCast.putExtras(bundle);
								sendBroadcast(broadCast);// 把收到的消息已广播的形式发送出去
								Toast.makeText(FriendActivity.this,
										"您有新的消息来自" + message.getFrom() + ":" + message.getBody(), 0).show();// 其他好友的消息，就先提示，并保存到数据库
							}
						}
					}
				});
			}
		});
		return;
	}

	public void processMsgBody(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			Log.d("processMsgBody element=", (String) jsonObj.get(Constants.PACKET_TYPE));
			String msgType = (String) jsonObj.get(Constants.PACKET_TYPE);
			switch (msgType) {
			/*
			 * case Constants.RESPONSE_ONLINECOST:
			 * Toast.makeText(DialogActivity.this,
			 * "收到responseOnlineCost报文，在DialogActivity中处理。", Toast.LENGTH_LONG)
			 * .show(); // System.out.println("eeeeeeeeeeeee"); //
			 * logPrint("收到responseOnlineCost报文，在DialogActivity中处理: " + //
			 * jsonObj.toString()); double offlineCost =
			 * oc.computeOfflineCostFromSource(DialogActivity.this,
			 * user.getName()); double onlineCost = jsonObj.getDouble("value");
			 * if (offlineCost * 10 < onlineCost) { //
			 * System.out.println("wwwwwwwwww"); //
			 * logPrint("在dialogActivity中经比较使用offline方式。");
			 * Toast.makeText(DialogActivity.this,
			 * "在dialogActivity中经比较使用offline方式", Toast.LENGTH_LONG).show();
			 * offlineMessageDB.insertMessage(util.getName(), util.getName(),
			 * user.getName(), System.currentTimeMillis(),
			 * Integer.parseInt(throwid), 3600000 * 2, contenttext, "",
			 * System.currentTimeMillis(), "null");
			 * waitToRecDB.insertData(util.getName(), user.getName(),
			 * contenttext.hashCode(), System.currentTimeMillis(), 1);
			 * logPrint("处理完waitToRecDB的操作。"); myApplication.setIsSendOn(true);
			 * } else { // System.out.println("dddddddddd"); //
			 * Toast.makeText(DialogActivity.this, //
			 * "在dialogActivity中经比较使用online方式.", // Toast.LENGTH_LONG).show();
			 * // logPrint("在dialogActivity中经比较使用online方式。");
			 * Toast.makeText(DialogActivity.this, "type" +
			 * jsonObj.getInt("type"), Toast.LENGTH_LONG).show(); int type =
			 * jsonObj.getInt("type"); waitToRecDB.insertData(util.getName(),
			 * user.getName(), contenttext.hashCode(),
			 * System.currentTimeMillis(), 0); if (type == 1) { //
			 * System.out.println("QQQQQQQQQQ"); //
			 * logPrint("在dialogActivity中发送directSCF_jso包。"); // 向服务器发送directSCF
			 * // System.out.println("source:" + util.getName() +" //
			 * destination:" + user.getName() + " context:" + // contenttext);
			 * DirectScfPacket directScfPacket = new DirectScfPacket();
			 * directScfPacket.setThrowid(throwid);
			 * directScfPacket.setSource(util.getName());
			 * directScfPacket.setDestination(user.getName());
			 * directScfPacket.setValue(contenttext);
			 * directScfPacket.setStarttime(MyDate.getDateEN());
			 * directScfPacket.setLife(3600000 * 24 + "");
			 * XmppTool.getConnection().sendPacket(directScfPacket); } else { //
			 * System.out.println("OOOOOOOOOOOO"); //
			 * logPrint("在dialogActivity中发送forwardSCF_jso包。"); ForwardScfPacket
			 * forwardScfPacket = new ForwardScfPacket();
			 * forwardScfPacket.setThrowid(throwid);
			 * forwardScfPacket.setSource(util.getName());
			 * forwardScfPacket.setDestination(user.getName());
			 * forwardScfPacket.setValue(contenttext);
			 * forwardScfPacket.setStarttime(System.currentTimeMillis());
			 * forwardScfPacket.setLife(3600000 * 24 + "");
			 * forwardScfPacket.setForwarder(jsonObj.getString("forwarder"));
			 * forwardScfPacket.addPass(util.getName());
			 * XmppTool.getConnection().sendPacket(forwardScfPacket);
			 * 
			 * } } break;
			 */

			// 1118 add
			/*case Constants.FILE_PROCESS:
				String file = jsonObj.getString("file");
				try {
					byte[] testByte = FileHelper.String2bytes(file);
					FileHelper.byte2File(testByte, "/storage/emulated/0/MSN/", "test.jpg");
					Toast.makeText(FriendActivity.this, "Receive a file located in /storage/emulated/0/MSN/ ",
							Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				break;*/
			case Constants.DIRECT_SCF: {
				Log.d("received a DIRECT_SCF message", jsonObj.toString());
				String source = jsonObj.getString("source");
				String destination = jsonObj.getString("destination");
				String date = jsonObj.getString("starttime");
				String msgcontent = jsonObj.getString("msgcontent");
				Log.d("destination", destination);
				Log.d("util.getName()", util.getName());
				if (destination.trim().equals(util.getName().trim())) {
					// is my message
					ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
					chatMsgEntity.setName(source);
					chatMsgEntity.setMessage(msgcontent);
					chatMsgEntity.setDate(date);
					chatMsgEntity.setImg(util.getImg());
					chatMsgEntity.setMsgType(true);
					messageDB.saveMsg(destination, chatMsgEntity);
					Intent broadCast = new Intent();
					broadCast.setAction(Constants.NORMAL_ACTION);
					Bundle bundle = new Bundle();
					bundle.putSerializable(Constants.NORMAL_MSGKEY, chatMsgEntity);
					broadCast.putExtras(bundle);
					sendBroadcast(broadCast);// 把收到的消息已广播的形式发送出去
					Log.d("receive msgtype DIRECT_SCF", chatMsgEntity.toString());

					// mDataArrays.add(chatMsgEntity);
					// mAdapter.notifyDataSetChanged();
				} else {
					// offline send
					Log.d("receive a message from", source + " to " + destination);
					// Looper.prepare();
					// Toast.makeText(FriendActivity.this, "receive a message
					// from "+source +" to "+destination,
					// Toast.LENGTH_LONG).show();
					// Looper.loop();
					OfflineMsgEntity offlineMsgEntity = new OfflineMsgEntity();
					offlineMsgEntity.setSource(source);
					String dst = null;
					String dstName = destination;
					int atIndex = dstName.indexOf(WqtConstants.SYMBOL_SPILT);
					if (atIndex == -1) {
						dst = dstName;
					} else {
						dst = dstName.substring(0, atIndex);
					}
					Log.d("offline send ", dst);
					offlineMsgEntity.setDestnation(dst);
					offlineMsgEntity.setDate(MyDate.getDateEN());
					offlineMsgEntity.setMsgContent(msgcontent);
					SendAPI.sendOfflineMessageWithoutLink(FriendActivity.this, offlineMsgEntity);
				}
			}

				break;
			case WqtConstants.ERROR_SCF: {
				// Toast.makeText(this, "received no friend online",
				// Toast.LENGTH_LONG).show();
				Log.d("processMsgBody", jsonObj.toString());
				String source = jsonObj.getString("source");
				Log.d("source=", source);
				String destination = jsonObj.getString("destination");
				String date = jsonObj.getString("starttime");
				String msgcontent = jsonObj.getString("msgcontent");
				Log.d("destination=", destination);
				Log.d("date=", date);
				Log.d("msgcontent=", msgcontent);
				OfflineMsgEntity offlineMsgEntity = new OfflineMsgEntity();
				offlineMsgEntity.setSource(source);
				// String dstName=destination;
				// String destination=null;
				// int onIndex=dstName.indexOf(WqtConstants.SYMBOL_SPILT);
				// if(onIndex==-1){
				// destination=dstName;
				// }else{
				// destination=dstName.substring(0, onIndex);
				// }
				offlineMsgEntity.setDestnation(destination);
				offlineMsgEntity.setDate(date);
				offlineMsgEntity.setMsgContent(msgcontent);
				SendAPI.sendOfflineMessageWithoutLink(FriendActivity.this,
				offlineMsgEntity);
			}
			break;

			default:
				;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 把xmpp的操作封装起来，获取好友列表中的所有分组
	public ArrayList<String> getGroups(Roster roster) {
		ArrayList<String> groupsList = new ArrayList<String>();
		Collection<RosterGroup> rosterGroups = roster.getGroups();
		Iterator<RosterGroup> i = rosterGroups.iterator();
		while (i.hasNext())
			groupsList.add(i.next().getName());
		return groupsList;
	}

	// 获取全部好友列表
	public ArrayList<ArrayList<String>> getEntries(Roster roster, ArrayList<String> groupsList) {
		friendListDB = FriendListDB.getInstance(FriendActivity.this);
		ArrayList<ArrayList<String>> childList = new ArrayList<ArrayList<String>>();

		/*
		 * Iterator<String> i = groupsList.iterator(); while (i.hasNext()) {
		 * RosterGroup rosterGroup = roster.getGroup(i.next());
		 * logPrint(i.next()); ArrayList<String> group = new
		 * ArrayList<String>(); Collection<RosterEntry> rosterEntry =
		 * rosterGroup.getEntries(); Iterator<RosterEntry> j =
		 * rosterEntry.iterator(); while (j.hasNext())
		 * group.add(j.next().getName()); childList.add(group); //11-3 存入数据库
		 * friendListDB.insertData(util.getName(), i.next(), group); }
		 */
		for (String groupname : groupsList) {
			RosterGroup rosterGroup = roster.getGroup(groupname);
			ArrayList<String> group = new ArrayList<String>();
			Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
			Iterator<RosterEntry> j = rosterEntry.iterator();
			while (j.hasNext())
				group.add(j.next().getName());
			childList.add(group);
			friendListDB.insertData(util.getName(), groupname, group);
		}
		return childList;
	}

	// 添加分组
	public boolean addGro(Roster roster, String groupName) {
		try {
			roster.createGroup(groupName);
			groupList.add(groupName);
			ArrayList<String> i = new ArrayList<String>();
			i.clear();
			childList.add(i);
			myAdapter.notifyDataSetChanged();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 填写好友列表
	private class MyExpandableListAdapter extends BaseExpandableListAdapter {
		private ArrayList<String> groupList;
		private ArrayList<ArrayList<String>> childList;

		MyExpandableListAdapter(ArrayList<String> groupList, ArrayList<ArrayList<String>> childList) {
			this.groupList = groupList;
			this.childList = childList;
		}

		// 获取指定组位置指定子列表项处的子列表项数
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childList.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childList.get(groupPosition).size();
		}

		// 组项的内容生成函数
		private TextView getTextView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40);
			TextView textView = new TextView(FriendActivity.this);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setPadding(50, 0, 0, 0);
			textView.setTextSize(15);
			textView.setTextColor(getResources().getColor(R.color.black));
			return textView;
		}

		// 子项的内容生成函数
		private Button getButton(final int groupPosition, final int childPosition) {
			final String selectFri = new String(childList.get(groupPosition).get(childPosition));
			Button button = new Button(FriendActivity.this);
			button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			button.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
			button.setBackgroundColor(getResources().getColor(R.color.white));
			button.setPadding(40, 0, 0, 0);
			button.setTextSize(20);
			button.setTextColor(getResources().getColor(R.color.black));
			// 单击好友，进入对话框
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					User user = new User();
					user.setName(selectFri);
					// user.setId(selectFriId);
					Intent i = new Intent(FriendActivity.this, DialogActivity.class);
					i.putExtra("user", user);
					startActivity(i);
				}
			});
			// 长触好友时，实行删除好友操作
			button.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					new AlertDialog.Builder(FriendActivity.this).setTitle("delete friend").setMessage("Sure to delete this user?")
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									try {
										if (selectFri.contains("@")) {
											RosterEntry entry = roster.getEntry(selectFri);
											roster.removeEntry(entry);
										} else {
											RosterEntry entry = roster
													.getEntry(selectFri + "@" + Constants.SERVER_NAME);
											roster.removeEntry(entry);
										}
										toastShow("Succeed");
										ArrayList<String> g = (ArrayList<String>) childList.get(groupPosition);
										g.remove(childPosition);
										childList.set(groupPosition, g);
										notifyDataSetChanged();
									} catch (Exception e) {
										e.printStackTrace();
										toastShow("Failed");
									}
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							}).create().show();
					return true;
				}
			});
			return button;
		}

		// 该方法决定每个子选项的外观
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			Button button = getButton(groupPosition, childPosition);
			button.setText(getChild(groupPosition, childPosition).toString());
			return button;
		}

		// 获取指定组位置处的组数据
		@Override
		public Object getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		// 该方法决定每个组选项的外观
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			LinearLayout ll = new LinearLayout(FriendActivity.this);
			ll.setOrientation(0);
			TextView textview = getTextView();
			textview.setText(getGroup(groupPosition).toString());
			ll.addView(textview);
			return ll;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
	}

	// 接收到消息的处理
	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x1233) {
				final LinearLayout dealRequest = (LinearLayout) getLayoutInflater().inflate(R.layout.deal_req, null);
				new AlertDialog.Builder(FriendActivity.this).setTitle("Friend request")
						.setMessage(Pfrom.split("@")[0] + " ask to make friends with you,agree or not?").setView(dealRequest)
						.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Presence presence_back = new Presence(Presence.Type.subscribed);
								presence_back.setTo(Pfrom);
								presence_back.setFrom(util.getName() + "@" + Constants.SERVER_NAME);
								presence_back.setStatus("ok");
								XmppTool.getConnection().sendPacket(presence_back);
								Presence presence_add = new Presence(Presence.Type.subscribe);
								presence_add.setTo(Pfrom);
								presence_add.setFrom(util.getName() + "@" + Constants.SERVER_NAME);
								presence_add.setStatus("wanner to add too");
								XmppTool.getConnection().sendPacket(presence_add);
								String groupName = ((EditText) dealRequest.findViewById(R.id.acceptGroup)).getText()
										.toString();
								ArrayList<String> g = new ArrayList<String>();
								if (!groupList.contains(groupName)) {
									groupList.add(groupName);
									g.add(Pfrom.split("@")[0]);
									childList.add(g);
								} else {
									int i = groupList.indexOf(groupName);
									g = (ArrayList<String>) childList.get(i);
									g.add(Pfrom.split("@")[0]);
									childList.set(i, g);
								}
								myAdapter.notifyDataSetChanged();
								expandListView.setSelection(groupList.size() - 1);
								toastShow("添加好友" + Pfrom.split("@")[0] + "成功");
							}
						}).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Presence presence_back = new Presence(Presence.Type.unsubscribed);
								presence_back.setStatus("Sorry, I won't be your friend.");
								presence_back.setTo(Pfrom.split("@")[0]);
								XmppTool.getConnection().sendPacket(presence_back);
							}
						}).create().show();
			} else if (msg.what == 0x1234) {
				groupList = getGroups(roster);
				childList = getEntries(roster, groupList);
				myAdapter = new MyExpandableListAdapter(groupList, childList);
				expandListView.setAdapter(myAdapter);
				myAdapter.notifyDataSetChanged();
				toastShow(Pfrom.split("@")[0] + "delete your relationship");
			} else if (msg.what == 0x1235) {
				final LinearLayout addNewFriend = (LinearLayout) getLayoutInflater().inflate(R.layout.add_new_friend,
						null);
				new AlertDialog.Builder(FriendActivity.this).setTitle("Group hint")
						.setMessage("You have added" + Pfrom.split("@")[0] + "Put him into which group").setView(addNewFriend)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String groupName = ((EditText) addNewFriend.findViewById(R.id.typeInNewGroupOfNewFri))
										.getText().toString();

								ArrayList<String> g = new ArrayList<String>();
								if (!groupList.contains(groupName)) {
									groupList.add(groupName);
									g.add(Pfrom.split("@")[0]);
									childList.add(g);
								} else {
									int i = groupList.indexOf(groupName);
									g = (ArrayList<String>) childList.get(i);
									g.add(Pfrom.split("@")[0]);
									childList.set(i, g);
								}
								myAdapter.notifyDataSetChanged();
								expandListView.setSelection(groupList.size() - 1);
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ArrayList<String> g = new ArrayList<String>();
								groupList.add("");
								g.add(Pfrom.split("@")[0]);
								childList.add(g);
								myAdapter.notifyDataSetChanged();
								expandListView.setSelection(groupList.size() - 1);
							}
						}).create().show();
			} else if (msg.what == 0x1236) {
				final LinearLayout dealRequest = (LinearLayout) getLayoutInflater().inflate(R.layout.deal_req, null);
				new AlertDialog.Builder(FriendActivity.this).setTitle("Friend request")
						.setMessage(Pfrom.split("@")[0] + "ask to make friends with you,agree or not?").setView(dealRequest)
						.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Presence presence_back = new Presence(Presence.Type.subscribed);
								presence_back.setTo(Pfrom);
								presence_back.setFrom(util.getName() + "@" + Constants.SERVER_NAME);
								presence_back.setStatus("ok");
								XmppTool.getConnection().sendPacket(presence_back);
								String groupName = ((EditText) dealRequest.findViewById(R.id.acceptGroup)).getText()
										.toString();
								ArrayList<String> g = new ArrayList<String>();
								if (!groupList.contains(groupName)) {
									groupList.add(groupName);
									g.add(Pfrom.split("@")[0]);
									childList.add(g);
								} else {
									int i = groupList.indexOf(groupName);
									g = (ArrayList<String>) childList.get(i);
									g.add(Pfrom.split("@")[0]);
									childList.set(i, g);
								}
								myAdapter.notifyDataSetChanged();
								expandListView.setSelection(groupList.size() - 1);
								toastShow("Add friend" + Pfrom.split("@")[0] + "Successfully");
							}
						}).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Presence presence_back = new Presence(Presence.Type.unsubscribed);
								presence_back.setStatus("Sorry, I won't be your friend.");
								presence_back.setTo(Pfrom.split("@")[0]);
								XmppTool.getConnection().sendPacket(presence_back);
							}
						}).create().show();
			}
		}
	};

	// 提示框函数
	public void toastShow(String text) {
		Toast.makeText(FriendActivity.this, text, 3000).show();
	}

	@Override
	public void getMessage(String msg) {
		// TODO Auto-generated method stub

	}

	/*
	 * //11-3 private void isConnect() { // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理） try
	 * { ConnectivityManager connectivity = (ConnectivityManager) this
	 * .getSystemService(this.CONNECTIVITY_SERVICE); if (connectivity != null) {
	 * // 获取网络连接管理的对象 NetworkInfo info = connectivity.getActiveNetworkInfo(); if
	 * (info != null && info.isConnected()) { // 已经连接 if (info.getState() ==
	 * NetworkInfo.State.CONNECTED ) { if(
	 * XmppTool.getConnection().isConnected()){ connected = true; }else{
	 * connected= false; } } }else{ connected= false; } } else{ //未连接 connected=
	 * false; } } catch (Exception e) { // TODO: handle exception Log.v("error",
	 * e.toString()); } // connected= false; }
	 */
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			appExit();
			return false;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTimer.cancel();
		// unregister recommend notice receiver
		//unregisterReceiver(mRecommendNoticeReceiver);
	}

	private void appExit() {

		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "Click again to quit", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			ActivityManager.getInstance().exit();
		}
	}

/*	class RecommendNoticeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification mNotification = new Notification(R.drawable.ic_launcher, "你有一个活动通知",
					System.currentTimeMillis());
			Intent intent = new Intent(arg0, NoticeActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(arg0, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			mNotification.setLatestEventInfo(arg0, "MSN", "附近有活动,点击查看", pendingIntent);
			notificationManager.notify(1, mNotification);
			Log.d("RecommendNoticeService","FriendActivity receive broadcast from service");
			//Toast.makeText(MainActivity.this, "Receive a notice", Toast.LENGTH_SHORT).show();
		}
	}*/
	
	
}
