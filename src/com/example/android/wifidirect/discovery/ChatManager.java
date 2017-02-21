
package com.example.android.wifidirect.discovery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.SCFData.DealSCFDataTableThread;
import com.example.SCFData.RecvSCFdata;
import com.example.cpr.data.DealSCFdataLableTable;

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class ChatManager implements Runnable {
   
    private Socket socket = null; 
    private Handler handler;
    private InputStream inputStream; 
    private OutputStream oStream; 
    public static final String TAG = "ChatHandler"; 
    private Activity context;
    public ChatManager( Activity context, Socket socket, Handler handler) {
            this.socket = socket;
            this.handler = handler;
            this.context = context; 
    }

    @Override
    public void run() {
            
        try {
            inputStream = socket.getInputStream(); 
            oStream = socket.getOutputStream();                       
            handler.obtainMessage(WiFiServiceDiscoveryActivity.MY_HANDLE, this).sendToTarget();   
            while (true) {       
                    
                try { 
                    byte[] buffer = new byte[1024];
                    int bytes = 0;   
                    bytes = inputStream.read(buffer);
                    if (bytes == -1) {
                            break;
                    } 
                    String readMessage = new String(buffer, 0, bytes);
//                            Log.d(TAG, "Rec bytes : " + bytes + "--message : " +readMessage );                    
                    if (readMessage.startsWith(WiFiChatFragment.ACTION_SEND_TXT_HEAD_START) 
                                    && readMessage.indexOf(WiFiChatFragment.ACTION_SEND_TXT_HEAD_END) != -1 ) {    
                            handler.obtainMessage(WiFiServiceDiscoveryActivity.MESSAGE_READ,
                                            bytes, -1, buffer).sendToTarget();
                    } else if (readMessage.indexOf(WiFiChatFragment.ACTION_SEND_FILE_HEAD_START) != -1 
                                            && readMessage.indexOf(WiFiChatFragment.ACTION_SEND_FILE_HEAD_END) != -1) { 
                        Log.d(TAG, "Start Recv File");
                        String headLen = readMessage.substring(0, readMessage.indexOf(WiFiChatFragment.ACTION_SEND_FILE_HEAD_START));
                        int headLength = Integer.parseInt(headLen);
                        int headStart = readMessage.indexOf(WiFiChatFragment.ACTION_SEND_FILE_HEAD_START)
                                                                + WiFiChatFragment.ACTION_SEND_FILE_HEAD_START.length();
                        int headEnd = readMessage.indexOf(WiFiChatFragment.ACTION_SEND_FILE_HEAD_END);
                        String headMessage = readMessage.substring(headStart, headEnd);  
                        String [] temp = null;
                        temp = headMessage.split(WiFiChatFragment.SYMBOL);
                        int fileSize = Integer.parseInt(temp[5]);
                        int recvSize = 0; 
                        String dataName = temp[2] + ".jpg";
                        final File f = new File(Environment.getExternalStorageDirectory() +"/MSN" + "/" + dataName);                              
                        File dirs = new File(f.getParent());
                        if (!dirs.exists()) {
                            dirs.mkdirs();
                        }
                        f.createNewFile();
                        OutputStream fileOut = new FileOutputStream( f );   
//                                Log.d(ChatManager.TAG, "length : " + headLength  + ", headMessage : " + headMessage);
                        
                       if ( (headLength + headLen.length()) <  bytes) {
                               //鏈夌矘鍖呮儏鍐靛彂鐢�
                           Log.d("$$$$$$$$$$$$$$$$$", "have packet splicing problem");                       
                           try { 
                               fileOut.write(buffer, headLength + headLen.length(), bytes - headLength- headLen.length());                                                          
                               recvSize = bytes - headLength - headLen.length();
                               RecvSCFdata recvSCFdata = new RecvSCFdata(context,inputStream, fileOut, fileSize, recvSize);
                               recvSCFdata.dealSCFdata();      
                           } catch (IOException e1) { 
                               e1.printStackTrace();
                           }   
                       } else {                                                   
                           //鏈彂鐢熺矘鍖�
                           //RecvFile recvFile = new RecvFile(context, inputStream, fileOut, headMessage);
                           //RecvFile recvFile = new RecvFile(context, inputStream, fileOut, fileSize);
                           //recvFile.dealSCFData();
                           RecvSCFdata recvSCFdata = new RecvSCFdata(context,inputStream, fileOut, fileSize, recvSize);
                           recvSCFdata.dealSCFdata(); 
                       } 
                          
                    } else if (readMessage.indexOf(WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_START) != -1 
                                            && readMessage.indexOf(WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_END) != -1) {
                            
                        String headLen = readMessage.substring(0, readMessage.indexOf(WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_START));
                        int headLength = Integer.parseInt(headLen);
                        int headStart = readMessage.indexOf(WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_START)
                                                                + WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_START.length();
                        int headEnd = readMessage.indexOf(WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_END);
                        String headMessage = readMessage.substring(headStart, headEnd);  
//                                System.out.println("test headMessage:" + headMessage);
                        String [] temp = null;
                        temp = headMessage.split(WiFiChatFragment.SYMBOL);
                        int fileSize = Integer.parseInt(temp[5]);
                        int recvSize = 0; 
                        
                        String dataName = temp[2] + ".jpg";
                        System.out.println("dataName:" + dataName);
                        final File f = new File(Environment.getExternalStorageDirectory() +"/SCF" + "/" + dataName);                              
                        File dirs = new File(f.getParent());
                        if (!dirs.exists()) {
                            dirs.mkdirs();
                        }
                        f.createNewFile();
                        OutputStream fileOut = new FileOutputStream( f );   
//                                Log.d(ChatManager.TAG, "length : " + headLength  + ", headMessage : " + headMessage);
                        
                       if ( (headLength + headLen.length()) <  bytes) {
                               //鏈夌矘鍖呮儏鍐靛彂鐢�
                           Log.d("$$$$$$$$$$$$$$$$$", "have packet splicing problem");                       
                           try { 
                               fileOut.write(buffer, headLength + headLen.length(), bytes - headLength- headLen.length());                                                          
                               recvSize = bytes - headLength - headLen.length();
                               RecvSCFdata recvSCFdata = new RecvSCFdata(context,inputStream, fileOut, fileSize, recvSize);
                               recvSCFdata.dealSCFdata();      
                           } catch (IOException e1) { 
                               e1.printStackTrace();
                           }   
                       } else {             
                           RecvSCFdata recvSCFdata = new RecvSCFdata(context,inputStream, fileOut, fileSize, recvSize);
                           recvSCFdata.dealSCFdata(); 
                       }
                       
                       StringBuilder dataInfomation = new StringBuilder();
                       dataInfomation.append(temp[0]);
                       dataInfomation.append(DealSCFdataLableTable.TAG);
                       dataInfomation.append(temp[1]);
                       dataInfomation.append(DealSCFdataLableTable.TAG);
                       dataInfomation.append(dataName);
                       dataInfomation.append(DealSCFdataLableTable.TAG);
                       dataInfomation.append(temp[4]); 
                       DealSCFdataLableTable dealSCFdata = new DealSCFdataLableTable(context, dataInfomation.toString());
                       Thread thread = new Thread(dealSCFdata);
                       thread.start(); 
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);                                    
                }  
            }                
        } catch (IOException e) {                
            e.printStackTrace();
        } 
    }

    public void write(byte[] buffer) {
            
        try {
            oStream.write(buffer);
            oStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }
    public Socket getSocket () {            
        return socket;
    }
}
