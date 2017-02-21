package com.example.SCFData;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DealSCFDataTableThread implements Runnable {

        public static final String TAG2 = "######";
        public static final int RECVSCFDATA = 0x400 + 10;
        public static final int SENDSCFDATA = 0x400 + 11;
        private SCFDataDBHelper scfDataDBHelper; 
        private String SCFDataInfor;
        private String destination;
        private String source;
        private String dataName;
        private String time;
        private int transmitNumber;
        private int Tag;
        public DealSCFDataTableThread(Context context, String SCFData) {
                this.scfDataDBHelper = new SCFDataDBHelper(context, "scfdata.db");  
                this.SCFDataInfor = SCFData;                
        }
        
        @Override
        public void run() {
                init(); 
                switch (Tag) {
                
                        case RECVSCFDATA:
                                try {
                                        SQLiteDatabase db = scfDataDBHelper.getWritableDatabase();    
                                        Cursor cursor = db.rawQuery("select * from SCFDataTable where dataName = ? and destination = ? and source = ?",
                                                        new String[] {dataName, destination,source});
                                        if (cursor.getCount() == 0) {
                                                db.execSQL("insert into SCFDataTable (id, destination, source, time,  transmitNumber, dataName)" +
                                                                " values ( null, ?, ?, ?, ?, ?)", 
                                                                new Object[] {destination, source, time, 3, dataName } );
                                               Log.d(TAG2, "insert into SCFDataTable finifsh");
                                        } else {
                                                //如果有相同的文件，则删除之前的文件再插入新接收的文件
                                                db.execSQL("delete from SCFDataTable where dataName = ? and destination = ? and source = ?", 
                                                                new String[]{ dataName, destination,source}); 
                                                db.execSQL("insert into SCFDataTable (id, destination, source, time,  transmitNumber, dataName)" +
                                                                " values ( null, ?, ?, ?, ?, ?)", 
                                                                new Object[] {destination, source, time, 3, dataName } );
                                                Log.d(TAG2, "have had the scf data");
                                        }
                                        cursor.close();
                                        db.close();
                                } catch (Exception e) {
                                        Log.d(TAG2, "insert into SCFDataTable fail");
                                }                                
                                break;                
                        case SENDSCFDATA:
                                try {
                                        
                                        SQLiteDatabase db = scfDataDBHelper.getWritableDatabase();
                                        Cursor cursor = db.rawQuery("select * from SCFDataTable where dataName = ? and destination = ? and source = ?", 
                                                        new String[]{dataName, destination,source});
                                        //Log.d(TransSCFData.TAG, "destination : " + destination + ",  dataName : " + dataName + ", size : " + cursor.getCount());
                                        while (cursor.moveToNext()) {
                                                transmitNumber = cursor.getInt(cursor.getColumnIndex("transmitNumber"));
                                                db.execSQL("update SCFDataTable set transmitNumber = ? where dataName = ? and destination = ? and source = ?", 
                                                                new Object[]{transmitNumber-1, dataName, destination,source});
                                                Log.d(TransSCFData.TAG, "update into SCFDataTable finish");
                                                if ( (transmitNumber - 1) <=  0) {
                                                        db.execSQL("delete from SCFDataTable where dataName = ? and destination = ? and source = ?", 
                                                                        new String[]{ dataName, destination,source}); 
                                                        Log.d(TransSCFData.TAG, "deiete SCFDataTable finish");
                                                }
                                        }        
                                        cursor.close();
                                        db.close();
                                } catch (Exception e) {
                                        Log.d(TransSCFData.TAG, "update SCFDataTable fail");
                                }
                                break;
                }
                
                SQLiteDatabase db = scfDataDBHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from SCFDataTable ", null);
                if ( cursor.getCount() !=  0 ) {
                        Log.d(TransSCFData.TAG, "scfdataTable getCount : " +  cursor.getCount());
                        while (cursor.moveToNext() ) {
                        Log.d("test scf dataTable", "destination : " + cursor.getString(cursor.getColumnIndex("destination")) + 
                                                                                " ,source : " + cursor.getString(cursor.getColumnIndex("source")) + 
                                                                                " ,time : " + cursor.getString(cursor.getColumnIndex("time")) + 
                                                                                " ,transmitNumber" + cursor.getInt(cursor.getColumnIndex("transmitNumber")) + 
                                                                                " ,dataName : " + cursor.getString(cursor.getColumnIndex("dataName")) );
                      }  
                }
                cursor.close();
                db.close();
        }
        
        public void init() {
                this.destination = SCFDataInfor.split(TAG2)[0];
                this.source = SCFDataInfor.split(TAG2)[1];
                this.time = SCFDataInfor.split(TAG2)[2] ;
                this.Tag = Integer.parseInt(SCFDataInfor.split(TAG2)[3] );
                this.dataName = SCFDataInfor.split(TAG2)[4];
        }

}
