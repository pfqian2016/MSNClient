package com.android.mobilesocietynetwork.client.notice;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.notice.CommentNoticeActivity.SerializableMap;
import com.android.mobilesocietynetwork.client.util.ImgHelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class CommentDetailActivity extends Activity {
	private ImageView mImageView;
	private SerializableMap myMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comment_detail);
		mImageView=(ImageView) findViewById(R.id.comment_detail);
		
		/*myMap=(SerializableMap) getIntent().getSerializableExtra("details");
		HashMap<String , Object> map=myMap.getMap();
		Bitmap imageBitmap=(Bitmap) map.get("image");*/
		String imageString=getIntent().getStringExtra("details");
		Bitmap imageBitmap=null;
		try {
    		byte[] imgByte = ImgHelper.decode(imageString);
				    imageBitmap=ImgHelper.bytesToBitmap(imgByte);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		mImageView.setImageBitmap(imageBitmap);
	}

}
