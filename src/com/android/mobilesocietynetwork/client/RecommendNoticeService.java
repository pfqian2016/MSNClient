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
		
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
	    mLocationClient.registerLocationListener(myListener );    //ע���������
	    LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		mLocationClient.setLocOption(option);
		//��ȡ��γ��
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
				// ���������߳�
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (receiveRecommend())// �յ��µĻ
							{
								handler.sendMessage(handler.obtainMessage(1, noticeList)); // ִ�к�ʱ�ķ���֮��������handler
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
		// ����һ��List<noticeInfo>���͵Ļ�б�
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
		public void handleMessage(Message msg) // handler���յ���Ϣ��ͻ�ִ�д˷���
		{
			super.handleMessage(msg);
			switch (msg.what) {
			case 1: // ������Ƽ��
					// Receive Notice Successfully
				
				 /* NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE); 
				  Notification mNotification=new Notification(R.drawable.ic_launcher,"����һ���֪ͨ",System.currentTimeMillis()); 
				  Intent intent=new Intent(MyApplication.getInstance(),NoticeActivity.class);
				  PendingIntent pendingIntent=PendingIntent.getActivity(MyApplication.getInstance(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				  mNotification.setLatestEventInfo(MyApplication.getInstance(), "MSN", "�����л,����鿴", pendingIntent);
				  notificationManager.notify(1,mNotification);*/
				 
				Intent intent1 = new Intent("com.android.mobilesocietynetwork.RECEIVE_NEW_NOTICE");
				sendBroadcast(intent1);
				logPrint("send broadcast");
				break;
			case 2: // ����ǽ���ɹ�

				break;
			case 3: // ����ǽ������ʧ��

				break;
			case 4: // ����Ǹ��½���
				Intent intent2 = new Intent("com.android.mobilesocietynetwork.RECEIVE_NEW_NOTICE");
				sendBroadcast(intent2);
				logPrint("send broadcast");
				break;
			case 5: // ����Ƕ�ʱ����

				break;
			case 0: // ���û������

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
