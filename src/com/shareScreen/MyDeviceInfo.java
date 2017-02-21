package com.shareScreen;

import java.net.InetAddress;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

public class MyDeviceInfo {
	private String id="hulei";
	private WifiP2pDevice device;
	private boolean isHopeBeOwner;
	private WifiP2pInfo info;
	private int width;
	private int height;
	private static MyDeviceInfo instance=new MyDeviceInfo();
	
	private MyDeviceInfo(){
	
	}
	public static MyDeviceInfo getInstance(){
		return instance;
	}
	public void setWidth(int w){
		this.width=w;
	}
	public void setHeight(int h){
		this.height=h;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return id;
	}
	public void setP2PDevice(WifiP2pDevice device){
		this.device=device;
	}
	public WifiP2pDevice getP2PDevice(){
		return device;
	}
	public void setIsHopeBeOwner(boolean isHopeBeOwner){
		this.isHopeBeOwner=isHopeBeOwner;
	}
	public boolean getIsHopeBeOwner(){
		return isHopeBeOwner;
	}
	
	public boolean getISGroupOwner(){
		return info.isGroupOwner;
	}
	public void setP2PInfo(WifiP2pInfo info){
		this.info=info;
	}
	public WifiP2pInfo getP2PInfo(){
		return info;
	}
	public InetAddress getGroupOwnerIp(){
		return info.groupOwnerAddress;		
	}
	public void update(){
		info=null;
		device=null;
		isHopeBeOwner=false;
		
	}
}
