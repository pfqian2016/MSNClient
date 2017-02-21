package scf.route.info;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SCFBroadcastInfoDBHelper extends SQLiteOpenHelper {
        
        private static final int VERSION = 1;
        
        String scfRouteInfoTable = "create table if not exists UserBroadcastInfo" + 
                                                                                "( id integer primary key, " +  
                                                                                "UserName varchar(20), " + 
                                                                                "CommunicatedUsers integer, " +
                                                                                "OwnerNumber integer, " + 
                                                                                "ForwardingNumber integer, " + 
                                                                                "ScannedUser integer,  " + 
                                                                                "Interests varchar(20), " + 
                                                                                "CommunicatedNumber varchar(20) )";
        
        public SCFBroadcastInfoDBHelper(Context context, String name,
                        CursorFactory factory, int version) {
                super(context, name, factory, version); 
        }
        
        public SCFBroadcastInfoDBHelper(Context context, String name) {
                super(context, name, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) { 
                db.execSQL(scfRouteInfoTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
                System.out.println("update DBHelper");
        }

}
