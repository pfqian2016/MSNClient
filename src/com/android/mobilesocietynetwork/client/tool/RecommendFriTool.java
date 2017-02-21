package com.android.mobilesocietynetwork.client.tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import com.android.mobilesocietynetwork.client.packet.RecommendPacket;


public class RecommendFriTool {

	private ArrayList<String> RecommendList;
	private ArrayList<Map<String, Object>> listItems;
	private int isFirst = 1;

	public void SendIQ() {

		// 注册一个IQProvider，提供名称和命名空间，名称为query，空间为com.msn.friendRecommend
		ProviderManager.getInstance().addIQProvider("query",
				"com.msn.friendRecommend", new RecommendIQProvider());
	    PacketFilter packetFilterEX2=new PacketExtensionFilter("query", "com.msn.friendRecommend");
	//	PacketFilter packetFilterIQ = new AndFilter(new IQTypeFilter(
	//			IQ.Type.RESULT), new PacketTypeFilter(IQ.class));
		XmppTool.getConnection().addPacketListener(
				new RecommendPacketListener(), packetFilterEX2);

		RecommendPacket rePacket = new RecommendPacket("query",
				"com.msn.friendRecommend");
		rePacket.setType(Type.GET);
		XmppTool.getConnection().sendPacket(rePacket);
		isFirst = 1;
	}

	// IQProvider用于解析XML文件
	private class RecommendIQProvider implements IQProvider {

		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
			
			RecommendPacket recommendPacket = new RecommendPacket("query",
					"com.msn.friendRecommend");
			
			while (isFirst == 1) {				
				recommendPacket.setType(Type.SET);
				RecommendList = new ArrayList<String>();
				recommendPacket.setUserList(RecommendList);
				int eventType = parser.getEventType();
				
				//解析一段xml文件
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						if ("item".equals(parser.getName())) {
							String user = parser.getAttributeValue("","username");
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
				RecommendList = recommendPacket.getUserList();
			}
		
			return recommendPacket;
		}

	}

	// 收到IQ包之后，显示推荐好友的名字列表
	private class RecommendPacketListener implements PacketListener {

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

	public ArrayList<String> getRecommendList() {
		return RecommendList;
	}

}
