
package com.example.android.wifidirect.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.SCFData.SendSCFDataThread;
import com.example.SCFData.TransSCFData;
import com.example.cpr.route.DealOneWeekContactLog;

public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler"; 
    private Handler handler;
    private ChatManager chat;
    private InetAddress mAddress;
    private Activity context;
    private String ownUsername;
    private String ownLable;
    private String contactLable;
    private String dataname;
    private boolean scf = false;
//    private String nextHop = null;
//    private int toNextHopIntent = -1;
//    public ClientSocketHandler(Context context, Handler handler, InetAddress groupOwnerAddress ,String nextInfo) {
//            
//        this.handler = handler;
//        this.mAddress = groupOwnerAddress;
//        this.context = context;
//        this.nextHop = nextInfo.split("#")[0];
//        this.toNextHopIntent = Integer.parseInt(nextInfo.split("#")[1]);
//    }
    public ClientSocketHandler(Activity context, Handler handler, InetAddress groupOwnerAddress, String ownUsername,
			String ownLable, String contactLable, String dataname, boolean scf) {

		this.handler = handler;
		this.mAddress = groupOwnerAddress;
		this.context = context; 
		this.ownUsername = ownUsername;
		this.ownLable = ownLable;
		this.contactLable = contactLable;
		this.dataname = dataname;
		this.scf = scf;
	}

    @Override
    public void run() {
       
    	Socket socket = new Socket();
        try { 
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                            WiFiServiceDiscoveryActivity.SERVER_PORT), 5000);
           
            Log.d(TAG, "Launching the I/O handler");
            chat = new ChatManager(context, socket, handler);
            new Thread(chat).start(); 
            
            if (scf) {
            	SendSCFDataThread sendSCFData = new SendSCFDataThread(context, socket, dataname);
				Thread SCFDataThread = new Thread(sendSCFData);
				SCFDataThread.start(); 
			}
                
            // �� ��ʱ��ͨ�����ݸ��µ� contact log table��
            DealOneWeekContactLog oneWeek = new DealOneWeekContactLog(context, ownUsername, ownLable, contactLable);
			oneWeek.start();
			
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
