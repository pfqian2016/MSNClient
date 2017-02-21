package com.android.mobilesocietynetwork.client;






import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.mobilesocietynetwork.client.util.Constants;

public class NetworkSetActivity extends Activity {
private EditText etIP;
private EditText etName;
private EditText etPort;
private Button btSubmit;
private Button btQuit;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_network_set);
		initView();
		initControl();
	    
	}

	private void initControl() {
		// TODO Auto-generated method stub
		MyOnClickListner setNetButtonOnclick = new MyOnClickListner();
		btSubmit.setOnClickListener(setNetButtonOnclick);
		btQuit.setOnClickListener(setNetButtonOnclick);
	}
	
	private class MyOnClickListner implements  OnClickListener {
		public void onClick(View arg0) {
			int buttonID = arg0.getId();
			switch (buttonID) {
			case R.id.bt_Submit:
				//点击完成的响应
				if (etIP.getText().toString().equals("")) {
					Toast.makeText(NetworkSetActivity.this, "Please set IP", 1).show();
					break;
				}
				if (etName.getText().toString().equals("")) {
					Toast.makeText(NetworkSetActivity.this, "Please set server domain", 1).show();
					break;
				}
				if (etPort.getText().toString().equals("")) {
					Toast.makeText(NetworkSetActivity.this, "Please set port", 1).show();
					break;
				}
				Constants.SERVER_IP = etIP.getText().toString();
				Constants.SERVER_NAME = etName.getText().toString();
				Constants.SERVER_PORT= Integer.parseInt(etPort.getText().toString());
				Toast.makeText(getApplicationContext(), "Set successfully", Toast.LENGTH_SHORT).show();
				finish();
				break;
				
			case R.id.bt_Quit:
				//点击取消
				NetworkSetActivity.this.finish();
				break;
			default:
				break;
			}
		}
	}
	
	    
	private void initView() {
		// TODO Auto-generated method stub
		etIP = (EditText)findViewById(R.id.et_ip);
		etName = (EditText)findViewById(R.id.et_name);
		etPort = (EditText)findViewById(R.id.et_port);
		btSubmit = (Button)findViewById(R.id.bt_Submit);
		btQuit = (Button)findViewById(R.id.bt_Quit);
	}
	
	


}
