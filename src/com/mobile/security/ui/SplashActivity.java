package com.mobile.security.ui;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.security.R;
import com.mobile.security.domain.UpdateInfo;
import com.mobile.security.engine.DownloadTask;
import com.mobile.security.engine.UpdateInfoService;

public class SplashActivity extends Activity {
	
	public static final String TAG = "Security";
	private UpdateInfo info;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "to check if need to update app");
			if(isNeedUpdate(versionStr)){
				showUpdateDialog();
			}
		}
	};
	private String versionStr;
	private ProgressDialog progressDialog;

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		TextView version = (TextView) findViewById(R.id.tv_splash_version);
		versionStr = getVersion();
		version.setText("版本号 " + versionStr);
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll_splash_main);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(3000);
		ll.startAnimation(alphaAnimation);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage("正在下载...");
		
		new Thread () {
			public void run() {
				try {
					sleep(3000);
					Log.d(TAG, "preper to check if need to update app");
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setIcon(icon);
		builder.setTitle("升级提醒");
		builder.setMessage(info.getDescription());
		builder.setCancelable(false);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					File dir = new File(Environment.getExternalStorageDirectory(), "/Security/update");
					if (!dir.exists()) {
						boolean success = dir.mkdirs();
						Log.d(TAG, "to create folder '/Security/update' status:" + success);
						Log.d(TAG, "to create folder path:" + dir.getPath());
					}
					String apkPath = dir.getPath() + "/new.apk";
					UpdateTask task = new UpdateTask(info.getUrl(), apkPath);
					progressDialog.show();
					Log.d(TAG, "to download new.apk");
					new Thread(task).start();
				} else {
					Toast.makeText(SplashActivity.this, "SD卡不可用,请插入SD卡", Toast.LENGTH_SHORT).show();
					loadMainUI();
				}
			}
		});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadMainUI();
			}
		});
		
		builder.create().show();
	}

	private boolean isNeedUpdate(String versionStr) {
		
		UpdateInfoService updateInfoService = new UpdateInfoService(this);
		try {
			
			info = updateInfoService.getUpdateInfo(R.string.serverUrl);
			String newVersion = info.getVersion();
			if (versionStr.equals(newVersion)) {
				Log.d(TAG, "no need to update");
				Log.d(TAG, "当前版本：" + versionStr);
                Log.d(TAG, "最新版本：" + newVersion);
                //Toast.makeText(this, "已是最新版本", Toast.LENGTH_SHORT).show();
                loadMainUI();
				return false;
			} else {
				Log.d(TAG, "need to update");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "获取版本信息异常,请稍后再试", Toast.LENGTH_SHORT).show();
            loadMainUI();
		}
		return false;
	}

	private void loadMainUI() {
		
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private String getVersion() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	public class UpdateTask implements Runnable {
		
		private String path;
		private String filePath;
		
		public UpdateTask(String path, String filePath) {
			this.path = path;
			this.filePath = filePath;
		}
		
		public void run() {
			try {
				File file = DownloadTask.getFile(path, filePath, progressDialog);
				progressDialog.dismiss();
				Log.d(TAG, "to install new.apk");
				install(file);
			} catch (Exception e) {
				e.printStackTrace();
				progressDialog.dismiss();
				Toast.makeText(SplashActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
				loadMainUI();
			}
		}

		private void install(File file) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			finish();
			startActivity(intent);
		}
	}
}
