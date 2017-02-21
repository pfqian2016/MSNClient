package com.android.mobilesocietynetwork.client;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.tool.XmppTool;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class XMPPConnectionListener implements ConnectionListener {
	private static final String TAG = "ConnectionListener";
	private Context mContext;
	private String mName;
	private String mPassword;
	private SharePreferenceUtil util;
	private ProgressDialog pd;
  //  private Timer tExit; 
	private final Timer tExit = new Timer();
	private TimerTask task;
	
	public XMPPConnectionListener(Context mContext) {
		//super();
		this.mContext = mContext;
		/*mName=util.getName();
		mPassword=util.getPasswd();*/
	}

	@Override
	public void connectionClosed() {
		// TODO Auto-generated method stub
		logPrint("connection closed");
	        // 關閉連接  
		XmppTool.closeConnection();  
	        // 重连服务器  
	        tExit.schedule(task,0, 10000);   
	        task = new TimerTask() {
		    @Override
		    public void run() {
	            mName=util.getName();
	    		mPassword=util.getPasswd();
	            if (mName != null && mPassword != null) {  
	            	logPrint("try login again");
	                // 连接服务器  
	            	try{
	        			XmppTool.getConnection().login(mName, mPassword);  
	        			Presence presence = new Presence( // Presence是Packet的一个子类
	    						Presence.Type.available);
	    				XmppTool.getConnection().sendPacket(presence);
	            	}
	            	catch (XMPPException e)
	    			{
	            		 logPrint("try login again");  
	         			
	    			}
	    			catch (IllegalStateException e)
	    			{
	    				 logPrint("try login again");  
	         
	    			}
	                    
	         		  }
	                }  
	            }; 
	}
	
	 
	        
 

	@Override
	public void connectionClosedOnError(Exception arg0) {
		// TODO Auto-generated method stub
		logPrint("connection closed on error");
		logPrint("connection closed");
        // 重连服务器  
 
        tExit.schedule(task,0, 5000);  
        task = new TimerTask() {
		    @Override
		    public void run() {
	            mName=util.getName();
	    		mPassword=util.getPasswd();
	            if (mName != null && mPassword != null) {  
	            	logPrint("try login again");
	                // 连接服务器  
	            	try{
	        			XmppTool.getConnection().login(mName, mPassword);  
	        			Presence presence = new Presence( // Presence是Packet的一个子类
	    						Presence.Type.available);
	    				XmppTool.getConnection().sendPacket(presence);
	            	}
	            	catch (XMPPException e)
	    			{
	            		 logPrint("try login again");  
	         			
	    			}
	    			catch (IllegalStateException e)
	    			{
	    				 logPrint("try login again");  
	         
	    			}
	                    
	         		  }
	                }  
	            }; 
	}

	@Override
	public void reconnectingIn(int arg0) {
		// TODO Auto-generated method stub
		logPrint("reconnecting in"+arg0);  
        
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		}).start();
		
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		// TODO Auto-generated method stub
		
		logPrint("reconnection failed");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				logPrint("sub thread");
			}
		}).start();
	}

	@Override
	public void reconnectionSuccessful() {
		tExit.cancel();
	
	
	}
	
	public void logPrint(String msg)
	{
		Log.d(TAG, msg);
	}
}
