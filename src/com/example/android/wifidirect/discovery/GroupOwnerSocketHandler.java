
package com.example.android.wifidirect.discovery;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.SCFData.SendSCFDataThread;
import com.example.SCFData.TransSCFData;
import com.example.cpr.route.DealOneWeekContactLog;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */
public class GroupOwnerSocketHandler extends Thread {
 
    private ServerSocket socket = null;
    private Socket sock;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private Activity context;
    private ChatManager chatManager;
    private static final String TAG = "GroupOwnerSocketHandler";
    private String nextHop = null;
    private String ownUsername;
    private String ownLable;
    private String contactLable;
    private String dataname;
    private boolean scf = false;
    private int toNextHopIntent = -1;
    public GroupOwnerSocketHandler(Activity context, Handler handler, String nextInfo) throws IOException {            
        this.context = context;
        this.nextHop = nextInfo.split("#")[0];
        this.toNextHopIntent = Integer.parseInt(nextInfo.split("#")[1]);
        try {
            socket = new ServerSocket(4545); 
            this.handler = handler; 
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }
    }
    
    public GroupOwnerSocketHandler(Activity context, Handler handler, String ownUsername,
		String ownLable, String contactLable, String dataname, boolean scf) throws IOException {            
        this.context = context;
        this.ownUsername = ownUsername;
        this.ownLable = ownLable;
        this.contactLable = contactLable;
        this.dataname = dataname;
        this.scf = scf;
        try {
            socket = new ServerSocket(4545); 
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
                //����SCF���ݴ���
                if (scf) {
                	SendSCFDataThread sendSCFData = new SendSCFDataThread(context, sock, dataname);
                    Thread SCFDataThread = new Thread(sendSCFData);
                    SCFDataThread.start(); 
				}
                
                // �� ��ʱ��ͨ�����ݸ��µ� contact log table��
                DealOneWeekContactLog oneWeek = new DealOneWeekContactLog(context, ownUsername, ownLable, contactLable);
				oneWeek.start();
                
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
