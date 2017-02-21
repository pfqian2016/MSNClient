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
 * ���ģʽ�������ԣ�
 * 1.���ڿͻ�����˵���ж��Լ��Ƿ����ߡ��ǣ���������������offline���ͣ�
 * 2.���ڷ������յ���message����ȡ�������dst���ж�dst�Ƿ����ߡ����ߣ�ֱ�ӷ��ͣ������ߣ�����dst�����г�source������ߺ��ѣ�
 * 
 * 3.���ڿͻ��ˣ������յ�һ��message�����ж��Ƿ���Լ��ģ��Ǹ��Լ��ģ����ѵ������Ǹ��Լ��ģ�˵���Ƿ�����Ҫ�Լ���Ϊ�м̵㣬�Ѹ���Ϣoffline���͵���
 * 4.���ڿͻ��ˣ������յ�һ��message�����ж��Ƿ���Լ��ģ��Ǹ��Լ��ģ����ѵ������Ǹ��Լ��ģ�˵���������ڵ�Ҫ�Լ���Ϊ�м̵㣬�Ѹ���Ϣoffline���͵���
 */
public class HybridAlgorithm {	
	
	/*
	 * �ж��Լ���Է��Ƿ�ͬʱ����
	 */
	/*
	public static boolean isSendOnline(){
		return isLocalOnline()&&isRemoteUserOnline();
	}
	*/
		
	
	/*
	 * �ж��Լ��Ƿ�����
	 */
	public static boolean isLocalOnline(Context context, XMPPConnection con, String userName){
		//to-do
		
		boolean connected=false;
		Roster roster;
		Presence presence;
		// ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// ��ȡ�������ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// �Ѿ�����
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						//connected = true;
						con = XmppTool.getConnection();
						roster = con.getRoster();
						presence = roster.getPresence(userName + "@" + Constants.SERVER_NAME);
						if (presence.isAvailable() == true) {
							// ������ߣ�
							connected = true;
						}
					} else {
						connected = false;
					}
				} else {
					// δ����
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
	 * �ж��Լ��������
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
	 * �ж϶Է��Ƿ�����
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
	 * �ж϶Է��Ƿ�����
	 */
	public static boolean isRemoteUserOnline(User remoteUser){
		if(remoteUser.getIsOnline()==1) return true;
		else return false;
	}

}
