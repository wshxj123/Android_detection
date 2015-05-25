package com.mobile.security.ui;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.mobile.security.R;
import com.mobile.security.utils.FileCopyUtil;
import com.mobile.security.utils.Logger;
import com.mobile.security.utils.MD5Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {

	protected static final int SCAN = 70;
	protected static final int VIRUS = 71;
	protected static final int FINISH = 72;
	private ImageView iv_anti_virus;
	private AnimationDrawable anti_anim;
	private Button bt_kill_virus;
	private ProgressBar pb;
	private LinearLayout ll;
	private PackageManager pm;
	private SQLiteDatabase db;
	private ScrollView sv;
	private boolean isscanning = false;
	private List<String> virusInfo;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCAN:
				TextView tv = new TextView(getApplicationContext());
				tv.setText("正在查杀" + (String) msg.obj);

				ll.addView(tv);
				sv.scrollBy(0, 60);
				break;

			case VIRUS:
				TextView tv2 = new TextView(getApplicationContext());
				tv2.setText("发现病毒,病毒类型" + (String) msg.obj);
				ll.addView(tv2);
				sv.scrollBy(0, 60);
				break;
			case FINISH:

				// ll.removeAllViews();
				TextView tv3 = new TextView(getApplicationContext());
				int count = (Integer) msg.obj;
				if (count > 0)
					bt_kill_virus.setVisibility(View.VISIBLE);
				tv3.setText("扫描完毕 发现" + count + "个病毒");
				ll.addView(tv3);
				sv.scrollBy(0, 60);
				isscanning = false;
				anti_anim.stop();
				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.antivirus);
		bt_kill_virus = (Button) this.findViewById(R.id.bt_kill_virus);
		bt_kill_virus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (String packname : virusInfo) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_DELETE);
					intent.setData(Uri.parse("package:" + packname));
					startActivity(intent);
				}

			}
		});
		sv = (ScrollView) this.findViewById(R.id.sv);
		virusInfo = new ArrayList<String>();
		iv_anti_virus = (ImageView) this.findViewById(R.id.iv_anti_virus);
		iv_anti_virus.setBackgroundResource(R.drawable.antivirus);
		pb = (ProgressBar) this.findViewById(R.id.pb);
		ll = (LinearLayout) this.findViewById(R.id.ll_anti_virus_status);
		anti_anim = (AnimationDrawable) iv_anti_virus.getBackground();
		File file = new File(getFilesDir(), "antivirus.db");
		pm = getPackageManager();
		if (!file.exists()) {
			// 拷贝数据库
			try {
				InputStream is = getAssets().open("antivirus.db");
				FileCopyUtil.copyFile(is, file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,
				SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (isscanning)
				return false;
			anti_anim.start();
			scanVirus();
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 查杀病毒,耗时操作
	 */
	public void scanVirus() {

		isscanning = true;
		new Thread() {
			public void run() {
				List<PackageInfo> packinfos = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
								| PackageManager.GET_SIGNATURES);
				pb.setMax(packinfos.size());
				int total = 0;
				int totalvirsu = 0;
				for (PackageInfo packinfo : packinfos) {
					try {
						sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String packname = packinfo.packageName;
					Message msg = Message.obtain();
					msg.what = SCAN;
					msg.obj = packname;
					handler.sendMessage(msg);

					Logger.i("###packname", packinfo.packageName);

					Signature[] signatures = packinfo.signatures;
					if (signatures != null) {
						StringBuilder sb = new StringBuilder();
						for (Signature s : signatures) {
							sb.append(s);
						}
						String signstr = sb.toString();
						String md5 = MD5Utils.encode(signstr);
						// 查询数据库 判断这个md5是否在数据库里面存在
						Cursor cursor = db.rawQuery(
								"select desc from datable where md5=?",
								new String[] { md5 });
						if (cursor.moveToFirst()) {
							String desc = cursor.getString(0);
							msg = Message.obtain();
							msg.what = VIRUS;
							msg.obj = desc;
							handler.sendMessage(msg);
							totalvirsu++;
							virusInfo.add(packname);
						}
						cursor.close();
					}
					total++;
					pb.setProgress(total);

				}

				Message msg = Message.obtain();
				msg.what = FINISH;
				msg.obj = totalvirsu;
				handler.sendMessage(msg);
			};
		}.start();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
	}

}