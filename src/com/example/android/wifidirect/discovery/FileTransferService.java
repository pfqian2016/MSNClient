// Copyright 2011 Google Inc. All Rights Reserved.

package com.example.android.wifidirect.discovery;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
 
/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String EXTRA_FILENAME = "file_name";
    
    private Handler handler;  
        @Override  
        public IBinder onBind(Intent intent){  
            return null;  
        }  

    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
 
            Context context = getApplicationContext();
            if (intent.getAction().equals(ACTION_SEND_FILE)) {
                    String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
                    String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS); 
                    int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
                    Socket socket = new Socket(); 
                    byte[] buf = new byte[1024*4];
                    int len;   
                    try { 
                            socket.bind(null);
                            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                            Log.d("WiFiDirectActivity.TAG", "Client socket - " + socket.isConnected());
                            OutputStream out = socket.getOutputStream();
                            InputStream input = socket.getInputStream(); 
                            
                            ContentResolver cr = context.getContentResolver();
                            InputStream inputStream = cr.openInputStream(Uri.parse(fileUri)); 
                            while ((len = inputStream.read(buf)) != -1) {
                                    out.write(buf, 0, len);        
                            }  
                            
                            len = input.read(buf);
                            if (len != -1) {
                                    return;
                            }
                            String done = new String(buf, 0, len);
                            if (done.equalsIgnoreCase("DONE")) {
                            	// handler = new Handler(Looper.getMainLooper()); 
                            	// handler.post(new Runnable() {    
                            	//	              @Override    
                            		//            public void run() {    
                            		//               Toast.makeText(getApplicationContext(), "Test",Toast.LENGTH_SHORT).show();    
                            		//              }   
                            //	 });
                            	   //Toast.makeText(getApplicationContext(), "File Transfer Over", Toast.LENGTH_SHORT).show();
                                    out.close();
                                    inputStream.close(); 
                                    Log.d("WiFiDirectActivity.TAG", "File Transfer Over~~");
                                    
                                    
                            }                           
                    } catch (IOException e) {
                            Log.e("testttttttttttttt", e.getMessage());
                    } finally {
                            if (socket != null) {
                                    if (socket.isConnected()) {
                                            try {
                                                    socket.close();
                                            } catch (IOException e) { 
                                                    e.printStackTrace();
                                            }
                                    }
                            }
                    }
            }
    }
}
