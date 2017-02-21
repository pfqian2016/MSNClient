package com.android.mobilesocietynetwork.client;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class LaunchActivity extends Activity {
	private TextView hint;
	private SharePreferenceUtil util;
	private XMPPConnection con;
 //  private boolean connected;
    private ProgressDialog pd;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_launch);
		hint = (TextView) findViewById(R.id.tvHint);
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		final int status = util.getStatus();
		pd = ProgressDialog.show(LaunchActivity.this, "Hint", "Entering,please wait");  
	    new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	        		int connected = isConnect();
	        		if(status==1&&connected==1){
	        			//如果有连接上服务器登录过
	        			handler.sendEmptyMessage(0);
	        		}else if(status==0&&connected==1){
	        			//如果连接上服务器未登录
	        			handler.sendEmptyMessage(1);
	        		}else if(status==1&&connected==0){
	        			//如果无连接上服务器登录过
	        			handler.sendEmptyMessage(2);
	        		}else if(status==0&&connected==0){
	        			//如果无连接服务器未登录
	        			handler.sendEmptyMessage(3);
	        		}else if(connected==-1){
	        			//如果无打开网络
	        			handler.sendEmptyMessage(4);
	        		}
	            }  	  
	        }).start();  				

		
		/*	final int i = status;
		   new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (i == 1) {
					Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
					startActivity(intent);
				} else if(i==0){
					Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
					startActivity(intent);
				}
				Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();			
			}
		}, 2000);*/
	}
	
	/**
	 * 网络连接检测，-1未打开网络，0打开网络，无法连接服务器，1连接服务器
	 * @return
	 */
		private int isConnect() {
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
							con = XmppTool.getConnection();
							if(con.isConnected()){
								return 1;
							}else{
								//无连接服务器
								return 0;		
							}
							}
						}else{
							//未打开网络
							return -1;				
						}
					}
					else{
						//未打开网络
					return -1;	
				}
				}
			catch (Exception e) {
				// TODO: handle exception
				Log.v("error", e.toString());
			}
		//	connected= false;
			return -1;
		}
		
	    Handler handler=new Handler()  
	    {   
	    	@Override  
	        public void handleMessage(Message msg)  // handler接收到消息后就会执行此方法 
	        {     
	    		super.handleMessage(msg);
	             switch(msg.what)  
	            {  
	            case 0:  //如果有网登录过
	            	pd.dismiss();
	          	  if(XmppTool.getConnection()!=null&&!XmppTool.getConnection().isAuthenticated()){
        			  try {
						XmppTool.getConnection().login(util.getName(), util.getPasswd());
						Presence presence = new Presence( // Presence是Packet的一个子类
								Presence.Type.available);
						XmppTool.getConnection().sendPacket(presence);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		  }
					
	            	Intent intent1 = new Intent(LaunchActivity.this, MainActivity.class);
	            	intent1.putExtra("status", 1);
					startActivity(intent1);
					finish();
	                break;  
	            case 1://如果有网无登录
	           pd.dismiss(); 
				Intent intent2 = new Intent(LaunchActivity.this, LoginActivity.class);
				startActivity(intent2);
				finish();		
	            break;
	            case 2:  //如果无网登录过
	            	pd.dismiss();
	            	Intent intent3 = new Intent(LaunchActivity.this, MainActivity.class);
	            	intent3.putExtra("status", 0);
					startActivity(intent3);
					finish();
	                break;  
	            case 3:  //如果无网无登录
		         pd.dismiss();// 关闭ProgressDialog 
					Intent intent4 = new Intent(LaunchActivity.this, LoginActivity.class);
					startActivity(intent4);
		         Toast.makeText(LaunchActivity.this,"Can not connect to server", Toast.LENGTH_SHORT).show();
		     	finish();
		         break;  
	            case 4:  //如果无打开网络
		         pd.dismiss();// 关闭ProgressDialog 
		         hint.setText("Hint:please open network and try again");
				hint.setVisibility(View.VISIBLE);
	              break;  
	            default:  
	                break;        
	            }  
	        }  
	    };

}
