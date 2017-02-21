package com.example.SCFData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SCFDataDBHelper extends SQLiteOpenHelper {
      //数据库版本
        private static final int VERSION = 1;
        //新建一个表
        String SCFDatatable = "create table if not exists SCFDataTable"+
                        "( id integer primary key,  " +
                        "destination varchar(20), " +
                        "source varchar(20), " +
                        "time verchar(20), " +
                        "transmitNumber integer, " +
                        "dataName varchar(20) )";

        public SCFDataDBHelper(Context context, String name,
                        CursorFactory factory, int version) {
                super(context, name, factory, version); 
        }

        public SCFDataDBHelper(Context context, String name, int version ) {
                this(context, name, null, version);
        }
        
        public SCFDataDBHelper(Context context, String name) {
                this(context, name, null, VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) { 
                db.execSQL(SCFDatatable);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
                Log.d("update", "update database");
        } 
}
