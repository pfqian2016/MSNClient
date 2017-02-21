package com.android.mobilesocietynetwork.client.util;

import com.android.mobilesocietynetwork.client.database.OfflineMessageDB;
import com.android.mobilesocietynetwork.client.database.OfflineThrowTimeDB;

import android.content.Context;


public class OfflineCost {
	String name;
	OfflineThrowTimeDB offThrDB;
	OfflineMessageDB offMesDB;
	double cost;

	public OfflineCost(String name)
	{
		this.name = name;
	}

	// Я��ʱ��Ϊ0
	public double computeOfflineCostFromSource(Context c, String destination)
	{
		offThrDB = new OfflineThrowTimeDB(c);
		cost = offThrDB.getWeightTime(name, destination);
		offThrDB.close();
		return cost;
	}

	// Ҫ����Я��ʱ��
	public double computeOfflineCost(Context c, String destination, long carryTime)
	{
		offThrDB = new OfflineThrowTimeDB(c);
		cost = offThrDB.getWeightTime(name, destination);
		offThrDB.close();
		return cost - carryTime;
	}
}
