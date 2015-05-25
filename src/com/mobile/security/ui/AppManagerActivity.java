package com.mobile.security.ui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.MotionEvent;
import android.view.View;
import com.mobile.security.R;
import com.mobile.security.domain.AppInfo;
import com.mobile.security.engine.AppInfoProvider;
import com.mobile.security.engine.PermissionSpinner;
import com.mobile.security.utils.DensityUtil;
import com.mobile.security.utils.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {
	protected static final int LOAD_APP_FINSISH = 40;
	public static final String TAG = "AppManagerActivity";
	private ListView lv_appmanger;
	private AppInfoProvider provider;
	private LinearLayout ll_loading;
	private List<AppInfo> appinfos;
	private List<AppInfo> userappinfos;
	private AppInfoAdapter adapter;
	private TextView tv_appmanger_title;
	private boolean allapp;
	private boolean loading;
	private PopupWindow popupWindow;
	private String packname;
	private PackageManager packageManager;
	private List<String> list = new ArrayList<String>();
	private HashMap<String, String> mmap = new HashMap<String, String>();
	Context mContext;
	private Map<String, String> permissionMap = new HashMap<String, String>();
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case LOAD_APP_FINSISH:
				// �������ڼ��س���Ľ��Ȳ�����ʾΪ���ɼ�
				ll_loading.setVisibility(View.INVISIBLE);
				getUserAppinfos();
				loading = false;
				// if (allapp) {
				adapter = new AppInfoAdapter(appinfos);
				// } else {
				// adapter = new AppInfoAdapter(userappinfos);
				// }
				lv_appmanger.setAdapter(adapter);
				setspinner();
				break;
			}

		};
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		list.add("���г���");
		list.add("�û�����");
		mContext = this.getApplicationContext();
		// TODO Auto-generated method stub
		allapp = true;
		loading = false;
		super.onCreate(savedInstanceState);
		packageManager = getPackageManager();
		setContentView(R.layout.applationinstall);
		Button btn_return = (Button) findViewById(R.id.ad_manager_fanhui);
		btn_return.setOnClickListener(AppManagerActivity.this);
		lv_appmanger = (ListView) this.findViewById(R.id.lv_appmanger);
		lv_appmanger.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dismissPopwindow();
				int[] arrayOfInt = new int[2];
				view.getLocationInWindow(arrayOfInt);

				int x = arrayOfInt[0] + 60;
				int y = arrayOfInt[1];

				/*
				 * TextView tv = new TextView(getApplicationContext());
				 * tv.setTextColor(Color.RED); tv.setTextSize(20); AppInfo
				 * appinfo = (AppInfo) lv_appmanger.getItemAtPosition(position);
				 * tv.setText(appinfo.getPackname());
				 */
				AppInfo appinfo = (AppInfo) lv_appmanger
						.getItemAtPosition(position);
				packname = appinfo.getPackname();
				Logger.i(TAG, "����" + packname);
				View popupview = View.inflate(getApplicationContext(),
						R.layout.app_popupwindow, null);
				LinearLayout ll_permission = (LinearLayout) popupview
						.findViewById(R.id.ll_app_permission);
				LinearLayout ll_start = (LinearLayout) popupview
						.findViewById(R.id.ll_app_start);
				LinearLayout ll_uninstall = (LinearLayout) popupview
						.findViewById(R.id.ll_app_uninstall);
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_permission.setOnClickListener(AppManagerActivity.this);
				// ��ж�ص����Բ��ֵ�view��������һ����ʶ
				ll_uninstall.setTag(appinfo);

				int top = view.getTop();
				int bottom = view.getBottom();

				popupWindow = new PopupWindow(popupview, getWindowManager()
						.getDefaultDisplay().getWidth()
						- DensityUtil.dip2px(getApplicationContext(), 80),
						bottom
								- top
								+ DensityUtil.dip2px(getApplicationContext(),
										20));
				LinearLayout ll_popup_app = (LinearLayout) popupview
						.findViewById(R.id.ll_popup_app);
				ScaleAnimation sa = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f);
				sa.setDuration(200);
				ll_popup_app.startAnimation(sa);
				// �ǳ���Ҫ popupwindow һ��Ҫȥ���ñ���ͼƬ
				Drawable background = getResources().getDrawable(
						R.drawable.local_popup_bg);
				ll_popup_app.setBackgroundDrawable(background);
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));

				popupWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, x,
						y);
			}
		});

		lv_appmanger.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismissPopwindow();

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopwindow();

			}
		});

		tv_appmanger_title = (TextView) this
				.findViewById(R.id.tv_appmanger_title);
		tv_appmanger_title.setOnClickListener(this);

		ll_loading = (LinearLayout) this
				.findViewById(R.id.ll_app_manger_loading);
		// lv_appmanger.setAdapter(adapter);
		provider = new AppInfoProvider(this);
		loadAppInfo();

	}

	private void loadAppInfo() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				loading = true;
				pRead();
				appinfos = provider.getAppInfos(list, mmap,permissionMap);
				Message msg = Message.obtain();
				msg.what = LOAD_APP_FINSISH;
				handler.sendMessage(msg);

			};
		}.start();
	}
	
	public void pRead() {
		permissionMap.clear();

		InputStream in = null;
		try {
			in = getResources().getAssets().open("permissionmap");
		} catch (IOException e1) {
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
		}
		BufferedReader reader;
		InputStreamReader read;
		try {
			read = new InputStreamReader(in, "UTF-8");
			reader = new BufferedReader(read);
			String tempcontent = "";
			while ((tempcontent = reader.readLine()) != null) {
				String stringarray[] = tempcontent.split("��", 2);
				permissionMap.put(stringarray[0], stringarray[1]);
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
	/**
	 * �رյ�popupwindow
	 */
	private void dismissPopwindow() {
		if (popupWindow != null) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private class AppInfoAdapter extends BaseAdapter {
		List<AppInfo> adapterappinfos;

		public AppInfoAdapter(List<AppInfo> adapterappinfos) {
			this.adapterappinfos = adapterappinfos;
		}

		public int getCount() {

			return adapterappinfos.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return adapterappinfos.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// convertView ��ʷ�Ļ����view����
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder = null;
			if (convertView == null) {// ���û����ʷ�����view���� �����ò����ļ�����һ��view����
				view = View.inflate(getApplicationContext(),
						R.layout.applationinstall_item, null);
				holder = new ViewHolder();
				Logger.i(TAG, "GETvIEW----" + position + "�����µ�view����");
				holder.tv_name = (TextView) view.findViewById(R.id.tv_appname);
				holder.tv_version = (TextView) view
						.findViewById(R.id.tv_appversion);
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_appicon);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
				Logger.i(TAG, "GETvIEW----" + position + "ʹ����ʷ��view����");
			}
			AppInfo appinfo = adapterappinfos.get(position);
			holder.tv_name.setText(appinfo.getAppname());
			holder.tv_version.setText(appinfo.getVersion());
			holder.iv_icon.setImageDrawable(appinfo.getAppicon());
			return view;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_version;
		ImageView iv_icon;
	}

	/**
	 * view�������ľ��Ǳ��������Ŀ
	 */
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.ll_app_permission:
			Logger.i(TAG, "Ȩ��" + packname);

			Intent getperIntent = new Intent();
			getperIntent.putExtra("name", packname);
			getperIntent.setClass(AppManagerActivity.this,
					GetPermissionActivity.class);

			startActivity(getperIntent);

			dismissPopwindow();
			break;

		case R.id.ll_app_start:
			Logger.i(TAG, "����" + packname);
			try {
				PackageInfo packinfo = packageManager.getPackageInfo(packname,
						PackageManager.GET_ACTIVITIES);
				ActivityInfo[] activityinfos = packinfo.activities;
				if (activityinfos != null && activityinfos.length > 0) {
					ActivityInfo activityinfo = packinfo.activities[0];
					String activityname = activityinfo.name;
					Intent intent = new Intent();
					intent.setClassName(packname, activityname);
					startActivity(intent);
				} else {
					Toast.makeText(this, "�޷�������Ӧ��", 0).show();
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(this, "�޷�������Ӧ��", 0).show();
			}
			dismissPopwindow();
			break;

		case R.id.ll_app_uninstall:
			Logger.i(TAG, "ж��" + packname);
			// �жϵ�ǰ�����Ƿ���ϵͳ��apk
			AppInfo appinfo = (AppInfo) v.getTag();
			if (appinfo.isUserapp()) {
				// ����ϵͳ���������ж��Ӧ�õĲ���
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DELETE);
				intent.setData(Uri.parse("package:" + packname));
				// startActivity(intent);
				startActivityForResult(intent, 0);
			} else {// ϵͳӦ��
					// TODO:������˾,��ʾ�û�ϵͳӦ�ò��ܱ�ж��
				Toast.makeText(getApplicationContext(), "ϵͳӦ�ò��ܱ�ж��", 0).show();
			}
			dismissPopwindow();
			break;

		case R.id.ad_manager_fanhui:
			finish();
			break;

		}

	}

	/**
	 * �ڻ�ȡ�����е�Ӧ�ó�����Ϣ�� ,��ȡ�û���Ӧ�ó���ļ���
	 */
	public void getUserAppinfos() {
		userappinfos = new ArrayList<AppInfo>();
		for (AppInfo appinfo : appinfos) {
			if (appinfo.isUserapp()) {
				userappinfos.add(appinfo);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// ���»ص�������������� ��Ҫ���� ui
		loadAppInfo();
	}

	/**
	 * ��ǰactivity����û����ɼ���ʱ�� ���õķ���
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		dismissPopwindow();
	}

	/**
	 * ����spinner
	 */
	public void setspinner() {

		final TextView myTextView;

		myTextView = (TextView) findViewById(R.id.tv_appmanger_title);

		final String[] listStrings = list.toArray(new String[list.size()]);
		Button button = (Button) this.findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(AppManagerActivity.this)
						.setItems(listStrings,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										List<AppInfo> tempappinfos;
										// TODO Auto-generated method stub
										/* ����ѡmySpinner ��ֵ����myTextView �� */
										myTextView.setText(listStrings[which]);
										if (listStrings[which] == "���г���") {
											adapter = new AppInfoAdapter(
													appinfos);
											lv_appmanger.setAdapter(adapter);
										} else if (listStrings[which] == "�û�����") {
											adapter = new AppInfoAdapter(
													userappinfos);
											lv_appmanger.setAdapter(adapter);
										} else {
											String temp = mmap
													.get(listStrings[which]);
											Logger.i("###quanxian", temp);
											PermissionSpinner tempps = new PermissionSpinner();
											tempappinfos = tempps.listPackages(
													temp, mContext);
											Logger.i("###infosize", String
													.valueOf(tempappinfos
															.size()));
											adapter = new AppInfoAdapter(
													tempappinfos);
											lv_appmanger.setAdapter(adapter);
										}
									}
								}).create().show();
			}
		});
	}

}
