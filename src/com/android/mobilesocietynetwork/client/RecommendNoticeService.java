package com.android.mobilesocietynetwork.client;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.android.mobilesocietynetwork.client.info.NoticeInfo;
import com.android.mobilesocietynetwork.client.notice.NoticeActivity;
import com.android.mobilesocietynetwork.client.notice.NoticeActivity.NoticeListviewAdapter;
import com.android.mobilesocietynetwork.client.packet.RecommendNoticePacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class RecommendNoticeService extends Service {

	private static final String TAG = "RecommendNoticeService";
	private Timer mTimer;
	private MyTimerTask myTimerTask;
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private double mLongtitude;
	private double mLatitude;
	private int count;
	private int mNoticeCount = 0;
	private ArrayList<NoticeInfo> noticeList = new ArrayList<NoticeInfo>();

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		super.onCreate();
		logPrint("oncreate");
		initBaiduMap();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				logPrint("onstartcommand");
				mTimer = new Timer();
				mTimer.schedule(new MyTimerTask(), 0, 300000);

			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		logPrint("ondestroy");
		mTimer.cancel();
		super.onDestroy();

	}

	public void initBaiduMap() {
		
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener(myListener );    //注册监听函数
	    LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		mLocationClient.setLocOption(option);
		//获取经纬度
		mLocationClient.start();
		mLocationClient.stop();
	}

	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();
		}
	}

	public class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (isNetworkAvailable()) {
				// TODO Send recommend notice packet
		
				RecommendNoticePacket recommendNoticeIQ = new RecommendNoticePacket();
				ReceiveNoticeIQTool.init();
				ReceiveNoticeIQTool.resetIsReceive();
				recommendNoticeIQ.setLongitude(mLongtitude + "");
				recommendNoticeIQ.setLatitude(mLatitude + "");
				recommendNoticeIQ.setDistance(1 + "");
				XmppTool.getConnection().sendPacket(recommendNoticeIQ);
				// 开启接收线程
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (receiveRecommend())// 收到新的活动
							{
								handler.sendMessage(handler.obtainMessage(1, noticeList)); // 执行耗时的方法之后发送消给handler
								logPrint("receive new notice");
							}
						else
							handler.sendMessage(handler.obtainMessage(0, "No data"));
					}
				}).start();
				logPrint("sending packet");
			} else {
				logPrint("network error");
			}
		}

	}

	private boolean receiveRecommend() {
		// 接收一个List<noticeInfo>类型的活动列表
		count = 0;
		// while(noticeList.size()==0&&count<20){
		while ((!ReceiveNoticeIQTool.getIsReceive()) && count < 60) {
			try {
				Thread.sleep(300);
				count++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		noticeList = ReceiveNoticeIQTool.getNoticeList();
		ReceiveNoticeIQTool.resetIsReceive();
		return receiveNewNotice(noticeList.size());
		
		//count = 0;
	  	//	while(noticeList.size()==0&&count<20){
	/*  		while((!ReceiveNoticeIQTool.getIsReceive())&&count<60){
	  		try {
	  			Thread.sleep(300);
	  			count++;
	  		} catch (InterruptedException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		}
	  		noticeList = ReceiveNoticeIQTool.getNoticeList();
	  		ReceiveNoticeIQTool.resetIsReceive();
	  		logPrint(noticeList.size()+"");
	  		return noticeList.size()!=0;*/
		 
	}


	private boolean receiveNewNotice(int listCount) {
		if (listCount > mNoticeCount) {
			mNoticeCount = listCount;
			return true;
		} else {
			return false;
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) // handler接收到消息后就会执行此方法
		{
			super.handleMessage(msg);
			switch (msg.what) {
			case 1: // 如果是推荐活动
					// Receive Notice Successfully
				
				 /* NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE); 
				  Notification mNotification=new Notification(R.drawable.ic_launcher,"你有一个活动通知",System.currentTimeMillis()); 
				  Intent intent=new Intent(MyApplication.getInstance(),NoticeActivity.class);
				  PendingIntent pendingIntent=PendingIntent.getActivity(MyApplication.getInstance(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				  mNotification.setLatestEventInfo(MyApplication.getInstance(), "MSN", "附近有活动,点击查看", pendingIntent);
				  notificationManager.notify(1,mNotification);*/
				 
				Intent intent1 = new Intent("com.android.mobilesocietynetwork.RECEIVE_NEW_NOTICE");
				sendBroadcast(intent1);
				logPrint("send broadcast");
				break;
			case 2: // 如果是结果成功

				break;
			case 3: // 如果是结果操作失败

				break;
			case 4: // 如果是更新界面
				Intent intent2 = new Intent("com.android.mobilesocietynetwork.RECEIVE_NEW_NOTICE");
				sendBroadcast(intent2);
				logPrint("send broadcast");
				break;
			case 5: // 如果是定时更新

				break;
			case 0: // 如果没有数据

				break;
			default:
				break;
			}
		}
	};

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	public void logPrint(String log) {
		Log.d(TAG, log);
	}
}
