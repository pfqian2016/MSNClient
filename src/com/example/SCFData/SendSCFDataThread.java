package com.example.SCFData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.xml.transform.Source;

import com.example.android.wifidirect.discovery.WiFiChatFragment;
import com.example.android.wifidirect.discovery.WiFiServiceDiscoveryActivity;
import com.example.cpr.data.SCFdataLable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class SendSCFDataThread implements Runnable {
        
        public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
        private static final String SYMBOL = "###############";
        private SCFDataDBHelper scfDataDBHelper;
        private DealSCFDataTableThread dealSCFData;
        private Socket socket;
        private Context context;
        private String destination; 
        private String source;
        private String dataName;
        private String SCFDataInfo;
        
//        public SendSCFDataThread (Context context,Socket socket, String destination) {
//                this.context = context;
//                this.socket = socket;
//                this.destination = destination;
//        }
        
        public SendSCFDataThread(Context context, Socket socket, String dataName) {
        	this.context = context;
        	this.socket = socket;
        	this.dataName = dataName;
        }
        @Override
        public void run() { 
//        	dataName = dataName + ".jpg";
        	System.out.println("SendSCFDataThread dataName:" + dataName);
        	SCFdataLable scFdataLable = new SCFdataLable(context, "data.db");
    		SQLiteDatabase db = scFdataLable.getWritableDatabase();
    		Cursor cursor = db.rawQuery("select * from dataLableTable where dataName = ? ", 
    				new String[]{dataName});
    		if (cursor.getCount() == 0) {
    			System.out.println("SendSCFDataThread cursor.getCount() == 0!");
				return;
			}
    		
    		while (cursor.moveToNext()) {
    			OutputStream outputStream = null;    
        		FileInputStream fis = null;
    			try { 
    	              int len = 0;
    	              byte buf[] = new byte[1024*4];      
    	              if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED) ) {
                          File sdCardDir = Environment.getExternalStorageDirectory ();   
                          fis = new FileInputStream(sdCardDir.getCanonicalPath() + "/SCF"+"/" + dataName);  
    	              } else {
                          Log.d("open scfdata failer", "open scfdata failer");
    	              }  
    	              int fileSize = fis.available();
    	              System.out.println("fileSize:" + fileSize);
    	              outputStream = socket.getOutputStream(); 
    	              destination = cursor.getString(cursor.getColumnIndex("destination"));
    	              source = cursor.getString(cursor.getColumnIndex("source")); 
    	              String head = WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_START + 
      						destination + WiFiChatFragment.SYMBOL + 
      						source + WiFiChatFragment.SYMBOL +
      						dataName + WiFiChatFragment.SYMBOL + 
      						"000" + WiFiChatFragment.SYMBOL + 
      						"LB1#LB2#LB3" + WiFiChatFragment.SYMBOL+
      						fileSize + WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_END;
    	              int headLength = head.length();
    	              outputStream.write((headLength + head).getBytes());  
    	              outputStream.flush();
    	              while ((len = fis.read(buf)) != -1) {
    	                      outputStream.write(buf, 0, len);
    	              }
    	              outputStream.write(WiFiChatFragment.ACTION_SEND_FILE_END.getBytes());
    	              outputStream.flush();
    	              Log.d("scfdata send over", "file trans over"); 
    			} catch (Exception e) {
                  Log.d("Exception", "transfer scfdata Exception!!" );
    			} finally {
    				
    				try {
    					//outputStream.close();
    					fis.close();
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}    
			}
//    		while (cursor.moveToNext()) {
//    		OutputStream outputStream = null;    
//    		FileInputStream fis = null;
//			try { 
//	              int len = 0;
//	              byte buf[] = new byte[1024*4];      
//	              if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED) ) {
//                      File sdCardDir = Environment.getExternalStorageDirectory ();   
//                      fis = new FileInputStream(sdCardDir.getCanonicalPath() + "/SCF"+"/" + dataName);  
//	              } else {
//                      Log.d("open scfdata failer", "open scfdata failer");
//	              }  
//	              int fileSize = fis.available();
//	              System.out.println("fileSize:" + fileSize);
//	              outputStream = socket.getOutputStream(); 
//	              String head = WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_START + 
//  						"test1" + WiFiChatFragment.SYMBOL + 
//  						"test2" + WiFiChatFragment.SYMBOL +
//  						dataName + WiFiChatFragment.SYMBOL + 
//  						"000" + WiFiChatFragment.SYMBOL + 
//  						"LB1#LB2#LB3" + WiFiChatFragment.SYMBOL+
//  						fileSize + WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_END;
//	              int headLength = head.length();
//	              outputStream.write((headLength + head).getBytes());  
//	              outputStream.flush();
//	              while ((len = fis.read(buf)) != -1) {
//	                      outputStream.write(buf, 0, len);
//	              }
//	              outputStream.write(WiFiChatFragment.ACTION_SEND_FILE_END.getBytes());
//	              outputStream.flush();
//	              Log.d("scfdata send over", "file trans over"); 
//			} catch (Exception e) {
//              Log.d("Exception", "transfer scfdata Exception!!" );
//			} finally {
//				
//				try {
//					//outputStream.close();
//					fis.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}    
////			} 
        }
        
//        @Override
//        public void run() {
//                 
//                scfDataDBHelper = new SCFDataDBHelper(context, "scfdata.db");
//                SQLiteDatabase db = scfDataDBHelper.getWritableDatabase();
//                Cursor cursor = db.rawQuery("select * from SCFDataTable ", null); 
//                
//                while (cursor.moveToNext()) {
//                       //鍙互浠巗cfdata鏁版嵁琛ㄤ腑閫夋嫨鍚堥�傜殑鏂囦欢鍚嶆潵浼犺緭
//                        try {
//                                FileInputStream fis = null;
//                                OutputStream outputStream;     
//                                int len = 0;
//                                byte buf[] = new byte[1024*4];      
//                                 if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED) ) {
//                                         File  sdCardDir  =  Environment.getExternalStorageDirectory ();   
//                                         fis  =  new  FileInputStream(sdCardDir.getCanonicalPath() + "/SCF"+"/" + cursor.getString(5));  
//                                 } else {
//                                         Log.d("open scfdata failer", "open scfdata failer");
//                                 }  
//                                outputStream = socket.getOutputStream(); 
//                                outputStream.write( (ACTION_SEND_FILE + destination + SYMBOL + 
//                                                "" + SYMBOL +
//                                                cursor.getString(cursor.getColumnIndex("dataName")) + SYMBOL +
//                                                fis.available() ).getBytes() );  
//                                while ((len = fis.read(buf)) != -1) {
//                                        outputStream.write(buf, 0, len);
//                                }
//                                Log.d("scfdata send over", "file trans over"); 
//                            } catch (Exception e) {
//                                  Log.d("Exception", "transfer scfdata Exception!!" );
//                            }              
//                        
//                        SCFDataInfo = destination + DealSCFDataTableThread.TAG2 + 
//                                                        "" + DealSCFDataTableThread.TAG2 + 
//                                                        "" + DealSCFDataTableThread.TAG2 + 
//                                                        DealSCFDataTableThread.SENDSCFDATA + DealSCFDataTableThread.TAG2 +
//                                                       cursor.getString(cursor.getColumnIndex("dataName")); 
//                        
//                        Log.d("test DealSCFDataTableThread", "start DealSCFDataTableThread");
//                        dealSCFData = new DealSCFDataTableThread(context, SCFDataInfo);
//                        Thread dealSCFDataThread = new Thread(dealSCFData);
//                        dealSCFDataThread.start();
//                        break;
//                        
//                }         
//                cursor.close(); 
//        } 
}
