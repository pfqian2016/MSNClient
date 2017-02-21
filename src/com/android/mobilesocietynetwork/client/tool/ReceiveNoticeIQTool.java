package com.android.mobilesocietynetwork.client.tool;

import java.util.ArrayList;
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

import com.android.mobilesocietynetwork.client.info.CommentInfo;
import com.android.mobilesocietynetwork.client.info.NoticeInfo;
import com.android.mobilesocietynetwork.client.packet.CreateNoticePacket;
import com.android.mobilesocietynetwork.client.packet.RecommendPacket;



public  class ReceiveNoticeIQTool {

	private static String result;
	private static String noticeID;
	//private int isFirst = 1;
	private static NoticeInfo noticeEntity ;
	private static CommentInfo commentEntity ;
	private static  ArrayList<NoticeInfo> noticeList = new ArrayList<NoticeInfo>();
	private static ArrayList<NoticeInfo> createdList = new ArrayList<NoticeInfo>();
	private static ArrayList<NoticeInfo> joinedList = new ArrayList<NoticeInfo>();
	private static  ArrayList<CommentInfo> comments = new ArrayList<CommentInfo>();
	private static ArrayList<String> participants = new ArrayList<String>();
	private static boolean isReceive = false;
	private static boolean isReceiveJoin = false;
	private static boolean isReceiveCreate = false;
	

	public static void init() {

		// ע��һ��IQProvider���ṩ���ƺ������ռ䣬����Ϊquery���ռ�Ϊcom.msn.friendRecommend
		ProviderManager.getInstance().addIQProvider("activity",
				"com.msn.activity", new NoticeIQProvider());
		PacketFilter packetFilterEX = new PacketExtensionFilter("activity",
				"com.msn.activity");
		// PacketFilter packetFilterIQ = new AndFilter(new IQTypeFilter(
		// IQ.Type.RESULT), new PacketTypeFilter(CreateNoticePacket.class));
		XmppTool.getConnection().addPacketListener(new ReceiveNoticeListener(),packetFilterEX);
	//	isFirst = 1;
	}

	// IQProvider���ڽ���XML�ļ�
	private static class NoticeIQProvider implements IQProvider {
		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {

			CreateNoticePacket p = new CreateNoticePacket();

		//	while (isFirst == 1) {
				// xmlÿһ�ֶε�����
				int eventType = parser.getEventType();
				// ��ȡ�Զ���IQ�����Զ�������
				String selfType = parser.getAttributeValue("", "type");
				// ����Ǵ��������ۡ�������ɾ����ȡ������
				if ("createActivity".equals(selfType) || "post".equals(selfType) || "join".equals(selfType) || "delete".equals(selfType)|| "cancel".equals(selfType)){
				p.setType(Type.SET);
				result= null;
					// ����һ��xml�ļ�
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG) {
							if ("result".equals(parser.getName())) {
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT)
									result = parser.getText();
							} else if ("activity_id".equals(parser.getName())) {
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT)
									noticeID = parser.getText();
							}
						} else if (eventType == XmlPullParser.END_TAG) {
							if ("activity".equals(parser.getName())) {
								isReceive = true;
								break;
							}
						}
						eventType = parser.next();
					}
					
				}
				// ����������Ƽ����鿴�Լ������ġ��鿴�Լ�������
			//	else if (selfType == "askRecommendation" || selfType == "myParticipation" || selfType == "myCreation") {
				else if("askRecommendation".equals(selfType)){
					p.setType(Type.SET);
					noticeList.clear();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG&&"item".equals(parser.getName())) {
						noticeEntity = new NoticeInfo();
							eventType = parser.next();
							if ("activity_id".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setId(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("creator".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setInformer(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("title".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setTitle(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("content".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setContent(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("imageString".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setImageString(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("time".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setStartTime(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("address".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setLocation(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("distance".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setDistance(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("labels".equals(parser.getName())){
								eventType = parser.next();
								while(eventType == XmlPullParser.START_TAG&&"label".equals(parser.getName())){
									eventType = parser.next();
									if (eventType == XmlPullParser.TEXT) noticeEntity.addLabel(parser.getText());
									eventType = parser.next();
									eventType = parser.next();		
								}
								eventType = parser.next();		
							}
							if ("number".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setPeopleNum(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("limit".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setLimit(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("isJoined".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setIsJoined(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							noticeList.add(noticeEntity);							
						} else if (eventType == XmlPullParser.END_TAG) {
							if ("activity".equals(parser.getName())) {
								 isReceive = true;
								break;
							}
						}
						eventType = parser.next();
					}
					}
				else if("myParticipation".equals(selfType)){
					p.setType(Type.SET);
					joinedList.clear();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG&&"item".equals(parser.getName())) {
						noticeEntity = new NoticeInfo();
							eventType = parser.next();
							if ("activity_id".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setId(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("creator".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setInformer(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("title".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setTitle(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("content".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setContent(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("imageString".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setImageString(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("time".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setStartTime(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("address".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setLocation(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("distance".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setDistance(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("labels".equals(parser.getName())){
								eventType = parser.next();
								while(eventType == XmlPullParser.START_TAG&&"label".equals(parser.getName())){
									eventType = parser.next();
									if (eventType == XmlPullParser.TEXT) noticeEntity.addLabel(parser.getText());
									eventType = parser.next();
									eventType = parser.next();		
								}
								eventType = parser.next();		
							}
							if ("number".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setPeopleNum(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("limit".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setLimit(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("isJoined".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setIsJoined(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							joinedList.add(noticeEntity);							
						} else if (eventType == XmlPullParser.END_TAG) {
							if ("activity".equals(parser.getName())) {
								isReceiveJoin = true;
								break;
							}
						}
						eventType = parser.next();
					}
					}
				else if("myCreation".equals(selfType)){
					p.setType(Type.SET);
					createdList.clear();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG&&"item".equals(parser.getName())) {
						noticeEntity = new NoticeInfo();
							eventType = parser.next();
							if ("activity_id".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setId(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("creator".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setInformer(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("title".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setTitle(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("content".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setContent(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("imageString".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setImageString(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("time".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setStartTime(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("address".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setLocation(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("distance".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setDistance(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("labels".equals(parser.getName())){
								eventType = parser.next();
								while(eventType == XmlPullParser.START_TAG&&"label".equals(parser.getName())){
									eventType = parser.next();
									if (eventType == XmlPullParser.TEXT) noticeEntity.addLabel(parser.getText());
									eventType = parser.next();
									eventType = parser.next();		
								}
								eventType = parser.next();		
							}
							if ("number".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setPeopleNum(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("limit".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setLimit(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("isJoined".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) noticeEntity.setIsJoined(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							createdList.add(noticeEntity);	
						} else if (eventType == XmlPullParser.END_TAG) {
							if ("activity".equals(parser.getName())) {
								System.out.println("**********���յ������б�*********"+"isReceiveCreate=true");  
								isReceiveCreate = true;
								break;
							}
						}
						eventType = parser.next();
					}
					}
				//����鿴������
				else if ("joinedPeople".equals(selfType)) {
					p.setType(Type.SET);	
					participants.clear();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG&&"participator".equals(parser.getName())) {
							participants.add(parser.getAttributeValue("","name"));
						} else if (eventType == XmlPullParser.END_TAG) {
							if ("activity".equals(parser.getName())) {
								isReceive = true;
								break;
							}
						}
						eventType = parser.next();
					}
					
				}
				//����ǲ鿴ĳһ�������
				else if("askComments".equals(selfType)){
					p.setType(Type.SET);	
					comments.clear();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG&&"item".equals(parser.getName())) {
							commentEntity = new CommentInfo();
							eventType = parser.next();
							if ("creator".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) commentEntity.setPubName(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("time".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) commentEntity.setPubDate(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("content".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) commentEntity.setContent(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							if ("imageString".equals(parser.getName())){
								eventType = parser.next();
								if (eventType == XmlPullParser.TEXT) commentEntity.setImageString(parser.getText());
								eventType = parser.next();
								eventType = parser.next();
							}
							comments.add(commentEntity);							
						} else if (eventType == XmlPullParser.END_TAG) {
							if ("activity".equals(parser.getName())) {
								isReceive = true;
								break;
							}
						}
						eventType = parser.next();
					}
					
				}
		
//				isFirst = 0;
//			}
			return p;
		}

	}

	private static class ReceiveNoticeListener implements PacketListener {
		@Override
		public void processPacket(Packet packet) {
		}

	}

	public static String getNoticeID() {
		return noticeID;
	}

	public static String getResult() {
		return result;
	}
	public static void resetResult() {
		 result = null;
	}
	
	public static ArrayList<NoticeInfo> getNoticeList() {
		return noticeList;
	}
	public static ArrayList<NoticeInfo> getCreatedList() {
		return createdList;
	}
	public static  ArrayList<NoticeInfo> getJoinedList() {
		return joinedList;
	}
	
	public static ArrayList<CommentInfo> getComments() {
		return comments;
	}
	
	public static ArrayList<String> getParticipants() {
		return participants;
	}
	public static boolean getIsReceive() {
		return isReceive;
	}
	
	public static void resetIsReceive() {
		isReceive = false;
	}
	
	public static boolean getIsReceiveJoin() {
		return isReceiveJoin;
	}
	public static void resettIsReceiveJoin() {
		isReceiveJoin = false;
	}
	
	public static boolean getIsReceiveCreate() {
		return isReceiveCreate;
	}
	
	public static void resettIsReceiveCreate() {
		isReceiveCreate = false;
	}
	
	
}
