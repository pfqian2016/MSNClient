package scf.route.info;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.example.android.wifidirect.discovery.WiFiServiceDiscoveryActivity;

import android.net.wifi.p2p.WifiP2pDevice; 
import android.util.Log;
/*
 * 根据一个Map<device,intent>结构中的intent值，值越大表示成为下一跳节点的可能性越高
 * 选择值最大所对应的device返回，以便后续进行相应的连接操作
 */
public class SelectNextHop{
        
        private Map<WifiP2pDevice, String> nextHopMap = new HashMap<WifiP2pDevice, String>();
        private WifiP2pDevice p2pDevice;  
        
        public SelectNextHop(Map<WifiP2pDevice, String> nextHopMap) {
                this.nextHopMap = nextHopMap;
        }
         
        public WifiP2pDevice getTargetDevice() {  
               int max = 0; 
               int temp = 0;
                for (String value : nextHopMap.values() ) {
                        temp = Integer.parseInt(value);
                        if  (Integer.parseInt(value) >= max) {
                                max = Integer.parseInt(value);
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
