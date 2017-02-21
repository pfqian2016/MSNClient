package com.android.mobilesocietynetwork.client.tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.mobilesocietynetwork.client.chat.RecommendFriActivity;
import com.android.mobilesocietynetwork.client.packet.RecommendLabelPacket;
import com.android.mobilesocietynetwork.client.packet.RecommendPacket;

public class RecommendComTool {

	private ArrayList<String> RecommendComList;
	private ArrayList<Map<String, Object>> listItems;
	private int isFirst = 1;
	//判断是普通推荐还是带标签的推荐
	private String mRecommendType;
	private String mLabel;
	

	
	public RecommendComTool(String mRecommendType) {
		this.mRecommendType = mRecommendType;
	}

	public RecommendComTool(String mRecommendType, String mLabel) {
		this.mRecommendType = mRecommendType;
		this.mLabel = mLabel;
	}
	
	public void RecommendComSendIQ() {

		ProviderManager.getInstance().addIQProvider("query",
				"com.msn.mucRecommend",//modify namespace 1101
				new RecommendComIQProvider());

//		PacketFilter packetFilterIQ = new AndFilter(new IQTypeFilter(
//				IQ.Type.RESULT), new PacketTypeFilter(IQ.class));
	    PacketFilter packetFilterEX1=new PacketExtensionFilter("query", "com.msn.mucRecommend");
		XmppTool.getConnection().addPacketListener(
				new RecommendComPacketListener(), packetFilterEX1);
		//1105 modify
		if(mRecommendType=="recommend"){
			RecommendPacket reComPacket = new RecommendPacket("query",
				"com.msn.mucRecommend");
		reComPacket.setType(Type.GET);
		reComPacket.setRecommendtype(mRecommendType);
		XmppTool.getConnection().sendPacket(reComPacket);
		isFirst = 1;
		}
		else if(mRecommendType=="labelRecommend"){
			RecommendLabelPacket reComPacket = new RecommendLabelPacket("query",
					"com.msn.mucRecommend");
			reComPacket.setType(Type.GET);
			reComPacket.setRecommendtype(mRecommendType);
			reComPacket.setLabel(mLabel);
			XmppTool.getConnection().sendPacket(reComPacket);
			isFirst = 1;
		}
	}

	// IQProvider用于解析XML文件
	private class RecommendComIQProvider implements IQProvider {

		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {

		
				RecommendPacket recommendPacket = new RecommendPacket("query",
						"com.msn.mucRecommend");
			while (isFirst == 1) {
		RecommendComList = new ArrayList<String>();
				recommendPacket.setType(Type.SET);
				recommendPacket.setUserList(RecommendComList);
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						if ("item".equals(parser.getName())) {
							String user = parser.getAttributeValue("",
									"roomname");
							recommendPacket.addUser(user);
						}
					} else if (eventType == XmlPullParser.END_TAG) {
						if ("query".equals(parser.getName())) {
							break;
						}
					}
					eventType = parser.next();
				}
				isFirst = 0;
				RecommendComList = recommendPacket.getUserList();
			}
			// RecommendList = recommendPacket.getUserList();
			return recommendPacket;
		}

	}

	private class RecommendComPacketListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			// TODO Auto-generated method stub
			RecommendPacket recommendPacket = (RecommendPacket) packet;
			if (recommendPacket.getType().toString().equals("result")) {
				// listItems=new ArrayList<Map<String, Object>>();
				// RecommendList = recommendPacket.getUserList();
				/*
				 * for(int i=0;i<RecommendList.size();i++) { Map<String, Object>
				 * listItem =new LinkedHashMap<String, Object>();
				 * listItem.put("name",RecommendList.get(i));
				 * listItems.add(listItem); }
				 */
			}
		}
	}

	public ArrayList<String> getRecommendComList() {
		return RecommendComList;
	}

}
