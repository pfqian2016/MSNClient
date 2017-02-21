package scf.route.info;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.example.SCFData.SCFDataDBHelper;

public class ResolveBroadcastInfo {
        
        private Context context;
        private WifiP2pDevice p2pDevice;
        private Map<String, String> broadcastInfo = new HashMap<String, String>();
        private String toNextHopIntent;
        private SCFDataDBHelper scfDataDBHelper;
        public ResolveBroadcastInfo(Context context, WifiP2pDevice p2pDevice, Map<String, String> record) {
                this.context = context;
                this.p2pDevice = p2pDevice;
                this.broadcastInfo = record;
        }
        
        public String getToNextHopIntent() {
                scfDataDBHelper = new SCFDataDBHelper(context, "scfdata.db");
                SQLiteDatabase db = scfDataDBHelper.getWritableDatabase(); 
                Cursor cursor = db.rawQuery("select * from SCFDataTable where destination = ? ", new String[] {p2pDevice.deviceName}); 
                if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                                if (cursor.getString(cursor.getColumnIndex("destination") ).equalsIgnoreCase(p2pDevice.deviceName)) { 
                                        Log.d("destination is : ", cursor.getString(cursor.getColumnIndex("destination")) + 
                                                        ", source is : " + cursor.getString(cursor.getColumnIndex("source")) + 
                                                         ", dataName is : " + cursor.getString(cursor.getColumnIndex("dataName"))+
                                                         "size : " + cursor.getCount());  
                                        toNextHopIntent = String.valueOf(999);
                                        cursor.close();
                                        db.close();
                                        return toNextHopIntent;
                                    }
                        }                      
                }                
                cursor.close();
                db.close();
                toNextHopIntent = getIntent(); 
                return toNextHopIntent;
        }
        
        private String getIntent() {
                
                String userBroadcastInfo = broadcastInfo.get("");
               
                String[] userInfo = userBroadcastInfo.split(CollectSCFBroadcastInfo.TAG1);
                String[] personalInfo= userInfo[0].split(CollectSCFBroadcastInfo.TAG);
                int intent = 0;
                for (int i = 1; i < personalInfo.length - 1; i++) {
                        intent += Integer.parseInt(personalInfo[i]);
                } 
                if (personalInfo[5].equals("football")) {
                        intent += 10;
                } else if (personalInfo[5].equals("bastketball")) {
                        intent += 8;
                }                            
                String[] commNumber = userInfo[1].split(CollectSCFBroadcastInfo.TAG);
                for (int i = 0; i < commNumber.length; i++) {
                        intent += Integer.parseInt(commNumber[i].split("-")[1]);
                }           
                return intent + "";
        }
}

