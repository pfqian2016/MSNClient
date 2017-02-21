package com.android.mobilesocietynetwork.client;


import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import com.android.mobilesocietynetwork.client.chat.DialogActivity;
import com.android.mobilesocietynetwork.client.chat.FriendActivity;
import com.android.mobilesocietynetwork.client.chat.MapActivity;
import com.android.mobilesocietynetwork.client.database.FriendDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.notice.CreateNoticeActivity;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.example.android.wifidirect.discovery.WiFiServiceDiscoveryActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.*;
import android.widget.*;

/*
 * 
 * ��½����
 * 
 * */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class LoginActivity extends BaseActivity
{
	private EditText uid;
	private EditText pwd;
	private Button log;
	private Button offline_log;
	private Button reg;
	private Button set;
	private SharePreferenceUtil util;
	private String mNametext;
	private String pwdtext;
    private ProgressDialog pd;  

    
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// *****2.3�������簲ȫ����Ͱ汾�����ݣ�������´���****
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
		// ******************************************
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initview();
		initData();		
	}

	private void initData() {
		// TODO Auto-generated method stub
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
	}

	private void initview()
	{
		uid = (EditText) findViewById(R.id.editusername);
		pwd = (EditText) findViewById(R.id.editpassword);
		log = (Button) findViewById(R.id.login);
		//offline_log=(Button) findViewById(R.id.offline_login);
		reg = (Button) findViewById(R.id.register);
		set = (Button) findViewById(R.id.set);
		log.setOnClickListener(new LoginListener());
		//offline_log.setOnClickListener(new LoginListener());
		reg.setOnClickListener(new RegisterListener());
		set.setOnClickListener(new SetListener());
	}

	private class LoginListener implements OnClickListener
	{
		public void onClick(View v)
		{
			switch (v.getId()) {
			//online ģʽ�µĵ�¼
			case R.id.login:
				 mNametext = uid.getText().toString();
				 pwdtext = pwd.getText().toString();
	/*			if (!isNetworkAvailable()){//���ߵ�¼
	  			Intent intent2 = new Intent();
					intent2.setClass(LoginActivity.this, FriendActivity.class);		
					intent2.putExtra("mName", mNametext);
					startActivity(intent2);
					finish();
	           	Toast.makeText(LoginActivity.this,"������������", Toast.LENGTH_SHORT).show();
		         }
				else*/
				if (mNametext.length() == 0 || pwdtext.length() == 0)
				{
					toastshow("Account or password invalid");
				}
				else if (pwdtext.equals("666") && mNametext.equals("test"))
				{
					// ����
					toastshow("Lo");
					util.setName("test");
					util.setImg(1);
					util.setTel("13243535571");
					util.setSex("men");
					util.setStatus(1);
					util.setEmail("492767618@qq.com");
				    Intent i = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(i);
					finish();
				}

				else
				{
					util.setName(mNametext);
					util.setPasswd(pwdtext);
					util.setImg(1);
					util.setTel("1001010086");
					util.setSex("men");
				//	util.setStatus(1);
					util.setEmail("baidu@www.baidu.com");
				    pd = ProgressDialog.show(LoginActivity.this, "Hint", "Connecting");  
				        new Thread(new Runnable() {  
				            @Override  
				            public void run() {  
				            	int result = receiveResult(mNametext,pwdtext);
				            		handler.sendEmptyMessage(result);
				            }  	  
				        }).start();  				
				}
				break;
			//offlineģʽ�µĵ�¼
			/*case R.id.offline_login:
				
				//TODO ������ߵ�¼��ťʵ���߼�
				util.setName(mNametext);
				util.setPasswd(pwdtext);
				util.setImg(1);
				util.setTel("1001010086");
				util.setSex("men");
			//	util.setStatus(1);
				util.setEmail("baidu@www.baidu.com");
				Intent i=new Intent(LoginActivity.this,MainActivity.class);
				startActivity(i);
				break;*/
			}
			
		}
	}

	class RegisterListener implements OnClickListener
	{
		public void onClick(View v)
		{
			Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
			startActivity(intent);
			
		}
	}
	
	class SetListener implements OnClickListener
	{
		public void onClick(View v)
		{
			Intent intent =new Intent(LoginActivity.this,NetworkSetActivity.class);
			startActivity(intent);
		}
	}
	

	private void toastshow(String s)
	{
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 1, 1, "����");
		menu.add(0, 2, 2, "�˳�");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		if (item.getItemId() == 2)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isNetworkAvailable()
	{
		ConnectivityManager mgr = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null)
		{
			for (int i = 0; i < info.length; i++)
			{
				if (info[i].getState() == NetworkInfo.State.CONNECTED)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	 private int receiveResult(String mNametext,String pwdtext) {  
		
     //���ߵ�¼
        	  try
			{
				// �������Ӳ���¼
        		  if(XmppTool.getConnection()!=null){
        			  XmppTool.getConnection().login(mNametext, pwdtext);
				Presence presence = new Presence( // Presence��Packet��һ������
						Presence.Type.available);
				XmppTool.getConnection().sendPacket(presence);
				return 1;		
        		  }else{
        			  return 3;
        		  }
				
			}
			catch (XMPPException e)
			{
				XmppTool.closeConnection();
				//toastshow("��¼ʧ�ܣ������˻���������");
				return 2;
			}
			catch (IllegalStateException e)
			{
			//	toastshow("����ʧ��");
				return 3;
			}
			
     }  
	 
	    Handler handler=new Handler()  
	    {   
	    	@Override  
	        public void handleMessage(Message msg)  // handler���յ���Ϣ��ͻ�ִ�д˷��� 
	        {     
	    		super.handleMessage(msg);
	             switch(msg.what)  
	            {  
	            case 1:  //�����¼�ɹ�
	            	pd.dismiss();// �ر�ProgressDialog 
					util.setStatus(1);
	  				Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);		
					intent.putExtra("mName", mNametext);
					startActivity(intent);
					finish();
	                break;  
	            case 2:  //����û����������
	          	pd.dismiss();// �ر�ProgressDialog 
	           	Toast.makeText(LoginActivity.this,"Fail to login,please check account and password", Toast.LENGTH_SHORT).show();
	            break; 
	            case 3:  //����������Ӵ���
		          	  pd.dismiss();// �ر�ProgressDialog 
		           	Toast.makeText(LoginActivity.this,"Cannot connect to server", Toast.LENGTH_SHORT).show();
	              break;  

	            default:  
	                break;        
	            }  
	        }  
	    };

		@Override
		public void getMessage(String msg) {
			// TODO Auto-generated method stub
			
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
	    
}
