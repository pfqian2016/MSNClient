package com.android.mobilesocietynetwork.offline;

import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.mobilesocietynetwork.client.database.OfflineMessageDB;
import com.android.mobilesocietynetwork.client.util.Constants;


/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class ChatManager implements Runnable
{
	private Socket socket = null;
	private Handler handler;
	private int throwID;
	private OfflineMessageDB offlineMessageDB;
	private String type_SCF;
	private String myName;

	public ChatManager(Socket socket, Handler handler, int throwID,
			OfflineMessageDB offlineMessageDB, String type_SCF, String myName)
	{
		this.throwID = throwID;
		this.offlineMessageDB = offlineMessageDB;
		this.socket = socket;
		this.type_SCF = type_SCF;
		this.myName = myName;
		this.handler = handler;
	}

	private InputStream iStream;
	private OutputStream oStream;
	private static final String TAG = "ChatHandler";

	@Override
	public void run()
	{
		try
		{
			if (throwID == 0)
			{
				iStream = socket.getInputStream();
				int count = iStream.available(); // 缓冲区
				while (count == 0)
				{
					count = iStream.available();
				}
				logPrint("Offline收到SCF数据！");
				byte[] buf = new byte[count];
				iStream.read(buf);
				handler.obtainMessage(Constants.SCF_RECEIVE, new String(buf, "UTF-8"))
						.sendToTarget();
			}
			else
			{
				logPrint("开始在ChatManager中发送SCF数据。");
				oStream = socket.getOutputStream();
				JSONObject jso = new JSONObject();
				jso.put(Constants.PACKET_TYPE, type_SCF);
				jso.put("throwID", throwID);
				jso.put("source", offlineMessageDB.getSource(myName, throwID));
				jso.put("value", offlineMessageDB.getData(myName, throwID));
				jso.put("startTime", offlineMessageDB.getStartTime(myName, throwID));
				jso.put("life", offlineMessageDB.getLife(myName, throwID));
				jso.put("pass", offlineMessageDB.getPass(myName, throwID));
				oStream.write(jso.toString().getBytes());
				logPrint("在ChatManager中发送SCF数据完毕。");
//				Toast.makeText(context, "", Toast.LENGTH_LONG).show();
			
				offlineMessageDB.deleteMessage(myName, throwID);
				handler.obtainMessage(Constants.SCF_SEND).sendToTarget();
				System.out.println("在ChatManager中发送SCF数据完毕");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void logPrint(String log)
	{
		Constants.logPrint(log);
	}

}
