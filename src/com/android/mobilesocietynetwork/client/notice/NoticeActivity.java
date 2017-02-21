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
	private boolean joinFlag = true;//��ǰ�����Ǳ�������ȡ������
	private boolean success = true;//��ǰ�����Ƿ�ɹ��ı�־
    private ProgressDialog pd;  
//	private ReceiveNoticeIQTool ReceiveNoticeIQTool = new ReceiveNoticeIQTool();
	//��ʱ��
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
//��ʼ�����棬�ҵ�Layout�еĿؼ�
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
	
//Ϊ���Ե���Ŀؼ����ü���
	private void initControl() {
		ReceiveNoticeIQTool.init();
//		ReceiveNoticeIQTool2.init();
		// TODO Auto-generated method stub
		MyOnClickListner noticeButtonOnclick = new MyOnClickListner();
		noticeCreate.setOnClickListener(noticeButtonOnclick);
		noticeManage.setOnClickListener(noticeButtonOnclick);
		addDistance.setOnClickListener(noticeButtonOnclick);
		redDistance.setOnClickListener(noticeButtonOnclick);
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
	    mLocationClient.registerLocationListener(myListener );    //ע���������
	    LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		mLocationClient.setLocOption(option);
		//��ȡ��γ��
		mLocationClient.start();
		mLocationClient.stop();
		//����б��е�ÿһ���������Ӧ
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
		//��ʼ����ʱ������
		  task = new TimerTask() {
			    @Override
			    public void run() {
			        // TODO Auto-generated method stub
			    	//����֮ǰ�������Ƿ���յ����ݵı�־Ϊfalse
			    	ReceiveNoticeIQTool.resetIsReceive();
			    	RecommendNoticePacket recommendNoticeIQ = new RecommendNoticePacket();
	           //		ReceiveNoticeIQTool.init();
	        		recommendNoticeIQ.setLongitude(longitude);
	        		recommendNoticeIQ.setLatitude(latitude);
	        		recommendNoticeIQ.setDistance(Integer.toString(dis));
	        		XmppTool.getConnection().sendPacket(recommendNoticeIQ);
	            	if(receiveRecommend())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
			        handler.sendEmptyMessage(1);
			    }
			};
	}
	//�Զ����࣬ʵ��view��������Ӧ
	private class MyOnClickListner implements View.OnClickListener {
		RecommendNoticePacket recommendNoticeIQ = new RecommendNoticePacket();
		public void onClick(View arg0) {
			int buttonID = arg0.getId();
			switch (buttonID) {
			case R.id.notice_create:
				//���������ť����Ӧ
				Intent intent1 = new Intent(NoticeActivity.this, CreateNoticeActivity.class);
				startActivity(intent1);		
				break;
			case R.id.notice_manage:
				//������ҵġ���ť����Ӧ
				Intent intent2 = new Intent(NoticeActivity.this, ManageNoticeActivity.class);
				startActivity(intent2);		
				break;
			//����޶��ľ�������
			case R.id.bt_addDistance:
				 dis = Integer.valueOf(distance.getText().toString());  
				 dis++;
				 if(dis == 2)redDistance.setEnabled(true);   
	                distance.setText(Integer.toString(dis));  
	                //�Ը��µľ�����½���
	           	//	ReceiveNoticeIQTool.init();
	               	ReceiveNoticeIQTool.resetIsReceive();
	        		recommendNoticeIQ.setLongitude(longitude);
	        		recommendNoticeIQ.setLatitude(latitude);
	        		recommendNoticeIQ.setDistance(Integer.toString(dis));
	        		XmppTool.getConnection().sendPacket(recommendNoticeIQ);
	        	     pd = ProgressDialog.show(NoticeActivity.this, "Hint", "loading����");  
	        		//���������߳�
	        	        new Thread(new Runnable() {  
	        	            @Override  
	        	            public void run() {  
	        	            	if(receiveRecommend())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
	        	                handler.sendMessage(handler.obtainMessage(4, noticeList));  // ִ�к�ʱ�ķ���֮��������handler  
	        	            	else 
	        	            		handler.sendMessage(handler.obtainMessage(0, "no data")); 
	        	            }  	  
	        	        }).start();  
				break;
			//����޶��ľ������
			case R.id.bt_redDistance:
				  dis = Integer.valueOf(distance.getText().toString());
	              dis--; 
	              if(dis == 1)redDistance.setEnabled(false);
	                distance.setText(Integer.toString(dis));  
	                //�Ը��µľ�����½���
	          	//	ReceiveNoticeIQTool.init();
	               	ReceiveNoticeIQTool.resetIsReceive();
	        		recommendNoticeIQ.setLongitude(longitude);
	        		recommendNoticeIQ.setLatitude(latitude);
	        		recommendNoticeIQ.setDistance(Integer.toString(dis));
	        		XmppTool.getConnection().sendPacket(recommendNoticeIQ);
	        	  //����һ��List<noticeInfo>���͵Ļ�б�
	       	     pd = ProgressDialog.show(NoticeActivity.this, "Hint", "loading����");  
	        		//���������߳�
	        	        new Thread(new Runnable() {  
	        	            @Override  
	        	            public void run() {  
	        	            	if(receiveRecommend())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
	        	                handler.sendMessage(handler.obtainMessage(4, noticeList));  // ִ�к�ʱ�ķ���֮��������handler  
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
	  


	  
	//��ʼ�����ݣ�����ʾ
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
		//���������߳̽���һ��List<noticeInfo>���͵Ļ�б�
        /* ��ʾProgressDialog */  
        pd = ProgressDialog.show(NoticeActivity.this, "Hint", "loading����");  
        /* ����һ�����̣߳������߳���ִ�н��� */  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	if(receiveRecommend())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
                handler.sendMessage(handler.obtainMessage(1, noticeList));  // ִ�к�ʱ�ķ���֮��������handler  
            	else 
            		handler.sendMessage(handler.obtainMessage(0, "no data")); 
            }  	  
        }).start();  
		return;
	}
	
	public class NoticeListviewAdapter extends BaseAdapter
	{
		//�Զ����Adapter�������ڲ����ļ������õ���ʽ��ʾ
		private Context context;
		private ArrayList<NoticeInfo> list;

		/*
		 * ���캯��:
		 * ����1:context����
		 * ����2:�б�����Դ
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
		//��ȡ�����б��View����
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
	        //holder�и�Ԫ��������Ӧ������
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
			//����Ѿ��������������İ�ťֱ�ӱ�Ϊȡ����
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
	            	//�����ϵ��ť֮�����Ӧ,������ҳ��
					User user = new User();
					user.setName(child.getInformer());
					Intent intend5 = new Intent(NoticeActivity.this,DialogActivity.class);
					intend5.putExtra("user", user);
					startActivity(intend5);
	           //Toast.makeText(NoticeActivity.this,"�������ϵ", Toast.LENGTH_SHORT).show();  
	            }  
	        }); 
	        vh.comment.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//������۰�ť֮�����Ӧ
	        		Intent intent4 = new Intent(NoticeActivity.this, CommentNoticeActivity.class);
	        		//����һ������
	        		//Bundle bund = new Bundle();
					//bund.putSerializable("NoticeID", child.getId());
					intent4.putExtra("NoticeID", child.getId());
					startActivity(intent4);		
	            }  
	        }); 
	        vh.join.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//����μӰ�ť֮�����Ӧ�����ж���Ҫ�μӻ�Ҫȡ��
	            	int intPeopleNum = Integer.parseInt(child.getPeopleNum());
	            	if(joinFlag){
	            	   //����Ǳ����μ�
	            	   	if(sendPacket(child.getId())){
	            	   		//�������ɹ�,�����μ��ϵ����ֱ�Ϊȡ��������������1
	            			joinFlag = false;
	            	         vh.joinText.setText("cancel");
	            	         vh.peopleNum.setText(String.valueOf(intPeopleNum + 1)); 
	            	         child.setPeopleNum(String.valueOf(intPeopleNum + 1));
	            	   	}
	               }else if(!joinFlag){
	            	   //�����ȡ������
	            	 	if(sendPacket(child.getId())){
	            	   		//�������ɹ�,ȡ���μ��ϵ����ֱ�Ϊ����������������1
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
			//����Ǳ���������
		JoinNoticePacket joinNoticeIQ = new JoinNoticePacket();
		joinNoticeIQ.setNoticeID(id);
		XmppTool.getConnection().sendPacket(joinNoticeIQ);
		//���������߳�
        pd = ProgressDialog.show(NoticeActivity.this, "Hint", "joining����");  
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
	//�����ȡ������������ȡ�����������ݰ������շ��������͵ķ���
//	   ReceiveNoticeIQTool2.init();
	CancelNoticePacket cancelNoticeIQ = new CancelNoticePacket();
	cancelNoticeIQ.setNoticeID(id);
	XmppTool.getConnection().sendPacket(cancelNoticeIQ);
    pd = ProgressDialog.show(NoticeActivity.this, "Hint", "canceling����");  
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
	
//���߳���ִ�еĺ�ʱ����  
    private boolean receiveRecommend() {  
  	  //����һ��List<noticeInfo>���͵Ļ�б�
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
    	  //����һ��List<noticeInfo>���͵Ļ�б�
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
        public void handleMessage(Message msg)  // handler���յ���Ϣ��ͻ�ִ�д˷��� 
        {     
    		super.handleMessage(msg);
             switch(msg.what)  
            {  
            case 1:  //������Ƽ��
            	  pd.dismiss();// �ر�ProgressDialog 
        		    noticeViewAdapter = new NoticeListviewAdapter(NoticeActivity.this, noticeList);
        	 		noticeListView.setAdapter(noticeViewAdapter);
        	 		//Receive Notice Successfully
        	 	/*	 NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        	 		 Notification mNotification=new Notification(R.drawable.ic_launcher,"����һ���֪ͨ",System.currentTimeMillis());
        	 		 Intent intent=new Intent(NoticeActivity.this,NoticeActivity.class);
        	 		 PendingIntent pendingIntent=PendingIntent.getActivity(NoticeActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        	 		 mNotification.setLatestEventInfo(NoticeActivity.this, "MSN", "�����л,����鿴", pendingIntent);
        	 		 notificationManager.notify(1,mNotification);*/
                break;  
            case 2:  //����ǽ���ɹ�
            	 pd.dismiss();// �ر�ProgressDialog
            	Toast.makeText(NoticeActivity.this,"Succeed", Toast.LENGTH_SHORT).show();
               break;  
            case 3:  //����ǽ������ʧ��
           	 pd.dismiss();// �ر�ProgressDialog
           	Toast.makeText(NoticeActivity.this,"Failed", Toast.LENGTH_SHORT).show();
              break;  
            case 4:  //����Ǹ��½���
          	  pd.dismiss();// �ر�ProgressDialog 
         	noticeViewAdapter.notifyDataSetChanged();
              break;  
            case 5:  //����Ƕ�ʱ����
            	
           	noticeViewAdapter.notifyDataSetChanged();
                break;  
            case 0:  //���û������
          	  pd.dismiss();// �ر�ProgressDialog 
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