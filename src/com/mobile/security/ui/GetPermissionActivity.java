package com.mobile.security.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobile.security.R;
import com.mobile.security.utils.Logger;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GetPermissionActivity extends ListActivity implements
		OnClickListener {
	/** Called when the activity is first created. */
	private TextView tv;
	private LinearLayout linear;
	private Context mContext;
	private Button btn_return;
	public Map<String, String> m = new HashMap<String, String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.permission);

		btn_return = (Button) findViewById(R.id.permission_fanhui);
		btn_return.setOnClickListener(GetPermissionActivity.this);

		mContext = this;

		tv = (TextView) findViewById(R.id.tv);
		linear = (LinearLayout) findViewById(R.id.linear);

		Intent getintent = getIntent();
		String packname = getintent.getStringExtra("name");
		// linear.removeAllViews();
		tv.setText(packname);

		SimpleAdapter adapter = new SimpleAdapter(this,
				getAppPemission(packname), R.layout.permission_item,
				new String[] { "group", "list" }, new int[] {
						R.id.permission_group, R.id.permission_list });
		setListAdapter(adapter);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.permission_fanhui:
			finish();
			break;
		}
	}

	public List<Map<String, Object>> getAppPemission(String pkgName) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		pRead();

		PackageManager pm = this.getPackageManager();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
			String[] packagePermissions = info.requestedPermissions;
			Logger.v("name", info.packageName);
			if (packagePermissions != null) {
				for (int i = 0; i < packagePermissions.length; i++) {
					// 解析permission名称
					if (m.containsKey(packagePermissions[i])) {
						Map<String, Object> tmap = new HashMap<String, Object>();
						String stringarray[] =m.get(packagePermissions[i]).split("，", 2);
						tmap.put("group", stringarray[0]);
						tmap.put("list", stringarray[1]);
						list.add(tmap);
						tmap = null;
					}
					Logger.v("result", packagePermissions[i]);
				}
			} else {
				Logger.v("name", info.packageName + ": no permissions");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void pRead() {
		m.clear();

		InputStream in = null;
		try {
			in = getResources().getAssets().open("permissionmap");
		} catch (IOException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		BufferedReader reader;
		InputStreamReader read;
		try {
			read = new InputStreamReader(in, "UTF-8");
			reader = new BufferedReader(read);
			String tempcontent = "";
			while ((tempcontent = reader.readLine()) != null) {
				String stringarray[] = tempcontent.split("，", 2);
				m.put(stringarray[0], stringarray[1]);
			}
			reader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}