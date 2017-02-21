package com.android.mobilesocietynetwork.client.notice;

import java.io.IOException;
import java.util.ArrayList;


import java.util.Timer;
import java.util.TimerTask;












import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.chat.DialogActivity;
import com.android.mobilesocietynetwork.client.info.NoticeInfo;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.packet.CancelNoticePacket;
import com.android.mobilesocietynetwork.client.packet.JoinNoticePacket;
import com.android.mobilesocietynetwork.client.packet.RecommendNoticePacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.ImgHelper;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NoticeActivity extends Activity
{
	private ListView noticeListView;
	private LinearLayout noticeCreate;
	private LinearLayout noticeManage;
	private Button addDistance;
	private Button redDistance;
	private TextView distance;
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private double mlongitude;
	private double mlatitude;
	private int count=0;
	private int dis=1;
	private NoticeListviewAdapter noticeViewAdapter;
	private String latitude;
	private String longitude;
    private ArrayList<NoticeInfo> noticeList  = new ArrayList<NoticeInfo>();
	private boolean joinFlag = true;//当前操作是报名还是取消报名
	private boolean success = true;//当前操作是否成功的标志
    private ProgressDialog pd;  
//	private ReceiveNoticeIQTool ReceiveNoticeIQTool = new ReceiveNoticeIQTool();
	//定时器
	private final Timer timer = new Timer();
	private TimerTask task;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notice);
		initView();
		initControl();
		initData();
		NotificationManager nmManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nmManager.cancel(1);
	//	timer.schedule(task, 30000, 180000);
		}
//初始化界面，找到Layout中的控件
	private void initView() {
		// TODO Auto-generated method stub
		noticeListView = (ListView) findViewById(R.id.listView_notice);
		noticeCreate = (LinearLayout) findViewById(R.id.notice_create);
		noticeManage =(LinearLayout) findViewById(R.id.notice_manage); 
		addDistance = (Button)findViewById(R.id.bt_addDistance);
		redDistance = (Button)findViewById(R.id.bt_redDistance);
		redDistance.setEnabled(false);
		distance = (TextView)findViewById(R.id.et_distance);
	}
	
//为可以点击的控件设置监听
	private void initControl() {
		ReceiveNoticeIQTool.init();
//		ReceiveNoticeIQTool2.init();
		// TODO Auto-generated method stub
		MyOnClickListner noticeButtonOnclick = new MyOnClickListner();
		noticeCreate.setOnClickListener(noticeButtonOnclick);
		noticeManage.setOnClickListener(noticeButtonOnclick);
		addDistance.setOnClickListener(noticeButtonOnclick);
		redDistance.setOnClickListener(noticeButtonOnclick);
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener(myListener );    //注册监听函数
	    LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		mLocationClient.setLocOption(option);
		//获取经纬度
		mLocationClient.start();
		mLocationClient.stop();
		//点击列表中的每一个子项的响应
		noticeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				  Intent intentDetail=new Intent(NoticeActivity.this, NoticeDetailActivity.class);
				  NoticeInfo notice = (NoticeInfo)arg0.getAdapter().getItem(arg2); 
		            Bundle bundle = new Bundle(); 
		            bundle.putSerializable("notice", notice); 
		            intentDetail.putExtras(bundle); 
		            startActivity(intentDetail); 
			}
		});
		//初始化定时器任务
		  task = new TimerTask() {
			    @Override
			    public void run() {
			        // TODO Auto-generated method stub
			    	//发送之前先重置是否接收到数据的标志为false
			    	ReceiveNoticeIQTool.resetIsReceive();
			    	RecommendNoticePacket recommendNoticeIQ = new RecommendNoticePacket();
	           //		ReceiveNoticeIQTool.init();
	        		recommendNoticeIQ.setLongitude(longitude);
	        		recommendNoticeIQ.setLatitude(latitude);
	        		recommendNoticeIQ.setDistance(Integer.toString(dis));
	        		XmppTool.getConnection().sendPacket(recommendNoticeIQ);
	            	if(receiveRecommend())// 接收推荐的活动,如果接受不为空，发送消息
			        handler.sendEmptyMessage(1);
			    }
			};
	}
	//自定义类，实现view点击后的响应
	private class MyOnClickListner implements View.OnClickListener {
		RecommendNoticePacket recommendNoticeIQ = new RecommendNoticePacket();
		public void onClick(View arg0) {
			int buttonID = arg0.getId();
			switch (buttonID) {
			case R.id.notice_create:
				//点击创建按钮的响应
				Intent intent1 = new Intent(NoticeActivity.this, CreateNoticeActivity.class);
				startActivity(intent1);		
				break;
			case R.id.notice_manage:
				//点击“我的”按钮的响应
				Intent intent2 = new Intent(NoticeActivity.this, ManageNoticeActivity.class);
				startActivity(intent2);		
				break;
			//点击限定的距离增加
			case R.id.bt_addDistance:
				 dis = Integer.valueOf(distance.getText().toString());  
				 dis++;
				 if(dis == 2)redDistance.setEnabled(true);   
	                distance.setText(Integer.toString(dis));  
	                //以更新的距离更新界面
	           	//	ReceiveNoticeIQTool.init();
	               	ReceiveNoticeIQTool.resetIsReceive();
	        		recommendNoticeIQ.setLongitude(longitude);
	        		recommendNoticeIQ.setLatitude(latitude);
	        		recommendNoticeIQ.setDistance(Integer.toString(dis));
	        		XmppTool.getConnection().sendPacket(recommendNoticeIQ);
	        	     pd = ProgressDialog.show(NoticeActivity.this, "Hint", "loading……");  
	        		//开启接收线程
	        	        new Thread(new Runnable() {  
	        	            @Override  
	        	            public void run() {  
	        	            	if(receiveRecommend())// 接收推荐的活动,如果接受不为空，发送消息
	        	                handler.sendMessage(handler.obtainMessage(4, noticeList));  // 执行耗时的方法之后发送消给handler  
	        	            	else 
	        	            		handler.sendMessage(handler.obtainMessage(0, "no data")); 
	        	            }  	  
	        	        }).start();  
				break;
			//点击限定的距离减少
			case R.id.bt_redDistance:
				  dis = Integer.valueOf(distance.getText().toString());
	              dis--; 
	              if(dis == 1)redDistance.setEnabled(false);
	                distance.setText(Integer.toString(dis));  
	                //以更新的距离更新界面
	          	//	ReceiveNoticeIQTool.init();
	               	ReceiveNoticeIQTool.resetIsReceive();
	        		recommendNoticeIQ.setLongitude(longitude);
	        		recommendNoticeIQ.setLatitude(latitude);
	        		recommendNoticeIQ.setDistance(Integer.toString(dis));
	        		XmppTool.getConnection().sendPacket(recommendNoticeIQ);
	        	  //接收一个List<noticeInfo>类型的活动列表
	       	     pd = ProgressDialog.show(NoticeActivity.this, "Hint", "loading……");  
	        		//开启接收线程
	        	        new Thread(new Runnable() {  
	        	            @Override  
	        	            public void run() {  
	        	            	if(receiveRecommend())// 接收推荐的活动,如果接受不为空，发送消息
	        	                handler.sendMessage(handler.obtainMessage(4, noticeList));  // 执行耗时的方法之后发送消给handler  
	        	            	else 
	        	            		handler.sendMessage(handler.obtainMessage(0, "no data")); 
	        	            }  	  
	        	        }).start();  
				break;
			default:
				break;
			}
		}
	}
	  private class MyLocationListener implements BDLocationListener {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
			            return ;
				mlatitude=location.getLatitude();
			    mlongitude=location.getLongitude();
			}
		}
	  


	  
	//初始化数据，并显示
	private void initData() {
		// TODO Auto-generated method stub
		longitude = mlongitude+"";
		latitude = mlatitude+"";
  	//	ReceiveNoticeIQTool.init();
	   	ReceiveNoticeIQTool.resetIsReceive();
		RecommendNoticePacket recommendNoticeIQ = new RecommendNoticePacket();
		recommendNoticeIQ.setLongitude(longitude);
		recommendNoticeIQ.setLatitude(latitude);
		recommendNoticeIQ.setDistance(Integer.toString(dis));
		XmppTool.getConnection().sendPacket(recommendNoticeIQ);
		//开启接收线程接收一个List<noticeInfo>类型的活动列表
        /* 显示ProgressDialog */  
        pd = ProgressDialog.show(NoticeActivity.this, "Hint", "loading……");  
        /* 开启一个新线程，在新线程里执行接收 */  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	if(receiveRecommend())// 接收推荐的活动,如果接受不为空，发送消息
                handler.sendMessage(handler.obtainMessage(1, noticeList));  // 执行耗时的方法之后发送消给handler  
            	else 
            		handler.sendMessage(handler.obtainMessage(0, "no data")); 
            }  	  
        }).start();  
		return;
	}
	
	public class NoticeListviewAdapter extends BaseAdapter
	{
		//自定义的Adapter，按照在布局文件中设置的样式显示
		private Context context;
		private ArrayList<NoticeInfo> list;

		/*
		 * 构造函数:
		 * 参数1:context对象
		 * 参数2:列表数据源
		 */
		public NoticeListviewAdapter(Context context, ArrayList<NoticeInfo> list)
		{
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		//获取二级列表的View对象
		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) 
		{
			@SuppressWarnings("unchecked")
	        View layoutNoticeChild = arg1;  
	        final ViewHolder vh;  
	        if (layoutNoticeChild == null) {  
	        	LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	layoutNoticeChild = layoutInflater.inflate(R.layout.exlistview_notice_child, null);    
	            vh = new ViewHolder();  
	            vh. title= (TextView)layoutNoticeChild.findViewById(R.id.tvItemName);
	            vh.distance= (TextView)layoutNoticeChild.findViewById(R.id. tvItemDistance);
	            vh. content= (TextView)layoutNoticeChild.findViewById(R.id.tvItemContent);
	            vh.image = (ImageView)layoutNoticeChild.findViewById(R.id.displayImage);
	            vh. informer= (TextView)layoutNoticeChild.findViewById(R.id.tvItemInformer);
	            vh. startTime= (TextView)layoutNoticeChild.findViewById(R.id.tvItemStartTime);
	            vh. location= (TextView)layoutNoticeChild.findViewById(R.id.tvItemLocation);
	            vh. limit = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLimit);
	            vh.peopleNum = (TextView)layoutNoticeChild.findViewById(R.id.tvItemPeopleNum);
	            vh.label1 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel1);
	            vh.label2 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel2);
	            vh.label3 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel3);
	            vh.label4 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel4);
	            vh.label5 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel5);
	            vh.labels.add(vh.label1);
	            vh.labels.add(vh.label2);
	            vh.labels.add(vh.label3);
	            vh.labels.add(vh.label4);
	            vh.labels.add(vh.label5);
	            vh.contact = (LinearLayout)layoutNoticeChild.findViewById(R.id.btContact);
	            vh.comment = (LinearLayout)layoutNoticeChild.findViewById(R.id.btComment);
	            vh.join =  (LinearLayout)layoutNoticeChild.findViewById(R.id.btJoin);
	            vh.joinText = (TextView)layoutNoticeChild.findViewById(R.id.tvItemJoinText);
	            layoutNoticeChild.setTag(vh);  
	        } else {  
	            vh = (ViewHolder)layoutNoticeChild.getTag();  
	        } 
	        //holder中各元素设置相应的属性
	    	final NoticeInfo child = (NoticeInfo) list.get(arg0);
	    	//ImgHelper imgHelper=new ImgHelper();
	    	if(child.getImageString()!=null && child.getImageString().length() > 0){   
	    	try {
					    Bitmap imageBitmap=ImgHelper.bytesToBitmap(ImgHelper.decode(child.getImageString()));
					    vh.image.setImageBitmap(imageBitmap); 
					    vh.image.setVisibility(View.VISIBLE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	       	vh.title.setText(child.getTitle());
			vh.distance.setText(child.getDistance()+"km");
			vh.content.setText(child.getContent());
			vh.informer.setText(child.getInformer());
			vh.startTime.setText(child.getStartTime());
			vh. location.setText(child.getLocation());
			vh. limit.setText(child.getLimit());
			vh.peopleNum.setText(child.getPeopleNum());
			//如果已经报名过，则报名的按钮直接变为取消，
			if(child.getIsJoined()!=null&&Integer.parseInt(child.getIsJoined())==1){
				joinFlag = false;
			    vh.joinText.setText("cancel");
			}
			for(int i=0;i<child.getLabels().size()&&i<5;i++)
			{
              vh.labels.get(i).setText(child.getLabels().get(i));
              vh.labels.get(i).setVisibility(View.VISIBLE);
			}
				
	        vh.contact.setOnClickListener(new OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//点击联系按钮之后的响应,打开聊天页面
					User user = new User();
					user.setName(child.getInformer());
					Intent intend5 = new Intent(NoticeActivity.this,DialogActivity.class);
					intend5.putExtra("user", user);
					startActivity(intend5);
	           //Toast.makeText(NoticeActivity.this,"点击了联系", Toast.LENGTH_SHORT).show();  
	            }  
	        }); 
	        vh.comment.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//点击评论按钮之后的响应
	        		Intent intent4 = new Intent(NoticeActivity.this, CommentNoticeActivity.class);
	        		//传递一捆数据
	        		//Bundle bund = new Bundle();
					//bund.putSerializable("NoticeID", child.getId());
					intent4.putExtra("NoticeID", child.getId());
					startActivity(intent4);		
	            }  
	        }); 
	        vh.join.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//点击参加按钮之后的响应，先判断是要参加还要取消
	            	int intPeopleNum = Integer.parseInt(child.getPeopleNum());
	            	if(joinFlag){
	            	   //如果是报名参加
	            	   	if(sendPacket(child.getId())){
	            	   		//如果请求成功,报名参加上的文字变为取消，报名人数减1
	            			joinFlag = false;
	            	         vh.joinText.setText("cancel");
	            	         vh.peopleNum.setText(String.valueOf(intPeopleNum + 1)); 
	            	         child.setPeopleNum(String.valueOf(intPeopleNum + 1));
	            	   	}
	               }else if(!joinFlag){
	            	   //如果是取消报名
	            	 	if(sendPacket(child.getId())){
	            	   		//如果请求成功,取消参加上的文字变为报名，报名人数加1
	            			joinFlag = true; 
	            	         vh.joinText.setText("join");
	            	         vh.peopleNum.setText(String.valueOf(intPeopleNum -1));
	            	         child.setPeopleNum(String.valueOf(intPeopleNum - 1));
	            	   	}
	               }          
	            }  
	        });      
			return layoutNoticeChild;
		}	
	}
	
	private boolean sendPacket(String id) {
		
		if(joinFlag){
			//如果是报名，发送
		JoinNoticePacket joinNoticeIQ = new JoinNoticePacket();
		joinNoticeIQ.setNoticeID(id);
		XmppTool.getConnection().sendPacket(joinNoticeIQ);
		//开启接收线程
        pd = ProgressDialog.show(NoticeActivity.this, "Hint", "joining……");  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	if(receiveResult()) 
            		handler.sendEmptyMessage(2);
            	else
            		{
            		handler.sendEmptyMessage(3); 
            	    success = false;
            		}
            }  	  
        }).start();  
   }
   else if(!joinFlag){
	//如果是取消报名，发送取消报名的数据包并接收服务器发送的反馈
//	   ReceiveNoticeIQTool2.init();
	CancelNoticePacket cancelNoticeIQ = new CancelNoticePacket();
	cancelNoticeIQ.setNoticeID(id);
	XmppTool.getConnection().sendPacket(cancelNoticeIQ);
    pd = ProgressDialog.show(NoticeActivity.this, "Hint", "canceling……");  
    new Thread(new Runnable() {  
        @Override  
        public void run() {  
        	if(receiveResult()) 
        		handler.sendEmptyMessage(2);
        	else
        		{
        		handler.sendEmptyMessage(3); 
        	    success = false;
        		}
        }  	  
    }).start();  
   }
		return success;
	}
	
	static class ViewHolder
	{

		public TextView title;
		public TextView distance;
		public TextView content;
		public ImageView image;
		public TextView informer;
		public TextView startTime;
		public TextView location;
		public TextView limit;	
		public TextView joinText;	
		public ArrayList<TextView> labels = new ArrayList<TextView>();
	   public TextView label1;
		public TextView label2;
		public TextView label3;
		public TextView label4;
		public TextView label5;
		public TextView peopleNum;
		public LinearLayout contact;
		public LinearLayout comment;
		public LinearLayout join;
	}
	
//在线程中执行的耗时操作  
    private boolean receiveRecommend() {  
  	  //接收一个List<noticeInfo>类型的活动列表
    	count = 0;
  	//	while(noticeList.size()==0&&count<20){
  		while((!ReceiveNoticeIQTool.getIsReceive())&&count<60){
  		try {
  			Thread.sleep(300);
  			count++;
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		}
  		noticeList = ReceiveNoticeIQTool.getNoticeList();
  		ReceiveNoticeIQTool.resetIsReceive();
  		return noticeList.size()!=0;
    }  
    
    private boolean receiveResult() {  
    	  //接收一个List<noticeInfo>类型的活动列表
    	count = 0;
		while(ReceiveNoticeIQTool.getResult()==null && count <20){
		try {
			Thread.sleep(300);
			count++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if("1".equals(ReceiveNoticeIQTool.getResult()))
			{
			ReceiveNoticeIQTool.resetResult();
			return true;	
			}
		else{
			ReceiveNoticeIQTool.resetResult();
			return false;
		} 		
      }   
    
    Handler handler=new Handler()  
    {   
    	@Override  
        public void handleMessage(Message msg)  // handler接收到消息后就会执行此方法 
        {     
    		super.handleMessage(msg);
             switch(msg.what)  
            {  
            case 1:  //如果是推荐活动
            	  pd.dismiss();// 关闭ProgressDialog 
        		    noticeViewAdapter = new NoticeListviewAdapter(NoticeActivity.this, noticeList);
        	 		noticeListView.setAdapter(noticeViewAdapter);
        	 		//Receive Notice Successfully
        	 	/*	 NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        	 		 Notification mNotification=new Notification(R.drawable.ic_launcher,"你有一个活动通知",System.currentTimeMillis());
        	 		 Intent intent=new Intent(NoticeActivity.this,NoticeActivity.class);
        	 		 PendingIntent pendingIntent=PendingIntent.getActivity(NoticeActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        	 		 mNotification.setLatestEventInfo(NoticeActivity.this, "MSN", "附近有活动,点击查看", pendingIntent);
        	 		 notificationManager.notify(1,mNotification);*/
                break;  
            case 2:  //如果是结果成功
            	 pd.dismiss();// 关闭ProgressDialog
            	Toast.makeText(NoticeActivity.this,"Succeed", Toast.LENGTH_SHORT).show();
               break;  
            case 3:  //如果是结果操作失败
           	 pd.dismiss();// 关闭ProgressDialog
           	Toast.makeText(NoticeActivity.this,"Failed", Toast.LENGTH_SHORT).show();
              break;  
            case 4:  //如果是更新界面
          	  pd.dismiss();// 关闭ProgressDialog 
         	noticeViewAdapter.notifyDataSetChanged();
              break;  
            case 5:  //如果是定时更新
            	
           	noticeViewAdapter.notifyDataSetChanged();
                break;  
            case 0:  //如果没有数据
          	  pd.dismiss();// 关闭ProgressDialog 
          	noticeViewAdapter = new NoticeListviewAdapter(NoticeActivity.this, noticeList);
	 		noticeListView.setAdapter(noticeViewAdapter);
           	Toast.makeText(NoticeActivity.this,"No data", Toast.LENGTH_SHORT).show();
              break;  
            default:  
                break;        
            }  
        }  
    };

}