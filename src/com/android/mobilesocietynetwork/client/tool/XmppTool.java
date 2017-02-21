package com.android.mobilesocietynetwork.client.tool;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.xmlpull.v1.XmlPullParser;

import com.android.mobilesocietynetwork.client.MyApplication;
import com.android.mobilesocietynetwork.client.XMPPConnectionListener;
import com.android.mobilesocietynetwork.client.chat.RecommendFriActivity;
import com.android.mobilesocietynetwork.client.util.Constants;

public class XmppTool
{
	private  static XMPPConnection con = null;
 
	static{ 
	    try{
	        Class.forName("org.jivesoftware.smack.ReconnectionManager");
	    }catch(Exception e){
		e.printStackTrace();
	    }
	}
	public static  XMPPConnection getConnection()
	{
		if (con == null)
		{

			openConnection();
		}
		return con;
	}

	private  static void openConnection()
	{
		try//***************************--------------------***************
		{
			if (null == con || !con.isAuthenticated()){
				XMPPConnection.DEBUG_ENABLED = true;
			// url、端口，也可以设置连接的服务器名字，地址，端口，用户
			ConnectionConfiguration connConfig = new ConnectionConfiguration(
 					Constants.SERVER_IP, 5222);
			//Allow reconnection
			connConfig.setReconnectionAllowed(true);
			
			//Allow send presence packet
			//connConfig.setSendPresence(false);
			
			con = new XMPPConnection(connConfig);
			
			con.connect();
			
			//add listener
			if(con.isConnected())
			{
				
				con.addConnectionListener(new XMPPConnectionListener(MyApplication.getInstance()));
			}
			//con.addConnectionListener(new XMPPConnectionListener(MyApplication.getInstance()));
			configureConnection(ProviderManager.getInstance());
			}
           // ConfigCon(ProviderManager.getInstance());  
			
		
		}
		catch (XMPPException xe)
		{
			xe.printStackTrace();
		}
	}

	public static void closeConnection()
	{
		if (con != null)
			{
			Presence presence = new Presence( // Presence是Packet的一个子类
					Presence.Type.unavailable);
			XmppTool.getConnection().sendPacket(presence);
			con.disconnect();
		    con = null;
		    }
		   
	}
	
	
	
	
	/*public  static void ConfigCon(ProviderManager pm)
	{
		
		pm.addIQProvider("query", "com.qiao.test.plugin.recommemd",
				new RecommendFriActivity.RecommendIQProvider());
		
	}*/
	
	 private static  void configureConnection(ProviderManager pm) {  
	        // Private Data Storage  
	        pm.addIQProvider("query", "jabber:iq:private",new PrivateDataManager.PrivateDataIQProvider());  
	        // Time  
	        try {  
	            pm.addIQProvider("query", "jabber:iq:time",Class.forName("org.jivesoftware.smackx.packet.Time"));  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        // Roster Exchange  
	        pm.addExtensionProvider("x", "jabberroster",new RosterExchangeProvider());  
	        // Message Events  
	        pm.addExtensionProvider("x", "jabberevent",new MessageEventProvider());  
	        // Chat State  
	        pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());  
	        pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());  
	        pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());  
	        pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());  
	        pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());  
	        // XHTML  
	        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",new XHTMLExtensionProvider());  
	        // Group Chat Invitations  
	        pm.addExtensionProvider("x", "jabberconference",new GroupChatInvitation.Provider());  
	        // Service Discovery # Items //解析房间列表  
	        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",new DiscoverItemsProvider());  
	        // Service Discovery # Info //某一个房间的信息  
	        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",new DiscoverInfoProvider());  
	        // Data Forms  
	        pm.addExtensionProvider("x", "jabberdata", new DataFormProvider());  
	        // MUC User  
	        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",new MUCUserProvider());  
	        // MUC Admin  
	        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",new MUCAdminProvider());  
	        // MUC Owner  
	        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",new MUCOwnerProvider());  
	        // Delayed Delivery  
	        pm.addExtensionProvider("x", "jabberdelay",new DelayInformationProvider());  
	        // Version  
	        try {  
	            pm.addIQProvider("query", "jabber:iq:version",Class.forName("org.jivesoftware.smackx.packet.Version"));  
	        } catch (ClassNotFoundException e) {  
	            // Not sure what's happening here.  
	        }  
	        // VCard  
	        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());  
	        // Offline Message Requests  
	        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",new OfflineMessageRequest.Provider());  
	        // Offline Message Indicator  
	        pm.addExtensionProvider("offline","http://jabber.org/protocol/offline",new OfflineMessageInfo.Provider());  
	        // Last Activity  
	        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());  
	        // User Search  
	        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());  
	        // SharedGroupsInfo  
	        pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup",new SharedGroupsInfo.Provider());  
	        // JEP-33: Extended Stanza Addressing  
	        pm.addExtensionProvider("addresses","http://jabber.org/protocol/address",new MultipleAddressesProvider());  
	        pm.addIQProvider("si", "http://jabber.org/protocol/si",new StreamInitiationProvider());  
	        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());  
	        pm.addIQProvider("command", "http://jabber.org/protocol/commands",new AdHocCommandDataProvider());  
	        pm.addExtensionProvider("malformed-action","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.MalformedActionError());  
	        pm.addExtensionProvider("bad-locale","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadLocaleError());  
	        pm.addExtensionProvider("bad-payload","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadPayloadError());  
	        pm.addExtensionProvider("bad-sessionid","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadSessionIDError());  
	        pm.addExtensionProvider("session-expired","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.SessionExpiredError());  
	    } 
	 
	 
	
	
}
