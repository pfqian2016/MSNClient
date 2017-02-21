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
	// ���������ϣ����ڱ�������Ӧ�õ�Activity
	private List<Activity> activityList = new LinkedList<Activity>();
//	private Client client;
	private boolean isClientStart;
	private boolean isReceiveOn;
	private boolean isSendOn;
	// ���豸��Wifi-Direct�豸��
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
/*		logPrint("ip�Ͷ˿���" + util.getIp() + " " + util.getPort());
		client = new Client(util.getIp(), util.getPort());// �������ļ��ж�ip�͵�ַ
*/		super.onCreate();
	}

	/*
	 * ���޷���ȡContext��ʱ����ô˷�������ȡMyApplication���Ӷ�Ҳʵ���˻�ȡContext���� ����LLR_sunshine
	 */
	public static MyApplication getInstance()
	{
		return instance;
	}

	// ����Acitivity�������С� ����LLR_sunshine
	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}

	public void exit()
	{
		// ���� ��������ɱ������Activity�� ����LLR_sunshine
		for (Activity activity : activityList)
		{
			if (!activity.isFinishing())
			{
				activity.finish();
			}
		}
		/*
		 * ɱ�����Ӧ�ó���Ľ��̣��ͷ��ڴ档 ����LLR_sunshine
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

	// ��δ����
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
