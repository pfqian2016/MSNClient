package com.android.mobilesocietynetwork.client.util;

import android.content.Context;
import android.content.SharedPreferences;

public class NetworkSearchingWillingSP
{
	private long predict;
	private long[] last;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public NetworkSearchingWillingSP(Context context, String file)
	{
		sp = context.getSharedPreferences(file, context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public void update(long onNetTime)
	{
		last[4] = onNetTime - sp.getLong("lastOnNetTime", 0);
		last[3] = sp.getLong("last1", 0);
		last[2] = sp.getLong("last2", 0);
		last[1] = sp.getLong("last3", 0);
		last[0] = sp.getLong("last4", 0);
		WeightCompute wc = new WeightCompute(last);
		predict = wc.getResult();
		editor.putLong("predict", predict);
		editor.putLong("last1", last[4]);
		editor.putLong("last2", last[3]);
		editor.putLong("last3", last[2]);
		editor.putLong("last4", last[1]);
		editor.putLong("last5", last[0]);
		editor.putLong("lastOnNetTime", onNetTime);
		editor.commit();
	}

	public long getWillingCost(long now)
	{
		return sp.getLong("predict", 0) - (now - sp.getLong("lastOnNetTime", 0));
	}

}
