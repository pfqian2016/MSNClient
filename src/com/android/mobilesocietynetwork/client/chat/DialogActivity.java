package com.android.mobilesocietynetwork.client.chat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.BaseActivity;
import com.android.mobilesocietynetwork.client.LoginActivity;
import com.android.mobilesocietynetwork.client.MainActivity;
import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.database.BufferedUpMessageDB;
import com.android.mobilesocietynetwork.client.database.CommunityListDB;
import com.android.mobilesocietynetwork.client.database.MessageDB;
import com.android.mobilesocietynetwork.client.database.MultiUserChatDB;
import com.android.mobilesocietynetwork.client.database.OfflineMessageDB;
import com.android.mobilesocietynetwork.client.database.OfflineThrowTimeDB;
import com.android.mobilesocietynetwork.client.database.OnlineThrowTimeDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.database.WaitToRecDB;
import com.android.mobilesocietynetwork.client.info.BufferMsgEntity;
import com.android.mobilesocietynetwork.client.info.ChatMsgEntity;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.info.hybrid.BeginAskOnlineInfo;
import com.android.mobilesocietynetwork.client.notice.CreateNoticeActivity;
import com.android.mobilesocietynetwork.client.packet.FileProcessPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.BeginAskOnlinePacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.DirectScfPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.ForwardScfPacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.FileHelper;
import com.android.mobilesocietynetwork.client.util.MyDate;
import com.android.mobilesocietynetwork.client.util.NetworkSearchingWillingSP;
import com.android.mobilesocietynetwork.client.util.NetworkSpeedSP;
import com.android.mobilesocietynetwork.client.util.OfflineCost;
import com.android.mobilesocietynetwork.offline.MainService;

import com.msn.wqt.OfflineMsgEntity;
import com.msn.wqt.SendAPI;
import com.msn.wqt.WqtConstants;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Build;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONObject;

public class DialogActivity extends BaseActivity {
	private TextView hint;
	private EditText content;
	private Button send, back, sendFile;
	private ListView mListView;
	private TextView mFriendName;
	private User user;
	private SharePreferenceUtil util;
	private ChatMsgViewAdapter mAdapter;// ÏûÏ¢ÊÓÍ¼µÄAdapter
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();// ÏûÏ¢¶ÔÏóÊý×é
	private MessageDB messageDB;
	private MultiUserChatDB multiUserChatDB;
	private BufferedUpMessageDB bufferDB;
	private OfflineThrowTimeDB offlineThrowTimeDB;
	private WaitToRecDB waitToRecDB;
	private OnlineThrowTimeDB onlineThrowTimeDB;
	private OfflineMessageDB offlineMessageDB;

	private OfflineCost oc;
	private NetworkSpeedSP netSpeedSP;
	private NetworkSearchingWillingSP netSeaWilAP;

	// private ChatManager cm;
	private Chat chat;
	private XMPPConnection con;
	private Roster roster;
	private MultiUserChat muc;
	private Presence presence;
	private int mode;
	private String forwarder;
	private ProgressDialog pd;
	private boolean flag;
	private String throwid;
	private String contenttext;
	private Builder builder = null;

	private NetworkchangeReceiver networkchangeReceiver;
	private boolean connected;
	private CommunityListDB communityListDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dialog);
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(DialogActivity.this);

		user = (User) getIntent().getSerializableExtra("user");
		// user = new User();
		// user.setName("test");
		con = XmppTool.getConnection();
		roster = con.getRoster();
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		messageDB = new MessageDB(this);
		bufferDB = new BufferedUpMessageDB(this);
		multiUserChatDB = MultiUserChatDB.getInstance();
		offlineThrowTimeDB = new OfflineThrowTimeDB(this);
		waitToRecDB = new WaitToRecDB(this);
		onlineThrowTimeDB = new OnlineThrowTimeDB(this);
		offlineMessageDB = new OfflineMessageDB(this);
		oc = new OfflineCost(util.getName());
		myApplication.setDirectTest(false);
		initView();
		initData();

		networkchangeReceiver = new NetworkchangeReceiver();
		// ×¢²áÍøÂçÁ¬½Ó×´Ì¬µÄ¹ã²¥£¬ÍøÂç×´Ì¬¸Ä±ä»áÊÕµ½¹ã²¥
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkchangeReceiver, intentFilter);
	}

	/**
	 * ³õÊ¼»¯½çÃæ
	 */
	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		content = (EditText) findViewById(R.id.chat_editmessage);
		send = (Button) findViewById(R.id.chat_send);
		back = (Button) findViewById(R.id.chat_back);
		sendFile = (Button) findViewById(R.id.chat_sendfile);
		hint = (TextView) findViewById(R.id.tvHint);
		mListView = (ListView) findViewById(R.id.listview);
		mFriendName = (TextView) findViewById(R.id.chat_name);
		mFriendName.setText(user.getName());
		if (user.isCom() == true) {
			// muc = multiUserChatDB.findMuc(user.getName());
			// muc.addMessageListener(new multiListener());
			if (XmppTool.getConnection().isConnected()) {
				communityListDB = CommunityListDB.getInstance(DialogActivity.this);
				String password = communityListDB.qureyPassword(util.getName(), user.getName());
				muc = entryMultiUserChat(user.getName(), password);
			}
		} else {
			ChatManager cm = initChatManager();
			// 1104 modify
			// chat = cm.createChat(user.getName() + "@wss-pc", null);
			chat = cm.createChat(user.getName() + "@openfire", null);
		}
		send.setOnClickListener(new SendListener());
		back.setOnClickListener(new BackListener());
		sendFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				builder = new AlertDialog.Builder(DialogActivity.this);
				View l = getLayoutInflater().inflate(R.layout.file_choose, null);
				builder.setTitle("�ļ���ʽ").setView(l).setNegativeButton("ȡ   ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
			/*	TextView picture = (TextView) l.findViewById(R.id.picture);
				TextView camera = (TextView) l.findViewById(R.id.camera);
				TextView music = (TextView) l.findViewById(R.id.music);
				TextView video = (TextView) l.findViewById(R.id.video);*/
				TextView file = (TextView) l.findViewById(R.id.file);
				/*picture.setOnClickListener(new filechooserlistener(R.id.picture));
				camera.setOnClickListener(new filechooserlistener(R.id.camera));
				music.setOnClickListener(new filechooserlistener(R.id.music));
				video.setOnClickListener(new filechooserlistener(R.id.video));*/
				file.setOnClickListener(new filechooserlistener(R.id.file));
			}
		});
	}

	// 1116 add
	/***************************** �ļ�����ʵ�� *************************************************/
	class filechooserlistener implements OnClickListener {
		private int id;

		public filechooserlistener(int r) {
			this.id = r;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i;
			switch (id) {
		/*	case R.id.picture:
				i = new Intent();
				i.setType("image/*");
				i.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(i, 0);
				break;
			case R.id.camera:
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					i = new Intent("android.media.action.IMAGE_CAPTURE");
					startActivityForResult(i, 1);
				} else {
					Toast.makeText(DialogActivity.this, "��abcd", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.music:
				i = new Intent();
				i.setType("audio/*");
				i.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(i, 2);
				break;
			case R.id.video:
				i = new Intent();
				i.setType("video/*");
				i.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(i, 3);
				break;*/
			case R.id.file:
				i = new Intent();
				i.setType("file/*");
				i.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(i, 4);
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 0:
				uri = data.getData();

				break;
			case 1:
				Bitmap photo = null;
				uri = data.getData();
				if (uri != null) {
					photo = BitmapFactory.decodeFile(uri.getPath());

				}
				if (photo == null) {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						photo = (Bitmap) bundle.get("data");

					} else {
						Toast.makeText(DialogActivity.this, "no picture", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				uri = data.getData();
				String path=uri.getPath();
				String suffix=path.split("\\.")[1];
				File file = new File(uri.getPath());
				String fileString = FileHelper.bytes2String(FileHelper.file2bytes(file));
				
				
				//split the String
				int n= 300000;
				
				int total= fileString.length()/n+1;
				//test
				//int total=2;
				String[] sub=new String[total];
			
				for(int i=0;i<total;i++){
					
					if(i==total-1){
						sub[i]=fileString.substring(n*i+0,n*i+fileString.length()%n);
					}else {
						sub[i]=fileString.substring(n*i+0,n*i+300000);
					}
	
					FileProcessPacket fileProcessPacket = new FileProcessPacket();
					fileProcessPacket.setFile(sub[i]);
					//test
					//fileProcessPacket.setFile("123");
					
					fileProcessPacket.setSource(util.getName());
					fileProcessPacket.setDestination(user.getName());
					fileProcessPacket.setCurrent(""+i);
					fileProcessPacket.setTotal(""+total);
					fileProcessPacket.setSuffix(suffix);
					XmppTool.getConnection().sendPacket(fileProcessPacket);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			
				break;
			}
		}
	}

/*	protected boolean receiveFileResult() {
		int count = 0;
		while (ReceiveNoticeIQTool.getResult() == null && count < 20) {
			try {
				Thread.sleep(500);
				count++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("1".equals(ReceiveNoticeIQTool.getResult())) {

			return true;
		} else {
			return false;
		}

	}*/

	// 1104 add
	protected ChatManager initChatManager() {
		ChatManager c = con.getChatManager();
		c.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener() {
					@Override
					public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
						String[] names = message.getFrom().split("@");
						ChatMsgEntity entity = new ChatMsgEntity(user.getName(), MyDate.getDateEN(), message.getBody(),
								util.getImg(), true);
						if (message.getBody() != null && message.getFrom().contains(user.getName())) {
							messageDB.saveMsg(user.getName(), entity);
							mDataArrays.add(entity);
							mAdapter.notifyDataSetChanged();
							// mListView.setSelection(mListView.getCount() - 1);
						} else if (message.getBody() != null) {
							messageDB.saveMsg(user.getName(), entity);// ±£´æµ½Êý¾Ý¿â
							Toast.makeText(DialogActivity.this,
									"ÄúÓÐÐÂµÄÏûÏ¢À´×Ô£º" + message.getFrom() + ":" + message.getBody(), 0).show();// ÆäËûºÃÓÑµÄÏûÏ¢£¬¾ÍÏÈÌáÊ¾£¬²¢±£´æµ½Êý¾Ý¿â
						}
					}

				});
			}
		});
		return c;
	}

	/**
	 * ³õÊ¼»¯Êý¾Ý
	 */
	public void initData() {
		String name = null;
		String userName = user.getName();
		int atIndex = userName.indexOf(WqtConstants.SYMBOL_SPILT);
		if (atIndex == -1) {
			name = userName;
		} else {
			name = userName.substring(0, atIndex);
		}
		List<ChatMsgEntity> list = messageDB.getMsg(util.getName(), name);
		mode = 0;
		flag = true;
		throwid = (int) (Math.random() * 1000) + "";
		if (list.size() > 0) {
			for (ChatMsgEntity entity : list) {
				if (entity.getImg() < 0) {
					entity.setImg(user.getImg());
				}
				mDataArrays.add(entity);
			}
			Collections.reverse(mDataArrays);
		}
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(mAdapter.getCount() - 1);
	}

	/**
	 * µ¥²¥ÐèÒª³õÊ¼»¯Â·ÓÉÄ£Ê½£º
	 * OnlineSelectModeÊÇÁªÍøÇé¿öÏÂÍ¨¹ý·þÎñÆ÷Ð­ÖúÍê³ÉÅÐ¶Ï£»
	 * OfflineSelectModeÊÇÍ¨¹ý×Ô¼º±¾µØµÄÊý¾Ý¿âÀ´Íê³ÉÅÐ¶Ï£»
	 * 
	 * modeÖµËµÃ÷£º mode = 0£ºË«·½¶¼ÔÚÏß£¬Ö±½Ó·¢ËÍ£» mode =
	 * 1£º·¢ËÍ·½ÔÚÏß£¬½ÓÊÕ·½²»ÔÚÏß£¬·þÎñÆ÷»º´æÏûÏ¢£¬Ö±½Ó¸ø½ÓÊÕ·½ mode =
	 * 2£º·¢ËÍ·½ÔÚÏß£¬½ÓÊÕ·½²»ÔÚÏß£¬·þÎñÆ÷Ñ¡ÔñÒ»¸öÔÚÏßµÄÖÐ×ªÕßSCF mode =
	 * 3£ºÖ±½ÓSCF mode = 4£º·¢ËÍ·½²»ÔÚÏß£¬»º´æµÈÉÏÏßÊ±·¢ËÍ
	 * 
	 */
	private void OfflineSelectMode() {
		// TODO Auto-generated method stub
		// ´ú¼ÛÊý¾Ý¿â»ñÈ¡Öµ±È½Ï
		double offlineCost = oc.computeOfflineCostFromSource(DialogActivity.this, user.getName());
		netSpeedSP = new NetworkSpeedSP(DialogActivity.this, Constants.NET_SPEED);
		netSeaWilAP = new NetworkSearchingWillingSP(DialogActivity.this, Constants.NET_WILLING);
		double onlineCost = 200 / (1024 * netSpeedSP.getWeightSpeed())
				+ netSeaWilAP.getWillingCost(System.currentTimeMillis())
				+ onlineThrowTimeDB.getWeightTime(util.getName(), user.getName());
		/*
		 * double onlineCost = onlineThrowTimeDB.getWeightTime(util.getName(),
		 * user.getName());
		 */
		if (offlineCost < onlineCost) {
			hint.setText("SCF");
			hint.setVisibility(View.VISIBLE);
			mode = 3;
		} else {
			hint.setText("");
			hint.setVisibility(View.VISIBLE);
			mode = 4;
		}
	}

	private void OnlineSelectMode() {
		// TODO Auto-generated method stub
		presence = roster.getPresence(user.getName() + "@" + Constants.SERVER_NAME);
		if (presence.isAvailable() == true) {
			// Èç¹ûÔÚÏß£¬Ö±½Ó·¢ËÍ
			ChatManager c = con.getChatManager();
			chat = c.createChat(user.getName() + "@" + Constants.SERVER_NAME, null);
			hint.setText("");
			hint.setVisibility(View.VISIBLE);
			mode = 0;
		} else if (presence.isAvailable() == false) {
			// Èç¹ûÀëÏß£¬Ñ¡ÔñµÈ´ý¸ø·þÎñÆ÷Ö±½Ó·¢ËÍ£¬»¹ÊÇÑ¡ÔñÈÃÆäËûÔÚÏßÓÃ»§SCF
			// ¿ªÆôÒ»¸öÏß³Ì£¬ÔÚÏß³Ì·¢ËÍ±¨ÎÄ1
			pd = ProgressDialog.show(DialogActivity.this, "loading", "");
			new Thread(new Runnable() {
				@Override
				public void run() {
					receiveResult();
					handler.sendEmptyMessage(mode);
				}
			}).start();
		}
	}

	private void receiveResult() {
		BeginAskOnlinePacket beginAskOnlinePacket = new BeginAskOnlinePacket();
		beginAskOnlinePacket.setThrowid(throwid);
		beginAskOnlinePacket.setDestination(user.getName());
		beginAskOnlinePacket.setSize("200/1024");// °´Ò»ÌõÏûÏ¢100¸ö×ÖÀ´Ëã
		XmppTool.getConnection().sendPacket(beginAskOnlinePacket);
		// ÔÚgetMessageÖÐ½ÓÊÕ½á¹û,½ÓÊÕµ½½á¹ûºó£¬Ñ­»·½áÊø
		int count = 1;
		while (flag && count < 20) {
			try {
				count++;
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (count == 20) {// Èç¹ûÊÇÒòÎª³¬Ê±ÁË
				mode = 3;
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1: // ÓÉ·þÎñÆ÷Ö±½Ó·¢ËÍ
				pd.dismiss();// ¹Ø±ÕProgressDialog
				hint.setText("");
				hint.setVisibility(View.VISIBLE);
				break;
			case 2: // ÓÉ·þÎñÆ÷¼ä½Ó·¢ËÍ
				pd.dismiss();// ¹Ø±ÕProgressDialog
				hint.setText("");
				hint.setVisibility(View.VISIBLE);
				break;
			case 3: // ÓÉSCF·¢ËÍ
				hint.setText("SCF");
				hint.setVisibility(View.VISIBLE);
				pd.dismiss();// ¹Ø±ÕProgressDialog
				break;
			case Constants.UPDATE_LIST:// ¸üÐÂÁÄÌìÐÅÏ¢
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * µã»÷·¢ËÍÖ®ºóµÄÏìÓ¦£¬ÅÐ¶ÏÊÇ¹ã²¥»¹ÊÇµ¥²¥£»¶ÔÓÚµ¥²¥¸ù¾Ýmode£¬·¢ËÍ²»Í¬µÄ±¨ÎÄ
	 *
	 */
	class SendListener implements OnClickListener {
		public void onClick(View v) {
			contenttext = content.getText().toString();
			throwid = Integer.parseInt(throwid) + 1 + "";
			if (contenttext.length() > 0) {
				// ¶Ô»°¿òÏÔÊ¾¶Ô»°
				ChatMsgEntity entity = new ChatMsgEntity(user.getName(), MyDate.getDateEN(), contenttext, util.getImg(),
						false);
				entity.setName(util.getName());
				entity.setDate(MyDate.getDateEN());
				entity.setMessage(contenttext);
				entity.setImg(util.getImg());
				entity.setMsgType(false);
				messageDB.saveMsg(util.getName(), entity);
				mDataArrays.add(entity);
				mAdapter.notifyDataSetChanged();// Í¨ÖªListView£¬Êý¾ÝÒÑ·¢Éú¸Ä±ä
				content.setText("");// Çå¿Õ±à¼­¿òÊý¾Ý
				mListView.setSelection(mListView.getCount() - 1);// ·¢ËÍÒ»ÌõÏûÏ¢Ê±£¬ListViewÏÔÊ¾Ñ¡Ôñ×îºóÒ»Ïî

				// ·¢ËÍ¸ø·þÎñÆ÷
				if (user.getName().equals("¹ã²¥ÔÚÏßºÃÓÑ")) // ¹ã²¥
				{
					Collection<RosterEntry> rosterEntry = roster.getEntries();
					Iterator<RosterEntry> i = rosterEntry.iterator();
					ChatManager c = con.getChatManager();
					while (i.hasNext()) {
						String name = i.next().getName();
						chat = c.createChat(name + "@" + Constants.SERVER_NAME, null);
						try {
							chat.sendMessage(contenttext);
						} catch (XMPPException e) {
							e.printStackTrace();
						}
					}
				} else if (user.isCom() == true) // ×é²¥
				{
					try {
						muc.sendMessage(contenttext);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else // µ¥²¥
				{
					if (connected && mode == 0) {
						// Õý³£·¢ËÍ
						try {
							/*
							 * hint.setText("ÌáÊ¾£ºÏûÏ¢Í¨¹ý·þÎñÆ÷·¢ËÍ");
							 * hint.setVisibility(View.VISIBLE); ChatManager c =
							 * con.getChatManager(); chat =
							 * c.createChat(user.getName() + "@" +
							 * Constants.SERVER_NAME, null);
							 * chat.sendMessage(contenttext);
							 */

							DirectScfPacket directScfPacket = new DirectScfPacket();
							directScfPacket.setThrowid(throwid);
							directScfPacket.setSource(util.getName());
							String remoteName = user.getName();
							int atIndex = remoteName.indexOf(WqtConstants.SYMBOL_SPILT);
							if (atIndex != -1) {
								directScfPacket.setDestination(remoteName.substring(0, atIndex));
							} else {
								directScfPacket.setDestination(remoteName);
							}
							// directScfPacket.setDestination(user.getName());
							directScfPacket.setValue(contenttext);
							directScfPacket.setStarttime(MyDate.getDateEN());
							directScfPacket.setMsgcontent(contenttext);
							directScfPacket.setLife("3600000 * 24");
							XmppTool.getConnection().sendPacket(directScfPacket);

							// test
							/*
							 * Log.d("send msg", "by offline method");
							 * OfflineMsgEntity offlineMsgEntity=new
							 * OfflineMsgEntity();
							 * offlineMsgEntity.setSource(util.getName());
							 * String dstName=user.getName(); String
							 * destination=null; int
							 * onIndex=dstName.indexOf(WqtConstants.SYMBOL_SPILT
							 * ); if(onIndex==-1){ destination=dstName; }else{
							 * destination=dstName.substring(0, onIndex); }
							 * offlineMsgEntity.setDestnation(destination);
							 * 
							 * offlineMsgEntity.setDate(directScfPacket.
							 * getStarttime());
							 * offlineMsgEntity.setMsgContent(contenttext);
							 * SendAPI.sendOfflineMessageWithoutLink(
							 * DialogActivity.this, offlineMsgEntity);
							 */
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (!connected) {
						/*
						 * double offlineCost =
						 * oc.computeOfflineCostFromSource(DialogActivity.this,
						 * user.getName()); netSpeedSP = new
						 * NetworkSpeedSP(DialogActivity.this,
						 * Constants.NET_SPEED); netSeaWilAP = new
						 * NetworkSearchingWillingSP(DialogActivity.this,
						 * Constants.NET_WILLING); onlineThrowTimeDB = new
						 * OnlineThrowTimeDB(DialogActivity.this); //
						 * netSpeedSP.getWeightSpeed()Îª0£¬»á±¨Òì³£ // double
						 * onlineCost = getMessageLength(contenttext) / //
						 * netSpeedSP.getWeightSpeed() // + //
						 * netSeaWilAP.getWillingCost(System.currentTimeMillis()
						 * ) // +
						 * onlineThrowTimeDB.getWeightTime(util.getName(), //
						 * user.getName()); double onlineCost =
						 * netSeaWilAP.getWillingCost(System.currentTimeMillis()
						 * ) + onlineThrowTimeDB.getWeightTime(util.getName(),
						 * user.getName()); // onlineCostÖµÎª¸º£¿£¿£¿£¿
						 * onlineCost = 10; if (onlineCost < offlineCost) {
						 * hint.setText("ÌáÊ¾£ºÏûÏ¢ÔÚÍøÂç»Ö¸´Ê±·¢ËÍ");
						 * hint.setVisibility(View.VISIBLE); //
						 * Toast.makeText(DialogActivity.this, "save in DB", //
						 * Toast.LENGTH_LONG).show();
						 * bufferDB.insertMessage(util.getName(),
						 * util.getName(), user.getName(),
						 * System.currentTimeMillis(), contenttext.hashCode(),
						 * 3600000 * 24, contenttext, ""); } else {
						 * hint.setText("ÌáÊ¾£ºÏûÏ¢Í¨¹ýSCF·¢ËÍ");
						 * hint.setVisibility(View.VISIBLE); //
						 * Toast.makeText(DialogActivity.this, "select scf", //
						 * Toast.LENGTH_LONG).show();
						 * offlineMessageDB.insertMessage(util.getName(),
						 * util.getName(), user.getName(),
						 * System.currentTimeMillis(), contenttext.hashCode(),
						 * 3600000 * 24, contenttext, "",
						 * getMessageLength(contenttext), "null");
						 * waitToRecDB.insertData(util.getName(),
						 * user.getName(), contenttext.hashCode(),
						 * System.currentTimeMillis(), 1);
						 * myApplication.setIsSendOn(true); }
						 */
						hint.setText("SCF");
						hint.setVisibility(View.VISIBLE);
						Log.d("send msg", "by offline method");
						OfflineMsgEntity offlineMsgEntity = new OfflineMsgEntity();
						offlineMsgEntity.setSource(util.getName());
						String destination = null;
						String dstName = user.getName();
						int atIndex = dstName.indexOf(WqtConstants.SYMBOL_SPILT);
						if (atIndex == -1) {
							destination = dstName;
						} else {
							destination = dstName.substring(0, atIndex);
						}
						offlineMsgEntity.setDestnation(destination);
						offlineMsgEntity.setDate(MyDate.getDateEN());
						offlineMsgEntity.setMsgContent(contenttext);
						SendAPI.sendOfflineMessageWithoutLink(DialogActivity.this, offlineMsgEntity);
					} // ·¢ËÍ·½Á¬½Óµ½·þÎñÆ÷
					else {
						// Toast.makeText(DialogActivity.this, "get online cost
						// from server", Toast.LENGTH_LONG).show();
						/*
						 * BeginAskOnlinePacket beginAskOnlinePacket = new
						 * BeginAskOnlinePacket();
						 * beginAskOnlinePacket.setThrowid(throwid);
						 * beginAskOnlinePacket.setDestination(user.getName());
						 * beginAskOnlinePacket.setSize("200/1024");//
						 * °´Ò»ÌõÏûÏ¢100¸ö×ÖÀ´Ëã
						 * XmppTool.getConnection().sendPacket(
						 * beginAskOnlinePacket);
						 */
					}
					// switch (mode) {
					// case 0:
					// try {
					// // ·¢ËÍÓïÒô£¬¼ÓÈëtype£¬·â×°Îªjson
					// chat.sendMessage(contenttext);
					// } catch (XMPPException e) {
					// e.printStackTrace();
					// }
					// break;
					// case 1:
					// // ·¢ËÍ¸ø·þÎñÆ÷£¬·þÎñÆ÷Ö±½Ó·¢ËÍ£¨±¨ÎÄ5£©
					// DirectScfPacket directScfPacket = new DirectScfPacket();
					// directScfPacket.setThrowid(throwid);
					// directScfPacket.setSource(util.getName());
					// directScfPacket.setDestination(user.getName());
					// directScfPacket.setValue(contenttext);
					// directScfPacket.setStarttime(MyDate.getDateLong());
					// directScfPacket.setLife("3600000 * 24");
					// XmppTool.getConnection().sendPacket(directScfPacket);
					// waitToRecDB.insertData(util.getName(), user.getName(),
					// Integer.parseInt(throwid),
					// MyDate.getDateLong(), 0);
					// break;
					// case 2:
					// // ·¢ËÍ¸ø·þÎñÆ÷£¬·þÎñÆ÷ÖÐ×ª£¨±¨ÎÄ6£©
					// ForwardScfPacket forwardScfPacket = new
					// ForwardScfPacket();
					// forwardScfPacket.setThrowid(throwid);
					// forwardScfPacket.setSource(util.getName());
					// forwardScfPacket.setDestination(user.getName());
					// forwardScfPacket.setForwarder(forwarder);
					// forwardScfPacket.setValue(contenttext);
					// forwardScfPacket.setStarttime(MyDate.getDateLong());
					// forwardScfPacket.setLife("3600000 * 24");
					// forwardScfPacket.addPass(util.getName());
					// XmppTool.getConnection().sendPacket(forwardScfPacket);
					// waitToRecDB.insertData(util.getName(), user.getName(),
					// Integer.parseInt(throwid),
					// MyDate.getDateLong(), 0);
					// break;
					// case 3: {
					// offlineMessageDB.insertMessage(util.getName(),
					// util.getName(), user.getName(),
					// System.currentTimeMillis(),
					// Integer.parseInt(throwid),
					// 3600000 * 24,
					// contenttext, util.getName(), 200/1024, "null");
					// waitToRecDB.insertData(util.getName(), user.getName(),
					// contenttext.hashCode(),
					// System.currentTimeMillis(), 1);
					// myApplication.setIsSendOn(true);
					// break;
					// }
					// case 4: {
					// //»º´æÔÚÊý¾Ý¿âÖÐ
					// ArrayList<String> passList = new ArrayList<String>();
					// passList.add(util.getName());
					// BufferMsgEntity bufEntity = new BufferMsgEntity(
					// throwid, user.getName(), util.getName(),"100",
					// MyDate.getDateEN(), contenttext,passList);
					// bufferDB.insertMessage(util.getName(), bufEntity);
					// waitToRecDB.insertData(util.getName(), user.getName(),
					// Integer.parseInt(throwid),
					// MyDate.getDateLong(), 0);
					// break;
					// }
					// default:
					// break;
					// }
				}
			}
		}
	}

	/**
	 * ·µ»Ø£¬¹Ø±Õ´ËÒ³Ãæ
	 *
	 */

	class BackListener implements OnClickListener {
		public void onClick(View v) {
			finish();
		}
	}

	/**
	 * »áÒéÊÒÏûÏ¢¼àÌýÀà
	 * 
	 */
	public class multiListener implements PacketListener {
		@Override
		public void processPacket(org.jivesoftware.smack.packet.Packet packet) {
			org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet; // ½ÓÊÕÀ´×ÔÁÄÌìÊÒµÄÁÄÌìÐÅÏ¢
			String body = message.getBody();
			String from = StringUtils.parseResource(message.getFrom());
			String fromRoomName = StringUtils.parseName(message.getFrom());
			if (!from.equals(util.getName())) // ºöÂÔ×Ô¼ºÀ´µÄÏûÏ¢
			{
				ChatMsgEntity entity = new ChatMsgEntity(from, MyDate.getDateEN(), message.getBody(), util.getImg(),
						true);
				if (fromRoomName.contains(user.getName())) {
					messageDB.saveMsg(fromRoomName, entity);
					mDataArrays.add(entity);
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mListView.getCount() - 1);
				} else {
					messageDB.saveMsg(fromRoomName, entity);// ±£´æµ½Êý¾Ý¿â
					Toast.makeText(DialogActivity.this,
							"ÄúÓÐÐÂµÄÏûÏ¢À´×Ô£º" + message.getFrom() + ":" + message.getBody(), 0).show();// ÆäËûºÃÓÑµÄÏûÏ¢£¬¾ÍÏÈÌáÊ¾£¬²¢±£´æµ½Êý¾Ý¿â
				}
			}
		}
	}

	/**
	 * ½ÓÊÕFriendActivity¹ã²¥¹ýÀ´µÄÏûÏ¢
	 * ºÃÓÑÏûÏ¢µÄ¼àÌý·Åµ½FriendActivityÖÐ£¬ÓÐÏûÏ¢¾Í·Åµ½Êý¾Ý¿âÖÐ£»
	 * ÓÃ¹ã²¥Í¨ÖªDialogÊÇ·ñÐèÒªÏÔÊ¾
	 */
	private BroadcastReceiver MsgReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			ChatMsgEntity entity = (ChatMsgEntity) intent.getSerializableExtra(Constants.NORMAL_MSGKEY);
			Log.d("onReceive", entity.toString());
			String name = null;
			String userName = user.getName();
			int atIndex = userName.indexOf(WqtConstants.SYMBOL_SPILT);
			if (atIndex == -1) {
				name = userName;
			} else {
				name = userName.substring(0, atIndex);
			}
			if (entity.getMessage() != null && entity.getName().contains(name)) {
				mDataArrays.add(entity);
				mAdapter.notifyDataSetChanged();
			}

		}
	};

	@Override
	public void onStart() {// ÔÚstart·½·¨ÖÐ×¢²á¹ã²¥½ÓÊÕÕß
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.NORMAL_ACTION);
		registerReceiver(MsgReceiver, intentFilter);// ×¢²á½ÓÊÜÕý³£ÏûÏ¢¹ã²¥
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		messageDB.close();
		unregisterReceiver(MsgReceiver);// ×¢Ïú¹ã²¥
		unregisterReceiver(networkchangeReceiver);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager mgr = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	// **´¦Àí»ìºÏÊ½ÏÂµÄÀ©Õ¹xmpp±¨ÎÄ¡¢
	// **½ÓÊÕÊÇÔÚFriendActivityÖÐµÄMessageLisenerÖÐ£¬
	// **½ÓÊÕµ½ºó·¢ËÍ¹ã²¥£¬¸øMainServiceºÍBaseActivityÖÐµÄgetMessage½øÐÐ´¦Àí

	@Override
	public void getMessage(String msg) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObj = new JSONObject(msg);
			switch ((String) jsonObj.get(Constants.PACKET_TYPE)) {
			case Constants.RESPONSE_ONLINECOST:
				Toast.makeText(DialogActivity.this, "ÊÕµ½responseOnlineCost±¨ÎÄ£¬ÔÚDialogActivityÖÐ´¦Àí¡£",
						Toast.LENGTH_LONG).show();
				// System.out.println("eeeeeeeeeeeee");
				// logPrint("ÊÕµ½responseOnlineCost±¨ÎÄ£¬ÔÚDialogActivityÖÐ´¦Àí:
				// " +
				// jsonObj.toString());
				double offlineCost = oc.computeOfflineCostFromSource(DialogActivity.this, user.getName());
				double onlineCost = jsonObj.getDouble("value");
				if (offlineCost * 10 < onlineCost) {
					// System.out.println("wwwwwwwwww");
					// logPrint("ÔÚdialogActivityÖÐ¾­±È½ÏÊ¹ÓÃoffline·½Ê½¡£");
					Toast.makeText(DialogActivity.this, "ÔÚdialogActivityÖÐ¾­±È½ÏÊ¹ÓÃoffline·½Ê½", Toast.LENGTH_LONG)
							.show();
					offlineMessageDB.insertMessage(util.getName(), util.getName(), user.getName(),
							System.currentTimeMillis(), Integer.parseInt(throwid), 3600000 * 2, contenttext, "",
							System.currentTimeMillis(), "null");
					waitToRecDB.insertData(util.getName(), user.getName(), contenttext.hashCode(),
							System.currentTimeMillis(), 1);
					logPrint("´¦ÀíÍêwaitToRecDBµÄ²Ù×÷¡£");
					myApplication.setIsSendOn(true);
				} else {
					// System.out.println("dddddddddd");
					// Toast.makeText(DialogActivity.this,
					// "ÔÚdialogActivityÖÐ¾­±È½ÏÊ¹ÓÃonline·½Ê½.",
					// Toast.LENGTH_LONG).show();
					// logPrint("ÔÚdialogActivityÖÐ¾­±È½ÏÊ¹ÓÃonline·½Ê½¡£");
					Toast.makeText(DialogActivity.this, "type" + jsonObj.getInt("type"), Toast.LENGTH_LONG).show();
					int type = jsonObj.getInt("type");
					waitToRecDB.insertData(util.getName(), user.getName(), contenttext.hashCode(),
							System.currentTimeMillis(), 0);
					if (type == 1) {
						// System.out.println("QQQQQQQQQQ");
						// logPrint("ÔÚdialogActivityÖÐ·¢ËÍdirectSCF_jso°ü¡£");
						// Ïò·þÎñÆ÷·¢ËÍdirectSCF
						// System.out.println("source:" + util.getName() +"
						// destination:" + user.getName() + " context:" +
						// contenttext);
						DirectScfPacket directScfPacket = new DirectScfPacket();
						directScfPacket.setThrowid(throwid);
						directScfPacket.setSource(util.getName());
						directScfPacket.setDestination(user.getName());
						directScfPacket.setValue(contenttext);
						directScfPacket.setStarttime(MyDate.getDateEN());
						directScfPacket.setLife(3600000 * 24 + "");
						XmppTool.getConnection().sendPacket(directScfPacket);
					} else {
						// System.out.println("OOOOOOOOOOOO");
						// logPrint("ÔÚdialogActivityÖÐ·¢ËÍforwardSCF_jso°ü¡£");
						ForwardScfPacket forwardScfPacket = new ForwardScfPacket();
						forwardScfPacket.setThrowid(throwid);
						forwardScfPacket.setSource(util.getName());
						forwardScfPacket.setDestination(user.getName());
						forwardScfPacket.setValue(contenttext);
						forwardScfPacket.setStarttime(System.currentTimeMillis());
						forwardScfPacket.setLife(3600000 * 24 + "");
						forwardScfPacket.setForwarder(jsonObj.getString("forwarder"));
						forwardScfPacket.addPass(util.getName());
						XmppTool.getConnection().sendPacket(forwardScfPacket);

					}
				}
				break;
			case Constants.DIRECT_SCF:
				Log.d("received a DIRECT_SCF message", jsonObj.toString());
				String source = jsonObj.getString("source");
				String destination = jsonObj.getString("destination");
				String date = jsonObj.getString("starttime");
				String msgcontent = jsonObj.getString("msgcontent");
				if (destination.equals(util.getName())) {
					// is my message
					ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
					chatMsgEntity.setName(source);
					chatMsgEntity.setMessage(msgcontent);
					chatMsgEntity.setDate(date);
					chatMsgEntity.setImg(util.getImg());
					chatMsgEntity.setMsgType(true);
					messageDB.saveMsg(util.getName(), chatMsgEntity);
					mDataArrays.add(chatMsgEntity);
					mAdapter.notifyDataSetChanged();
				} else {
					// offline send
				}

				break;
			default:
				;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public short getMessageLength(String message) {
		return (short) message.getBytes().length;
	}

	// ÄÚ²¿Àà£¬¼Ì³ÐBroadcastReceiver£¬Ã¿´ÎÍøÂç×´Ì¬±ä»¯£¬»áÖ´ÐÐonReceive
	class NetworkchangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ÍøÂç×´Ì¬±ä»¯Ê±£¬½øÈëisConnectÅÐ¶ÏÊÇÍøÂçÁ¬½Ó»¹ÊÇ¶Ï¿ª
			isConnect();
		}

	}

	/**
	 * ¼ì²éÍøÂç×´Ì¬£¬Ã»ÓÐÍøÂçÊ±ÌáÊ¾ÓÃ»§ÍøÂç²»¿ÉÓÃ£¬ ·¢ËÍÊ±´æÊý¾Ý¿âÖÐ£¬ÁªÍøÊ±ºò·¢
	 */
	private void isConnect() {
		// »ñÈ¡ÊÖ»úËùÓÐÁ¬½Ó¹ÜÀí¶ÔÏó£¨°üÀ¨¶Ôwi-fi,netµÈÁ¬½ÓµÄ¹ÜÀí£©
		try {
			ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// »ñÈ¡ÍøÂçÁ¬½Ó¹ÜÀíµÄ¶ÔÏó
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// ÒÑ¾­Á¬½Ó
					if (info.getState() == NetworkInfo.State.CONNECTED && XmppTool.getConnection().isConnected()) {
						connected = true;
						con = XmppTool.getConnection();
						roster = con.getRoster();
						if (user.getName().contains("@")) {
							presence = roster.getPresence(user.getName());
						} else {
							presence = roster.getPresence(user.getName() + "@" + Constants.SERVER_NAME);
						}
						if (presence.isAvailable() == true) {
							// Èç¹ûÔÚÏß£¬Ö±½Ó·¢ËÍ
							ChatManager c = con.getChatManager();
							if (user.getName().contains("@")) {
								chat = c.createChat(user.getName(), null);
							} else {
								chat = c.createChat(user.getName() + "@" + Constants.SERVER_NAME, null);
							}
							// chat = c.createChat(user.getName() + "@" +
							// Constants.SERVER_NAME, null);
							mode = 0;
						}
					} else {
						connected = false;
					}
				} else {
					// Î´Á¬½Ó
					connected = false;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		// connected= false;
	}

	// 1108 modify
	private MultiUserChat entryMultiUserChat(String roomsName, String password) {
		// TODO Auto-generated method stub
		try {
			// ʹ��XMPPConnection����һ��MultiUserChat����
			MultiUserChat muc = new MultiUserChat(XmppTool.getConnection(),
					roomsName + "@conference." + XmppTool.getConnection().getServiceName());
			// �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// �û�����������
			muc.join(util.getName(), password, history, SmackConfiguration.getPacketReplyTimeout());
			muc.addMessageListener(new multiListener());
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
	}

}
