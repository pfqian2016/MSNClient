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
 * Activity管理，用于程序退出时，结束整个程序。
 * 
 * */
public class ActivityManager extends Application
{
	// 建立链表集合
	private static List<Activity> activityList = new LinkedList<Activity>();
	
	// 用单例模式，保证，这个ActivityManager 在整个应用中只有一个
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

	// 向链表中，添加Activity
	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}

	// 结束整个应用程序
	public  void exit()
	{

		// 遍历 链表，依次杀掉各个Activity
		for (Activity activity : activityList)
		{
			if (!activity.isFinishing())
			{
				activity.finish();
			}
		}
		// 杀掉，这个应用程序的进程，释放 内存
		int id = android.os.Process.myPid();
		if (id != 0)
		{
			android.os.Process.killProcess(id);
		}
		System.exit(0);
	}
	
	//// 退出登录
	public  void logout()
	{
/*		// 遍历 链表，依次杀掉各个Activity
		for (Activity activity : activityList)
		{
			if (!activity.isFinishing())
			{
				activity.finish();
			}
		}*/
		//初始化连接
		XmppTool.closeConnection();
		
	}
}


