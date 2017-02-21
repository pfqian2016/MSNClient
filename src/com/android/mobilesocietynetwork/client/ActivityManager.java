package com.android.mobilesocietynetwork.client;

import java.util.LinkedList;
import java.util.List;

import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.app.Activity;
import android.app.Application;
/**
 * 
 * Activity�������ڳ����˳�ʱ��������������
 * 
 * */
public class ActivityManager extends Application
{
	// ����������
	private static List<Activity> activityList = new LinkedList<Activity>();
	
	// �õ���ģʽ����֤�����ActivityManager ������Ӧ����ֻ��һ��
	private static  ActivityManager instance;

	private ActivityManager()
	{

	}

	public static  ActivityManager getInstance()
	{
		if (instance == null)
		{
			instance = new ActivityManager();
		}
		return instance;
	}

	// �������У����Activity
	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}

	// ��������Ӧ�ó���
	public  void exit()
	{

		// ���� ��������ɱ������Activity
		for (Activity activity : activityList)
		{
			if (!activity.isFinishing())
			{
				activity.finish();
			}
		}
		// ɱ�������Ӧ�ó���Ľ��̣��ͷ� �ڴ�
		int id = android.os.Process.myPid();
		if (id != 0)
		{
			android.os.Process.killProcess(id);
		}
		System.exit(0);
	}
	
	//// �˳���¼
	public  void logout()
	{
/*		// ���� ��������ɱ������Activity
		for (Activity activity : activityList)
		{
			if (!activity.isFinishing())
			{
				activity.finish();
			}
		}*/
		//��ʼ������
		XmppTool.closeConnection();
		
	}
}


