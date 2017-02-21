package com.android.mobilesocietynetwork.client.packet;



import java.util.ArrayList;

import org.jivesoftware.smack.packet.PacketExtension;

//×Ô¶¨ÒåPacketExtension
public class LonlatExtensionPacket implements PacketExtension
{
	  private String elementName;
	  private String namespace;
	  private ArrayList<String> longitudeList;
	  private ArrayList<String> latitudeList;
	  private ArrayList<String> userList;

	    /**
	     * Creates a new generic packet extension.
	     *
	     * @param elementName the name of the element of the XML sub-document.
	     * @param namespace the namespace of the element.
	     */
	    public LonlatExtensionPacket(String elementName, String namespace) {
	        this.elementName = elementName;
	        this.namespace = namespace;
	        this.longitudeList=new ArrayList<String>();
	        this.latitudeList=new ArrayList<String>();
	        this.userList=new ArrayList<String>();
	    }

	     /**
	     * Returns the XML element name of the extension sub-packet root element.
	     *
	     * @return the XML element name of the packet extension.
	     */
	    public String getElementName() {
	        return elementName;
	    }

	    /**
	     * Returns the XML namespace of the extension sub-packet root element.
	     *
	     * @return the XML namespace of the packet extension.
	     */
	    public String getNamespace() {
	        return namespace;
	    }

	    
	    

		public ArrayList<String> getLongitudeList()
		{
			return longitudeList;
		}

		public void addLongitude(String longitude)
		{
			this.longitudeList.add(longitude);
		}

		public ArrayList<String> getLatitudeList()
		{
			return latitudeList;
		}

		public void addLatitude(String latitude)
		{
			this.latitudeList.add(latitude);
		}

		public ArrayList<String> getUserList()
		{
			return userList;
		}

		public void addUser(String user)
		{
			this.userList.add(user);
		}

		public String toXML() {
			StringBuffer buf = new StringBuffer();
			buf.append("<").append(elementName).append(" xmlns=\"").append(namespace).append("\">");
			for(int i=0;i<userList.size();i++)
			{
				buf.append("<").append("item").append(" user=\"").append(userList.get(0)).append("\"");
				buf.append(" lon=\"").append(longitudeList.get(0)).append("\" lat=\"").append(latitudeList.get(0)).append("\"/>");
			}
			buf.append("</").append(elementName).append(">");
			return buf.toString();
	    }

}
