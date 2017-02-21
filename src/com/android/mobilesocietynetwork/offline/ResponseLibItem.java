package com.android.mobilesocietynetwork.offline;

/**
 * �Խ��յ���ĳ���㲥�������Ӧ��������Ӧ��ı�����ʽ�������ݺ͹㲥�����������ԡ����㲥�����ﵽĳ�����޾ͽ���item����Ӧ����ɾ����
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
