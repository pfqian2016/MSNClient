package com.android.mobilesocietynetwork.client.util;

public class WeightCompute {
	
	private long[] valueArray;
	private long result;

	public WeightCompute(long[] valueArray)
	{
		this.valueArray = valueArray;
	}

	public long getResult()
	{
		result = valueArray[4] / 3 + 4 * valueArray[3] / 15 + valueArray[2] / 5 + 2 * valueArray[1] / 15
				+ valueArray[0] / 15;
		return result;
	}

}
