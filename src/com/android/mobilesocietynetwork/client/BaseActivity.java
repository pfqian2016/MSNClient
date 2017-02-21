package com.android.mobilesocietynetwork.client;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.mobilesocietynetwork.client.util.Constants;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;


public abstract class BaseActivity extends Activity {
	public MyApplication myApplication;
	private long exitTime = 0;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		myApplication = (MyApplication) this.getApplicationContext();
		myApplication.addActivity(this);
	}

	private static int ClientOrServer;

	// private MyApplication myApplication;

	public static int getClientOrServer()
	{
		return ClientOrServer;
	}

	public static void setClientOrServer(int clientOrServer)
	{
		ClientOrServer = clientOrServer;
	}

	private BroadcastReceiver MsgReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String msg = intent.getStringExtra(Constants.MSGKEY);
			if (msg != null)
			{
				// 如果不是空，说明是消息广播
				getMessage(msg);// 把收到的消息传递给子类
			}
			else
			{
				// 如果是空消息，说明是关闭应用的广播
				logPrint("收到关闭应用的广播。");
			}
		}
	};

	/**
	 * 抽象方法，用于子类处理消息。每个子类（Activity）需要调用此方法来获得Packet。
	 * 
	 * @author LLR_sunshine
	 * 
	 * @param msg
	 *            传递给子类的消息对象
	 * 
	 */
	public abstract void getMessage(String msg);

	/**
	 * 子类直接调用这个方法关闭应用
	 */
	public void close()
	{
		myApplication.exit();
	}

	@Override
	public void onStart()
	{
		// 在start方法中注册广播接收者. ——LLR_sunshine
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION);
		// 注册接受消息广播. ——LLR_sunshine
		registerReceiver(MsgReceiver, intentFilter);
	}

	@Override
	protected void onStop()
	{
		// 在stop方法中注销广播接收者. ——LLR_sunshine
		super.onStop();
		// 注销接受消息广播. ——LLR_sunshine
		unregisterReceiver(MsgReceiver);
	}

	public void toastShow(String showContent, boolean isLong)
	{
		if (isLong)
		{
			Toast.makeText(this, showContent, Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(this, showContent, Toast.LENGTH_SHORT).show();
		}
	}

	public final void logPrint(String log)
	{
		Constants.logPrint(log);
	}
	
/*	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            appExit();
            return false;
        }
        else
        return super.onKeyDown(keyCode, event);
    }
 */
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {

			// 过滤按键动作
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

				moveTaskToBack(true);

			}

			return super.onKeyDown(keyCode, event);
		}
	 
    private void appExit() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
        	ActivityManager.getInstance().exit();
        }
    }

}
