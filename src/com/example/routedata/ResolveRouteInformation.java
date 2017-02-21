package com.example.routedata;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.example.SCFData.SCFDataDBHelper;
import com.example.android.wifidirect.discovery.WiFiServiceDiscoveryActivity;
/*
 * 该类是解析收集到的SCF路由数据，返回一Intent值，该值表示的是作为下一跳节点的想要程度
 */
public class ResolveRouteInformation {
 
        public static final int ROUTE_MESSAGE = 0x400 + 3;
        private  static final int TOP_INTENT = 100;
        private SCFDataDBHelper scfDataDBHelper; 
        private Context context;
        private WifiP2pDevice device; 
        private String toNextHopIntent = null;
        private Map<String, String> record = new HashMap<String, String>();
        
      
        
        public ResolveRouteInformation(Context context, WifiP2pDevice device, Map<String, String> record) {
                this.context = context;
                this.device = device;
                this.record = record;
        }
        
        /*
         * 返回一个作为下一跳节点的想要程度
         */        
        public String  nextHopIntent() {
                
                Log.d(WiFiServiceDiscoveryActivity.TAG, "get the device to the nextHopIntent");
                scfDataDBHelper = new SCFDataDBHelper(context, "scfdata.db");
                SQLiteDatabase db = scfDataDBHelper.getWritableDatabase(); 
                Cursor cursor = db.rawQuery("select * from SCFDataTable where destination = ? ", new String[] {device.deviceName}); 
                if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                                if (cursor.getString(cursor.getColumnIndex("destination") ).equalsIgnoreCase(device.deviceName)) { 
                                        Log.d("destination is : ", cursor.getString(cursor.getColumnIndex("destination")) + 
                                                        ", source is : " + cursor.getString(cursor.getColumnIndex("source")) + 
                                                         ", dataName is : " + cursor.getString(cursor.getColumnIndex("dataName"))+
                                                         "size : " + cursor.getCount());  
                                        toNextHopIntent = String.valueOf(TOP_INTENT);
                                        cursor.close();
                                        return toNextHopIntent;
                                    }
                        }                      
                }
                cursor.close();
                db.close();
                toNextHopIntent = getSelectNextHopInfo();
                Log.d(WiFiServiceDiscoveryActivity.TAG, "the device to intentToNextHop : " + toNextHopIntent);
                return toNextHopIntent; 
        }
        
        private String getSelectNextHopInfo(){
                
                String routeInfoToSelectNextHop = record.get("");
                Log.d(WiFiServiceDiscoveryActivity.TAG, "routeInfo : " + routeInfoToSelectNextHop);
                String[] routeInfo = routeInfoToSelectNextHop.split(DealUserRouteTable.TAG1);
                int intentToNextHop = 0;
                for (int i = 0; i < routeInfo.length; i++) {
                        String[] userRouteInfo = routeInfo[i].split(","); 
                        String communicateName = userRouteInfo[1].split("#")[0];
                        int commNumber = Integer.parseInt( userRouteInfo[1].split("#")[1] );
                        int ownerNumber = Integer.parseInt(userRouteInfo[2]);
                        if (communicateName.equals("test1") || userRouteInfo[3].equals("football")) {
                               intentToNextHop += 5 + commNumber + ownerNumber;
                               //Log.d("test intentToNextHop", "communicateNumber : " + Integer.parseInt(userRouteInfo[1].split("##")[1]) + "ownerNumber : " + Integer.parseInt(userRouteInfo[2]) );
                        } else {
                                intentToNextHop += 2 + Integer.parseInt( userRouteInfo[1].split("#")[1] ) + Integer.parseInt(userRouteInfo[2]) ;
                                //Log.d("test intentToNextHop", "communicateNumber : " + Integer.parseInt(userRouteInfo[1].split("##")[1]) + "ownerNumber : " + Integer.parseInt(userRouteInfo[2]) );
                        }
                }                
                
                return intentToNextHop + "";
        }
               
}