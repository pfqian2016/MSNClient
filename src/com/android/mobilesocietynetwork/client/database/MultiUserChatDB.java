package com.android.mobilesocietynetwork.client.database;

import java.util.ArrayList;

import org.jivesoftware.smackx.muc.MultiUserChat;

/*
 *  保存会议室的数据库
 * */


public class MultiUserChatDB
{
	private static ArrayList<MultiUserChat> mucMap;
	private static MultiUserChatDB instance;
	
	private MultiUserChatDB(){
		mucMap=new ArrayList<MultiUserChat>();
	}
	
	public static synchronized MultiUserChatDB getInstance()
	{
		if(instance==null)
		{
			instance = new MultiUserChatDB();
		}
		return instance;
	}
	
	public synchronized void addMuc(MultiUserChat muc){
		mucMap.add(muc);
	}
	
	public synchronized void removeMuc(MultiUserChat muc)
	{
		for(MultiUserChat entry:mucMap)
		{
			if(entry.getRoom().split("@")[0].equals(muc.getNickname()))
			{
				mucMap.remove(entry);
				break;
			}
		}		
	}
	
	public synchronized MultiUserChat findMuc(String name){
		for(MultiUserChat entry:mucMap)
		{
			if(entry.getRoom().split("@")[0].equals(name))
			{
				return entry;
			}
		}		
		return null;
	}
	
	public synchronized ArrayList<String> getAllName(){
		ArrayList<String> list=new ArrayList<String>();
		for(MultiUserChat entry:mucMap)
		{
			list.add(entry.getRoom().split("@")[0]);
		}		
		return list;
	}
	
	
	public synchronized ArrayList<MultiUserChat> getAll(){
		return mucMap;
	}
	
}
