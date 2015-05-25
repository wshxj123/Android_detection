package com.mobile.security.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.mobile.security.R;
import com.mobile.security.utils.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetWordsActivity extends Activity {
	// private MyDialog dialog;
	private LinearLayout layout;
	private TextView tView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setwords_dialog);
		// dialog=new MyDialog(this);
		tView = (TextView) findViewById(R.id.jiancewords);
		layout = (LinearLayout) findViewById(R.id.setwords_layout);

		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Securitytest", "Setwords.txt");
		String mwords = "";
		try {
			InputStreamReader read = null;
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader reader = new BufferedReader(read);
			String tempcontent = "";
			while ((tempcontent = reader.readLine()) != null) {
				mwords += tempcontent;
			}
			reader.close();
			Logger.i("###mwords", mwords);
			tView.setText(mwords);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void exitbutton0(View v) {
		String temp = tView.getText().toString();
		Logger.i("##textview dudaode", temp);
		if (temp.equals("")) {
			Toast.makeText(getApplicationContext(), "错误：检测文字不能为空！",
					Toast.LENGTH_SHORT).show();
		} else {
			File file = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/Securitytest", "Setwords.txt");
			try {
				FileOutputStream out = new FileOutputStream(file);
				out.write(temp.getBytes());
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			this.finish();
		}
	}

	public void exitbutton1(View v) {
		this.finish();
	}

	public void exitbutton2(View v) {
		tView.setText("秘密;机密;绝密");
	}

}
