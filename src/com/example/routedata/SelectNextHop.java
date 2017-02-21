package com.example.routedata;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.example.android.wifidirect.discovery.WiFiServiceDiscoveryActivity;

import android.net.wifi.p2p.WifiP2pDevice; 
import android.util.Log;
/*
 * 鏍规嵁涓�涓狹ap<device,intent>缁撴瀯涓殑intent鍊硷紝鍊艰秺澶ц〃绀烘垚涓轰笅涓�璺宠妭鐐圭殑鍙兘鎬ц秺楂�
 * 閫夋嫨鍊兼渶澶ф墍瀵瑰簲鐨刣evice杩斿洖锛屼互渚垮悗缁繘琛岀浉搴旂殑杩炴帴鎿嶄綔
 */
public class SelectNextHop{
        
        private Map<WifiP2pDevice, Double> nextHopMap = new HashMap<WifiP2pDevice, Double>();
        private WifiP2pDevice p2pDevice;   
         
        public SelectNextHop(Map<WifiP2pDevice, Double> nextHopMap) {
			// TODO Auto-generated constructor stub
        	this.nextHopMap = nextHopMap;
		}

		public WifiP2pDevice getTargetDevice() {  
			
		    double max = 0; 
		    double temp = 0;
		    for (Double value : nextHopMap.values() ) {
		        temp = value;
		        if  (value >= max) {
		           max = value;
		        }                                
		    }
			Log.d(WiFiServiceDiscoveryActivity.TAG, "test nextHopMap the max : " + max);
			String nextHop = String.valueOf(max);
			Set<WifiP2pDevice> keySet = nextHopMap.keySet();  
			Iterator<WifiP2pDevice> it = keySet.iterator();
			while (it.hasNext()) {
			      
			    p2pDevice = (WifiP2pDevice) it.next(); 
			    if (nextHop.equals(nextHopMap.get(p2pDevice))) {
			        break; 
			    } 
			}
			Log.d(WiFiServiceDiscoveryActivity.TAG,"test selected deviceName : " +  p2pDevice.deviceName);
			return p2pDevice;                                              
        }
 
}
