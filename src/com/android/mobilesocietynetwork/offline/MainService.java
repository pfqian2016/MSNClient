package com.android.mobilesocietynetwork.offline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




























import com.android.mobilesocietynetwork.client.MyApplication;
import com.android.mobilesocietynetwork.client.database.BroadcastReqGotRecordDB;
import com.android.mobilesocietynetwork.client.database.BroadcastResponseGotRecordDB;
import com.android.mobilesocietynetwork.client.database.BufferedUpMessageDB;
import com.android.mobilesocietynetwork.client.database.CostWaitToResponseDB;
import com.android.mobilesocietynetwork.client.database.MessageDB;
import com.android.mobilesocietynetwork.client.database.OfflineMessageDB;
import com.android.mobilesocietynetwork.client.database.OfflineThrowTimeDB;
import com.android.mobilesocietynetwork.client.database.OnlineThrowTimeDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.database.WaitToRecDB;
import com.android.mobilesocietynetwork.client.info.ChatMsgEntity;
import com.android.mobilesocietynetwork.client.info.hybrid.AcceptNoticeInfo;
import com.android.mobilesocietynetwork.client.info.hybrid.BeginAskOnlineInfo;
import com.android.mobilesocietynetwork.client.info.hybrid.ScfInfo;
import com.android.mobilesocietynetwork.client.packet.hybrid.BeginAskOnlinePacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.DirectScfPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.ForwardScfPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.HalfAskOnlinePacket;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.MyDate;
import com.android.mobilesocietynetwork.client.util.NetworkSearchingWillingSP;
import com.android.mobilesocietynetwork.client.util.NetworkSpeedSP;
import com.android.mobilesocietynetwork.client.util.OfflineCost;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * ��Ҫ��offline����service��
 * 
 * @author LLR_sunshine
 *
 */
public class MainService extends Service implements ConnectionInfoListener, Handler.Callback
{
	public static final String TAG = "wifidirectdemo";
	private TheBinder binder = new TheBinder();
	private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager;
	private Channel channel;
	private WifiP2pDnsSdServiceRequest serviceRequest;
	// private static final String TXTRECORD_PROP_AVAILABLE =
	// "BroadcastMessage";
	public static final String SERVICE_INSTANCE = "_hybridMSN";
	public static final String SERVICE_REG_TYPE = "_presence._tcp";
	public ArrayList<String> broadcastReq;
	public ArrayList<Map<String, String>> a;

	NetworkSpeedSP netSpeedSP;
	NetworkSearchingWillingSP netSeaWilAP;
	SharePreferenceUtil util;
	WifiP2pDnsSdServiceInfo service;
	MyApplication myApplication;
	Map<String, String> record;
	WiFiDirectBroadcastReceiver receiver;
	private Handler handler = new Handler(this);

	private OfflineMessageDB offlineMessageDB;
	private BroadcastReqGotRecordDB broadcastReqGotRecordDB;
	private BroadcastResponseGotRecordDB broadcastResponseGotRecordDB;
	private ArrayList<ResponseLibItem> responseLib;
	private OnlineThrowTimeDB onlineThrowTimeDB;
	private OfflineCost oc;
	private CostWaitToResponseDB costWaitToResponseDB;
	// ��ÿһ��offlineMessageDB����Ϣ�յ��������ڵ����Ӧ��ʱ�����Դ������Ƚϵ���ʱ��Ӧ��
	private HashMap<String, Hashtable<WifiP2pDevice, Double>> bufferCostResponseItem;
	private BufferedUpMessageDB bufferedUpMessageDB;
	private OfflineThrowTimeDB offlineThrowTimeDB;
	private MessageDB messageDB;
	private WaitToRecDB waitToRecDB;

	// ����SCFͨ�ŵı�־����Ϣ
	private int throwID_SCF;
	private String type_SCF;
	private boolean active_SCF;
	
	//��xmpp��������������ʱ��Ҫ�õ������ݽṹ
	private BeginAskOnlineInfo acceptInfo= new BeginAskOnlineInfo();
	private BeginAskOnlineInfo acceptInfo2= new BeginAskOnlineInfo();
	private ScfInfo scfinfo = new ScfInfo();
	private ScfInfo scfinfo2 = new ScfInfo();
	private AcceptNoticeInfo acceptNoticeInfo = new AcceptNoticeInfo();

	private BroadcastReceiver MsgReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String msg = intent.getStringExtra(Constants.MSGKEY);
			if (msg != null)
			{// ������ǿգ�˵������Ϣ�㲥������getMessage�н��д���
				getMessage(msg);
			}
			else
			{// ����ǿ���Ϣ��˵���ǹر�Ӧ�õĹ㲥
				logPrint("��MainService���յ��ر�Ӧ�õĹ㲥��");
			}
		}
	};

	public Handler getHandler()
	{
		return handler;
	}

	public class TheBinder extends Binder
	{
		public void setActivity(Activity a)
		{
			//activity = a;
		}
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.d(TAG, "LLR_sunshine ----- " + "onBind successful.");
		// Bundle b = intent.getExtras();
		// offlineMessageDestination = b.getStringArrayList("broadcastData");
		// startRegistrationAndDiscovery();
		return binder;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		offlineMessageDB = new OfflineMessageDB(this);
		broadcastReqGotRecordDB = new BroadcastReqGotRecordDB(this);
		broadcastResponseGotRecordDB = new BroadcastResponseGotRecordDB(this);
		myApplication = (MyApplication) getApplicationContext();
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		netSpeedSP = new NetworkSpeedSP(MainService.this, Constants.NET_SPEED);
		netSeaWilAP = new NetworkSearchingWillingSP(this, Constants.NET_WILLING);
		onlineThrowTimeDB = new OnlineThrowTimeDB(this);
		costWaitToResponseDB = new CostWaitToResponseDB(this);
		bufferedUpMessageDB = new BufferedUpMessageDB(this);
		offlineThrowTimeDB = new OfflineThrowTimeDB(this);
		waitToRecDB = new WaitToRecDB(this);
		messageDB = new MessageDB(this);
		waitToRecDB = new WaitToRecDB(this);
		bufferCostResponseItem = new HashMap<String, Hashtable<WifiP2pDevice, Double>>();
		responseLib = new ArrayList<>();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
		registerReceiver(receiver, intentFilter);
		
		
		// ע�������Ϣ�㲥
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION);
		registerReceiver(MsgReceiver, intentFilter);
		
		
		recieveBroadcast();
		sendBroadcast();
	}

	// ����OFFLINE�㲥
	private void recieveBroadcast()
	{
		new Timer().schedule(new TimerTask() {
			@Override
			public void run()
			{
				if (myApplication.getIsRecieveOn())
				{
					if (serviceRequest != null)
					{
						manager.clearServiceRequests(channel, new ActionListener() {
							@Override
							public void onSuccess()
							{
								logPrint("Clear Service request.");
							}

							@Override
							public void onFailure(int error)
							{
								logPrint("Failed to clear service request.");
							}
						});
					}
					discoverService();
				}
			}

		}, 0, 10000);
	}

	// ���ַ��񣬼����չ㲥�ĺ��Ĺ���
	private void discoverService()
	{
		/*
		 * Register listeners for DNS-SD services. These are callbacks invoked
		 * by the system when a service is actually discovered.
		 */
		manager.setDnsSdResponseListeners(channel, new DnsSdServiceResponseListener() {
			@Override
			public void onDnsSdServiceAvailable(String instanceName, String registrationType,
					WifiP2pDevice srcDevice)
			{
				// A service has been discovered. Is this our app?
				System.out.println("onDnsSdServiceAvailable");
				Toast.makeText(MainService.this,
						"onDnsSdServiceAvailable", Toast.LENGTH_LONG).show();
				toastShow("Service discovered", false);
				logPrint("onBonjourServiceAvailable " + instanceName);
				if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE))
				{
				}
			}
		}, new DnsSdTxtRecordListener() {
			/**
			 * A new TXT record is available. Pick up the advertised buddy name.
			 */
			@Override
			public void onDnsSdTxtRecordAvailable(String fullDomainName,
					Map<String, String> record, WifiP2pDevice srcDevice)
			{
				Toast.makeText(MainService.this,
						"public void onDnsSdTxtRecordAvailable(String fullDomainName", Toast.LENGTH_LONG).show();
//				System.out.println("	public void onDnsSdTxtRecordAvailable(String fullDomainName");
				int requestNumber = Integer.parseInt(record.get("requestN"));
				// ��ÿһ��������д���
//				System.out.println("requestNumber:" + requestNumber);
				for (int i = 0; i < requestNumber; i++)
				{
					logPrint("��ʼ��������" + i);
					String[] value = (record.get("request" + (i + 1))).split("\\+");//value[0]:ҵ�����ͣ�value[1]:Ŀ�ķ���value[2]:���ݴ�С��value[3]:throwID
					
					logPrint("������" + value[0]);
					Toast.makeText(MainService.this,
							"destination:" +value[1], Toast.LENGTH_LONG).show();
					Toast.makeText(MainService.this,
							"me:" +util.getName(), Toast.LENGTH_LONG).show();
					//*****������
					responseLib.add(new ResponseLibItem(value[0] + "+"
							+ srcDevice.deviceName + "+" + (-1) + "+" + value[3]));
					myApplication.setIsSendOn(true);
					//*****end test
//					if (!broadcastReqGotRecordDB.isIn(util.getName(), srcDevice.deviceName, Integer
//							.parseInt(value[3]), value[1], (byte) (value[0].equals("U") ? 0 : 1)))
//					{
						Toast.makeText(MainService.this, "*******broadcastReqGotRecordDB.isIn false***********", Toast.LENGTH_LONG).show();
						if (value[1].equals(util.getName()))
						{
							toastShow("���Ի���-1����ֵ", false);
							logPrint("���Ի���-1����ֵ��");
							responseLib.add(new ResponseLibItem(value[0] + "+"
									+ srcDevice.deviceName + "+" + (-1) + "+" + value[3]));
							myApplication.setIsSendOn(true);
						}
						else
						{
							if (!isNetworkAvailable())
							{
								double offlineCost=0;
								double onlineCost=0;
//								Toast.makeText(MainService.this, "if (!isNetworkAvailable())", Toast.LENGTH_LONG).show();
								offlineCost = oc.computeOfflineCostFromSource(MainService.this,
										value[1]);
								onlineCost = Short.parseShort(value[2])
										/ netSpeedSP.getWeightSpeed()
										+ netSeaWilAP.getWillingCost(System.currentTimeMillis())
										+ onlineThrowTimeDB.getWeightTime(util.getName(), value[1]);
//								Toast.makeText(MainService.this, "*******************Start add response info ************** ", Toast.LENGTH_LONG).show();
								responseLib.add(new ResponseLibItem(value[0] + "+"
										+ srcDevice.deviceName + "+"
										+ (onlineCost < offlineCost ? onlineCost : offlineCost)
										+ "+" + value[3]));
//								Toast.makeText(MainService.this, "*******************End add response info ************** ", Toast.LENGTH_LONG).show();
								costWaitToResponseDB.insertData(util.getName(),
										(byte) (value[0].equals("U") ? 0 : 1),
										(byte) (onlineCost > offlineCost ? 1
										: 2), "null", value[1], Integer.parseInt(value[3]),
										srcDevice.deviceName,
										(onlineCost < offlineCost ? onlineCost : offlineCost));
								myApplication.setIsSendOn(true);
							}
							else
							{
									// �ڻ�δ�յ�responseOnlineCost֮ǰ�ȰѼ�¼���롣
									Toast.makeText(MainService.this, "on network!!!!", Toast.LENGTH_LONG).show();
									costWaitToResponseDB.insertData(util.getName(),
											(byte) (value[0].equals("U") ? 0 : 1), (byte) 0, "null", value[1],
											Integer.parseInt(value[3]), srcDevice.deviceName, 0);
									BeginAskOnlinePacket beginAskOnlinePacket = new BeginAskOnlinePacket();
									beginAskOnlinePacket.setThrowid(value[3]);
									beginAskOnlinePacket.setDestination(value[1]);
									beginAskOnlinePacket.setSize(value[2]);
									XmppTool.getConnection().sendPacket(beginAskOnlinePacket);
		/*							JSONObject jso = new JSONObject();
									jso.put(Constants.PACKET_TYPE, "askOnlineCost");
									jso.put("throwID", Integer.parseInt(value[3]));
									jso.put("destination", value[1]);
									jso.put("size", Short.parseShort(value[2]));
									Client client = myApplication.getClient();
									ClientOutputThread out = client.getClientOutputThread();
									out.setMsg(jso);*/
									logPrint("��offlineģʽ�²�ѯ���ۡ�");
							}
						}
						broadcastReqGotRecordDB.insertData(util.getName(),
								(byte) (value[0].equals("U") ? 0 : 1), srcDevice.deviceName,
								value[1], Integer.parseInt(value[3]), System.currentTimeMillis());
						logPrint("����������" + i);
//					}
					Toast.makeText(MainService.this, "*******broadcastReqGotRecordDB.isIn == true***********", Toast.LENGTH_LONG).show();
				}
				logPrint("��������������");
				Toast.makeText(MainService.this, "*******��������������***********", Toast.LENGTH_LONG).show();
				if (offlineMessageDB.isNotEmpty(util.getName()))
				{
					Toast.makeText(MainService.this, "*******��ʼ������Ӧ***********", Toast.LENGTH_LONG).show();
					int responseNumber = Integer.parseInt(record.get("responseN"));
					Toast.makeText(MainService.this, "responseNumber : " + responseNumber, Toast.LENGTH_LONG).show();
					for (int i = 0; i < responseNumber; i++)
					{
						logPrint("��ʼ������Ӧ" + i);
						Toast.makeText(MainService.this, "responseNumber : " + responseNumber, Toast.LENGTH_LONG).show();
						final String[] value = (record.get("response" + (i + 1))).split("\\+");
						String myDeviceName = myApplication.getWifiP2pDevice().deviceName;
						Toast.makeText(MainService.this, "*********value[1]="+value[1]+"myDeviceName="+myDeviceName+"***************", Toast.LENGTH_LONG).show();
						if (value[1].equals(myDeviceName))
						{
							Toast.makeText(MainService.this, "value[1].equals(myDeviceName))", Toast.LENGTH_LONG).show();
							if (!broadcastResponseGotRecordDB.isIn(util.getName(),
									srcDevice.deviceName, Integer.parseInt(value[3]),
									(byte) (value[0].equals("U") ? 0 : 1)))
							{
								Toast.makeText(MainService.this, "	if (!broadcastResponseGotRecordDB.isIn(util.getName(),", Toast.LENGTH_LONG).show();
								if (value[2].equals("-1"))
								{
									// ����Ӧ�ڵ㼴ΪĿ�Ľڵ㣬ֱ��Ͷ��
									Toast.makeText(MainService.this, "if (value[2].equals(-1))", Toast.LENGTH_LONG).show();
									active_SCF = true;
									throwID_SCF = Integer.parseInt(value[3]);
									type_SCF = "directSCF";
									connectP2p(srcDevice);
								}
								else
								{
									final String mapKey = value[0] + "+" + value[3];
									if (!bufferCostResponseItem.containsKey(mapKey))
									{
										Hashtable<WifiP2pDevice, Double> ht = new Hashtable<>();
										ht.put(srcDevice, Double.parseDouble(value[2]));
										bufferCostResponseItem.put(mapKey, ht);
										new Thread() {
											public void run()
											{
												try
												{
													String mapKeyInThread = mapKey;
													// ��ʱѡ10S�������ٸĳ���Ӧ�Զ����Ӧ���
													Thread.sleep(10000);

													// �Ƚ�ѡȡ���š�
													Hashtable<WifiP2pDevice, Double> ht = bufferCostResponseItem
															.get(mapKeyInThread);
													WifiP2pDevice bestKey = null;
													double bestValue = 0;
													int i = 0;
													for (WifiP2pDevice key : ht.keySet())
													{
														if (i == 0)
														{
															bestKey = key;
															bestValue = ht.get(key);
															i = 1;
														}
														else if (ht.get(key) < bestValue)
														{
															bestKey = key;
															bestValue = ht.get(key);
														}
													}
													// ѡ�꿪ʼ�ͱ��رȽ��ˣ��ȿ�����û�������
													if (!isNetworkAvailable())
													{
														double offlineCost = oc.computeOfflineCost(
																MainService.this,
																offlineMessageDB.getDestination(
																		util.getName(),
																		Integer.parseInt(mapKeyInThread
																				.split("\\+")[1])),
																System.currentTimeMillis()
																		- offlineMessageDB.getInsertTime(
																				util.getName(),
																				Integer.parseInt(mapKeyInThread
																						.split("\\+")[1])));
														double onlineCost = getMessageLength(offlineMessageDB
																.getData(util.getName(), Integer
																		.parseInt(mapKeyInThread
																				.split("\\+")[1])))
																/ netSpeedSP.getWeightSpeed()
																+ netSeaWilAP.getWillingCost(System
																		.currentTimeMillis())
																+ onlineThrowTimeDB.getWeightTime(
																		util.getName(),
																		mapKeyInThread.split("\\+")[1]);
														double localCost = onlineCost < offlineCost ? onlineCost
																: offlineCost;
														// �Ƚ���������
														if (localCost < bestValue)
														{
															if (onlineCost < offlineCost)
															{

																bufferedUpMessageDB.insertMessage(
																		util.getName(),
																		offlineMessageDB.getSource(
																				util.getName(),
																				Integer.parseInt(mapKeyInThread
																						.split("\\+")[1])),
																		offlineMessageDB.getDestination(
																				util.getName(),
																				Integer.parseInt(mapKeyInThread
																						.split("\\+")[1])),
																		offlineMessageDB.getStartTime(
																				util.getName(),
																				Integer.parseInt(mapKeyInThread
																						.split("\\+")[1])),
																		Integer.parseInt(mapKeyInThread
																				.split("\\+")[1]),
																		3600000 * 24,
																		offlineMessageDB.getData(
																				util.getName(),
																				Integer.parseInt(mapKeyInThread
																						.split("\\+")[1])),
																		offlineMessageDB.getPass(
																				util.getName(),
																				Integer.parseInt(mapKeyInThread
																						.split("\\+")[1])));
																offlineMessageDB.deleteMessage(util
																		.getName(), Integer
																		.parseInt(mapKeyInThread
																				.split("\\+")[1]));

															}
														}
														else
														{
															// ����SCF����
															active_SCF = true;
															throwID_SCF = Integer
																	.parseInt(mapKeyInThread
																			.split("\\+")[1]);
															type_SCF = "forwardSCF";
															connectP2p(bestKey);
														}
													}
													// �����������������resp�ȽϽ���浽���ݿ⣬���յ�����������ʱȡ���Ƚ�
													else
													{
														offlineMessageDB.updateRespInfo(util
																.getName(), bestValue + "+"
																+ bestKey.deviceAddress, Integer
																.parseInt(mapKeyInThread
																		.split("\\+")[1]));
													try
														{
														BeginAskOnlinePacket beginAskOnlinePacket = new BeginAskOnlinePacket();
														beginAskOnlinePacket.setThrowid(mapKeyInThread
																		.split("\\+")[1]);
														beginAskOnlinePacket.setDestination(offlineMessageDB.getDestination(
																util.getName(),
																Integer.parseInt(mapKeyInThread
																		.split("\\+")[1])));
														beginAskOnlinePacket.setSize(getMessageLength(offlineMessageDB.getData(
																util.getName(),
																Integer.parseInt(mapKeyInThread
																		.split("\\+")[1])))+"");
														XmppTool.getConnection().sendPacket(beginAskOnlinePacket);

														}
														catch (Exception e)
														{
															e.printStackTrace();
														}
													}
													bufferCostResponseItem.remove(mapKeyInThread);
												}
												catch (InterruptedException e)
												{
													e.printStackTrace();
												}
											}
										}.start();
									}
									bufferCostResponseItem.get(mapKey).put(srcDevice,
											Double.parseDouble(value[2]));
								}
								broadcastResponseGotRecordDB.insertData(util.getName(),
										(byte) (value[0].equals("U") ? 0 : 1),
										srcDevice.deviceName, Integer.parseInt(value[3]),
										System.currentTimeMillis());
							}
						}
						logPrint("��������Ӧ" + i);
					}
					logPrint("������������Ӧ��");
				}
			}
		});

		// After attaching listeners, create a service request and initiate
		// discovery.
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		manager.addServiceRequest(channel, serviceRequest, new ActionListener() {
			@Override
			public void onSuccess()
			{
				// ToastNotification("Added service discovery request", false);
				logPrint("Successful to Add service discovery request");
			}

			@Override
			public void onFailure(int arg0)
			{
				// ToastNotification("Failed adding service discovery request",
				// false);
				logPrint("Failed to add service discovery request");
			}
		});
		manager.discoverServices(channel, new ActionListener() {
			@Override
			public void onSuccess()
			{
				// ToastNotification("Service discovery initiated", false);
				logPrint("Service discovery initiated");
			}

			@Override
			public void onFailure(int arg0)
			{
				// ToastNotification("Service discovery failed", false);
				logPrint("Service discovery failed");
			}
		});
	}

	// ����OFFLINE�㲥
	private void sendBroadcast()
	{
		new Timer().schedule(new TimerTask() {
			@Override
			public void run()
			{
				if (myApplication.getIsSendOn())
				{
					if (service != null)
					{
						manager.clearLocalServices(channel, new ActionListener() {
							@Override
							public void onSuccess()
							{
								logPrint("Clear Local Service.");
							}

							@Override
							public void onFailure(int error)
							{
								logPrint("Failed to clear local service.");
							}
						});
						record.clear();
					}
					fillBroadcastContent();
					logPrint("׼����װ����record�����ǣ�" + record.toString());
					service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE,
							SERVICE_REG_TYPE, record);
					manager.addLocalService(channel, service, new ActionListener() {
						@Override
						public void onSuccess()
						{
							toastShow("Added Local Service", false);
							logPrint("Added Local Service");
						}

						@Override
						public void onFailure(int error)
						{
							toastShow("Failed to add a service", false);
							logPrint("Failed to add a service");
						}
					});
				}
			}
		}, 0, 10000);
	}

	public void fillBroadcastContent()
	{
		record = new HashMap<String, String>();
		int i = 0, j = 0;
		if (offlineMessageDB.isNotEmpty(util.getName()))
		{
			System.out.println("	if (offlineMessageDB.isNotEmpty(util.getName()))");
			broadcastReq = offlineMessageDB.getBroadcastData(util.getName());
			Iterator<String> itReq = broadcastReq.iterator();
			while (itReq.hasNext())
			{
				i++;
				//Toast.makeText(MainService.this, "add request : " + itReq.next(), Toast.LENGTH_LONG).show();
				record.put("request" + i, itReq.next());
				logPrint("�������offlineMessage��");
			}
		}  
		record.put("requestN", i + "");

		if (responseLib.size() != 0) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!(responseLib.size() != 0!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println("	if (responseLib.size() != 0) ");
		}else
			System.out.println("****************(responseLib.size() == 0**************");
		ListIterator<ResponseLibItem> itResp = responseLib.listIterator();
		 
		while (itResp.hasNext())
		{
			System.out.println( "****************"+responseLib.size()+"**************");
			ResponseLibItem rl = itResp.next();
			if (rl.getBroadcastTime() < 5)
			{
				System.out.println("add response info~#####################");
				//Toast.makeText(MainService.this, "add response info~", Toast.LENGTH_LONG).show();
				//Toast.makeText(MainService.this, "" + rl.getResponseContent(), Toast.LENGTH_LONG).show();
				j++;
				record.put("response" + j, rl.getResponseContent());
				itResp.remove();
				rl.addBroadcastTime();
				itResp.add(rl);
			}
			else
			{
				itResp.remove();
				logPrint(rl.getResponseContent() + "����Ӧ��������5�μ�¼����ɾ֮��");
			}
		}
		record.put("responseN", j + "");
		logPrint("ι��record�ˡ�");
		// handler.obtainMessage(Constants.TOASTSHOW, "ι��record��");
	}

	public void connectP2p(WifiP2pDevice device)
	{
		System.out.println("public void connectP2p(WifiP2pDevice device)");
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		myApplication.setIsRecieveOn(false);
		myApplication.setIsSendOn(false);
		if (serviceRequest != null)
			manager.removeServiceRequest(channel, serviceRequest, new ActionListener() {

				@Override
				public void onSuccess()
				{
					logPrint("��ʱֹͣ���չ㲥�ˡ�");
				}

				@Override
				public void onFailure(int arg0)
				{
					logPrint("ֹͣ���չ㲥����ʧ�ܡ�");
				}
			});

		manager.connect(channel, config, new ActionListener() {
			@Override
			public void onSuccess()
			{
				toastShow("Connecting to service", false);
				logPrint("����SCF���ӳɹ���");
			}

			@Override
			public void onFailure(int errorCode)
			{
				toastShow("Failed connecting to service", false);
				logPrint("����SCF����ʧ�ܣ�");
			}
		});
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo)
	{
		Thread handler = null;
		/*
		 * The group owner accepts connections using a server socket and then
		 * spawns a client socket for every client. This is handled by {@code
		 * GroupOwnerSocketHandler}
		 */
		toastShow("**************�Ѿ�������*****************", false);
		if (p2pInfo.isGroupOwner)
		{
			logPrint("�Ѿ������ˣ�����ӵ���ߡ�");
			try
			{
				if (active_SCF)
				{
					logPrint("���뷢����������");
					handler = new GroupOwnerSocketHandler(getHandler(), throwID_SCF,
							offlineMessageDB, type_SCF, util.getName());
				}
				else
				{
					handler = new GroupOwnerSocketHandler(getHandler(), 0, null, null, null);
				}
				handler.start();
			}
			catch (IOException e)
			{
				Log.d(TAG, "Failed to create a server thread - " + e.getMessage());
				return;
			}
		}
		else
		{
			logPrint("�Ѿ������ˣ������Ա��");
			if (active_SCF)
			{
				logPrint("���뷢����������");
				handler = new ClientSocketHandler(getHandler(), p2pInfo.groupOwnerAddress,
						throwID_SCF, offlineMessageDB, type_SCF, util.getName());
			}
			else
			{
				handler = new ClientSocketHandler(getHandler(), p2pInfo.groupOwnerAddress, 0, null,
						null, null);
			}
			handler.start();
		}
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch (msg.what)
		{
			case Constants.SCF_RECEIVE:
			{
				Intent broadCast = new Intent();
				broadCast.putExtra(Constants.MSGKEY, (String) msg.obj);
				broadCast.setAction(Constants.ACTION);
				// ���յ�����Ϣ�ѹ㲥����ʽ���ͳ�ȥ��ÿ��Activity�����յ��˹㲥��
				sendBroadcast(broadCast);
				if (manager != null && channel != null)
				{
					manager.removeGroup(channel, new ActionListener() {
						@Override
						public void onFailure(int reasonCode)
						{
							toastShow("�Ͽ�SCF����ʧ�ܣ�", false);
							logPrint("�Ͽ�SCF����ʧ�ܣ�Reason :" + reasonCode);
						}

						@Override
						public void onSuccess()
						{
							toastShow("�Ͽ�SCF���ӳɹ���", false);
							logPrint("�Ͽ�SCF���ӳɹ���");
						}

					});
				}
				active_SCF = false;
				throwID_SCF = 0;
				type_SCF = null;
				myApplication.setIsRecieveOn(true);
				if (offlineMessageDB.isNotEmpty(util.getName()) || !responseLib.isEmpty())
				{
					myApplication.setIsSendOn(true);
				}
				break;
			}
			case Constants.SCF_SEND:
			{
				if (manager != null && channel != null)
				{
					manager.removeGroup(channel, new ActionListener() {
						@Override
						public void onFailure(int reasonCode)
						{
							toastShow("�Ͽ�SCF����ʧ�ܣ�", false);
							logPrint("�Ͽ�SCF����ʧ�ܣ�Reason :" + reasonCode);
						}

						@Override
						public void onSuccess()
						{
							toastShow("�Ͽ�SCF���ӳɹ���", false);
							logPrint("�Ͽ�SCF���ӳɹ���");
						}

					});
				}
				active_SCF = false;
				throwID_SCF = 0;
				type_SCF = null;
				myApplication.setIsRecieveOn(true);
				active_SCF = true;
				if (offlineMessageDB.isNotEmpty(util.getName()) || !responseLib.isEmpty())
				{
					myApplication.setIsSendOn(true);
				}
				break;
			}
			case Constants.TOASTSHOW:
				toastShow((String) msg.obj, false);
				logPrint("UIThr�յ���ι����toastҪ��");
			default:
				;
		}
		return true;
	}

	public void getMessage(String msg)
	{
		try
		{
			logPrint("�յ�message���ģ���MainService�ﴦ��");
			JSONObject jso = new JSONObject(msg);

			switch ((String) jso.getString(Constants.PACKET_TYPE))
			{
				case Constants.NOTICE_ACCEPT:
				{
					logPrint("�յ�noticeAccept���ģ���MainService�ﴦ��");
					logPrint("�������ݣ�" + jso.toString());
					String destination = jso.getString("destination");
					int throwID = jso.getInt("throwid");
					logPrint("�õ���destination��throwid��" + destination + "��" + throwID);
					int type = waitToRecDB.getType(util.getName(), throwID, destination);
					logPrint("��MainService���ȡ��type�ǣ� " + type);
					if (type == 0)
					{
						onlineThrowTimeDB = new OnlineThrowTimeDB(this);
						logPrint("��ʼ����onlineThrowTimeDB��");
						logPrint("��ӡʱ��:" + jso.getLong("arriveTime"));
						onlineThrowTimeDB.insertData(util.getName(), destination, jso.getLong("arriveTime")
								- waitToRecDB.getThrowTime(util.getName(), throwID, destination));
						logPrint("��MainService��ִ����onlineThrowTimeDB�Ĳ��������");
						waitToRecDB.deleteData(util.getName(), throwID, destination, 0);
					}
					else if (type == 1)
					{

						offlineThrowTimeDB.insertData(
								util.getName(),
								destination,
								jso.getLong("throwTime")
										- waitToRecDB.getThrowTime(util.getName(), throwID,
												destination));
						waitToRecDB.deleteData(util.getName(), throwID, destination, 1);
					}
					logPrint("��MainActivity�ﴦ����noticeAccept���ġ�");
					break;
				}
				case Constants.RESPONSE_ONLINECOST:
				{
					Toast.makeText(MainService.this,
							"�յ�responseOnlineCost���ģ���MainService�д���", Toast.LENGTH_LONG).show();
					logPrint("�յ�responseOnlineCost���ģ���MainService�д���");
					logPrint("�������ݣ�" + jso.toString());
					System.out.println("@@@@@@@@@@@");
					System.out.println("�������ݣ�" + jso.toString());
					// �ж��з�����offline��cost�����Ӧ�û����������뼴ʱ������������Ϣ��cost����ͱ��������ͻ��
					// ����LLR_sunshine
					if (costWaitToResponseDB.isIn(util.getName(), jso.getInt("throwid"),
							jso.getString("destination"), (byte) 0))
					{
						System.out.println("##############");
						double offlineCost = oc.computeOfflineCostFromSource(MainService.this,
								jso.getString("destination"));
						double onlineCost = jso.getDouble("value");
						if (offlineCost < onlineCost)
						{
							System.out.println("offlineCost < onlineCost");
							costWaitToResponseDB.updateData(util.getName(), (byte) 0,
									jso.getString("destination"), jso.getInt("throwid"), (byte) 2,
									offlineCost, null);
							responseLib.add(new ResponseLibItem("U"
									+ "+"
									+ costWaitToResponseDB.getDeviceName(util.getName(),
											jso.getInt("throwid"), jso.getString("destination"),
											(byte) 0) + "+" + offlineCost + "+"
									+ jso.getInt("throwid")));
						}
						else
						{
							System.out.println("~~~~~~~~~~~~");
							int type = jso.getInt("type");
							if (type == 1)
							{
								System.out.println("int type = jso.getInt(type);");
								costWaitToResponseDB.updateData(util.getName(), (byte) 0,
										jso.getString("destination"), jso.getInt("throwid"),
										(byte) 3, jso.getDouble("value"), null);
								responseLib.add(new ResponseLibItem("U"
										+ "+"
										+ costWaitToResponseDB.getDeviceName(util.getName(),
												jso.getInt("throwid"),
												jso.getString("destination"), (byte) 0) + "+"
										+ onlineCost + "+" + jso.getInt("throwid")));
							}
							else
							{
								costWaitToResponseDB.updateData(util.getName(), (byte) 0,
										jso.getString("destination"), jso.getInt("throwid"),
										(byte) 4, jso.getDouble("value"),
										jso.getString("forwarder"));
								responseLib.add(new ResponseLibItem("U"
										+ "+"
										+ costWaitToResponseDB.getDeviceName(util.getName(),
												jso.getInt("throwid"),
												jso.getString("destination"), (byte) 0) + "+"
										+ onlineCost + "+" + jso.getInt("throwid")));
							}
						}
						myApplication.setIsSendOn(true);
					}  

					// �ж��з񱾵������Ӧ����Ӧ�������뼴ʱ������������Ϣ��cost������յ�������offline��cost����ĳ�ͻ��
					// ����LLR_sunshine
					else if (offlineMessageDB.isIn(util.getName(), jso.getInt("throwid"),
							jso.getString("destination")))
					{
						System.out.println("$$$$$$$$$$$$$$$$$");
						double offlineCost = oc.computeOfflineCost(
								MainService.this,
								jso.getString("destination"),
								System.currentTimeMillis()
										- offlineMessageDB.getInsertTime(util.getName(),
												jso.getInt("throwid")));
						double onlineCost = jso.getDouble("value");
						//offlineMessageDB��respInfo�����offlineģʽ����С����ֵ����һ���豸
						String respInfo = offlineMessageDB.getRespInfo(util.getName(),
								jso.getInt("throwid"));
						Toast.makeText(MainService.this, respInfo, Toast.LENGTH_LONG).show();
						double bestValue = Double.parseDouble(respInfo.split("\\+")[0]);
						String bestDeviceAddress = respInfo.split("\\+")[1];
						// ***************************
						// ���жԱȴ���
						//offlineCost���Լ�����offline��onlineCost���Լ�����online��bestValue���ھ�����С�Ĵ���
						// ***************************
						double localCost = onlineCost < offlineCost ? onlineCost : offlineCost;
						// �Ƚ���������
						if (localCost < bestValue)
						{
							System.out.println("	if (localCost < bestValue)");
							if (onlineCost < offlineCost)
							{
								System.out.println("	if (localCost < bestValue) && 	if (onlineCost < offlineCost)" );
								if (jso.get("type").equals("1"))
								{
									//�����������directSCF
					            	DirectScfPacket directScfPacket = new DirectScfPacket();
						        	directScfPacket.setThrowid( jso.optString("throwid"));
						        	directScfPacket.setSource(
						        			offlineMessageDB.getSource(util.getName(),
						        					jso.getInt("throwid")));
						        	directScfPacket.setDestination(
											offlineMessageDB.getDestination(util.getName(),
													jso.getInt("throwid")));
						        	directScfPacket.setValue(
						        			offlineMessageDB.getData(util.getName(),
											jso.getInt("throwid")));
						        	//directScfPacket.setStarttime(
						        	//		offlineMessageDB.getStartTime(util.getName(),
									//		jso.getInt("throwid")));
						        	directScfPacket.setLife(
						        			offlineMessageDB.getLife(util.getName(),
													jso.getInt("throwid"))+"");    
						        	//���;����
						        	String[] newstr = offlineMessageDB.getPass(util.getName(),
											jso.getInt("throwid")).split("+");
						    		for(int i =0;i<newstr.length;i++){
						    			directScfPacket.addPass(newstr[i]);
						    		}
									XmppTool.getConnection().sendPacket(directScfPacket);
									waitToRecDB.insertData(util.getName(), directScfPacket.getDestination(),
											 Integer.parseInt(directScfPacket.getThrowid()), MyDate.getDateLong(), 0);
								}
								else
								{
									//�����������forwardSCF
									System.out.println("	�����������forwardSCF" );
									ForwardScfPacket forwardScfPacket = new ForwardScfPacket();
									forwardScfPacket.setThrowid(jso.optString("throwid"));
									forwardScfPacket.setSource(
											offlineMessageDB.getSource(util.getName(),
				        					jso.getInt("throwid")));
									forwardScfPacket.setDestination(
											offlineMessageDB.getDestination(util.getName(),
											jso.getInt("throwid")));
									forwardScfPacket.setForwarder(jso.optString("forwarder"));								
									forwardScfPacket.setValue(
											offlineMessageDB.getData(util.getName(),
											jso.getInt("throwid")));
									forwardScfPacket.setStarttime(
											offlineMessageDB.getStartTime(util.getName(),
											jso.getInt("throwid")));
									forwardScfPacket.setLife(
											offlineMessageDB.getLife(util.getName(),
											jso.getInt("throwid"))+"");
									//���;����
						        	String[] newstr = offlineMessageDB.getPass(util.getName(),
											jso.getInt("throwid")).split("+");
						    		for(int i =0;i<newstr.length;i++){
						    			forwardScfPacket.addPass(newstr[i]);
						    		}
									XmppTool.getConnection().sendPacket(forwardScfPacket);	
									waitToRecDB.insertData(util.getName(), forwardScfPacket.getDestination(),
											 Integer.parseInt(forwardScfPacket.getThrowid()), MyDate.getDateLong(), 0);
								}
								offlineMessageDB.deleteMessage(util.getName(),
										jso.getInt("throwid"));
								if (offlineMessageDB.isNotEmpty(util.getName())
										|| costWaitToResponseDB.isNotEmpty(util.getName()))
								{
									myApplication.setIsSendOn(true);
								}
								else
								{
									myApplication.setIsSendOn(false);
								}
							}
							else
							{
								myApplication.setIsSendOn(true);
							}
						}
						else
						{
							// ����SCF����
							System.out.println("	����SCF����" );
							active_SCF = true;
							throwID_SCF = jso.getInt("throwid");
							type_SCF = "forwardSCF";
							WifiP2pDevice wpd = new WifiP2pDevice();
							wpd.deviceAddress = bestDeviceAddress;
							connectP2p(wpd);

							offlineMessageDB.deleteMessage(util.getName(), jso.getInt("throwid"));
							if (offlineMessageDB.isNotEmpty(util.getName())
									|| costWaitToResponseDB.isNotEmpty(util.getName()))
							{
								myApplication.setIsSendOn(true);
							}
							else
							{
								myApplication.setIsSendOn(false);
							}
						}
					} else {
						System.out.println("AAAAAAAAAAAA");
					}
					break;
				}
				case Constants.HELP_ONLINECOST:
				{
					logPrint("�յ�helpOnlineCost���ģ���MainService�д���.");
					logPrint("�������ݣ�" + jso.toString());
					//���յ�askOnlinehalf�����������Ӧ�Լ��Ĵ���ֵ
					
					acceptInfo2.setThrowid(jso.optString("throwid"));
					acceptInfo2.setFromwho(jso.optString("fromwho"));
					acceptInfo2.setDestination(jso.optString("destination"));
					acceptInfo2.setSize(jso.optString("size"));
				  //�ڴ˵ط����ͱ���4
					HalfAskOnlinePacket halfAskOnlinePacket = new HalfAskOnlinePacket();
					halfAskOnlinePacket.setThrowid(acceptInfo2.getThrowid());
					halfAskOnlinePacket.setTowho(acceptInfo2.getFromwho());
					//costBack��ʼΪ�գ���ô�죿������������������������������
					double costBack = offlineThrowTimeDB.getWeightTime(util.getName(),
							acceptInfo2.getDestination());
					costBack = 100;
					halfAskOnlinePacket.setValue(costBack+"");
					XmppTool.getConnection().sendPacket(halfAskOnlinePacket);
					logPrint("�ѻ���helpOnlineCost���ġ�");
					break;
				}
			case Constants.FORWARD_SCF:
				{
					logPrint("��MainService�յ�FORWARD_SCF����.");
					logPrint("�������ݣ�" + jso.toString());
					toastShow("��MainService�յ�FORWARD_SCF����", false);
					String passString = null;
					//�յ�passes��һ���б����Ǵ����ݿ�����+���ӵ��ַ���
					try {
						JSONArray passList = jso.getJSONArray("passes");
						for(int i=0; i<passList.length(); i++){
							if(i==0){
								passString=passList.getString(i);
							}else{
								passString = passString +"+"+passList.getString(i);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						offlineMessageDB.insertMessage(util.getName(), jso.getString("source"),
								jso.getString("destination"), jso.getLong("starttime"),
								jso.getInt("throwid"), jso.getLong("life"), jso.getString("value"),
								passString + "+" + util.getName(),
								System.currentTimeMillis(), "null");
					waitToRecDB.insertData(util.getName(), jso.getString("destination"),
							jso.getInt("throwid"), System.currentTimeMillis(), 1);
					myApplication.setIsSendOn(true);
					break;
				}
				case Constants.DIRECT_SCF:
				{
					logPrint("��MainService�յ�directSCF����: " + jso.toString());
					logPrint("�������ݣ�" + jso.toString());
					toastShow(
							"��MainService�յ�" + jso.getString("source") + "����Ϣ��"
									+ jso.getString("value"), false);
					ChatMsgEntity entity = new ChatMsgEntity(jso.getString("source"),
							MyDate.getDateEN(), jso.getString("value"), 2, true);
					messageDB.saveMsg(util.getName(), entity);
                    //���㲥��Dialogҳ��
                    Intent broadCast = new Intent();
                    broadCast.setAction(Constants.NORMAL_ACTION);
		            Bundle bundle = new Bundle(); 
		            bundle.putSerializable(Constants.NORMAL_MSGKEY, entity); 
		            broadCast.putExtras(bundle); 
                    sendBroadcast(broadCast);// ���յ�����Ϣ�ѹ㲥����ʽ���ͳ�ȥ
					logPrint("��MainService�д�����directSCF���ĵĴ洢��");
					break;
				}
				default:
					logPrint("��MainService�յ�δ֪���ģ�" + jso.toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean isNetworkAvailable()
	{
		ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);
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

	public short getMessageLength(String message)
	{
		return (short) message.getBytes().length;
	}

	public void logPrint(String log)
	{
		Constants.logPrint(log);
	}

	// Toast����ʾ����
	public void toastShow(String str, boolean bool)
	{
		if (!bool)
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
}
