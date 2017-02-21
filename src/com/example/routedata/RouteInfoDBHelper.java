package com.example.routedata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RouteInfoDBHelper extends SQLiteOpenHelper{

        private static final int VERSION = 1;
        String routeInfoTable = "create table if not exists UserRouteTable"+
                        "( id integer primary key,  " +
                        "userName varchar(20), " +
                        "communicateNumber varchar(20)," +
                        "OwnerNumber integer, "+
                        "interest varchar(20))";
        
        public RouteInfoDBHelper(Context context, String name,
                        CursorFactory factory, int version) {
                super(context, name, factory, version); 
        }

        public RouteInfoDBHelper(Context context, String name) {
                super(context, name, null, VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) { 
                db.execSQL(routeInfoTable);
                
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
                System.out.println("update DBhelper!");                
        }
        
}
