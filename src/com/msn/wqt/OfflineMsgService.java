package com.msn.wqt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.packet.hybrid.DirectScfPacket;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.baidu.mapapi.utils.d;
import com.baidu.platform.comapi.map.r;
import com.msn.wqt.OfflineAlgorithm;

import com.msn.wqt.WqtConstants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path.FillType;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class OfflineMsgService extends Service {
	private SharePreferenceUtil util;
	// private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager;
	private Channel channel;
	private WifiP2pDnsSdServiceRequest serviceRequest;
	private WifiP2pDnsSdServiceInfo service;
	private boolean isFirstRun;
	Map<String, String> record;
	private static String SERVICE_INSTANCE = "_msnTest";
	private static String SERVICE_REG_TYPE = "_prsence._tcp";
	private DnsSdTxtRecordListener txtListener;
	private DnsSdServiceResponseListener responseListener;
	private static LinkedList<String> messageList = new LinkedList<String>();
	private static LinkedList<String> receivedMsgList = new LinkedList<String>();
	private TimerTask sendTimerTask;
	private TimerTask recvTimerTask;
	private Timer timer;
	private OfflineMsgDB offlineMessagedb;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		timer = new Timer();
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);  
		isFirstRun = false;
		offlineMessagedb = new OfflineMsgDB(this);
		// intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		// intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		// intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		// intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		record = new HashMap<String, String>();

		service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		txtListener = new DnsSdTxtRecordListener() {

			@Override
			public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap,
					WifiP2pDevice srcDevice) {
				// TODO Auto-generated method stub

				Log.d("runOnTxtRecord", "isRunning");
				Log.d("device", srcDevice.deviceName);
				Set<String> keySet = txtRecordMap.keySet();
				for (String str : keySet) {
					if (str.contains("req")) {
						String reqString = txtRecordMap.get(str);
						Log.d("onReceiveReq", str + "  " + reqString);
						if (OfflineAlgorithm.isMyMessage(reqString, util.getName())) {
							// 閺勵垰褰傜紒娆掑殰瀹歌京娈戦敍灞惧复閺�拷
							OfflineMsgEntity offlineMsgEntity = WqtUtil.convertToOfflineMsgEntity(reqString);

							if (offlineMessagedb.isIn(OfflineMsgDB.TABLE_NAME, offlineMsgEntity.getSource(),
									offlineMsgEntity.getDestnation(), offlineMsgEntity.getDate())) {
								// received a duplicated msg
								Log.d("is my message", "received a duplicated msg");
								String msgHeader = WqtUtil.getMsgHeader(reqString);
								if (!receivedMsgList.contains(msgHeader))
									receivedMsgList.add(msgHeader);
							} else {
								// new msg
								Log.d("is my message", "received a new msg");
								offlineMessagedb.insertMessage(OfflineMsgDB.TABLE_NAME, offlineMsgEntity.getSource(),
										offlineMsgEntity.getDestnation(), offlineMsgEntity.getDate(),
										offlineMsgEntity.getMsgContent());
								Intent intent = new Intent();
								intent.setAction(WqtConstants.MsgOnRsvWithNoLinkAction);

								intent.putExtra("message", offlineMsgEntity);
								sendBroadcast(intent);
								// 濞ｈ濮為崚鏉垮嚒閹恒儲鏁归崚妤勩�娑擄拷
								String msgHeader = WqtUtil.getMsgHeader(reqString);
								if (!receivedMsgList.contains(msgHeader))
									receivedMsgList.add(msgHeader);
							}

						} else if (OfflineAlgorithm.isMyFriendMessage(OfflineMsgService.this, reqString,
								util.getName())) {
							OfflineMsgEntity offlineMsgEntity = WqtUtil.convertToOfflineMsgEntity(reqString);
							if (offlineMessagedb.isIn(OfflineMsgDB.TABLE_NAME, offlineMsgEntity.getSource(),
									offlineMsgEntity.getDestnation(), offlineMsgEntity.getDate())) {

							} else {
								offlineMessagedb.insertMessage(OfflineMsgDB.TABLE_NAME, offlineMsgEntity.getSource(),
										offlineMsgEntity.getDestnation(), offlineMsgEntity.getDate(),
										offlineMsgEntity.getMsgContent());
								/*
								if(isConnectedToServer()){
									DirectScfPacket directScfPacket = new DirectScfPacket();
									String throwid=UUID.randomUUID().toString();
									directScfPacket.setThrowid(throwid);
									directScfPacket.setSource(offlineMsgEntity.getSource());
									directScfPacket.setDestination(offlineMsgEntity.getDestnation());
									directScfPacket.setStarttime(offlineMsgEntity.getDate());
									directScfPacket.setLife("3600000 * 24");
									directScfPacket.setValue(offlineMsgEntity.getMsgContent());
									directScfPacket.setMsgcontent(offlineMsgEntity.getMsgContent());
									XmppTool.getConnection().sendPacket(directScfPacket);
								}else{
									if (!messageList.contains(reqString))
										messageList.add(reqString);
								}
								*/
								if (!messageList.contains(reqString))
									messageList.add(reqString);
							}

						}

					}
					if (str.contains("resp")) {
						Log.d("onReceiveResp", str);
						String respString = txtRecordMap.get(str);
						for (String offlineMsgString : messageList) {
							if (offlineMsgString.contains(respString)) {
								messageList.remove(offlineMsgString);
								// removeInDB(respString);
							}
						}
					}
				}
			}

		};

		responseListener = new DnsSdServiceResponseListener() {

			@Override
			public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
				// TODO Auto-generated method stub
				/*
				 * Log.d("onDnsSdServiceAvailable", srcDevice.deviceName);
				 * Log.d("onDnsSdServiceAvailable", instanceName);
				 * Log.d("onDnsSdServiceAvailable", registrationType);
				 */
			}
		};

		manager.setDnsSdResponseListeners(channel, responseListener, txtListener);

		sendTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (service != null) {
					manager.clearLocalServices(channel, new ActionListener() {

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							Log.d("clearLocalServices", "success");
						}

						@Override
						public void onFailure(int reason) {
							// TODO Auto-generated method stub
							Log.d("clearLocalServices", "failure");
							Toast.makeText(OfflineMsgService.this, "clearLocalServices fail", Toast.LENGTH_LONG).show();
						}
					});

				}
				record.clear();
				fillRecordContent();
				service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
				manager.addLocalService(channel, service, new ActionListener() {
					@Override
					public void onSuccess() {
						Log.d("addLocalService", "success");
					}

					@Override
					public void onFailure(int error) {
						Toast.makeText(OfflineMsgService.this, "addLocalService fail", Toast.LENGTH_LONG).show();
					}
				});
			}
		};

		recvTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				manager.clearServiceRequests(channel, new ActionListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.d("clearServiceRequests", "success");
					}

					@Override
					public void onFailure(int reason) {
						// TODO Auto-generated method stub
						Toast.makeText(OfflineMsgService.this, "clearServiceRequests fail", Toast.LENGTH_LONG).show();
					}
				});
				serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
				manager.addServiceRequest(channel, serviceRequest, new ActionListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.d("addServiceRequest", "success");
					}

					@Override
					public void onFailure(int reason) {
						// TODO Auto-generated method stub
						Toast.makeText(OfflineMsgService.this, "addServiceRequest fail", Toast.LENGTH_LONG).show();
					}
				});
				discoverService();
			}
		};
		// manager.setDnsSdResponseListeners(channel, responseListener,
		// txtListener);
		// startRegistrationAndDiscovery();
		isFirstRun = true;
		sendBroadcast();
		receiveBroadcast();

	}
	
	public boolean isConnectedToServer(){
		if(XmppTool.getConnection()==null)
			return false;
		else if(!XmppTool.getConnection().isConnected()){
			return false;
		}else{
			return true;
		}
	}
	/*
	 * protected void removeInDB(String respString) { // TODO Auto-generated
	 * method stub String source = OfflineMsgEntity.getSourceName(respString);
	 * String dst = OfflineMsgEntity.getDstName(respString); String date =
	 * OfflineMsgEntity.getDate(respString);
	 * offlineMessagedb.deleteMessage(OfflineMsgDB.TABLE_NAME, source, dst,
	 * date); }
	 */

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent==null){
			return super.onStartCommand(intent, flags, startId);
		}
		String message = intent.getStringExtra("message");
		if (message != null) {
			Log.d("onStartCommand", "message:" + message);
			messageList.add(message);
			OfflineMsgEntity offlineMsgEntity = WqtUtil.convertToOfflineMsgEntity(message);
			offlineMessagedb.insertMessage(OfflineMsgDB.TABLE_NAME, offlineMsgEntity.getSource(),
					offlineMsgEntity.getDestnation(), offlineMsgEntity.getDate(), offlineMsgEntity.getMsgContent());
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// offlineMessagedb.close();
		super.onDestroy();
		offlineMessagedb.close();
		sendTimerTask.cancel();
		recvTimerTask.cancel();

	}

	private void receiveBroadcast() {
		timer.schedule(recvTimerTask, 0, 10000);

	}

	private void sendBroadcast() {
		timer.schedule(sendTimerTask, 1000, 10000);
	}

	private void discoverService() {

		manager.discoverServices(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.d("discoverServiceAction", "success");
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				Log.d("discoverServiceAction", "reasonCode==" + reason);
				Toast.makeText(OfflineMsgService.this, "discoverServiceAction fail reasonCode="+reason, Toast.LENGTH_LONG).show();			}

		});
	}

	private void fillRecordContent() {
		// TODO Auto-generated method stub
		// record.put("remoteUser", util.getName());
		// List<String> offlineMsgList =
		// offlineMessagedb.getOfflineMsg(OfflineMsgDB.TABLE_NAME);

		if (!messageList.isEmpty()) {
			for (int i = 0; i < messageList.size(); i++) {
				record.put("req" + i, messageList.get(i));
				Log.d("put req", messageList.get(i));
			}
		} else {
			Log.d("messageList", "is empty");
		}

		/*
		 * if (!offlineMsgList.isEmpty()) { for (int i = 0; i <
		 * offlineMsgList.size(); i++) { record.put("req" + i,
		 * messageList.get(i)); } }
		 */
		if (!receivedMsgList.isEmpty()) {
			for (int i = 0; i < receivedMsgList.size(); i++) {
				record.put("resp" + i, receivedMsgList.get(i));
				Log.d("put resp", receivedMsgList.get(i));
			}
		}
		receivedMsgList.clear();
		Log.d("fillRecordContent", "complete");

	}

	/*
	 * private void startRegistrationAndDiscovery() {
	 * 
	 * // Map<String, String> record = new HashMap<String, String>(); //
	 * GetContactLogInfomation contactInfomation = new //
	 * GetContactLogInfomation(WiFiServiceDiscoveryActivity.this); // String
	 * contactLog = contactInfomation.getLastWeek(); // record.put("remoteUser",
	 * "wqt"); // Log.d(TAG, "testUserRouteInfo : " + contactLog); //
	 * WifiP2pDnsSdServiceInfo service = //
	 * WifiP2pDnsSdServiceInfo.newInstance("msn_test", "_prsence._tcp", //
	 * record); // (2),闂佺尨鎷峰▔娑㈠春鐎规姕scovery婵炴垶鏌ㄩ鍛村箖濡ゅ啰鍗氶悗锝庝簻缁侇噣鏌涘┑鍡櫺㈢紒顭掓嫹
	 * 
	 * manager.addLocalService(channel, service, new ActionListener() {
	 * 
	 * @Override public void onSuccess() { Log.d("addLocalService", "success");
	 * }
	 * 
	 * @Override public void onFailure(int error) { Log.d("addLocalService",
	 * "failure"); } }); // discoverService(); // timer = new
	 * Timer(timerHandler); // timer.start();
	 * 
	 * manager.setDnsSdResponseListeners(channel, new
	 * DnsSdServiceResponseListener() {
	 * 
	 * @Override public void onDnsSdServiceAvailable(String instanceName, String
	 * registrationType, WifiP2pDevice srcDevice) { // TODO Auto-generated
	 * method stub Log.d("onDnsSdServiceAvailable", "is Running "); } }, new
	 * DnsSdTxtRecordListener() {
	 * 
	 * @Override public void onDnsSdTxtRecordAvailable(String fullDomainName,
	 * Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) { // TODO
	 * Auto-generated method stub Log.d("onDnsSdTxtRecordAvailable",
	 * "is Running "); if (txtRecordMap.containsKey("message")) { String message
	 * = txtRecordMap.get("message"); Log.d("onDnsSdTxtRecordAvailable",
	 * message); Intent intent = new Intent();
	 * intent.setAction("com.action.msg"); intent.putExtra("message", message);
	 * sendBroadcast(intent); Log.d("onDnsSdTxtRecordAvailable", "has message");
	 * } else { Log.d("onDnsSdTxtRecordAvailable", "no message"); }
	 * 
	 * }
	 * 
	 * });
	 * 
	 * discoverService(); }
	 */

}
