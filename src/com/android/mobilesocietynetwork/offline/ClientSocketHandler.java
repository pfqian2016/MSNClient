package com.android.mobilesocietynetwork.offline;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.android.mobilesocietynetwork.client.database.OfflineMessageDB;
import com.android.mobilesocietynetwork.client.util.Constants;



public class ClientSocketHandler extends Thread
{
	private Handler handler;
	private ChatManager chat;
	private InetAddress mAddress;
	private int throwID;
	private OfflineMessageDB offlineMessageDB;
	private String type_SCF;
	private String myName;

	public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress, int throwID,
			OfflineMessageDB offlineMessageDB, String type_SCF, String myName)
	{
		this.throwID = throwID;
		this.handler = handler;
		this.mAddress = groupOwnerAddress;
		this.offlineMessageDB = offlineMessageDB;
		this.type_SCF = type_SCF;
		this.myName = myName;
	}

	@Override
	public void run()
	{
		Socket socket = new Socket();
		try
		{
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(), 4545), 5000);
			logPrint("SocketSCF Started");
			chat = new ChatManager(socket, handler, throwID, offlineMessageDB, type_SCF, myName);
			new Thread(chat).start();
			logPrint("Æô¶¯ÁËClientSocketHandlerµÄChatManager¡£");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			try
			{
				socket.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			return;
		}
	}

	private void logPrint(String string)
	{
		Constants.logPrint(string);
		
	}

	public ChatManager getChat()
	{
		return chat;
	}

}
