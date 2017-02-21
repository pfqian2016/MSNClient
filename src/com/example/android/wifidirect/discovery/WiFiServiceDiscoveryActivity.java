
package com.example.android.wifidirect.discovery;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.mobilesocietynetwork.client.R;
import com.example.android.wifidirect.discovery.WiFiChatFragment.MessageTarget;
import com.example.android.wifidirect.discovery.WiFiDirectServicesList.DeviceClickListener;
import com.example.android.wifidirect.discovery.WiFiDirectServicesList.WiFiDevicesAdapter;
import com.example.cpr.data.BecomeNormalNode;
import com.example.cpr.data.SCFdataLable;
import com.example.cpr.route.CalculateOtherNodeUtility;
import com.example.cpr.route.CompareUtility;
import com.example.cpr.route.ConvertTimetoCurrentTS;
import com.example.cpr.route.GetContactLogInfomation;
import com.example.cpr.route.ParseReceiveNodeContactInfomation;
import com.example.routedata.SelectNextHop;
import com.example.routedata.TimeThread;


public class WiFiServiceDiscoveryActivity extends Activity implements
        DeviceClickListener, Handler.Callback, MessageTarget,
        ConnectionInfoListener, GroupInfoListener {

    public static final String ACTION_SEND_SCFDATA_HEAD_START = "SEND_SCFDATA_HEAD_START"; 
    public static final String ACTION_SEND_SCFDATA_HEAD_END = "SEND_SCFDATA_HEAD_END";
    public static final String TAG = "wifidirectdemo"; 
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_wifidemotest";
    public static final String SERVICE_REG_TYPE = "_presence._tcp"; 
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    public static final int ROUTE_MESSAGE = 0x400 + 3;
    public static final int SOURCEINFO = 0x400 + 4; 
    public static final int DEALROUTEINFO = 0x400 + 13;
    static final int SERVER_PORT = 4545;  
    private int findDevice = 0; 
    private boolean SCF_CONNECT = false;    
    private String nextHopDeviceName = ""; 
    private String source = "test";
    private String contactUsername;
    private final String ownLable = "LB2"; 
    private String contactLable = "LB3";
    private String SCFDataname = null;   
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest; 
    private String lastWeekContactInfo = null;
    private Timer timer = null;
    
    private Handler handler = new Handler(this);
    private WiFiChatFragment chatFragment; 
    private WiFiDirectServicesList servicesList;   
    private TextView statusTxtView; 
    private Map<WifiP2pDevice, Double> nextHop = new HashMap<WifiP2pDevice, Double>(); 
    private Calendar calendar = Calendar.getInstance();
    
    @SuppressLint("HandlerLeak")
	private Handler handlerTime = new Handler() {
                        
        public void handleMessage (Message msg) {  
                                    
            //Log.d(TAG, "total recv device size : " + nextHop.size()); 
            findDevice = 0;
            SelectNextHop selectNextHop = new SelectNextHop(nextHop);
            WifiP2pDevice p2pDevice = selectNextHop.getTargetDevice(); 
            for (WifiP2pDevice p2pDevice2 : nextHop.keySet() ) {
                if (p2pDevice2.deviceName.equals(p2pDevice.deviceName)) {
                        connectP2p(p2pDevice);
                        break;
                }                        
            }
            ConvertTimetoCurrentTS convert = new ConvertTimetoCurrentTS(calendar);
			int currentTS = convert.getCurrentTS();
			CompareUtility compareUtility = new CompareUtility(getApplicationContext(), lastWeekContactInfo);
			if (compareUtility.isBetterNode(currentTS)) {
				BecomeNormalNode normalNode = new BecomeNormalNode(getApplicationContext(), SCFDataname);
				normalNode.start();
			} 
        }
    };
     
    @SuppressLint("HandlerLeak")
	private Handler timerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) { 
			//清除已注册的local service
			manager.clearLocalServices(channel, new ActionListener() {
				
				@Override
				public void onSuccess() {
					System.out.println("clear local service success");
				}
				
				@Override
				public void onFailure(int reason) {
					System.out.println("fail clear local service");
				}
			});
			//通过定时器，更新通信记录
			startRegistrationAndDiscovery();	
		} 
    };
    
    public Handler getHandler() {
            return handler;
    }

    public void setHandler(Handler handler) {
            this.handler = handler;
    }
    
    public void setSource(String source) {
            this.source = source; 
    }
  
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
            
        super.onCreate(savedInstanceState);
//        ActionBar actionBar = getActionBar();  
//        actionBar.setLogo(R.drawable.ic_launcher);  
//        actionBar.setDisplayUseLogoEnabled(true);  
//        actionBar.setDisplayShowHomeEnabled(true);  
       requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        statusTxtView = (TextView) findViewById(R.id.status_text);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);  
        startRegistrationAndDiscovery();  
        discoverService(); 
        
        servicesList = new WiFiDirectServicesList();
        getFragmentManager().beginTransaction().add(R.id.container_root, servicesList, "services").commit();
        super.onStart();
              
    }
 

    @Override
    protected void onRestart() {
        Fragment frag = getFragmentManager().findFragmentByTag("services");
        if (frag != null) {
            getFragmentManager().beginTransaction().remove(frag).commit();
        }
        super.onRestart();
    }
 
    
    @Override
    protected void onDestroy() {
            
        if (manager != null && channel != null) {
                
            manager.removeGroup(channel, new ActionListener() {
                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);                            
                } 
            
                @Override
                public void onSuccess() {
                    Log.d(TAG, "removeGroup success");
                }
            });
        }
        super.onDestroy();
    }

 
    private void startRegistrationAndDiscovery() { 
    	
    	Map<String, String> record = new HashMap<String, String>();   
        GetContactLogInfomation contactInfomation = new GetContactLogInfomation(WiFiServiceDiscoveryActivity.this);
        String contactLog = contactInfomation.getLastWeek();
        record.put("", contactLog);   
        Log.d(TAG, "testUserRouteInfo : " + contactLog);
        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance( SERVICE_INSTANCE, SERVICE_REG_TYPE, record); 
        //(2),鏀跺埌discovery涔嬪悗绔嬪嵆鍝嶅簲
        manager.addLocalService(channel, service, new ActionListener() {
            @Override
            public void onSuccess() {
//                appendStatus("Added Local Service");
            }
            @Override
            public void onFailure(int error) {
//                appendStatus("Failed to add a service");
            }
        });
        //discoverService();     
        timer = new Timer(timerHandler);
        timer.start();
    }

    private void discoverService() { 
            
        manager.setDnsSdResponseListeners(channel, new DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                     
                if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                    WiFiDirectServicesList fragment = (WiFiDirectServicesList) 
                    		getFragmentManager().findFragmentByTag("services");
                    if (fragment != null) { 
                        WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment.getListAdapter());
                        WiFiP2pService service = new WiFiP2pService();
                        service.device = srcDevice;
                        service.instanceName = instanceName;
                        service.serviceRegistrationType = registrationType;
                        adapter.add(service.device);
                        adapter.notifyDataSetChanged(); 
                    }
                }
            }
        }, new DnsSdTxtRecordListener() {  
                
                @Override
                public void onDnsSdTxtRecordAvailable( String fullDomainName, Map<String, String> record, WifiP2pDevice device) {  
 
                	 Log.d(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + record.get(""));
                     if (record.get("") != null) {
                    	 //将收到的内容进行解析，只有节点的LB是SCF数据LB的其中之一菜进行并且具有比本节点更高的可能性传输到目的节点才进行转发
                    	 receiveUserBroadcastInfo();
                         ParseReceiveNodeContactInfomation otherNodeInformation = 
                         		new ParseReceiveNodeContactInfomation( getApplicationContext(), record); 
                         
                         SCFdataLable scf = new SCFdataLable(getApplicationContext(), "data.db");
                         SQLiteDatabase db = scf.getWritableDatabase();
                         Cursor cursor = db.rawQuery("select * from dataLableTable", null);
                         if (cursor.getCount() != 0) {//show the device having scf data
                            cursor.moveToNext(); 
                            contactLable = otherNodeInformation.getContactLB();				//get will contact lable
                            contactUsername = otherNodeInformation.getOtherUsername();	    //get will contact username 
                         	 
                         	SCFDataname = cursor.getString(cursor.getColumnIndex("dataName"));
                         	System.out.println("onDnsSdTxtRecordAvailable SCFDataname:" + SCFDataname);
                         	if (otherNodeInformation.isDestination()) {		//发送contact infomation 的节点是目的节点
         						//直接连接节点，发送data数据 
                         		System.out.println("otherNodeInformation.isDestination()");
                             	connectP2p(device);
                             	BecomeNormalNode normalNode = new BecomeNormalNode(getApplicationContext(), SCFDataname);
                             	normalNode.start();
                             	SCF_CONNECT = true;
         					} else if (otherNodeInformation.isContainsLB()) {	//发送contact infomation的节点具有数据的lable
         						//计算其probability 和 utility，

                             	SCF_CONNECT = true;
//         						Log.d("*********", "otherNodeInformation.isContainsLB");
         						lastWeekContactInfo = otherNodeInformation.getLastContactInformation();      						
         						CalculateOtherNodeUtility otherNode = new CalculateOtherNodeUtility(
         																getApplicationContext(), lastWeekContactInfo);	
         		                ConvertTimetoCurrentTS convert = new ConvertTimetoCurrentTS(calendar);
         						int currentTS = convert.getCurrentTS();
         						double value = otherNode.getUtilityvalue(currentTS);
         						System.out.println("test on onDnsSdTxtRecordAvailable value:" + value);
         						
         						nextHop.put(device, Double.valueOf(value)); 
         						findDevice++;
         						if (findDevice == 1) {  
         							TimeThread timeThread = new TimeThread(handlerTime);
         							timeThread.start();
         						} 
         					} else {								//发送 contact infomation不具有数据的lable
         						//忽略该节点，不进行任何处理
         						Log.d("************", "information lable not include contact lable");
         					} 
     					}cursor.close();//yjy add
					}
                    
                }
            } );
        
        // After attaching listeners, create a service request and initiate discovery. 
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,new ActionListener() {
            @Override
            public void onSuccess() {
                appendStatus("Added service discovery request"); 
            }

            @Override
            public void onFailure(int arg0) {
                appendStatus("Failed adding service discovery request");
            }
        }); 
        
        manager.discoverServices(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                appendStatus("Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                appendStatus("Service discovery failed");
            }
        }); 
    }

    public String receiveUserBroadcastInfo() {
		String userBroadcastInfo = null;
		 
    	return userBroadcastInfo;
    }
    @Override
    public void connectP2p(WifiP2pDevice p2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();  
        config.deviceAddress = p2pDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;  
        
        if (serviceRequest != null) {
                
            manager.removeServiceRequest(channel, serviceRequest, new ActionListener() {
                    
                @Override
                public void onSuccess() {
                }
                @Override
                public void onFailure(int arg0) {
                }
            });
        }      
               
        manager.connect(channel, config, new ActionListener() { 
                
            @Override
            public void onSuccess() {
                appendStatus("Connecting to service");
            }

            @Override
            public void onFailure(int errorCode) {
                appendStatus("Failed connecting to service");
            }
        });
    }

    
    @Override
    public boolean handleMessage(Message msg) {
            
        switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj; 
                String readMessage = new String(readBuf, 0, msg.arg1);  
                int start = readMessage.indexOf(WiFiChatFragment.ACTION_SEND_TXT_HEAD_END) + WiFiChatFragment.ACTION_SEND_TXT_HEAD_END.length();
                int end = readMessage.indexOf(WiFiChatFragment.ACTION_SEND_TXT_END);
                readMessage = readMessage.substring(start, end);
                readMessage = readMessage.replace("?", " : ");
                (chatFragment).pushMessage(readMessage);
                break;            
            case MY_HANDLE:
                Object obj = msg.obj;
                (chatFragment).setChatManager( (ChatManager) obj);
                break;        
        }
        return true;
    }

    @Override
    public void onResume() {            
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter); 
    }

    @Override
    public void onPause() {            
        super.onPause();
        unregisterReceiver(receiver);
    }
     
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) { 
    	 
        Thread handler = null;   
        if (p2pInfo.isGroupOwner) {          
            Log.d(TAG, "Connected as group owner");    
            try { 
                handler = new GroupOwnerSocketHandler( WiFiServiceDiscoveryActivity.this,  
                		( (MessageTarget) this).getHandler(), source, ownLable, contactLable, SCFDataname, SCF_CONNECT);
                handler.start(); 
            } catch (IOException e) {
                Log.d(TAG, "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {  
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler( WiFiServiceDiscoveryActivity.this, ( (MessageTarget) this).getHandler(), 
            		p2pInfo.groupOwnerAddress, source, ownLable, contactLable, SCFDataname, SCF_CONNECT);
            handler.start();  
        }
        SCF_CONNECT = false;
        //System.out.println("test nextHopDeviceName:" + nextHopDeviceName);
        if (chatFragment == null) {
            chatFragment = new WiFiChatFragment(source, nextHopDeviceName); 
            getFragmentManager().beginTransaction().replace(R.id.container_root, chatFragment, "chatService").commit();
        } else {
            Fragment frag = getFragmentManager().findFragmentByTag("chatService"); 
            getFragmentManager().beginTransaction().show(frag); 
        } 
        
        statusTxtView.setVisibility(View.GONE);
    } 
    
    public void appendStatus(String status) { 
        String current = statusTxtView.getText().toString();
        statusTxtView.setText(current + "\n" + status);
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
            
        WifiP2pDevice p2pDevice = null;
        p2pDevice = group.getOwner(); 
        
        Collection<WifiP2pDevice> clients = new HashSet<WifiP2pDevice>();
        clients = group.getClientList(); 
        Iterator<WifiP2pDevice> it = clients.iterator();
        while (it.hasNext()) {
          p2pDevice = it.next(); 
        }          
        nextHopDeviceName = p2pDevice.deviceName;
    }
}
