package com.android.mobilesocietynetwork.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.mobilesocietynetwork.client.chat.CommunityActivity;
import com.android.mobilesocietynetwork.client.chat.DialogActivity;
import com.android.mobilesocietynetwork.client.chat.FriendActivity;
import com.android.mobilesocietynetwork.client.chat.MapActivity;
import com.android.mobilesocietynetwork.client.database.BufferedUpMessageDB;
import com.android.mobilesocietynetwork.client.database.OfflineMessageDB;
import com.android.mobilesocietynetwork.client.database.OfflineThrowTimeDB;
import com.android.mobilesocietynetwork.client.database.OnlineThrowTimeDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.database.WaitToRecDB;
import com.android.mobilesocietynetwork.client.info.BufferMsgEntity;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.info.hybrid.BeginAskOnlineInfo;
import com.android.mobilesocietynetwork.client.notice.NoticeActivity;
import com.android.mobilesocietynetwork.client.packet.hybrid.BufferedUpPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.DirectScfPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.ForwardScfPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.NetworkSpeedPacket;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.offline.MainService;
import com.msn.wqt.MsgBroadcastReceiver;
import com.msn.wqt.OfflineMsgService;
import com.msn.wqt.WqtConstants;
import com.shareScreen.MyDeviceInfo;
import com.shareScreen.WiFiDirectActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
	protected static final String TAG = "HybridTest";
	private SharePreferenceUtil util;
	private RadioGroup radioGroup;
	private Button menuButton;
	private TextView userName; // 用户昵称
	private TextView userState; // 用户状态
	private ImageButton userImage; // 用户头像
	private final int LISTDIALOG = 1;
	// 页卡内容
	private ViewPager mPager;
	// Tab页面列表
	private List<View> listViews;
	// 当前页卡编号
	@SuppressWarnings("deprecation")
	private LocalActivityManager manager = null;
	private MyPagerAdapter mpAdapter = null;
	private int index;

	// Broadcasr Receiver
	private IntentFilter mIntentFilter;
	private RecommendNoticeReceiver mRecommendNoticeReceiver;
	// private ProgressDialog pd;
	// ReceiveAskOnlineTool receiveAskOnlineTool = new ReceiveAskOnlineTool();
	// private BeginAskOnlineInfo acceptInfo;

	public OfflineMessageDB offlineMessageDB;
	public BufferedUpMessageDB bufferedUpMessageDB;
	public WaitToRecDB waitToRecDB;
	public OnlineThrowTimeDB onlineHDB;
	public OfflineThrowTimeDB offlineThrowTimeDB;

	private ActivityManager exitM = ActivityManager.getInstance();
    private boolean connected;
    private NetworkchangeReceiver myNetReceiver = new NetworkchangeReceiver();
    
	private MsgBroadcastReceiver msgBroadcastReceiver = null;
	private IntentFilter intentFilter = new IntentFilter(WqtConstants.MsgOnRsvWithNoLinkAction);

	// bind Xmpp service
	/*
	 * private ServiceConnection conn=new ServiceConnection() {
	 * 
	 * @Override public void onServiceDisconnected(ComponentName arg0) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onServiceConnected(ComponentName arg0, IBinder
	 * arg1) { // TODO Auto-generated method stub myBinder=(MyBinder) arg1; } };
	 */
	// 绑定mainService
	/*
	 * MainService.TheBinder binder; private ServiceConnection conn = new
	 * ServiceConnection() {
	 * 
	 * @Override public void onServiceConnected(ComponentName name, IBinder
	 * service) { Toast.makeText(MainActivity.this, "Service is connected",
	 * Toast.LENGTH_SHORT).show(); Log.d(TAG, "LLR_sunshine ----- " +
	 * "--Service Connected--"); // 获取Service的onBind方法所返回的TheBinder对象 binder =
	 * (MainService.TheBinder) service; binder.setActivity(MainActivity.this); }
	 * 
	 * // 当该Activity与Service断开连接时回调该方法
	 * 
	 * @Override public void onServiceDisconnected(ComponentName name) {
	 * logPrint("MainService disconnected."); } };
	 */
	// 更新intent传过来的值
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getIntent() != null) {
			index = getIntent().getIntExtra("index", 0);
			mPager.setCurrentItem(index);
			setIntent(null);
		} else {
			if (index < 2) {
				index = index + 1;
				mPager.setCurrentItem(index);
				index = index - 1;
				mPager.setCurrentItem(index);
			} else if (index == 2) {
				index = index - 1;
				mPager.setCurrentItem(index);
				index = index + 1;
				mPager.setCurrentItem(index);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		exitM.addActivity(MainActivity.this);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		initView();
		isConnect();
		initData();
		initControl();
		// sendHybridInfo();

		// register recommend notice receiver
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.android.mobilesocietynetwork.RECEIVE_NEW_NOTICE");
		mRecommendNoticeReceiver = new RecommendNoticeReceiver();
		msgBroadcastReceiver=new MsgBroadcastReceiver(this);
		registerReceiver(mRecommendNoticeReceiver, mIntentFilter);
		registerReceiver(msgBroadcastReceiver, intentFilter);
		Intent intent=new Intent(this,OfflineMsgService.class);
		startService(intent);
		// start recommend notice service
		Intent startIntent = new Intent(MainActivity.this, RecommendNoticeService.class);
		startService(startIntent);
		/*
		 * Intent bindIntent=new Intent(MainActivity.this,XmppService.class);
		 * startService(bindIntent); bindService(bindIntent, conn,
		 * BIND_AUTO_CREATE);
		 */
	}

	/*
	 * private void sendHybridInfo() {
	 * 
	 * // 开启Wifi-Direct探索服务内容功能，使得接收广播信号使能. final Intent serviceIntent = new
	 * Intent(this, MainService.class); bindService(serviceIntent, conn,
	 * Service.BIND_AUTO_CREATE); myApplication.setIsRecieveOn(true);
	 * 
	 * // 是否携带需要分布式投递数据包？ offlineMessageDB = new OfflineMessageDB(this); if
	 * (offlineMessageDB.isNotEmpty(util.getName())) {
	 * myApplication.setIsSendOn(true); logPrint("开启广播"); } else {
	 * Toast.makeText(this, "There is no offline data to broadcast",
	 * Toast.LENGTH_SHORT) .show(); }
	 * 
	 * bufferedUpMessageDB = new BufferedUpMessageDB(this); if
	 * (bufferedUpMessageDB.isNotEmpty(util.getName())){ //是否有缓存的需要联网投递数据包？
	 * //发送离线收到的缓存消息，在线的时候发送 List<BufferMsgEntity> msgList =
	 * bufferedUpMessageDB.getData(util.getName());
	 * //从数据库获取数据，得到的是一个列表，遍历列表，把消息发送出去 while(msgList.iterator().hasNext()){
	 * BufferMsgEntity msgEntity = msgList.iterator().next(); BufferedUpPacket
	 * bufferedUpPacket = new BufferedUpPacket(); //填入数据，从数据库获得
	 * bufferedUpPacket.setThrowid(msgEntity.getThrowid());
	 * bufferedUpPacket.setSource(msgEntity.getSource());
	 * bufferedUpPacket.setDestination(msgEntity.getDestination());
	 * bufferedUpPacket.setValue(msgEntity.getMessage());
	 * bufferedUpPacket.setStarttime(msgEntity.getDate());
	 * bufferedUpPacket.setLife(msgEntity.getLife());
	 * bufferedUpPacket.setPassList(msgEntity.getPassList());
	 * XmppTool.getConnection().sendPacket(bufferedUpPacket); } //将数据库缓存数据清空
	 * bufferedUpMessageDB.clear(util.getName()); }
	 * 
	 * 
	 * //获取当前的网速，并发送给服务器 long lastTotalRxBytes =
	 * TrafficStats.getTotalRxBytes()/1024; long lastTimeStamp =
	 * System.currentTimeMillis(); //1s后再获取收到的数据包的数量 try { Thread.sleep(1000); }
	 * catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } long nowTotalRxBytes =
	 * TrafficStats.getTotalRxBytes()/1024; long nowTimeStamp =
	 * System.currentTimeMillis(); long speed = ((nowTotalRxBytes -
	 * lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
	 * //发送网速的iq包 NetworkSpeedPacket speedPacket = new NetworkSpeedPacket();
	 * speedPacket.setSpeed(speed);
	 * XmppTool.getConnection().sendPacket(speedPacket);
	 * 
	 * //*****************联网时发送noticePacket(目的方的接收响应)，暂未实现
	 * 
	 * }
	 */

	private void initData() {
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		offlineThrowTimeDB = new OfflineThrowTimeDB(this);
		userName.setText(util.getName());
		userImage.setBackgroundResource(Constants.IMGS[util.getImg()]);
	}

	private void initView() {
		mPager = (ViewPager) findViewById(R.id.main_viewpager);
		userName = (TextView) findViewById(R.id.main_uname);
		userState= (TextView) findViewById(R.id.tv_state);
		userImage = (ImageButton) findViewById(R.id.main_uimage);
	    menuButton = (Button) findViewById(R.id.main_menu);
		radioGroup = (RadioGroup) this.findViewById(R.id.main_radiogroup);
		InitViewPager();
	}

	private void InitViewPager() {
		Intent intent = null;
		listViews = new ArrayList<View>();
		mpAdapter = new MyPagerAdapter(listViews);
		intent = new Intent(MainActivity.this, FriendActivity.class);
		listViews.add(getView("A", intent));
		intent = new Intent(MainActivity.this, CommunityActivity.class);
		listViews.add(getView("B", intent));
		//intent = new Intent(MainActivity.this, NoticeActivity.class);
		//listViews.add(getView("C", intent));
		//intent = new Intent(MainActivity.this, MapActivity.class);
		//listViews.add(getView("D", intent));
		mPager.setOffscreenPageLimit(0);
		mPager.setAdapter(mpAdapter);
		mPager.setCurrentItem(0);
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	private void initControl() {
		// 点击用户头像
		userImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(MainActivity.this,UserCenterActivity.class);
				 startActivity(intent);
			}
		});

		// 点击菜单
		menuButton.setOnClickListener(new OnClickListener(){
			
		 @Override
		 public void onClick(View v) { showDialog(LISTDIALOG); 
		 }
		 });
		

		radioGroup.check(R.id.main_radio_friend);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.main_radio_friend:
					index = 0;
					listViews.set(0, getView("A", new Intent(MainActivity.this, FriendActivity.class)));
					mpAdapter.notifyDataSetChanged();
					mPager.setCurrentItem(0);
					break;
				case R.id.main_radio_community:
					index = 1;
					listViews.set(1, getView("B", new Intent(MainActivity.this, CommunityActivity.class)));
					mpAdapter.notifyDataSetChanged();
					mPager.setCurrentItem(1);
					break;
				/*case R.id.main_radio_notice:
					index = 2;
					listViews.set(2, getView("C", new Intent(MainActivity.this, NoticeActivity.class)));
					mpAdapter.notifyDataSetChanged();
					mPager.setCurrentItem(2);
					break;
				case R.id.main_radio_map:
					index = 3;
					listViews.set(3, getView("D", new Intent(MainActivity.this, MapActivity.class)));
					mpAdapter.notifyDataSetChanged();
					mPager.setCurrentItem(3);
					break;*/
				default:
					break;
				}
			}
		});
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	       IntentFilter mFilter = new IntentFilter();  
	       mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);  
	       registerReceiver(myNetReceiver, mFilter);  
	}

	/**
	 * 页卡切换监听，ViewPager改变同样改变TabHost内容
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@SuppressWarnings("deprecation")
		public void onPageSelected(int arg0) {
			manager.dispatchResume();
			switch (arg0) {
			case 0:
				index = 0;
				radioGroup.check(R.id.main_radio_friend);
				listViews.set(0, getView("A", new Intent(MainActivity.this, FriendActivity.class)));
				mpAdapter.notifyDataSetChanged();
				break;
			case 1:
				index = 1;
				radioGroup.check(R.id.main_radio_community);
				listViews.set(1, getView("B", new Intent(MainActivity.this, CommunityActivity.class)));
				mpAdapter.notifyDataSetChanged();
				break;
			/*case 2:
				index = 2;
				radioGroup.check(R.id.main_radio_notice);
				listViews.set(2, getView("C", new Intent(MainActivity.this, NoticeActivity.class)));
				mpAdapter.notifyDataSetChanged();
				break;
			case 3:
				index = 3;
				radioGroup.check(R.id.main_radio_map);
				listViews.set(3, getView("D", new Intent(MainActivity.this, MapActivity.class)));
				mpAdapter.notifyDataSetChanged();
				break;*/
			}
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case LISTDIALOG:
			android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Special Services");
			builder.setItems(new String[] { "Activity service", "Location service", "Screen sharing" },
					new DialogInterface.OnClickListener() {
						// builder.setIcon(R.drawable.dialog);
						public void onClick(DialogInterface dialogInterface, int which) {
							switch (which) { 
							case 0:
							{
								//活动推荐
								if( XmppTool.getConnection().isConnected()){	
								Intent intentNotice = new Intent();
								intentNotice = new Intent(MainActivity.this, NoticeActivity.class);
								startActivity(intentNotice);
								}else{
									Toast.makeText(MainActivity.this, "Service unavailable,please check network", Toast.LENGTH_SHORT).show();
								}
								break; 
								
							}
							case 1:
							{
								//位置服务
								if( XmppTool.getConnection().isConnected()){	
								Intent intentMap = new Intent();
								intentMap = new Intent(MainActivity.this, MapActivity.class);
								startActivity(intentMap);
								}else{
									Toast.makeText(MainActivity.this, "Service unavailable,please check network", Toast.LENGTH_SHORT).show();
								}
								break; 
							}
							case 2:
							{
								//屏幕共享
								AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this)
								.setTitle("提示")
								.setMessage("是否为共享端？")
								.setPositiveButton("是", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										 try {
									        	Process p = Runtime.getRuntime().exec("su");  
									            // 获取输出流  
									            OutputStream outputStream = p.getOutputStream();  
									            DataOutputStream dataOutputStream = new DataOutputStream(  
									                    outputStream);  
									            // 将命令写入  
									            dataOutputStream.writeBytes("chmod 777 /dev/graphics/fb0");  
									            // 提交命令  
									            dataOutputStream.flush();  
									            // 关闭流操作  
									            dataOutputStream.close();  
									            outputStream.close();  
												Log.i("11", "chmod has run");
											} catch (IOException e) {
												// TODO Auto-generated catch block
												AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
												builder.setTitle("Hint").setMessage("Can not get ROOT permission, can not use this function！").setPositiveButton("Close", new DialogInterface.OnClickListener() {
													
													@Override
													public void onClick(DialogInterface dialog, int which) {
														// TODO Auto-generated method stub
														finish();
													}
												});
												e.printStackTrace();
												return;
											}
										MyDeviceInfo info=MyDeviceInfo.getInstance();
										info.setIsHopeBeOwner(true);
										Intent intentShare = new Intent();
										intentShare = new Intent(MainActivity.this, WiFiDirectActivity.class);
										startActivity(intentShare);
									}
								})
								.setNegativeButton("No", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										MyDeviceInfo info=MyDeviceInfo.getInstance();
										info.setIsHopeBeOwner(false);
										Intent intentShare = new Intent();
										intentShare = new Intent(MainActivity.this, WiFiDirectActivity.class);
										startActivity(intentShare);
									}
								})
								;
	builder.create().show();
								break; 
							}
							default:
								break; 
							}
						
						}
					});
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	/*
	 * //-----------------------------------askOnline
	 * begin发送后的接收--------------------------------// //测试扩展IQ包的耗时操作 private
	 * boolean receiveResult() { //接收一个BeginAskOnlineInfo类型的数值
	 * while(receiveAskOnlineTool.getAcceptInfo()==null ){ try {
	 * Thread.sleep(500); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } } acceptInfo =
	 * receiveAskOnlineTool.getAcceptInfo(); return true; } //测试扩展IQ包的线程处理函数
	 * Handler handler=new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) // handler接收到消息后就会执行此方法
	 * { super.handleMessage(msg); switch(msg.what) { case 1: //如果收到数据
	 * pd.dismiss();// 关闭ProgressDialog //获取所需要的数据 acceptInfo.getThrowid();
	 * acceptInfo.getValue(); acceptInfo.getType();
	 * if("2".equals(acceptInfo.getType())) acceptInfo.getForwarder(); break;
	 * default: break; } } }; //-------------------------------askOnline
	 * begin发送后的接收end------------------------------//
	 */
	@Override
	public void getMessage(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在退出的时候需要解除bind，否则运行几次后会报 leaked ServiceConnection 的错误
		// stop recommend notice service
		Intent stopIntent = new Intent(MainActivity.this, RecommendNoticeService.class);
		stopService(stopIntent);

		// unregister recommend notice receiver
		unregisterReceiver(mRecommendNoticeReceiver);
		// unbindService(conn);
		/*NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(1);*/
		if(myNetReceiver!=null){  
            unregisterReceiver(myNetReceiver);  
        }  
		
		unregisterReceiver(msgBroadcastReceiver);
		stopService(new Intent(this,MainService.class));
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		moveTaskToBack(true);
		super.onBackPressed();
	}
	
	/**
	 * 监听是否有新活动的广播
	 * @author qianpf
	 *
	 */
	class RecommendNoticeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification mNotification = new Notification(R.drawable.ic_launcher, "You have a notice",
					System.currentTimeMillis());
			Intent intent = new Intent(arg0, NoticeActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(arg0, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			mNotification.setLatestEventInfo(arg0, "MSN", "Activities around,tap to view", pendingIntent);
			notificationManager.notify(1, mNotification);
			Log.d("RecommendNoticeService","MainActivity receive broadcast from service");
			Toast.makeText(MainActivity.this, "Receive a notice", Toast.LENGTH_SHORT).show();
		}
	}
	
	//11-3
	private void isConnect() {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) this
					.getSystemService(this.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED ) {
						if( XmppTool.getConnection().isConnected()){
							connected = true;
							userState.setText("online");
						}else{
							connected= false;	
							userState.setText("offline");
						}
						}
					}else{
						connected= false;	
						userState.setText("offline");
					}
				}
				else{
					//未连接
				connected= false;	
			}
			}
		catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
	//	connected= false;
	}
	
	private long exitTime = 0;
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            appExit();
            return false;
        }
        else
        return super.onKeyDown(keyCode, event);
    }
 
    private void appExit() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "Click again to quit",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
        	ActivityManager.getInstance().exit();
        }
    }
    
    // 内部类，继承BroadcastReceiver，每次网络状态变化，会执行onReceive
 	class NetworkchangeReceiver extends BroadcastReceiver {

 		@Override
 		public void onReceive(Context context, Intent intent) {
 			//网络状态变化时，进入isConnect判断是网络连接还是断开
 			isConnect();
 		}

 	}
	

}
