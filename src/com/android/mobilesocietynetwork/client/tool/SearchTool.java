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

import com.android.mobilesocietynetwork.client.packet.SearchPacket;


public class SearchTool {

	private ArrayList<String> SearchFriList;
	private ArrayList<String> SearchComList;

	private int isFirst = 1;

	public void SendSeFriIQ(String sex,String age, String distance) {

		// ע��һ��IQProvider���ṩ���ƺ������ռ䣬����Ϊquery���ռ�Ϊcom.msn.friendSearch
		ProviderManager.getInstance().addIQProvider("query",
				"com.msn.friendSearch", new SearchFriIQProvider());
		PacketFilter packetFilterEX3=new PacketExtensionFilter("query", "com.msn.friendSearch");
		//PacketFilter packetFilterIQ = new AndFilter(new IQTypeFilter(
		//		IQ.Type.RESULT), new PacketTypeFilter(IQ.class));
		XmppTool.getConnection().addPacketListener(
				new SearchPacketListener(), packetFilterEX3);

		SearchPacket seFriPacket = new SearchPacket("query",
				"com.msn.friendSearch");
		seFriPacket.setType(Type.GET);
		//���ͱ��ĵ������������������ڴ˴�����
		ArrayList<String> list = new ArrayList<String>();
		list.add(sex);
		list.add(age);
		list.add(distance);
		seFriPacket.addList(list);
		
		XmppTool.getConnection().sendPacket(seFriPacket);
		isFirst = 1;
	}
	
	public void SendSeComIQ(String type) {

		// ע��һ��IQProvider���ṩ���ƺ������ռ䣬����Ϊquery���ռ�Ϊcom.msn.communitySearch
		ProviderManager.getInstance().addIQProvider("query",
				"com.msn.communitySearch", new SearchComIQProvider());
		// PacketFilter packetFilterIQ = new PacketTypeFilter(IQ.class);
		PacketFilter packetFilterIQ = new AndFilter(new IQTypeFilter(
				IQ.Type.RESULT), new PacketTypeFilter(IQ.class));
		XmppTool.getConnection().addPacketListener(
				new SearchPacketListener(), packetFilterIQ);

		SearchPacket seComPacket = new SearchPacket("query",
				"com.msn.communitySearch");
		seComPacket.setType(Type.GET);
		//���ͱ��ĵ�����Ϊ�������ͣ��ڴ˴�����
		ArrayList<String> list = new ArrayList<String>();
		list.add(type);
		seComPacket.addList(list);
		
		XmppTool.getConnection().sendPacket(seComPacket);
		isFirst = 1;
	}

	// IQProvider���ڽ����յ�XML�ļ��������������ĺ�����ֱ�ӷ���SearchFriList��
	private class SearchFriIQProvider implements IQProvider {

		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
			
			SearchPacket searchPacket = new SearchPacket("query",
					"com.msn.friendSearch");
			
			while (isFirst == 1) {				
				searchPacket.setType(Type.SET);
				SearchFriList = new ArrayList<String>();
				searchPacket.setUserList(SearchFriList);
				int eventType = parser.getEventType();
				
				//����һ��xml�ļ�
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						if ("item".equals(parser.getName())) {
							String user = parser.getAttributeValue("",
									"username");
							searchPacket.addUser(user);
						}
					} else if (eventType == XmlPullParser.END_TAG) {
						if ("query".equals(parser.getName())) {
							break;
						}
					}
					eventType = parser.next();
				}
				
				isFirst = 0;
				SearchFriList = searchPacket.getUserList();
			}
		
			return searchPacket;
		}

	}

	private class SearchComIQProvider implements IQProvider {

		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
			
			SearchPacket searchPacket = new SearchPacket("query",
					"com.msn.communitySearch");
			
			while (isFirst == 1) {				
				searchPacket.setType(Type.SET);
				SearchComList = new ArrayList<String>();
				searchPacket.setUserList(SearchComList);
				int eventType = parser.getEventType();
				
				//����һ��xml�ļ�
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						if ("item".equals(parser.getName())) {
							String user = parser.getAttributeValue("",
									"username");
							searchPacket.addUser(user);
						}
					} else if (eventType == XmlPullParser.END_TAG) {
						if ("query".equals(parser.getName())) {
							break;
						}
					}
					eventType = parser.next();
				}
				
				isFirst = 0;
				SearchComList = searchPacket.getUserList();
			}
		
			return searchPacket;
		}

	}
	
	// �յ�IQ��֮�����Ӧ����ʵ�Ǹ��պ���
	private class SearchPacketListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			// TODO Auto-generated method stub
			SearchPacket searchPacket = (SearchPacket) packet;
			if (searchPacket.getType().toString().equals("result")) {
			
			}
		}
	}

	//��ȡ�������ĺ����б�
	public ArrayList<String> getSearchFriList() {
		return SearchFriList;
	}
	
	//��ȡ�������������б�
		public ArrayList<String> getSearchComList() {
			return SearchComList;
		}

}
