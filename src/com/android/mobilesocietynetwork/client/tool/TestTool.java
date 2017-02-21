package com.android.mobilesocietynetwork.client.tool;

import java.util.ArrayList;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import com.android.mobilesocietynetwork.client.info.hybrid.ScfInfo;
import com.android.mobilesocietynetwork.client.packet.hybrid.DirectScfPacket;
import com.android.mobilesocietynetwork.client.packet.hybrid.ForwardScfPacket;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
public class TestTool{
	
private IQ iq;	
private int flag;

public void init() {
        flag = 0;
		// 注册一个IQProvider，提供名称和命名空间
		ProviderManager.getInstance().addIQProvider("test",
				"com.msn.test", new testIQProvider());
		PacketFilter packetFilterEX = new PacketExtensionFilter("test",
				"com.msn.test");
		XmppTool.getConnection().addPacketListener(new ReceiveListener(),packetFilterEX);
	}
	// IQProvider用于解析XML文件
	private class testIQProvider implements IQProvider {
		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
      flag = 1;
        return iq;
		}
		}

	private class ReceiveListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			
		}
	}
	
	public int getFlag() {
		return flag;
	}

		

}

