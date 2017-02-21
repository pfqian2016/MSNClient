package com.example.SCFData;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.android.wifidirect.discovery.WiFiChatFragment;
import com.example.android.wifidirect.discovery.WiFiServiceDiscoveryActivity;

public class TransSCFData {
        
        public static final String TAG = "TransSCFData"; 
        private SCFDataDBHelper scfDataDBHelper;
        private DealSCFDataTableThread dealSCFData;
        private Socket socket;
        private Context context; 
        private String destination;  
        private String source;
        private String dataName;
        private String nextHop;
        public TransSCFData(Context context,Socket socket, String nextHop) {
                this.context = context;
                this.socket = socket; 
                this.nextHop = nextHop;
        }
        
        public void transferSCFData() { 
                scfDataDBHelper = new SCFDataDBHelper(context, "scfdata.db");
                SQLiteDatabase db = scfDataDBHelper.getWritableDatabase();
                Cursor cursor = db.rawQuery("select * from SCFDataTable ", null); 
                
                while (cursor.moveToNext()) {
                       //可以从scfdata数据表中选择合适的文件名来传输
                        try {
                                FileInputStream fis = null;
                                OutputStream outputStream = socket.getOutputStream();
                                int len = 0;
                                byte buf[] = new byte[1024*8];      
                                 if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED) ) {
                                         File  sdCardDir  =  Environment.getExternalStorageDirectory ();   
                                         fis  =  new  FileInputStream(sdCardDir.getCanonicalPath() + "/SCF"+"/" + cursor.getString(5));  
                                 } else {
                                         Log.d(TAG, "open scfdata failer");
                                 }   
                                 
                                destination = cursor.getString(cursor.getColumnIndex("destination"));
                                source = cursor.getString(cursor.getColumnIndex("source"));
                                dataName = cursor.getString(cursor.getColumnIndex("dataName"));
                                String head = WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_START + 
                                                destination + WiFiChatFragment.SYMBOL + 
                                                source + WiFiChatFragment.SYMBOL +
                                                dataName + WiFiChatFragment.SYMBOL +
                                                cursor.getString(cursor.getColumnIndex("time")) + WiFiChatFragment.SYMBOL +
                                                fis.available() + WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_END;
                                int headLength = head.length();
                                outputStream.write( (headLength + head).getBytes() );  
                                outputStream.flush();
                                while ((len = fis.read(buf)) != -1) {
                                        outputStream.write(buf, 0, len); 
                                        outputStream.flush();
                                }
                                outputStream.write(WiFiChatFragment.ACTION_SEND_FILE_END.getBytes());
                                outputStream.flush();
                                fis.close();
                                Log.d(TAG, "SCF File Transfer Finish "); 
                        } catch (Exception e) {
                              Log.d(TAG, "SCF File Transfer Exception !" );
                        }            
                        
                        if (destination.equals(nextHop)) { 
                                Log.d("@@@@@@@@@@@@@@@@@", "直接删除下一跳是目的节点的表项");
                                db.execSQL("delete from SCFDataTable where dataName = ? and destination = ? and source = ?", 
                                                new String[]{ dataName, destination,source}); 
                        } else {
                                String SCFDataInfo;
                                SCFDataInfo =   destination + DealSCFDataTableThread.TAG2 + 
                                                                source + DealSCFDataTableThread.TAG2 + 
                                                                cursor.getString(cursor.getColumnIndex("time")) + DealSCFDataTableThread.TAG2 + 
                                                                DealSCFDataTableThread.SENDSCFDATA + DealSCFDataTableThread.TAG2 +
                                                               cursor.getString(cursor.getColumnIndex("dataName")); 
                                Log.d("test DealSCFDataTableThread", "start DealSCFDataTableThread");
                                dealSCFData = new DealSCFDataTableThread(context, SCFDataInfo);
                                Thread dealSCFDataThread =  new Thread(dealSCFData);
                                dealSCFDataThread.start();
                        } 
                        try { 
                                Thread.sleep(300);                                
                        } catch (Exception e) { 
                        }
                }
                cursor.close();
                db.close();
        }

}
