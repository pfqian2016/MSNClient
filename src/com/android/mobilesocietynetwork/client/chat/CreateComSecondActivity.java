package com.android.mobilesocietynetwork.client.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.chat.CommunityActivity.textlonglister;
import com.android.mobilesocietynetwork.client.database.MultiUserChatDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.packet.LabelPacket;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;



import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;  
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateComSecondActivity extends Activity {

	// 定义变量
	private Button okButton;
	private SharePreferenceUtil util;
	private MultiUserChatDB mutiUserChatDB;
	
	private ExpandableListView mainlistview ;
	private List<String> parent;
	private Map<String, List<String>> map;
	 private String selectComType;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_com2);
		  mainlistview = null;
		 parent = null;
		 map = null;
	//	util = new SharePreferenceUtil(this, Constants.SAVE_USER);	
		okButton=(Button)findViewById(R.id.ButtonCreateComOK);
		okButton.setOnClickListener(new createComListener());
		//初始化界面
		mainlistview = (ExpandableListView) this
                .findViewById(R.id.comTypeList);
        initData();
        final ExpandableListAdapter adapter = new MyAdapter();
        mainlistview.setAdapter(adapter);
       
        //设置item点击的监听器  
        mainlistview.setOnChildClickListener(new OnChildClickListener(){     	  
            @Override  
            public boolean onChildClick(ExpandableListView parent, View v,  
                    int groupPosition, int childPosition, long id) {  
                    selectComType = adapter.getChild(groupPosition, childPosition).toString();  
                    Toast.makeText(  
                    		CreateComSecondActivity.this,  
                            "Successfully set type to" + adapter.getChild(groupPosition, childPosition),  
                            Toast.LENGTH_SHORT).show();  
                   return false;  
                   }  
        } );
        
	}
	//初始化社团类型的二级列表
    public void initData() {
        parent = new ArrayList<String>();
        parent.add("Sports");
        parent.add("TV Series");
        parent.add("Leisure");
        parent.add("Interests");
        parent.add("Focus");
 
        map = new HashMap<String, List<String>>();
 
        List<String> list1 = new ArrayList<String>();
        list1.add("basketball");
        list1.add("football");
        list1.add("swimming");
        list1.add("badminton");
        list1.add("table tennis");
        map.put("Sports", list1);
 
        List<String> list2 = new ArrayList<String>();
        list2.add("SKTV");
        list2.add("ATV");
        list2.add("BTV");
        list2.add("JTV");
        list2.add("CTV");
        map.put("TV Series", list2);
 
        List<String> list3 = new ArrayList<String>();
        list3.add("movie");
        list3.add("cartoon");
        list3.add("travel");
        list3.add("shopping");
        list3.add("KTV");
        map.put("Leisure", list3);
        
        List<String> list4 = new ArrayList<String>();
        list4.add("reading");
        list4.add("writing");
        list4.add("drawing");
        list4.add("music");
        list4.add("dancing");
       
        map.put("Interests", list4);
        
        List<String> list5 = new ArrayList<String>();
        list5.add("S&T");
        list5.add("economy");
        list5.add("realestate");
        list5.add("art");
        list5.add("politics");
       
        map.put("Focus", list5);
 
    }
	//自定义适配器，定义显示的格式
    class MyAdapter extends BaseExpandableListAdapter {
       
        //得到子item需要关联的数据
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String key = parent.get(groupPosition);
            return (map.get(key).get(childPosition));
        }
 
        //得到子item的ID
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
 
        //设置子item的组件
        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
            String key = CreateComSecondActivity.this.parent.get(groupPosition);
            String info = map.get(key).get(childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) CreateComSecondActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_listview_children, null);
            }
            TextView tv = (TextView) convertView
                    .findViewById(R.id.second_textview);
            tv.setText(info);
            return convertView;
        }
 
        //获取当前父item下的子item的个数
        @Override
        public int getChildrenCount(int groupPosition) {
            String key = parent.get(groupPosition);
            int size = 0;
            size = map.get(key).size();
            return size;
        }
      //获取当前父item的数据
        @Override
        public Object getGroup(int groupPosition) {
            return parent.get(groupPosition);
        }
 
        @Override
        public int getGroupCount() {
            return parent.size();
        }
 
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
       //设置父item组件
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) CreateComSecondActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_listview_parent, null);
            }
            TextView tv = (TextView) convertView
                    .findViewById(R.id.parent_textview);
            tv.setText(CreateComSecondActivity.this.parent.get(groupPosition));
            return convertView;
        }
 
        @Override
        public boolean hasStableIds() {
            return true;
        }
 
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
 
    }

	//点击完成后的响应
	class createComListener implements OnClickListener{

		@Override
		public void onClick(View arg0)
		{
			// 先判断是否有选择社团类型
	
			if(selectComType.length()!=0)
			{
	//发送标签IQ包
				        //1101 modify
						LabelPacket labelPacket = new LabelPacket("item","com.msn.mucRecommend",CreateComFirstActivity.getComName());
						ArrayList<String> list = new ArrayList<String>();
						list.add(selectComType);
						labelPacket.addlabelList(list);
						XmppTool.getConnection().sendPacket(labelPacket);
						Toast.makeText(getApplicationContext(), "Successfully create group chat", 3000)
						.show();
						finish();
					} 
					else 
						Toast.makeText(getApplicationContext(), "Successfully create group chat", 3000)
								.show();				
					}
		
		
	}

}
