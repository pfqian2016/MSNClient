package com.android.mobilesocietynetwork.client.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.database.MultiUserChatDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.packet.LabelPacket;
import com.android.mobilesocietynetwork.client.tool.RecommendFriTool;
import com.android.mobilesocietynetwork.client.tool.SearchTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.R;
















import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchFriActivity extends Activity
{

	private Spinner spinSearchFriAge;
	private Spinner spinSearchFriSex;
	private Spinner spinSearchFriLocation;
	private ListView lvSearchFri;
	private Button btSearchFri;
	private static XMPPConnection connection;
	private ArrayList<Map<String, Object>> listItems;
	private SimpleAdapter simpleAdapter;



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_fri);
		
		lvSearchFri = (ListView) findViewById(R.id.listViewSearchFri);
		btSearchFri = (Button)findViewById(R.id.buttonSearchFri);
		spinSearchFriSex = (Spinner)findViewById(R.id.spinnerSexStatic);
		spinSearchFriAge = (Spinner)findViewById(R.id.spinnerAgeStatic);
		spinSearchFriLocation = (Spinner)findViewById(R.id.spinnerLocationStatic);
	    connection = XmppTool.getConnection();
	   btSearchFri.setOnClickListener(new SearchFriListener());
		listItems = new ArrayList<Map<String, Object>>();
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.list_view, new String[] { "name" },
				new int[] { R.id.item_community });

	}

	
	private class SearchFriListener implements OnClickListener{

		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			String sexInfo = spinSearchFriSex.getSelectedItem().toString();
			String ageInfo =  spinSearchFriAge.getSelectedItem().toString();
			String locationInfo =  spinSearchFriLocation.getSelectedItem().toString();
			
			SearchTool setool = new SearchTool();
			setool.SendSeFriIQ( sexInfo,ageInfo,locationInfo);


			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<String> list = setool.getSearchFriList();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> listItem = new LinkedHashMap<String, Object>();
				listItem.put("name", list.get(i));
				listItems.add(listItem);
			}
			lvSearchFri.setAdapter(simpleAdapter);

		}

	}
}
