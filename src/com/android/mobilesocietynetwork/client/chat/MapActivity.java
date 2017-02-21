package com.android.mobilesocietynetwork.client.chat;




import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.database.FriendDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.packet.LonlatPacket;
import com.android.mobilesocietynetwork.client.packet.RecommendPacket;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.MyDate;
import com.android.mobilesocietynetwork.client.R;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;  
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.platform.comapi.map.l;
//import com.baidu.baidulocationdemo.LocationApplication.MyLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
//如果使用地理围栏功能，需要import如下类
import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocationStatusCodes;
import com.baidu.location.LocationClientOption.LocationMode;

















import android.os.Bundle;
import android.os.Vibrator;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MapActivity extends Activity {  
    private MapView mMapView = null;  
    private BaiduMap mBaiduMap;
    private LinearLayout mLocationButton;
    private double mlongitude;
    private double mlatitude;
    private MapStatus mMapStatus;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private MyLocationListener mMyLocationListener;
    private LinearLayout mUploadButton;
    private LinearLayout mGetFriendLocationButton;
    private LinearLayout mTestButton;
    private SharePreferenceUtil util;
    private ListView mListView;
    private MyListAdapter myAdapter;
    private ArrayList<User> userList;
    private FriendDB friendDB;
    private ArrayList<String> lonList;
	private ArrayList<String> latList;
	private ArrayList<String> nameList;
	private int isfirst;
	private String lastPid;
	private String getFriendTime;

	
    private TextView mLocationResult,logMsg;

    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
       SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_map);  
        
        ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(MapActivity.this);
        //获取地图控件引用  
        util = new SharePreferenceUtil(this, Constants.SAVE_USER);
        mMapView = (MapView) findViewById(R.id.bmapView);  
        mBaiduMap = mMapView.getMap();  
        userList=new ArrayList<User>();
        lonList=new ArrayList<String>();
        latList=new ArrayList<String>();
        nameList=new ArrayList<String>();
        isfirst=1;
    	lastPid="";
        mListView=(ListView) findViewById(R.id.listView_map_actvity);
        myAdapter = new MyListAdapter(this,R.layout.listview_items_map);
        mListView.setAdapter(myAdapter);
		mListView.setOnItemClickListener(new ListItemsClickListener());
		friendDB= new FriendDB(MapActivity.this);
        mUploadButton= (LinearLayout)findViewById(R.id.upload_bt);
        mUploadButton.setOnClickListener(new UploadListener());
        mGetFriendLocationButton=(LinearLayout) findViewById(R.id.getFriendLocation_bt);
        mGetFriendLocationButton.setOnClickListener(new GetFriLocaListener());
        mLocationButton = (LinearLayout)findViewById(R.id.location_bt);
        mLocationButton.setOnClickListener(new OnClickListener() {
        	@Override  
        	public void onClick(View v)
    		{
    			// TODO Auto-generated method stub
        		
    			mLocationClient.start();
    			//一直开着线程无法再次定位。
    			mLocationClient.stop();
    		}
        });
//        mTestButton=(Button)findViewById(R.id.test_bt_map);
//        mTestButton.setOnClickListener(new TestListener());
        
        mLocationResult=(TextView)findViewById(R.id.longla_text);
        mLocationResult.isInEditMode();
        //普通地图  
	    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  
	    //卫星地图  
	    //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
	    LatLng cenpt = new LatLng(30.67,104.06); 
	    mMapStatus = new MapStatus.Builder().target(cenpt).zoom(12).build();
	    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
	    mBaiduMap.setMapStatus(mMapStatusUpdate); 
	    mBaiduMap.setMyLocationEnabled(true);  
	    mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener(myListener );    //注册监听函数
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		mLocationClient.setLocOption(option);
		
		ProviderManager.getInstance().addIQProvider("query", "com.qiao.test.plugin.location",new LocationIQProvider());
        PacketFilter packetFilterIQ = new PacketTypeFilter(IQ.class);  
     PacketFilter packetFilterEX4=new PacketExtensionFilter("query", "com.qiao.test.plugin.location");

		XmppTool.getConnection().addPacketListener(new LocationPacketListener(), packetFilterIQ);
	 //  XmppTool.getConnection().addPacketListener(new LocationPacketListener(), packetFilterEX4);

		
    }  
    
    
    
    private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
		            return ;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time:"+location.getTime());
//			sb.append("\nerror code : ");
//			sb.append(location.getLocType());
			sb.append("\nlongitude:"+location.getLongitude());
			sb.append("\nlatitude:"+location.getLatitude());
			mlatitude=location.getLatitude();
		    mlongitude=location.getLongitude();
//			sb.append("\nradius: ");
//			sb.append(location.getRadius());
//			if (location.getLocType() == BDLocation.TypeGpsLocation){
//				sb.append("\nspeed : "+location.getSpeed());
//				sb.append("\nsatellite : "+location.getSatelliteNumber());
//			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//				sb.append("\naddr : "+sb.append(location.getAddrStr()));
//			} 
			logMsg(sb.toString()); 
		    //定义Maker坐标点  
		    LatLng point = new LatLng(mlatitude, mlongitude);   //这里是先纬度，再经度》-》
		    //构建Marker图标  
		    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);  
		    //构建MarkerOption，用于在地图上添加Marker  
		    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);  
		    //在地图上添加Marker，并显示  
		    mBaiduMap.addOverlay(option);
		    
		    mMapStatus = new MapStatus.Builder().target(point).zoom(15).build();
		    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		    mBaiduMap.animateMapStatus(mMapStatusUpdate,500); 
		}
	}
    
    //共享地理位置
    private class UploadListener implements OnClickListener{

		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			//原报文
//			HashMap<String,String> locationMap=new HashMap<String, String>();
//			locationMap.clear();
//			locationMap.put("latitude", mlatitude+"");
//			locationMap.put("longitude", mlongitude+"");
//			LabelPacket labelPacket=new LabelPacket("query","com.qiao.test.plugin.location");
//			labelPacket.addMap(locationMap);
//			XmppTool.getConnection().sendPacket(labelPacket);
//			String ss=labelPacket.toXML();
			//第二种方法
//			LonlaPacket lonlaPacket=new LonlaPacket("com.qiao.test.plugin.location");
//			lonlaPacket.setLongitude(mlongitude+"");
//			lonlaPacket.setLatitude(mlatitude+"");
//			lonlaPacket.setUser(util.getName());
//			XmppTool.getConnection().sendPacket(lonlaPacket);
//			String ss2=lonlaPacket.toXML();

		
			//第三种方法
			LonlatPacket lonlatPacket=new LonlatPacket("query", "com.qiao.test.plugin.location");
			lonlatPacket.setTypeSet();
			
			lonlatPacket.addInfo(util.getName(), mlongitude+"", mlatitude+"");
			XmppTool.getConnection().sendPacket(lonlatPacket);
			String ss1=lonlatPacket.toXML();
			Toast.makeText(getApplicationContext(), "Succeed", Toast.LENGTH_SHORT).show();
		}
	}
    
    //获得好友地理位置
    private class GetFriLocaListener implements OnClickListener{

		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			LonlatPacket lonlatPacket=new LonlatPacket("query", "com.qiao.test.plugin.location");
			lonlatPacket.setTypeGet();
			XmppTool.getConnection().sendPacket(lonlatPacket);
			String ss1=lonlatPacket.toXML();
			Toast.makeText(getApplicationContext(), "Succeed", Toast.LENGTH_SHORT).show();
		
		}
    	
    }

    private class LocationIQProvider implements IQProvider{

  		@Override
  		public IQ parseIQ(XmlPullParser parser) throws Exception
  		{
  		    LonlatPacket lonlatPacket=new LonlatPacket("query", "com.qiao.test.plugin.location");
  		    lonlatPacket.setTypeGet();
  		    int eventType = parser.getEventType(); 
  		    while (eventType!= XmlPullParser.END_DOCUMENT) 
  		    {
  		    	if (eventType == XmlPullParser.START_TAG) 
  		    	{
  		    		if ("item".equals(parser.getName())) {
  		    			String username=parser.getAttributeValue("", "username");
  		    			String lon=parser.getAttributeValue("", "lon");
  		    			String lat=parser.getAttributeValue("", "lat");
  		    			lonlatPacket.addInfo(username, lon, lat);	
  		    		}
  		    	}
  		    	else if(eventType == XmlPullParser.END_TAG)
  		    	{
  		    		if ("query".equals(parser.getName()))
  		    		{
  		    			break;
  		    		}
  		    	}
  		    	eventType = parser.next();  
  		    }
  			return lonlatPacket; 
  		}	
    }
    
   
    
    
    private class LocationPacketListener implements PacketListener{

		@Override
		public void processPacket(Packet packet)
		{
			// TODO Auto-generated method stub
			LonlatPacket lonlatPacket=(LonlatPacket)packet;
			if((isfirst==1&&lonlatPacket.getType().toString().equals("result"))
					||(isfirst==0&&lastPid!=lonlatPacket.getPacketID()&&lonlatPacket.getType().toString().equals("result"))){
				nameList.clear();
				nameList=lonlatPacket.getUserList();
				lonList=lonlatPacket.getLonArrayList();
				latList=lonlatPacket.getLatArrayList();
				for(int i=0;i<lonList.size();i++)
				{
			  
					LatLng point = new LatLng(Double.parseDouble(latList.get(i)), 
							Double.parseDouble(lonList.get(i)));  
					//构建Marker图标  
					BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka_friend);  
					//构建MarkerOption，用于在地图上添加Marker  
					OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);  
					mBaiduMap.addOverlay(option);
				}
				lastPid=lonlatPacket.getPacketID();
				isfirst=0;
				getFriendTime=MyDate.getDateEN();
				myAdapter.notifyDataSetChanged();
			}	
		}
    	
    }
    
 
    

	private class ListItemsClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
		        long arg3)
		{
			// TODO Auto-generated method stub
			LatLng point = new LatLng(Double.parseDouble(latList.get(position)), 
					Double.parseDouble(lonList.get(position)));  
			mMapStatus = new MapStatus.Builder().target(point).zoom(15).build();
		    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		    mBaiduMap.animateMapStatus(mMapStatusUpdate,500); 
		    StringBuffer sb = new StringBuffer(256);
			sb.append("time:"+getFriendTime);
			sb.append("\nlongitude:"+lonList.get(position));
			sb.append("\nlatitude:"+latList.get(position));
			logMsg(sb.toString()); 
		}
	}
	
	private class TestListener implements OnClickListener{

		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			 LatLng cenpt = new LatLng(30.67,104.06); 
			 mMapStatus = new MapStatus.Builder().target(cenpt).zoom(14).build();
			 MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);//改变地图状态
			 mBaiduMap.setMapStatus(mMapStatusUpdate);
			 
		}
		
	}
    
	private class MyListAdapter extends ArrayAdapter<Object>
	{
		int mTextViewResourceID = 0;
		private Context mContext;

		public MyListAdapter(Context context, int textViewResourceId)
		{
			super(context, textViewResourceId);
			mTextViewResourceID = textViewResourceId;
			mContext = context;
		}

		public int getCount()
		{
			return nameList.size();
		}

		@Override
		public boolean areAllItemsEnabled()
		{
			return false;
		}

		public Object getItem(int position)
		{
			return position;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(final int position, View convertView,
		        ViewGroup parent)
		{
			ImageView iamge = null;
			TextView name = null;

//			if (convertView == null)
//			{
				convertView = LayoutInflater.from(mContext).inflate( mTextViewResourceID, null);
				iamge = (ImageView) convertView.findViewById(R.id.image_listitems_map);
				name = (TextView) convertView.findViewById(R.id.name_listitems_map);
				
				iamge.setImageResource(Constants.IMGS[1]);
				name.setText(nameList.get(position));
//			}
			return convertView;
		}
	}
    
    
    
    /**
	 * 显示请求字符串
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			if (mLocationResult != null)
				mLocationResult.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  
    
    //1229add
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	finish();
    }



}
