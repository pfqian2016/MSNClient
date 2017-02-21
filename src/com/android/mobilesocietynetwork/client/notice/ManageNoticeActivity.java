package com.android.mobilesocietynetwork.client.notice;

import java.util.ArrayList;
import java.util.List;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.R;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ManageNoticeActivity extends Activity {
	
	private RadioGroup radioGroup;
private Button btReturn;
	private final int LISTDIALOG = 1;
	// ҳ������
	private ViewPager mPager;
	// Tabҳ���б�
	private List<View> listViews;
	// ��ǰҳ�����
	private LocalActivityManager manager = null;
	private MyPagerAdapter mpAdapter = null;
	private int index;
	

	// ����intent��������ֵ
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
		setContentView(R.layout.activity_manage_notice);
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(ManageNoticeActivity.this);
		
		mPager = (ViewPager) findViewById(R.id.manageNotice_viewpager);
		btReturn = (Button) findViewById(R.id.btReturn);
		btReturn.setOnClickListener(new OnClickListener(){
	    	
	    	@Override
	    	public void onClick(View v){
	    		ManageNoticeActivity.this.finish();
	    	}});
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		InitViewPager();
		radioGroup = (RadioGroup) this.findViewById(R.id.manageNotice_radiogroup);
		radioGroup.check(R.id. bt_created);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId)
					{
						switch (checkedId)
						{
							case R.id. bt_created:
								index = 0;
								listViews.set(
										0,
										getView("A", new Intent(
												ManageNoticeActivity.this,
												ManageCreatedNoticeActivity.class)));
								mpAdapter.notifyDataSetChanged();
								mPager.setCurrentItem(0);
								break;
							case R.id.bt_joined:
								index = 1;
								listViews.set(
										1,
										getView("B", new Intent(
												ManageNoticeActivity.this,
												ManageJoinedNoticeActivity.class)));
								mpAdapter.notifyDataSetChanged();
								mPager.setCurrentItem(1);
								break;
							default:
								break;
						}
					}
				});
	}

	/**
	 * ��ʼ��ViewPager
	 */
	private void InitViewPager()
	{
		Intent intent = null;
		listViews = new ArrayList<View>();
		mpAdapter = new MyPagerAdapter(listViews);
		intent = new Intent(ManageNoticeActivity.this, ManageCreatedNoticeActivity.class);
		listViews.add(getView("A", intent));
		intent = new Intent(ManageNoticeActivity.this, ManageJoinedNoticeActivity.class);
		listViews.add(getView("B", intent));
	
		mPager.setOffscreenPageLimit(0);
		mPager.setAdapter(mpAdapter);
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * ViewPager������
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
	 * ҳ���л�������ViewPager�ı�ͬ���ı�TabHost����
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
					radioGroup.check(R.id.bt_created);
					listViews.set(
							0,
							getView("A", new Intent(ManageNoticeActivity.this,
									ManageCreatedNoticeActivity.class)));
					mpAdapter.notifyDataSetChanged();
					break;
				case 1:
					index = 1;
					radioGroup.check(R.id.bt_joined);
					listViews.set(
							1,
							getView("B", new Intent(ManageNoticeActivity.this,
									ManageJoinedNoticeActivity.class)));
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


}

