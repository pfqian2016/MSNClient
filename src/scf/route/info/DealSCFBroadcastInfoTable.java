package scf.route.info;
 
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DealSCFBroadcastInfoTable implements Runnable {
        
        private String userName;
        private int communicatedUsers;
        private int ownerNumber;
        private int forwardingNumber;
        private int scannedUser;
        private String interests;
        private String communicatedNumber;  
        private String commName;
        private String userBroadcastInfo;
        private boolean isExitingCommname = false; 
        private SCFBroadcastInfoDBHelper aBroadcastInfoDBHelper;
        public static final String ACTION_BROADCAST_INFO_LIMIT = "BROADCAST_INFO_LIMIT";
        public static final String ACTION_COMMUNICATED_NUMBER_LIMIT = "COMMUNICATED_NUMBER_LIMIT";
        public DealSCFBroadcastInfoTable (Context context, String userBroadcastInfo) {
                this.userBroadcastInfo = userBroadcastInfo;
                aBroadcastInfoDBHelper = new SCFBroadcastInfoDBHelper(context, "BroadcastInfo.db");
        }

        @Override
        public void run() { 
                //user broadcast info only one row
                broadcastInfoInit();
                SQLiteDatabase aDatabase = aBroadcastInfoDBHelper.getWritableDatabase(); 
                Cursor cursor = aDatabase.rawQuery("select * from UserBroadcastInfo", null);
                commName = communicatedNumber.split("-")[0];
                int commNum = Integer.parseInt(communicatedNumber.split("-")[1]);
                if (cursor.getCount() != 0 ) { 
                        //已经具有数据表格则更新数据表                         
                        while (cursor.moveToNext()) {
                                String existingCommName = cursor.getString(cursor.getColumnIndex("CommunicatedNumber"));
                                if (existingCommName.indexOf(commName) != -1) {
                                        //update
                                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                                        int users = cursor.getInt(cursor.getColumnIndex("CommunicatedUsers"));
                                        int owner = cursor.getInt(cursor.getColumnIndex("OwnerNumber"));
                                        int forwarding = cursor.getInt(cursor.getColumnIndex("ForwardingNumber"));
                                        int scan = cursor.getInt(cursor.getColumnIndex("ScannedUser"));
                                        int commNumber = Integer.parseInt(existingCommName.split("-")[1]);
                                        aDatabase.execSQL("update UserBroadcastInfo set CommunicatedUsers = ?, OwnerNumber = ?, ForwardingNumber= ?, " +
                                        		"ScannedUser = ?, CommunicatedNumber = ?  where id = ?",  
                                        		new Object[] { users + communicatedUsers, owner + ownerNumber, forwarding + forwardingNumber,
                                                        scan + scannedUser, commName + "-"+(commNum + commNumber), id} );
                                        isExitingCommname = true; 
                                        break;
                                } 
                        } 
                }  
                
                if ( ! isExitingCommname ) { 
                        aDatabase.execSQL("insert into UserBroadcastInfo (id, UserName, CommunicatedUsers, OwnerNumber, ForwardingNumber, " +
                                "ScannedUser, Interests, CommunicatedNumber ) values (null, ?, ?, ?, ?, ?, ?, ?)",
                                new Object[] {userName, communicatedUsers, ownerNumber, forwardingNumber, scannedUser, interests, communicatedNumber});
                }
                cursor.close();
                aDatabase.close();
        }

        public void broadcastInfoInit() {
                String[] personalInfo = userBroadcastInfo.split(ACTION_BROADCAST_INFO_LIMIT); 
                userName = personalInfo[0];
                communicatedUsers = Integer.parseInt(personalInfo[1]);
                ownerNumber = Integer.parseInt(personalInfo[2]);
                forwardingNumber = Integer.parseInt(personalInfo[3]);
                scannedUser = Integer.parseInt(personalInfo[4]);
                interests = personalInfo[5];
                communicatedNumber = personalInfo[6];
        }
}
