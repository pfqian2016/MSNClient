package com.android.mobilesocietynetwork.client.notice;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
//import org.jivesoftware.smack.XmppConnection;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.packet.CreateNoticePacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.DateTimePickDialogUtil;
import com.android.mobilesocietynetwork.client.util.ImgHelper;
import com.android.mobilesocietynetwork.client.R;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

















import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.Log;



public class CreateNoticeActivity extends MyActivity {
	public static final int REQUEST_CODE_PHOTO_PICKED = 1;
	public static final int REQUEST_CODE_VIDEO_PICKED = 2;
	private EditText etTitle;
	private EditText etStartTime;
	private EditText etLocation;
	private EditText etContent;
	private EditText etLimit;
	private ImageView imgNoticePic;
	private ImageView imgFullScreen;
	private ListView elNoticeType;
	private Button btSubmit;
	private Button btQuit;
	private Button btChooseImage;
	//add video
	//private Button btChooseVideo;
	//private VideoView videoNotice;
	private MyAdapter adapter;
	private boolean[][] LabelSelect = new boolean[5][5];
	// �ٶȵ�ͼ
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private double mlongitude;
	private double mlatitude;
	//private Uri videoUri;
	
    private ProgressDialog pd;  
   

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_notice);
		elNoticeType = null;
		initView();
		initControl();
	}

	private void initControl() {
		// TODO Auto-generated method stub
		MyOnClickListner createNoticeButtonOnclick = new MyOnClickListner();
		btSubmit.setOnClickListener(createNoticeButtonOnclick);
		btQuit.setOnClickListener(createNoticeButtonOnclick);
		btChooseImage.setOnClickListener(createNoticeButtonOnclick);
		//btChooseVideo.setOnClickListener(createNoticeButtonOnclick);
		imgNoticePic.setOnClickListener(createNoticeButtonOnclick);
		imgFullScreen.setOnClickListener(createNoticeButtonOnclick);
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		mLocationClient.setLocOption(option);
		// ��ȡ��γ��
		mLocationClient.start();
		mLocationClient.stop();
		// ʱ��ѡ��ؼ�
		etStartTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(CreateNoticeActivity.this, "");
				dateTimePicKDialog.dateTimePicKDialog(etStartTime);

			}
		});
	}

	// get image from media and then set ImageView
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		Bitmap bitmap = null;
		Bitmap bitmapCompress = null;
		ContentResolver cr = getContentResolver();
		if (requestCode == REQUEST_CODE_PHOTO_PICKED) {
			try {
				Uri imageUri = data.getData();
				bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
				//ѹ��ͼƬ
				bitmapCompress=comp(bitmap);
				imgNoticePic.setVisibility(View.VISIBLE);
				imgNoticePic.setImageBitmap(bitmapCompress);
				bitmap.recycle();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	//	if (requestCode == REQUEST_CODE_VIDEO_PICKED) {
	//		   	videoUri = data.getData();
	//			File videoFile = new File(videoUri.toString());
	//			videoNotice.setVisibility(View.VISIBLE);
	//			videoNotice.setVideoPath(videoFile.getPath());
	//	
	//	}

	}

	private class MyOnClickListner implements OnClickListener {

		public void onClick(View arg0) {
			int buttonID = arg0.getId();
			switch (buttonID) {
		//	case R.id.video_NoticeVideo:
		//		Intent intent=new Intent(Intent.ACTION_VIEW);
		//		intent.setDataAndType(videoUri, "video/mp4");
		//		startActivity(intent);
		//		break;
			case R.id.img_NoticePic:
				imgFullScreen.setVisibility(View.VISIBLE);
				imgFullScreen.setImageBitmap(((BitmapDrawable) imgNoticePic.getDrawable()).getBitmap());
				break;
			case R.id.iv_fullscreen:
				imgFullScreen.setVisibility(View.GONE);
				break;
			case R.id.bt_Submit:
				// ����ύ��ť����Ӧ
				if (etTitle.getText().toString().equals("")) {
					Toast.makeText(CreateNoticeActivity.this, "Please enter title", 1).show();
					break;
				}
				if (etStartTime.getText().toString().equals("")) {
					Toast.makeText(CreateNoticeActivity.this, "Please enter time", 1).show();
					break;
				}
				if (etLocation.getText().toString().equals("")) {
					Toast.makeText(CreateNoticeActivity.this, "Please enter place", 1).show();
					break;
				}
				String title = etTitle.getText().toString();
				String startTime = etStartTime.getText().toString();
				String location = etLocation.getText().toString();
				String content = etContent.getText().toString();
				ImgHelper imgHelper = new ImgHelper();
				String imageString="";
				if(imgNoticePic.getDrawable()!=null)
				{
					imageString = imgHelper
						.bitmapToString(((BitmapDrawable) imgNoticePic.getDrawable()).getBitmap());
				}
				
				int limit = 50;
				limit = Integer.parseInt(etLimit.getText().toString());
				// ��ȡ����
				ArrayList<String> list = new ArrayList<String>();
				for (int i = 0; i < LabelSelect.length; i++) {
					for (int j = 0; j < LabelSelect[i].length; j++) {
						if (LabelSelect[i][j] == true)
							list.add(Constants.LabelListEN[i][j]);
					}
				}			
				// ���ʹ������IQ��
							ReceiveNoticeIQTool.init();
							CreateNoticePacket createNoticeIQ = new CreateNoticePacket();
							createNoticeIQ.setTitle(title);
							createNoticeIQ.setTime(startTime);
							createNoticeIQ.setAddress(location);
							createNoticeIQ.setContent(content);
							createNoticeIQ.setLimit(limit);
							createNoticeIQ.setImageString(imageString);
							createNoticeIQ.setLabelList(list);

							String longitude = mlongitude + "";
							String latitude = mlatitude + "";
							createNoticeIQ.setLongitude(longitude);
							createNoticeIQ.setLatitude(latitude);

						XmppTool.getConnection().sendPacket(createNoticeIQ);
				
		        /* ��ʾProgressDialog */  
		        pd = ProgressDialog.show(CreateNoticeActivity.this, "Hint", "Creating����");  
		        /* ����һ�����̣߳������߳���ִ�н��շ��ؽ�� */  
		        new Thread(new Runnable() {  
		            @Override  
		            public void run() {  
		            	if(receiveResult())// ���յ��ɹ�������Ϣ
		                handler.sendEmptyMessage(1);  // ִ�к�ʱ�ķ���֮��������handler  
		            	else 
		            		 handler.sendEmptyMessage(0);  
		            }  	  
		        }).start();  
				break;
			case R.id.bt_Quit:
				// ���ȡ��
				CreateNoticeActivity.this.finish();
				break;
			case R.id.bt_ChooseImage:
				// ������ͼƬ
				// start Intent
				Intent intentImage = new Intent(Intent.ACTION_GET_CONTENT, null);
				intentImage.setType("image/*");
				startActivityForResult(intentImage, REQUEST_CODE_PHOTO_PICKED);
				break;
		//	case R.id.bt_ChooseVideo:
		//		Intent intentVideo = new Intent(Intent.ACTION_GET_CONTENT, null);
		//		intentVideo.setType("video/*");
		//		startActivityForResult(intentVideo, REQUEST_CODE_VIDEO_PICKED);
		//		break;
			default:
				break;
			}
		}
	}

	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			mlatitude = location.getLatitude();
			mlongitude = location.getLongitude();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		etTitle = (EditText) findViewById(R.id.et_Title);
		etStartTime = (EditText) findViewById(R.id.et_StartTime);
		etLocation = (EditText) findViewById(R.id.et_Location);
		etContent = (EditText) findViewById(R.id.et_Content);
		etLimit = (EditText) findViewById(R.id.et_Limit);
		imgNoticePic = (ImageView) findViewById(R.id.img_NoticePic);
		imgFullScreen = (ImageView) findViewById(R.id.iv_fullscreen);
		//btChooseVideo = (Button) findViewById(R.id.bt_ChooseVideo);
		//videoNotice = (VideoView) findViewById(R.id.video_NoticeVideo);
		elNoticeType = (ListView) findViewById(R.id.noticeTypeList);
		btSubmit = (Button) findViewById(R.id.bt_Submit);
		btQuit = (Button) findViewById(R.id.bt_Quit);
		btChooseImage = (Button) findViewById(R.id.bt_ChooseImage);
		adapter = new MyAdapter(this, R.layout.listview_items_register);
		elNoticeType.setAdapter(adapter);

	}

	private class MyAdapter extends ArrayAdapter<Object> {
		int mTextViewResourceID = 0;
		private Context mContext;

		public MyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			mTextViewResourceID = textViewResourceId;
			mContext = context;
		}

		public int getCount() {
			return Constants.ListTitle.length;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			TextView title = null;
			CheckBox checkBox1 = null;
			CheckBox checkBox2 = null;
			CheckBox checkBox3 = null;
			CheckBox checkBox4 = null;
			CheckBox checkBox5 = null;

			// if (convertView == null)
			// {
			convertView = LayoutInflater.from(mContext).inflate(mTextViewResourceID, null);
			title = (TextView) convertView.findViewById(R.id.title_textview_listitems_register);
			checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1_listitems_register);
			checkBox2 = (CheckBox) convertView.findViewById(R.id.checkBox2_listitems_register);
			checkBox3 = (CheckBox) convertView.findViewById(R.id.checkBox3_listitems_register);
			checkBox4 = (CheckBox) convertView.findViewById(R.id.checkBox4_listitems_register);
			checkBox5 = (CheckBox) convertView.findViewById(R.id.checkBox5_listitems_register);
			title.setText(Constants.ListTitle[position]);
			checkBox1.setText(Constants.LabelListCN[position][0]);
			checkBox2.setText(Constants.LabelListCN[position][1]);
			checkBox3.setText(Constants.LabelListCN[position][2]);
			checkBox4.setText(Constants.LabelListCN[position][3]);
			checkBox5.setText(Constants.LabelListCN[position][4]);
			checkBox1.setChecked(LabelSelect[position][0]);
			checkBox2.setChecked(LabelSelect[position][1]);
			checkBox3.setChecked(LabelSelect[position][2]);
			checkBox4.setChecked(LabelSelect[position][3]);
			checkBox5.setChecked(LabelSelect[position][4]);
			checkBox1.setOnCheckedChangeListener(new CheckBoxListener(position));
			checkBox2.setOnCheckedChangeListener(new CheckBoxListener(position));
			checkBox3.setOnCheckedChangeListener(new CheckBoxListener(position));
			checkBox4.setOnCheckedChangeListener(new CheckBoxListener(position));
			checkBox5.setOnCheckedChangeListener(new CheckBoxListener(position));

			// }
			return convertView;
		}
	}

	private class CheckBoxListener implements OnCheckedChangeListener {

		private int position;

		public CheckBoxListener(int position) {
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		@Override
		public void onCheckedChanged(CompoundButton ButtonView, boolean arg1) {
			// TODO Auto-generated method stub
			switch (ButtonView.getId()) {
			case R.id.checkBox1_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][0] = true;
				} else {
					LabelSelect[position][0] = false;
				}
				break;
			case R.id.checkBox2_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][1] = true;
				} else {
					LabelSelect[position][1] = false;
				}
				break;
			case R.id.checkBox3_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][2] = true;
				} else {
					LabelSelect[position][2] = false;
				}
				break;
			case R.id.checkBox4_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][3] = true;
				} else {
					LabelSelect[position][3] = false;
				}
				break;
			case R.id.checkBox5_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][4] = true;
				} else {
					LabelSelect[position][4] = false;
				}
				break;

			default:
				break;
			}
		}

	}

	// if it is fullscreen,press back to return to createActivity,
	// if not,finish this activity
	@Override
	public void onBackPressed() {
		if (imgFullScreen.getVisibility() == 0) {
			imgFullScreen.setVisibility(View.GONE);
		} else {
			finish();
		}
	}
	
  private boolean receiveResult() {  
    	  //����һ��List<noticeInfo>���͵Ļ�б�
    	int count = 0;
		while(ReceiveNoticeIQTool.getResult()==null && count <20){
		try {
			Thread.sleep(500);
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
	            case 1:  //��������ɹ�
	            	  pd.dismiss();// �ر�ProgressDialog 
	            		Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
						CreateNoticeActivity.this.finish();
		                break;  
	            case 0:
	            	pd.dismiss();
	            	Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
	            default:  
	                break;     
	            }
	        }
	    };
	    
	    //ѹ��bitmap
	    private Bitmap comp(Bitmap image) {  
	        
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();         
	        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	        if( baos.toByteArray().length / 1024>1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���    
	            baos.reset();//����baos�����baos  
	            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��  
	        }  
	        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
	        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
	        newOpts.inJustDecodeBounds = true;  
	        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	        newOpts.inJustDecodeBounds = false;  
	        int w = newOpts.outWidth;  
	        int h = newOpts.outHeight;  
	        //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ  
	        float hh = 800f;//�������ø߶�Ϊ800f  
	        float ww = 480f;//�������ÿ��Ϊ480f  
	        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��  
	        int be = 1;//be=1��ʾ������  
	        if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
	            be = (int) (newOpts.outWidth / ww);  
	        } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����  
	            be = (int) (newOpts.outHeight / hh);  
	        }  
	        if (be <= 0)  
	            be = 1;  
	        newOpts.inSampleSize = be;//�������ű���  
	        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
	        isBm = new ByteArrayInputStream(baos.toByteArray());  
	        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	        return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��  
	    }  
	    
	    //����ѹ��
	    private Bitmap compressImage(Bitmap image) {  
	    	  
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
	        int options = 100;  
	        while ( baos.toByteArray().length / 1024>100) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��         
	            baos.reset();//����baos�����baos  
	            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
	            options -= 10;//ÿ�ζ�����10  
	        }  
	        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
	        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
	        return bitmap;  
	    }



}
