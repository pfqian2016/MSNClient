package com.example.android.wifidirect.discovery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class FileTransferThread implements Runnable {
 
        private Socket socket;
        private Context context; 
        private Uri uri;
        private String destAndSource;
        private InputStream inputStream;
        private String head;
        public FileTransferThread (Context context, Socket socket, Uri uri,String destAndSource) {
                this.context = context;
                this.socket = socket;
                this.uri = uri;
                this.destAndSource = destAndSource;
        }
        
        public FileTransferThread(Context context,Socket socket, InputStream inputStream, String head) {
        	this.context = context;    
        	this.socket = socket;
                this.inputStream = inputStream;
                this.head = head;
        }
        
        @Override
        public void run() {  
        	
              

        	//System.out.println("test head:" + head);
        	Log.d("test",head);
        	
	        try {
	            byte buf[] = new byte[1024*4];
	            int len = 0;
	            OutputStream out = socket.getOutputStream();
	            out.write(head.getBytes());
	            out.flush();
	           
	            while ( (len = inputStream.read(buf)) != -1) {
                    out.write(buf, 0, len);
                    out.flush();
	            }
	            out.write(WiFiChatFragment.ACTION_SEND_FILE_END.getBytes());
	            out.flush();
	            inputStream.close();
	            System.out.println("scf data trans over");
	            Toast.makeText(context, "Download succeeded", Toast.LENGTH_SHORT).show();
	        } catch (Exception e) { 
	            e.printStackTrace();
	        }
            
            /*int len;        
            try {
                ContentResolver cr = context.getContentResolver();    
                InputStream inputStream = null;  
                inputStream = cr.openInputStream(Uri.parse(uri.toString()));
                String fileName = GetRealNameFromURI.getFilename(GetRealNameFromURI.getImageAbsolutePath(context, uri));  
                OutputStream out = socket.getOutputStream(); 
                int headLength = 0; 
                int time = (int) System.currentTimeMillis();
                
                String head = WiFiChatFragment.ACTION_SEND_FILE_HEAD_START +  
                                                destAndSource + WiFiChatFragment.SYMBOL +
                                                fileName + ".jpg"+ WiFiChatFragment.SYMBOL +
                                                "" + time + WiFiChatFragment.SYMBOL + 
                                                inputStream.available() + WiFiChatFragment.ACTION_SEND_FILE_HEAD_END;
                headLength = head.length();
                out.write( (headLength + head).getBytes()); 
                out.flush();
                
                while ((len = inputStream.read(buf)) != -1) {
                        out.write(buf, 0, len);        
                        out.flush();
                }  
                out.write(WiFiChatFragment.ACTION_SEND_FILE_END.getBytes());
                out.flush();
                inputStream.close(); 
                Log.d(WiFiChatFragment.SYMBOL, "File Transfer Finish");
            } catch (FileNotFoundException e) { 
                e.printStackTrace();
            } catch (IOException e) { 
                Log.d(WiFiChatFragment.SYMBOL, "File Transfer failer");
                    e.printStackTrace();
            }           */ 
        }

}
