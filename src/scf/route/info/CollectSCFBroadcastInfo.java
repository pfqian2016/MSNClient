package scf.route.info;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*
 *draw the scfbroadcastinfo from the  SCFBroadcastInfotable
 */

public class CollectSCFBroadcastInfo {
        
        private String userName;
        private int communicatedUsers = 0;
        private int ownerNumber = 0;
        private int forwardingNumber = 0;
        private int scannedUser = 0;
        private String interests;
        private String communicatedNumber = "";  
        private String userBroadcastInfo = ""; 
        public static final String TAG = "#";
        public static final String TAG1 = "@";
        private SCFBroadcastInfoDBHelper aBroadcastInfoDBHelper;
        private SQLiteDatabase aSqLiteDatabase;
        public CollectSCFBroadcastInfo(Context context) {
               this.aBroadcastInfoDBHelper = new SCFBroadcastInfoDBHelper(context, "BroadcastInfo.db");
               aSqLiteDatabase = aBroadcastInfoDBHelper.getReadableDatabase();
        }
        
        public String draw () { 
                Cursor cursor = aSqLiteDatabase.rawQuery("select * from UserBroadcastInfo", null); 
                if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {          
                                communicatedUsers += cursor.getInt(cursor.getColumnIndex("CommunicatedUsers"));
                                ownerNumber += cursor.getInt(cursor.getColumnIndex("OwnerNumber"));
                                forwardingNumber += cursor.getInt(cursor.getColumnIndex("ForwardingNumber"));
                                scannedUser += cursor.getInt(cursor.getColumnIndex("ScannedUser"));
                                //communicatedNumber = communicatedNumber + cursor.getString(cursor.getColumnIndex("CommunicatedNumber"));
                                communicatedNumber = communicatedNumber + cursor.getString(cursor.getColumnIndex("CommunicatedNumber")) + TAG;
                                if (cursor.isLast()) {
                                        userName = cursor.getString(cursor.getColumnIndex("UserName"));
                                        interests = cursor.getString(cursor.getColumnIndex("Interests"));
                                        //communicatedNumber = communicatedNumber + cursor.getString(cursor.getColumnIndex("CommunicatedNumber"));
                                        userBroadcastInfo = userName + TAG + communicatedUsers + TAG + ownerNumber  + TAG +
                                                        forwardingNumber + TAG +  scannedUser + TAG + interests  + TAG1 + communicatedNumber;
                                        break;
                                }                                                                                   
                        }
                }
                cursor.close();
                aSqLiteDatabase.close();
                return userBroadcastInfo;
        }

}
