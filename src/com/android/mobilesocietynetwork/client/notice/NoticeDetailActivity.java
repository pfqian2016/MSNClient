package com.android.mobilesocietynetwork.client.notice;

import java.util.ArrayList;
import java.util.List;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.info.NoticeInfo;
import com.android.mobilesocietynetwork.client.packet.CancelNoticePacket;
import com.android.mobilesocietynetwork.client.packet.JoinNoticePacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.R;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class NoticeDetailActivity extends Activity {

	private RadioGroup radioGroup;
    private Button btReturn;
	private final int LISTDIALOG = 1;
	// 页卡内容
	private ViewPager mPager;
	// Tab页面列表
	private List<View> listViews;
	// 当前页卡编号
	private LocalActivityManager manager = null;
	private MyPagerAdapter mpAdapter = null;
	private int index;
	
	//private ImageView img;
	private TextView title;
	private TextView distance;
	private TextView content;
	private TextView informer;
	private TextView startTime;
	private TextView location;
	private TextView limit;	
   private TextView label1;
	private TextView label2;
	private TextView label3;
	private TextView label4;
	private TextView label5;
	private ArrayList<TextView> labels = new ArrayList<TextView>();
	private TextView peopleNum;
	public TextView joinText;
	public LinearLayout join;
	private NoticeInfo notice = new NoticeInfo();
	private boolean joinFlag = true;
    private Intent intentParticipant;
    private Intent intentComment;

	// 更新intent传过来的值
	@Override
	protected void onNewIntent(Intent intent)
	{
		setIntent(intent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{}

	@Override
	public void onBackPressed()
	{
		Log.i("", "onBackPressed()");
		super.onBackPressed();
	}

	@Override
	protected void onPause()
	{
		Log.i("", "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		Log.i("", "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		Log.i("", "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (getIntent() != null)
		{
			index = getIntent().getIntExtra("index", 0);
			mPager.setCurrentItem(index);
			setIntent(null);
		}
		else
		{
			if (index < 2)
			{
				index = index + 1;
				mPager.setCurrentItem(index);
				index = index - 1;
				mPager.setCurrentItem(index);
			}
			else if (index == 2)
			{
				index = index - 1;
				mPager.setCurrentItem(index);
				index = index + 1;
				mPager.setCurrentItem(index);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notice_detail);
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(NoticeDetailActivity.this);
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
	    notice = (NoticeInfo) getIntent().getSerializableExtra("notice"); 
		initView();
		initControl();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
	//	img.setImageResource(notice.getImg());  
	    title.setText(notice.getTitle());
		distance.setText(notice.getDistance()+"km");
	    content.setText(notice.getContent());
		informer.setText(notice.getInformer());
		startTime.setText(notice.getStartTime());
		location.setText(notice.getLocation());
	    limit.setText(notice.getLimit());
		peopleNum.setText(notice.getPeopleNum());
		for(int i=0;i<notice.getLabels().size()&&i<5;i++)
		{
          labels.get(i).setText(notice.getLabels().get(i));
          labels.get(i).setVisibility(View.VISIBLE);
		}
	}

	private void initControl() {
		// TODO Auto-generated method stub

		btReturn.setOnClickListener(new OnClickListener(){
	    	
	    	@Override
	    	public void onClick(View v){
	    		NoticeDetailActivity.this.finish();
	    	}});
		radioGroup.check(R.id. bt_participants);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId)
					{
						switch (checkedId)
						{
							case R.id. bt_participants:
								index = 0;
								listViews.set(
										0,
										getView("A",intentParticipant));
								mpAdapter.notifyDataSetChanged();
								mPager.setCurrentItem(0);
								break;
							case R.id.bt_comments:
								index = 1;
								listViews.set(
										1,
										getView("B", intentComment));
								mpAdapter.notifyDataSetChanged();
								mPager.setCurrentItem(1);
								break;
							default:
								break;
						}
					}
				});
        join.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	//点击参加按钮之后的响应，先判断是要参加还要取消
            	join.setEnabled(false);
            	if(joinFlag){
            	   //如果是报名参加
            	   	if(sendPacket(notice.getId())){
            	   		//如果请求成功,报名参加上的文字变为取消，报名人数减1
            			joinFlag = false;
            	         join.setEnabled(true);
            	         joinText.setText("cancel");
            	   	}else   //如果请求失败，不改变上面文字
            	   		join.setEnabled(true);
               }else if(!joinFlag){
            	   //如果是取消报名
            	 	if(sendPacket(notice.getId())){
            	   		//如果请求成功,取消参加上的文字变为报名，报名人数加1
            			joinFlag = true; 
            	 		join.setEnabled(true);
            	        joinText.setText("join");
            	   	}else   //如果请求失败，不改变上面文字
            	   		join.setEnabled(true);
               }          
            }  
        });      
	}

	private void initView() {
		// TODO Auto-generated method stub
		mPager = (ViewPager) findViewById(R.id.noticeDetail_viewpager);
		btReturn = (Button) findViewById(R.id.btReturn);
		radioGroup = (RadioGroup) this.findViewById(R.id.noticeDetail_radiogroup);
	//	img = (ImageView)findViewById(R.id.imageViewNotice);
        title= (TextView)findViewById(R.id.tvItemName);
        distance= (TextView)findViewById(R.id. tvItemDistance);
        content= (TextView)findViewById(R.id.tvItemContent);
        informer= (TextView)findViewById(R.id.tvItemInformer);
        startTime= (TextView)findViewById(R.id.tvItemStartTime);
        location= (TextView)findViewById(R.id.tvItemLocation);
        limit = (TextView)findViewById(R.id.tvItemLimit);
        peopleNum = (TextView)findViewById(R.id.tvItemPeopleNum);
        label1 = (TextView)findViewById(R.id.tvItemLabel1);
        label2 = (TextView)findViewById(R.id.tvItemLabel2);
        label3 = (TextView)findViewById(R.id.tvItemLabel3);
        label4 = (TextView)findViewById(R.id.tvItemLabel4);
        label5 = (TextView)findViewById(R.id.tvItemLabel5);
        labels.add(label1);
        labels.add(label2);
        labels.add(label3);
        labels.add(label4);
        labels.add(label5);
		for(int i=0;i<notice.getLabels().size()&&i<5;i++)
		{
          labels.get(i).setText(notice.getLabels().get(i));
          labels.get(i).setVisibility(View.VISIBLE);
		}
        join =  (LinearLayout)findViewById(R.id.btJoin);
        joinText = (TextView)findViewById(R.id.tvItemJoinText);
        //若不判断是否为null，则程序会自动闪退
		if(notice.getIsJoined()!=null&&Integer.parseInt(notice.getIsJoined())==1){
			joinFlag = false;
		    joinText.setText("取消");
		}
		InitViewPager();
	}
	
	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager()
	{
		listViews = new ArrayList<View>();
		mpAdapter = new MyPagerAdapter(listViews);
		intentParticipant = new Intent(NoticeDetailActivity.this, NoticeDetailParticipantActivity.class);
		intentParticipant.putExtra("NoticeID", notice.getId());
		listViews.add(getView("A", intentParticipant));
		intentComment = new Intent(NoticeDetailActivity.this, NoticeDetailCommentActivity.class);
		intentComment.putExtra("NoticeID", notice.getId());
		listViews.add(getView("B", intentComment));
		mPager.setOffscreenPageLimit(0);
		mPager.setAdapter(mpAdapter);
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter
	{
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews)
		{
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0)
		{}

		@Override
		public int getCount()
		{
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1)
		{
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{}

		@Override
		public Parcelable saveState()
		{
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{}
	}

	/**
	 * 页卡切换监听，ViewPager改变同样改变TabHost内容
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener
	{
		@SuppressWarnings("deprecation")
		public void onPageSelected(int arg0)
		{
			manager.dispatchResume();
			switch (arg0)
			{
				case 0:
					index = 0;
					radioGroup.check(R.id.bt_participants);
					listViews.set(
							0,
							getView("A",intentParticipant));
					mpAdapter.notifyDataSetChanged();
					break;
				case 1:
					index = 1;
					radioGroup.check(R.id.bt_comments);
					listViews.set(
							1,
							getView("B", intentComment));
					mpAdapter.notifyDataSetChanged();
					break;
			}
		}

		public void onPageScrolled(int arg0, float arg1, int arg2)
		{}

		public void onPageScrollStateChanged(int arg0)
		{}
	}


	@SuppressWarnings("deprecation")
	private View getView(String id, Intent intent)
	{
		return manager.startActivity(id, intent).getDecorView();
	}
	
	private boolean sendPacket(String id) {
		ReceiveNoticeIQTool recieveTool = new ReceiveNoticeIQTool();
		if(joinFlag){
			//如果是报名，发送
		recieveTool.init();
		JoinNoticePacket joinNoticeIQ = new JoinNoticePacket();
		joinNoticeIQ.setNoticeID(id);
		XmppTool.getConnection().sendPacket(joinNoticeIQ);
		while(recieveTool.getResult()==null){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if("1".equals(recieveTool.getResult())){
			Toast.makeText(NoticeDetailActivity.this,"Succeed", Toast.LENGTH_SHORT).show();
			return true;	
		}
		else Toast.makeText(NoticeDetailActivity.this,"Failed", Toast.LENGTH_SHORT).show();  
   }
   else if(!joinFlag){
	//如果是取消报名，发送取消报名的数据包并接收服务器发送的反馈
	recieveTool.init();
	CancelNoticePacket cancelNoticeIQ = new CancelNoticePacket();
	cancelNoticeIQ.setNoticeID(id);
	XmppTool.getConnection().sendPacket(cancelNoticeIQ);
	while(recieveTool.getResult()==null){
	try {
		Thread.sleep(100);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	if("1".equals(recieveTool.getResult())){
		Toast.makeText(NoticeDetailActivity.this,"Cancel successfully", Toast.LENGTH_SHORT).show(); 
       return true;
	}
	else Toast.makeText(NoticeDetailActivity.this,"Cancel failed", Toast.LENGTH_SHORT).show();  
   }
		return false;
	}

}
