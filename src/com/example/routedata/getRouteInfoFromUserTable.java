package com.example.routedata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

public class getRouteInfoFromUserTable implements Runnable {

        public static final String TAG1 = "#####";
        private String userName;
        private int owerNumber;
        private Handler handler;
        private String communicateNumber;
        private String interest;
        private SQLiteDatabase aDatabase;
        private RouteInfoDBHelper aRouteInfoDBHelper;   
        public getRouteInfoFromUserTable(Context context, Handler handler) {
                this.handler = handler;
                aRouteInfoDBHelper = new RouteInfoDBHelper(context, "routeInfo.db");
                aDatabase = aRouteInfoDBHelper.getReadableDatabase();
        }
        @Override
        public void run() { 
                String userSelectNextHopInfo = "";
                Cursor cursor = aDatabase.rawQuery("select * from UserRouteTable ", null);
                if (cursor.getCount() != 0 ) { 
                        while(cursor.moveToNext()){
                                userName = cursor.getString(cursor.getColumnIndex("userName"));
                                communicateNumber = cursor.getString(cursor.getColumnIndex("communicateNumber"));
                                owerNumber = cursor.getInt(cursor.getColumnIndex("OwnerNumber"));
                                interest = cursor.getString(cursor.getColumnIndex("interest"));
                                userSelectNextHopInfo += userName + "," + communicateNumber + "," + owerNumber + "," + interest + TAG1;
                        }
                } 
                handler.obtainMessage(0, userSelectNextHopInfo).sendToTarget(); 
                cursor.close();
                aDatabase.close();
        }

}
