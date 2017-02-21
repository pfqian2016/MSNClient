package com.example.routedata;

import com.example.android.wifidirect.discovery.WiFiServiceDiscoveryActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ExtractRouteInforFromUsertable {

        private String userName;
        private int owerNumber;
        private String communicateNumber;
        private String interest;
        private SQLiteDatabase aDatabase;
        private RouteInfoDBHelper aRouteInfoDBHelper;   
        public ExtractRouteInforFromUsertable(Context context){ 
                aRouteInfoDBHelper = new RouteInfoDBHelper(context, "routeInfo.db");
                aDatabase = aRouteInfoDBHelper.getReadableDatabase();
        }
        public String getSelectNextHopInfo() {
                String userSelectNextHopInfo = "";
                Cursor cursor = aDatabase.rawQuery("select * from UserRouteTable ", null);
                Log.d(WiFiServiceDiscoveryActivity.TAG, "test UserRouteTable cursor.getCount : " + cursor.getCount());
                if (cursor.getCount() != 0 ) { 
                        while(cursor.moveToNext()) {
                                userName = cursor.getString(cursor.getColumnIndex("userName"));
                                communicateNumber = cursor.getString(cursor.getColumnIndex("communicateNumber"));
                                owerNumber = cursor.getInt(cursor.getColumnIndex("OwnerNumber"));
                                interest = cursor.getString(cursor.getColumnIndex("interest"));
                                userSelectNextHopInfo += userName + "," + communicateNumber + "," + owerNumber + "," + interest + DealUserRouteTable.TAG1;
                        }
                }  
                Log.d(WiFiServiceDiscoveryActivity.TAG, "userSelectNextHopInfo from userrouteTAble:" + userSelectNextHopInfo);
                cursor.close();
                aDatabase.close();
                return userSelectNextHopInfo;
        }
        
}
