package com.mobile.security.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.view.View;
import com.mobile.security.R;
import com.mobile.security.domain.DetectionInfo;
import com.mobile.security.utils.Logger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public  class DetectionDetailsActivity extends Activity implements android.view.View.OnClickListener {
	protected static final int LOAD_DET_FINSISH = 40;
	public static final String TAG = "DetectionDetalsActivity";
	private ListView dd_manager;
	private LinearLayout dd_loading;
	private List<DetectionInfo> detinfos=new ArrayList<DetectionInfo>();
	private DetectionInfoAdapter adapter;
	private TextView dd_title;
	private boolean loading;
	private PopupWindow popupWindow;
	private String mtype;
	static Context mContext;
	private Button btn_return;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case LOAD_DET_FINSISH:
				// �������ڼ��س���Ľ��Ȳ�����ʾΪ���ɼ�
				dd_loading.setVisibility(View.INVISIBLE);
				loading = false;
				adapter = new DetectionInfoAdapter(detinfos);
				dd_manager.setAdapter(adapter);
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		mContext = this.getApplicationContext();
		// TODO Auto-generated method stub
		loading = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detectiondetails);
		dd_manager = (ListView) this.findViewById(R.id.dd_manager);

		btn_return = (Button) findViewById(R.id.dd_fanhui);
		btn_return.setOnClickListener(DetectionDetailsActivity.this);
		
		Intent getintent = getIntent();
		mtype = getintent.getStringExtra("mtype");

		dd_title = (TextView) this.findViewById(R.id.dd_title);

		dd_loading = (LinearLayout) this.findViewById(R.id.dd_loading);
		// dd_manager.setAdapter(adapter);
		loadDetInfo();

	}

	private void loadDetInfo() {
		dd_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				loading = true;

				if (mtype.equals("SMS"))
				{
					readSMSlist();
					readdelSMSlist();
				}
				else if (mtype.equals("Internet"))
					readInternetlist();
				else if (mtype.equals("QQ"))
					readQQlist();

				Message msg = Message.obtain();
				msg.what = LOAD_DET_FINSISH;
				handler.sendMessage(msg);

			};
		}.start();
	}

	private void readSMSlist() {
		
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Securitytest", "SMS.txt");
		if (!file.exists()) {
			return;
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String filecontent = "";
		String tempcontent = "";
		try {
			while ((tempcontent = reader.readLine()) != null) {
				filecontent += tempcontent;
			}
			reader.close();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
		if(filecontent.equals(""))
			return;
		String parts1[] = filecontent.split("]");
		for (int i = 0; i < parts1.length; i++) {
			DetectionInfo detinfo = new DetectionInfo();
			String tparts[] = parts1[i].split(",");
			String part = null;
			if (tparts[0].equals("[null")) {
				part = "����:δ֪  �绰:" + tparts[1] + '\n' + tparts[2] + '\n'
						+ tparts[3] + " " + tparts[4];
			} else {
				part = "����:" + tparts[0].substring(1) + "  �绰:" + tparts[1]
						+ '\n' + tparts[2] + '\n' + tparts[3] + " " + tparts[4];
			}
			detinfo.setContent(part);
			detinfos.add(detinfo);
			detinfo=null;
		}
	}
	private void readdelSMSlist() {
		
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Securitytest", "delSMS.txt");
		if (!file.exists()) {
			return;
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String filecontent = "";
		String tempcontent = "";
		try {
			while ((tempcontent = reader.readLine()) != null) {
				filecontent += tempcontent;
			}
			reader.close();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}

		String parts2[] = filecontent.split(";");
		for (int i = 0; i < parts2.length; i++) {
			DetectionInfo detinfo = new DetectionInfo();
			String part = parts2[i];
			detinfo.setContent(part);
			detinfos.add(detinfo);
			detinfo=null;
		}
	}

	private void readInternetlist() {

		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Securitytest", "Internet.txt");
		if (!file.exists()) {
			return;
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String filecontent = "";
		String tempcontent = "";
		try {
			while ((tempcontent = reader.readLine()) != null) {
				filecontent += tempcontent;
			}
			reader.close();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}

		String parts[] = filecontent.split("</history>");
		for (int i = 0; i < parts.length - 1; i++) {
			
			DetectionInfo detinfo = new DetectionInfo();
			String regEx1 = "<title>([\\s\\S]*?)</title>";
			Pattern pat1 = Pattern.compile(regEx1);
			Matcher mat1 = pat1.matcher(parts[i]);
			mat1.find();

			String regEx2 = "<url>([\\s\\S]*?)</url>";
			Pattern pat2 = Pattern.compile(regEx2);
			Matcher mat2 = pat2.matcher(parts[i]);
			mat2.find();

			String regEx3 = "<date>([\\s\\S]*?)</date>";
			Pattern pat3 = Pattern.compile(regEx3);
			Matcher mat3 = pat3.matcher(parts[i]);
			mat3.find();

			String part = "����:" + mat1.group(1) + '\n' + "����:" + mat2.group(1)
					+ '\n' + "ʱ��:" + mat3.group(1);
			detinfo.setContent(part);
			
			detinfos.add(detinfo);
			detinfo=null;
		}
	}
	
	private void readQQlist() {

		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Securitytest", "QQ.txt");
		if (!file.exists()) {
			return;
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String filecontent = "";
		String tempcontent = "";
		try {
			while ((tempcontent = reader.readLine()) != null) {
				filecontent += tempcontent;
			}
			reader.close();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}

		String parts[] = filecontent.split("</chat>");
		for (int i = 0; i < parts.length - 1; i++) {
			DetectionInfo detinfo = new DetectionInfo();
			
			String regEx1 = "<qq>([\\s\\S]*?)</qq>";
			Pattern pat1 = Pattern.compile(regEx1);
			Matcher mat1 = pat1.matcher(parts[i]);
			mat1.find();
			
			String regEx2 = "<time>([\\s\\S]*?)</time>";
			Pattern pat2 = Pattern.compile(regEx2);
			Matcher mat2 = pat2.matcher(parts[i]);
			mat2.find();

			String regEx3 = "<content>([\\s\\S]*?)</content>";
			Pattern pat3 = Pattern.compile(regEx3);
			Matcher mat3 = pat3.matcher(parts[i]);
			mat3.find();

			String part = "��¼QQ:" + mat1.group(1) + "  ʱ��:" + mat2.group(1) + '\n' + "����:\n" + mat3.group(1);
			detinfo.setContent(part);
			
			detinfos.add(detinfo);
			detinfo=null;
		}
	}

	private class DetectionInfoAdapter extends BaseAdapter {
		List<DetectionInfo> adapterdetinfos;

		public DetectionInfoAdapter(List<DetectionInfo> adapterdetinfos) {
			this.adapterdetinfos = adapterdetinfos;
		}

		public int getCount() {
			return adapterdetinfos.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return adapterdetinfos.get(position);
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
				view = View.inflate(DetectionDetailsActivity.this,
						R.layout.detectiondetails_item, null);
				holder = new ViewHolder();
				Logger.i(TAG, "GETvIEW----" + position + "�����µ�view����");
				holder.tv_content = (TextView) view
						.findViewById(R.id.dd_content);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
				Logger.i(TAG, "GETvIEW----" + position + "ʹ����ʷ��view����");
			}
			DetectionInfo tdetinfo = adapterdetinfos.get(position);
			
//			holder.tv_content.setAutoLinkMask(Linkify.ALL);
//			holder.tv_content.setAutoLinkMask(Linkify.PHONE_NUMBERS |Linkify.WEB_URLS);
//			holder.tv_content.setMovementMethod(LinkMovementMethod
//					.getInstance());
			holder.tv_content.setText(tdetinfo.getContent());
			return view;
		}

	}

	static class ViewHolder {
		TextView tv_content;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dd_fanhui:
			finish();
			break;
		}
	}
}
