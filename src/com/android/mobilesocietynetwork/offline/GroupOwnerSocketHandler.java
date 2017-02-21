package com.android.mobilesocietynetwork.offline;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.android.mobilesocietynetwork.client.MyApplication;
import com.android.mobilesocietynetwork.client.database.OfflineMessageDB;
import com.android.mobilesocietynetwork.client.util.Constants;



public class GroupOwnerSocketHandler extends Thread
{
	ServerSocket socket = null;
	private final int THREAD_COUNT = 10;
	private Handler handler;
	private int throwID;
	private OfflineMessageDB offlineMessageDB;
	private String type_SCF;
	private String myName;
	private MyApplication myApplication = MyApplication.getInstance();

	public GroupOwnerSocketHandler(Handler handler, int throwID, OfflineMessageDB offlineMessageDB,
			String type_SCF, String myName) throws IOException
	{
		this.offlineMessageDB = offlineMessageDB;
		this.throwID = throwID;
		this.type_SCF = type_SCF;
		this.myName = myName;
		try
		{
			socket = new ServerSocket(4545);
			this.handler = handler;
			logPrint("SocketSCF Started");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			pool.shutdownNow();
			throw e;
		}
	}

	/**
	 * A ThreadPool for client sockets.
	 */
	private final ThreadPoolExecutor pool = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 10,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	@Override
	public void run()
	{
		// **************待处理
		// 在这儿控制连接的断连
		while (!myApplication.getIsRecieveOn())
		{
			try
			{
				// A blocking operation. Initiate a ChatManager instance when
				// there is a new connection
				pool.execute(new ChatManager(socket.accept(), handler, throwID, offlineMessageDB,
						type_SCF, myName));
				logPrint("启动了GroupOwnerSocketHandler的ChatManager。");
			}
			catch (IOException e)
			{
				try
				{
					if (socket != null && !socket.isClosed())
						socket.close();
				}
				catch (IOException ioe)
				{
					
				}
				e.printStackTrace();
				pool.shutdownNow();
				break;
			}
		}
	}
	private void logPrint(String string)
	{
		Constants.logPrint(string);
		
	}
}
