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
				// ������ǿգ�˵������Ϣ�㲥
				getMessage(msg);// ���յ�����Ϣ���ݸ�����
			}
			else
			{
				// ����ǿ���Ϣ��˵���ǹر�Ӧ�õĹ㲥
				logPrint("�յ��ر�Ӧ�õĹ㲥��");
			}
		}
	};

	/**
	 * ���󷽷����������ദ����Ϣ��ÿ�����ࣨActivity����Ҫ���ô˷��������Packet��
	 * 
	 * @author LLR_sunshine
	 * 
	 * @param msg
	 *            ���ݸ��������Ϣ����
	 * 
	 */
	public abstract void getMessage(String msg);

	/**
	 * ����ֱ�ӵ�����������ر�Ӧ��
	 */
	public void close()
	{
		myApplication.exit();
	}

	@Override
	public void onStart()
	{
		// ��start������ע��㲥������. ����LLR_sunshine
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION);
		// ע�������Ϣ�㲥. ����LLR_sunshine
		registerReceiver(MsgReceiver, intentFilter);
	}

	@Override
	protected void onStop()
	{
		// ��stop������ע���㲥������. ����LLR_sunshine
		super.onStop();
		// ע��������Ϣ�㲥. ����LLR_sunshine
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

			// ���˰�������
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

				moveTaskToBack(true);

			}

			return super.onKeyDown(keyCode, event);
		}
	 
    private void appExit() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
        	ActivityManager.getInstance().exit();
        }
    }

}
