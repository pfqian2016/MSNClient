
package com.msn.wqt;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.util.Log;



public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler"; 
    private Handler handler;
    private ChatManager chat;
    private InetAddress mAddress;
    private Context context;

    public static final int SERVER_PORT = 4545; 
    

    public ClientSocketHandler(Context context, Handler handler, InetAddress groupOwnerAddress) {

		this.handler = handler;
		this.mAddress = groupOwnerAddress;
		this.context = context; 
		
	}

    @Override
    public void run() {
       
    	Socket socket = new Socket();
        try { 
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                            ClientSocketHandler.SERVER_PORT), 5000);
           
            Log.d(TAG, "Launching the I/O handler");
            chat = new ChatManager(context, socket, handler);
            new Thread(chat).start(); 
		
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        return;
        }
    }
     
}
