package com.mobile.security.ui;

import com.mobile.security.R;
import com.mobile.security.engine.AndroidDeviceInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MobileInfoActivity extends Activity implements OnClickListener  {
	
	private Button btn_return;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		AndroidDeviceInfo ADI=new AndroidDeviceInfo(MobileInfoActivity.this);
		String temp=ADI.getPhoneInfo();

		TextView  TV = (TextView) findViewById(R.id.textView1);
		TV.setText(temp);
		
		btn_return = (Button) findViewById(R.id.info_fanhui);
		btn_return.setOnClickListener(MobileInfoActivity.this);
		 
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.info_fanhui:
			finish();
			break;
		}
	}
}