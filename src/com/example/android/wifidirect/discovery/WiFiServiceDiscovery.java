package com.example.android.wifidirect.discovery;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.R.integer;
import android.app.Activity; 
import android.content.Context; 
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest; 
import android.util.Log;

public class WiFiServiceDiscovery extends Activity{
	 
	 
	private WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
  	private Channel channel = manager.initialize(this, getMainLooper(), null);
  	public void registerUserBroadcastInfo(WifiP2pDnsSdServiceInfo service) {
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
    }
	 
	 
	 
	 public void discoverService(){
    	 
	    	WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
	    	
	         manager.addServiceRequest(channel, serviceRequest,
	                 new ActionListener() {

	                     @Override
	                     public void onSuccess() {
//	                         appendStatus("Added service discovery request");
	                     }

	                     @Override
	                     public void onFailure(int arg0) {
//	                         appendStatus("Failed adding service discovery request");
	                     }
	                 });
	         manager.discoverServices(channel, new ActionListener() {

	             @Override
	             public void onSuccess() {
//	                 appendStatus("Service discovery initiated");
	             }

	             @Override
	             public void onFailure(int arg0) {
//	                 appendStatus("Service discovery failed");

	             }
	         });
	    }
	
	 private String contactLog = null;
	    public String receiveUserBroadcastInfo(){
//	    	String contactLog = null;
	    	manager.setDnsSdResponseListeners(channel,
	    			new DnsSdServiceResponseListener() {

	    		@Override
	    		public void onDnsSdServiceAvailable(String instanceName,
	    				String registrationType, WifiP2pDevice srcDevice) {


	    		}
	    	}, new DnsSdTxtRecordListener() {

	    
	    		@Override
	    		public void onDnsSdTxtRecordAvailable( String fullDomainName, Map<String, String> record, WifiP2pDevice device) {
	    			if (record.get("") != null) {
						contactLog = record.get("");
						
					}
	    		
	    		}
	    	});
			return contactLog;
	    	
	    }
	
	 public WifiP2pDevice selectBestNextHop(Map<WifiP2pDevice, Double> neighborNode) {
		  
    	 WifiP2pDevice p2pDevice = null;
		 double max = 0; 
	     double temp = 0;
	     for (Double value : neighborNode.values() ) {
	        temp = value;
	        if  (value >= max) {
	           max = value;
	        }                                
	     }
		 Log.d(WiFiServiceDiscoveryActivity.TAG, "test nextHopMap the max : " + max);
		 String nextHop = String.valueOf(max);
		 Set<WifiP2pDevice> keySet = neighborNode.keySet();  
		 Iterator<WifiP2pDevice> it = keySet.iterator();
		 while (it.hasNext()) {
		      
		    p2pDevice = (WifiP2pDevice) it.next(); 
		    if (nextHop.equals(neighborNode.get(p2pDevice))) {
		        break; 
		    } 
	     }
		 Log.d(WiFiServiceDiscoveryActivity.TAG,"test selected deviceName : " +  p2pDevice.deviceName);
    	 return p2pDevice;
    }
	 
}
