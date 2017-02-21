package com.android.mobilesocietynetwork.offline;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.android.mobilesocietynetwork.client.MyApplication;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver
{
	private static final String TAG = "HybridTest";
	private WifiP2pManager manager;
	private Channel channel;
	private Service activity;
	private MyApplication myApplication;

	/**
	 * @param manager
	 *            WifiP2pManager system service
	 * @param channel
	 *            Wifi p2p channel
	 * @param activity
	 *            activity associated with the receiver
	 */
	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Service service)
	{
		super();
		this.manager = manager;
		this.channel = channel;
		this.activity = service;
		myApplication = MyApplication.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		logPrint(action);
		if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
		{

			if (manager == null)
			{
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected())
			{

				// we are connected with the other device, request connection
				// info to find group owner IP
				logPrint("Connected to p2p network. Requesting network details");
				manager.requestConnectionInfo(channel, (ConnectionInfoListener) activity);
			}
			else
			{
				// It's a disconnect
			}
		}
		else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
		{
			WifiP2pDevice device = (WifiP2pDevice) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
			logPrint("Device status -" + device.status);
			myApplication.setWifiP2pDevice(device);
		}
	}

	public void logPrint(String log)
	{
		Log.d(TAG, "LLR_sunshine ----- " + log);
	}

}