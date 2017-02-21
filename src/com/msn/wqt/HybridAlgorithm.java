package com.msn.wqt;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import com.android.mobilesocietynetwork.client.database.FriendDB;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.R.bool;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/*
 * 混合模式基本策略：
 * 1.对于客户端来说，判断自己是否在线。是，发给服务器；否，offline发送；
 * 2.对于服务器收到的message包。取出里面的dst。判断dst是否在线。在线，直接发送；不在线，发给dst的所有除source外的在线好友；
 * 
 * 3.对于客户端，在线收到一个message包，判断是否给自己的；是给自己的，消费掉；不是给自己的，说明是服务器要自己作为中继点，把该消息offline发送掉；
 * 4.对于客户端，离线收到一个message包，判断是否给自己的；是给自己的，消费掉；不是给自己的，说明是其他节点要自己作为中继点，把该消息offline发送掉；
 */
public class HybridAlgorithm {	
	
	/*
	 * 判断自己与对方是否同时在线
	 */
	/*
	public static boolean isSendOnline(){
		return isLocalOnline()&&isRemoteUserOnline();
	}
	*/
		
	
	/*
	 * 判断自己是否在线
	 */
	public static boolean isLocalOnline(Context context, XMPPConnection con, String userName){
		//to-do
		
		boolean connected=false;
		Roster roster;
		Presence presence;
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						//connected = true;
						con = XmppTool.getConnection();
						roster = con.getRoster();
						presence = roster.getPresence(userName + "@" + Constants.SERVER_NAME);
						if (presence.isAvailable() == true) {
							// 如果在线，
							connected = true;
						}
					} else {
						connected = false;
					}
				} else {
					// 未连接
					connected = false;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.v("HybridAlgorithmImp", e.toString());
		}
		// connected= false;

		return connected;
		//return true;		
	}
	
	/*
	 * 判断自己在线与否
	 *
	 */
	public static boolean isMeOnline(){
		boolean connected=false;
		XMPPConnection connection=XmppTool.getConnection();
		if(connection==null) return connected;
		else{
			connected=connection.isConnected();
			return connected;
		}		
	}
	
	/*
	 * 判断对方是否在线
	 */
	public static boolean isRemoteUserOnline(Context context,String myUserName,String remoteUserName){
		//to-do
		FriendDB friendDB=new FriendDB(context);
		User user=friendDB.searchfriendbyname(myUserName, remoteUserName);
		if(remoteUserName.equals(user.getName())) 
			return true;
		else 
			return false;
	}
	/*
	 * 判断对方是否在线
	 */
	public static boolean isRemoteUserOnline(User remoteUser){
		if(remoteUser.getIsOnline()==1) return true;
		else return false;
	}

}
