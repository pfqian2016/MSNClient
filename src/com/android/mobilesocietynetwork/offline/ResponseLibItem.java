package com.android.mobilesocietynetwork.offline;

/**
 * 对接收到的某个广播请求的响应内容在响应库的表现形式，有内容和广播次数两个属性。当广播次数达到某个上限就将该item从响应库中删除。
 * @author LLR_sunshine
 * 
 */
public class ResponseLibItem
{
	private String responseContent;
	private int broadcastTime;

	public ResponseLibItem(String responseContent)
	{
		this.responseContent = responseContent;
		broadcastTime = 0;
	}

	public String getResponseContent()
	{
		return responseContent;
	}

	public void setResponseContent(String responseContent)
	{
		this.responseContent = responseContent;
	}

	public void addBroadcastTime()
	{
		broadcastTime++;
	}

	public void setBroadcastTime(int newBroadcastTime)
	{
		broadcastTime = newBroadcastTime;
	}

	public int getBroadcastTime()
	{
		return broadcastTime;
	}
}
