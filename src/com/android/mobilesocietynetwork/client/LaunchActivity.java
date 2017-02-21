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
	        			//����������Ϸ�������¼��
	        			handler.sendEmptyMessage(0);
	        		}else if(status==0&&connected==1){
	        			//��������Ϸ�����δ��¼
	        			handler.sendEmptyMessage(1);
	        		}else if(status==1&&connected==0){
	        			//����������Ϸ�������¼��
	        			handler.sendEmptyMessage(2);
	        		}else if(status==0&&connected==0){
	        			//��������ӷ�����δ��¼
	        			handler.sendEmptyMessage(3);
	        		}else if(connected==-1){
	        			//����޴�����
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
	 * �������Ӽ�⣬-1δ�����磬0�����磬�޷����ӷ�������1���ӷ�����
	 * @return
	 */
		private int isConnect() {
			// ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
			try {
				ConnectivityManager connectivity = (ConnectivityManager) this
						.getSystemService(this.CONNECTIVITY_SERVICE);
				if (connectivity != null) {
					// ��ȡ�������ӹ���Ķ���
					NetworkInfo info = connectivity.getActiveNetworkInfo();
					if (info != null && info.isConnected()) {
						// �Ѿ�����
						if (info.getState() == NetworkInfo.State.CONNECTED ) {
							con = XmppTool.getConnection();
							if(con.isConnected()){
								return 1;
							}else{
								//�����ӷ�����
								return 0;		
							}
							}
						}else{
							//δ������
							return -1;				
						}
					}
					else{
						//δ������
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
	        public void handleMessage(Message msg)  // handler���յ���Ϣ��ͻ�ִ�д˷��� 
	        {     
	    		super.handleMessage(msg);
	             switch(msg.what)  
	            {  
	            case 0:  //���������¼��
	            	pd.dismiss();
	          	  if(XmppTool.getConnection()!=null&&!XmppTool.getConnection().isAuthenticated()){
        			  try {
						XmppTool.getConnection().login(util.getName(), util.getPasswd());
						Presence presence = new Presence( // Presence��Packet��һ������
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
	            case 1://��������޵�¼
	           pd.dismiss(); 
				Intent intent2 = new Intent(LaunchActivity.this, LoginActivity.class);
				startActivity(intent2);
				finish();		
	            break;
	            case 2:  //���������¼��
	            	pd.dismiss();
	            	Intent intent3 = new Intent(LaunchActivity.this, MainActivity.class);
	            	intent3.putExtra("status", 0);
					startActivity(intent3);
					finish();
	                break;  
	            case 3:  //��������޵�¼
		         pd.dismiss();// �ر�ProgressDialog 
					Intent intent4 = new Intent(LaunchActivity.this, LoginActivity.class);
					startActivity(intent4);
		         Toast.makeText(LaunchActivity.this,"Can not connect to server", Toast.LENGTH_SHORT).show();
		     	finish();
		         break;  
	            case 4:  //����޴�����
		         pd.dismiss();// �ر�ProgressDialog 
		         hint.setText("Hint:please open network and try again");
				hint.setVisibility(View.VISIBLE);
	              break;  
	            default:  
	                break;        
	            }  
	        }  
	    };

}
