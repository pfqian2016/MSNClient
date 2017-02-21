package com.android.mobilesocietynetwork.client.tool;

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
import org.xbill.DNS.tests.primary;
import org.xmlpull.v1.XmlPullParser;

import com.android.mobilesocietynetwork.client.packet.FileProcessPacket;

import android.util.Xml;

public class ReceiveFileTool {
	private static String file;
	private static String result;
	private static String source;
	private static String destination;
	private static String current;
	private static String total;
	private static String suffix;
	private static boolean isReceive = false;

	public static void init() {
		ProviderManager.getInstance().addIQProvider("query", "com.msn.fileProcess", new FileIQProvider());
		PacketFilter packetFilterEX = new PacketExtensionFilter("query", "com.msn.fileProcess");
		XmppTool.getConnection().addPacketListener(new ReceiveFileListener(), packetFilterEX);
	}

	private static class FileIQProvider implements IQProvider {
		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
			int eventType = parser.getEventType();
			FileProcessPacket packet = new FileProcessPacket();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if ("source".equals(parser.getName())) {
						eventType = parser.next();
						if (eventType == XmlPullParser.TEXT) {
							source = parser.getText();
						}
					} else if ("destination".equals(parser.getName())) {
						eventType = parser.next();
						if (eventType == XmlPullParser.TEXT) {
							destination = parser.getText();
						}
					} else if ("file".equals(parser.getName())) {
						eventType = parser.next();
						if (eventType == XmlPullParser.TEXT) {
							file = parser.getText();
						}
					} else if ("current".equals(parser.getName())) {
						eventType = parser.next();
						if (eventType == XmlPullParser.TEXT) {
							current = parser.getText();
						}
					} else if ("total".equals(parser.getName())) {
						eventType = parser.next();
						if (eventType == XmlPullParser.TEXT) {
							total = parser.getText();
						}
					} else if ("suffix".equals(parser.getName())) {
						eventType = parser.next();
						if (eventType == XmlPullParser.TEXT) {
							suffix = parser.getText();
						}
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					if ("query".equals(parser.getName())) {
						isReceive = true;
						break;
					}
				}
				eventType = parser.next();
			}
			return packet;
		}
	}

	private static class ReceiveFileListener implements PacketListener {
		@Override
		public void processPacket(Packet packet) {

		}
	}

	public static String getResult() {
		return result;
	}

	public static void resetResult() {
		result = null;
	}

	public static String getFile() {
		return file;
	}

	public static String getSource() {
		return source;
	}

	public static String getDestination() {
		return destination;
	}

	public static String getCurrent() {
		return current;
	}

	public static String getTotal() {
		return total;
	}

	public static String getSuffix() {
		return suffix;
	}

	public static boolean getIsReceive() {
		return isReceive;
	}

	public synchronized static void resetIsReceive() {
		// TODO Auto-generated method stub
		isReceive = false;
	}
}
