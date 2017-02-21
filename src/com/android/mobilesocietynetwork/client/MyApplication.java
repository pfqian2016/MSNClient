package com.android.mobilesocietynetwork.client;

import java.util.LinkedList;
import java.util.List;

import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.net.wifi.p2p.WifiP2pDevice;


public class MyApplication extends Application{
	private static MyApplication instance;
	// 建立链表集合，用于保存整个应用的Activity
	private List<Activity> activityList = new LinkedList<Activity>();
//	private Client client;
	private boolean isClientStart;
	private boolean isReceiveOn;
	private boolean isSendOn;
	// 本设备的Wifi-Direct设备名
	private WifiP2pDevice wifiP2pDevice;

	private NotificationManager mNotificationManager;
	private int newMsgNum = 0;
	// ******************************
	private boolean directTest;

	// *******************************

	public boolean isDirectTest()
	{
		return directTest;
	}

	public void setDirectTest(boolean directTest)
	{
		this.directTest = directTest;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		SharePreferenceUtil util = new SharePreferenceUtil(this, Constants.SAVE_USER);
/*		logPrint("ip和端口是" + util.getIp() + " " + util.getPort());
		client = new Client(util.getIp(), util.getPort());// 从配置文件中读ip和地址
*/		super.onCreate();
	}

	/*
	 * 当无法获取Context的时候调用此方法来获取MyApplication（从而也实现了获取Context）。 ——LLR_sunshine
	 */
	public static MyApplication getInstance()
	{
		return instance;
	}

	// 保存Acitivity到链表中。 ——LLR_sunshine
	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}

	public void exit()
	{
		// 遍历 链表，依次杀掉各个Activity。 ——LLR_sunshine
		for (Activity activity : activityList)
		{
			if (!activity.isFinishing())
			{
				activity.finish();
			}
		}
		/*
		 * 杀掉这个应用程序的进程，释放内存。 ——LLR_sunshine
		 */
		int id = android.os.Process.myPid();
		if (id != 0)
		{
			android.os.Process.killProcess(id);
		}
	}

	public WifiP2pDevice getWifiP2pDevice()
	{
		return wifiP2pDevice;
	}

	public void setWifiP2pDevice(WifiP2pDevice wifiP2pDevice)
	{
		this.wifiP2pDevice = wifiP2pDevice;
	}

	/*public Client getClient()
	{
		return client;
	}
*/
	public boolean getIsClientStart()
	{
		return isClientStart;
	}

	public void setClientStart(boolean isClientStart)
	{
		this.isClientStart = isClientStart;
	}

	public void setIsRecieveOn(boolean b)
	{
		isReceiveOn = b;
	}

	public boolean getIsRecieveOn()
	{
		return isReceiveOn;
	}

	public void setIsSendOn(boolean b)
	{
		isSendOn = b;
	}

	public boolean getIsSendOn()
	{
		return isSendOn;
	}

	public void logPrint(String log)
	{
		Constants.logPrint(log);
	}

	// 尚未启用
	public NotificationManager getmNotificationManager()
	{
		return mNotificationManager;
	}

	public void setmNotificationManager(NotificationManager mNotificationManager)
	{
		this.mNotificationManager = mNotificationManager;
	}

	public int getNewMsgNum()
	{
		return newMsgNum;
	}

	public void setNewMsgNum(int newMsgNum)
	{
		this.newMsgNum = newMsgNum;
	}
}
