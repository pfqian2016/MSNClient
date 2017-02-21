package com.android.mobilesocietynetwork.client.util;

import java.util.ArrayList;
import java.util.List;







import com.android.mobilesocietynetwork.client.database.FriendDB;
import com.android.mobilesocietynetwork.client.info.User;

import android.R.integer;

public class GetData
{
	private FriendDB db;
	private String mName;
	
	public GetData(FriendDB db, String name)
	{
		this.db = db;
		this.mName = name;
	}
	
	private int lenUser, lenBuf, lenGroup;
	private User uBuf = new User(); 
	private String sBuf = new String();
	private List<User> listGetBuf = new ArrayList<User>();
	private List<User> listGet = new ArrayList<User>();
	ArrayList<String> groupList = new ArrayList<String>();
	
	public ArrayList<String> getGroupList()
	{
		listGetBuf = db.getFriendlist(mName);
		listGet.addAll(listGetBuf);
		lenUser = listGetBuf.size();
		lenBuf = lenUser;
		for(int i=0;i<lenBuf;i++)
		{
			int k=0;
			for(int j=0;j<groupList.size();j++)
				if(groupList.get(j).equals(listGetBuf.get(i).getGroup()))
					k++;
			if(k==0) groupList.add(listGetBuf.get(i).getGroup());
		}
		lenGroup=groupList.size();
		return groupList;
	}
	
	public ArrayList<ArrayList<String>> getChildList()
	{
		ArrayList<ArrayList<String>> childList = new ArrayList<ArrayList<String>>();
		for(int i=0;i<lenGroup;i++)
		{
			ArrayList<String> listItem = new ArrayList<String>();
			for(int j=0;j<lenUser;j++)
			{
				if(groupList.get(i).equals(listGetBuf.get(j).getGroup()))
					listItem.add(listGetBuf.get(j).getName());
			}
			childList.add(listItem);
		}
		return childList;
	}
}
