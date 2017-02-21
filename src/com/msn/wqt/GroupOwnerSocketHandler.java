
package com.msn.wqt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */
public class GroupOwnerSocketHandler extends Thread {
 
    private ServerSocket socket = null;
    private Socket sock;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private Context context;
    private ChatManager chatManager;
    private static final String TAG = "GroupOwnerSocketHandler";
    public static int BIND_PORT=4545;

    public GroupOwnerSocketHandler(Context context, Handler handler) throws IOException {            
        this.context = context;     
        try {
            socket = new ServerSocket(BIND_PORT); 
            this.handler = handler; 
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }
    }
    
   

    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
                    THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>());

    @Override
    public void run() {
          
    	while (true) {
            try { 
                sock = socket.accept();
                chatManager = new ChatManager(context, sock , handler ); 
                pool.execute(chatManager);
                //进行SCF数据传输
             
                Log.d(TAG, "Launching the I/O handler");
            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                    	socket.close();
                } catch (IOException ioe) {
                	
                } 
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }
      
}
