package com.mobile.security.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteException;
//import net.sqlcipher.database.SQLiteDatabase;

import org.apache.http.util.EncodingUtils;

import com.mobile.security.ac.AcApply;
import com.mobile.security.ac.AhoCorasick;
import com.mobile.security.ac.SearchResult;
import com.mobile.security.engine.EncryptionByMD5;
import com.mobile.security.utils.FileCopyUtil;
import com.mobile.security.utils.Logger;

import com.mobile.security.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ContentDetectionActivity extends Activity implements
		OnClickListener {
	/** Called when the activity is first created. */
	private static String[] wordsStrings;

	private ImageView iv_return;
	private Button btn_setwords, btn_return, btn_detection;
	private RelativeLayout rl_decting, rl_SMS, rl_Internet, rl_QQ, rl_Wx;
	private ProgressBar progressBar;
	private int i, armprogress;
	private FileOutputStream out = null;
	private Context mCtx;
	private LinearLayout detection_main_tv, clear_main_tv2;
	private String wordstempStrings = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		mCtx = getApplicationContext();
		i = 0;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.detection);

		rl_SMS = (RelativeLayout) findViewById(R.id.SMS_detection);
		rl_Internet = (RelativeLayout) findViewById(R.id.Internet_detection);
		rl_QQ = (RelativeLayout) findViewById(R.id.qq_detection);
		rl_Wx = (RelativeLayout)findViewById(R.id.wechat_detection);
		// rl_ackge = (RelativeLayout)
		// findViewById(R.id.clear_main_ll_package);
		btn_return = (Button) findViewById(R.id.detection_fanhui);
		btn_detection = (Button) findViewById(R.id.detection_main);
		detection_main_tv = (LinearLayout) findViewById(R.id.detection_main_tv);
		clear_main_tv2 = (LinearLayout) findViewById(R.id.clear_main_tv2);
		rl_decting = (RelativeLayout) findViewById(R.id.main_decting);
		iv_return = (ImageView) findViewById(R.id.clear_main_return2);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		btn_setwords = (Button) findViewById(R.id.set_words);

		rl_SMS.setOnClickListener(this);
		rl_Internet.setOnClickListener(this);
		rl_QQ.setOnClickListener(this);
		rl_Wx.setOnClickListener(this);
		// rl_packge.setOnClickListener(this);
		btn_return.setOnClickListener(this);
		btn_detection.setOnClickListener(this);
		btn_setwords.setOnClickListener(this);
		iv_return.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.SMS_detection:
			Intent SMSIntent = new Intent();
			SMSIntent.putExtra("mtype", "SMS");
			SMSIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			SMSIntent.setClass(ContentDetectionActivity.this,
					DetectionDetailsActivity.class);
			startActivity(SMSIntent);
			break;

		case R.id.Internet_detection:
			Intent InterIntent = new Intent();
			InterIntent.putExtra("mtype", "Internet");
			InterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			InterIntent.setClass(ContentDetectionActivity.this,
					DetectionDetailsActivity.class);
			startActivity(InterIntent);
			break;

		case R.id.qq_detection:
			Intent qqIntent = new Intent();
			qqIntent.putExtra("mtype", "QQ");
			qqIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			qqIntent.setClass(ContentDetectionActivity.this,
					DetectionDetailsActivity.class);
			startActivity(qqIntent);
			break;
			
		case R.id.wechat_detection:
			Intent wxIntent = new Intent();
			wxIntent.putExtra("mtype", "Wechat");
			wxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			wxIntent.setClass(ContentDetectionActivity.this,
					DetectionDetailsActivity.class);
			startActivity(wxIntent);
			break;

		case R.id.set_words:
			Intent setIntent = new Intent(this, SetWordsActivity.class);
			startActivity(setIntent);
			break;

		/*
		 * case R.id.clear_main_ll_garbage: Intent intent3 = new Intent(this,
		 * Clear_Garbage.class); startActivity(intent3); break; case
		 * R.id.clear_main_ll_privacy: Intent intent4 = new Intent(this,
		 * Clear_Privacy.class); startActivity(intent4); break; case
		 * R.id.clear_main_ll_package: Intent intent5 = new Intent(this,
		 * Clear_Packge.class); startActivity(intent5); break;
		 */

		case R.id.detection_fanhui:
			finish();
			break;
		case R.id.detection_main:
			i = 0;
			// clear_main_tv.setVisibility(View.GONE);
			btn_detection.setVisibility(View.GONE);
			rl_decting.setVisibility(View.VISIBLE);
			readwords();
			get_information();
			break;
		case R.id.clear_main_return2:
			finish();
			break;
		/*
		 * case R.id.clear_success_btn_success: finish(); break; case
		 * R.id.clear_success_btn_share: WeiBoUtil.shareText(this, APP_KEY);
		 * break;
		 */
		}
	}

	Handler handler0 = new Handler();
	Runnable rannable = new Runnable() {
		public void run() {
			i++;
			progressBar.setProgress(i);
			handler0.postDelayed(rannable, 100);
			if (i == armprogress) {
				handler0.removeCallbacks(rannable);
			}
		}
	};
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// clear_main_tv2.setVisibility(View.VISIBLE);
				btn_detection.setVisibility(View.VISIBLE);
				rl_decting.setVisibility(View.INVISIBLE);
				break;
			case 1:
				armprogress = msg.arg1;
				handler0.post(rannable);
				break;
			}
		}
	};

	public void readwords() {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Securitytest", "Setwords.txt");
		if (!file.exists()) {
			try {
				wordstempStrings = "秘密;机密;绝密";
				file.createNewFile();
				out = new FileOutputStream(file);
				out.write(wordstempStrings.getBytes());
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				Logger.i("##file", "创建文件失败！");
			}
		} else {
			wordstempStrings = "";
			InputStreamReader read = null;
			try {
				read = new InputStreamReader(new FileInputStream(file), "UTF-8");
				BufferedReader reader = new BufferedReader(read);
				String tempcontent = "";
				while ((tempcontent = reader.readLine()) != null) {
					wordstempStrings += tempcontent;
				}
				reader.close();
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		String[] tempss = wordstempStrings.split(";");
		wordsStrings = tempss;
	}

	public void get_information() {
		new Thread(new Runnable() {
			public void run() {
				AcApply obj = new AcApply();
				File file = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/Securitytest");
				if (!file.exists() && !file.isDirectory())
					file.mkdir();

				// Message m2 = handler.obtainMessage();
				// m2.what = 2;
				// m2.sendToTarget();

				/*
				 * 获得短信内容
				 */
				Message m2 = handler.obtainMessage();
				m2.what = 1;
				m2.arg1 = 20;
				m2.sendToTarget();

				String strSMS = getSmsInPhone();

				file = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/Securitytest", "SMS.txt");

				try {
					if (!file.exists()) {
						file.createNewFile();
					}
					out = new FileOutputStream(file);
					out.write(strSMS.getBytes());
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

				/*
				 * 获得已删除短信内容
				 */

				m2 = handler.obtainMessage();
				m2.what = 1;
				m2.arg1 = 40;
				m2.sendToTarget();

				try {
					file = new File(getFilesDir(), "mmssms.db");
					InputStream is;
					is = new FileInputStream(
							"/data/data/com.android.providers.telephony/databases/mmssms.db");
					FileCopyUtil.copyFile(is, file);
					file = new File(getFilesDir(), "mmssms.db-wal");
					is = new FileInputStream(
							"/data/data/com.android.providers.telephony/databases/mmssms.db-wal");
					FileCopyUtil.copyFile(is, file);
					file = new File(getFilesDir(), "mmssms.db-shm");
					is = new FileInputStream(
							"/data/data/com.android.providers.telephony/databases/mmssms.db-shm");
					FileCopyUtil.copyFile(is, file);
				} catch (FileNotFoundException e2) {
					// TODO 自动生成的 catch 块
					e2.printStackTrace();
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

				String dbnameString = getFilesDir() + "/mmssms.db";
				SQLiteDatabase db = openOrCreateDatabase(dbnameString,
						Context.MODE_PRIVATE, null);
				db.rawQuery("PRAGMA journal_mode=WAL; ", null).close();
				Cursor cus = db.rawQuery("PRAGMA wal_checkpoint;", null);
				cus.close();
				db.close();

				String delSMSString = "";
				try {
					Set delSMSSet = getDeleteSMS();
					if (!delSMSSet.isEmpty()) {
						for (Iterator it = delSMSSet.iterator(); it.hasNext();) {
							String aString = it.next().toString();
							if (!strSMS.contains(aString))
								delSMSString += "【已删除】" + aString + ";";
						}
					}
				} catch (Exception e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}

				file = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/Securitytest", "delSMS.txt");

				try {
					if (!file.exists()) {
						file.createNewFile();
					}
					out = new FileOutputStream(file);
					out.write(delSMSString.getBytes());
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

				/*
				 * 获得默认浏览器浏览记录
				 */

				m2 = handler.obtainMessage();
				m2.what = 1;
				m2.arg1 = 60;
				m2.sendToTarget();

				String strInternet = getInternet();
				file = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/Securitytest", "Internet.txt");

				try {
					if (!file.exists()) {
						file.createNewFile();
					}
					out = new FileOutputStream(file);
					out.write(strInternet.getBytes());
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

				/*
				 * 获得QQ聊天记录
				 */
				m2 = handler.obtainMessage();
				m2.what = 1;
				m2.arg1 = 80;
				m2.sendToTarget();

				try {
					String strQQ = getQQ();
					file = new File(Environment.getExternalStorageDirectory()
							.getPath() + "/Securitytest", "QQ.txt");
					if (!file.exists())
						file.createNewFile();
					out = new FileOutputStream(file);
					out.write(strQQ.getBytes());
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * 获得微信聊天记录
				 */
				m2 = handler.obtainMessage();
				m2.what = 1;
				m2.arg1 = 100;
				m2.sendToTarget();

				try {
					String strwx = "";
					try {
						strwx = getWechat();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					file = new File(Environment.getExternalStorageDirectory()
							.getPath() + "/Securitytest", "Wechat.txt");
					if (!file.exists())
						file.createNewFile();
					out = new FileOutputStream(file);
					out.write(strwx.getBytes());
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// if (i == 100) {
				m2 = handler.obtainMessage();
				m2.what = 0;
				m2.sendToTarget();
				// }
			}
		}).start();
	}

	/**
	 * 获取手机短信内容
	 * 
	 * @return
	 */
	public String getSmsInPhone() {
		AcApply obj = new AcApply();
		final String SMS_URI_ALL = "content://sms/"; // 所有短信
		final String SMS_URI_INBOX = "content://sms/inbox"; // 收信箱
		final String SMS_URI_SEND = "content://sms/sent"; // 发信箱
		final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿箱
		StringBuilder smsBuilder = new StringBuilder();
		try {
			ContentResolver cr = getContentResolver();
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, null, null, "date desc");
			if (cur.moveToFirst()) {
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;
				int nameColumn = cur.getColumnIndex("person");// 姓名
				int phoneNumberColumn = cur.getColumnIndex("address");// 手机号
				int smsbodyColumn = cur.getColumnIndex("body");// 短信内容
				int dateColumn = cur.getColumnIndex("date");// 日期
				int typeColumn = cur.getColumnIndex("type");// 收发类型 1表示接受 2表示发送
				do {
					// name = cur.getString(nameColumn);
					phoneNumber = cur.getString(phoneNumberColumn);
					name = getContactByAddr(getBaseContext(), phoneNumber);
					smsbody = cur.getString(smsbodyColumn);
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
					date = dateFormat.format(d);
					int typeId = cur.getInt(typeColumn);
					if (typeId == 1) {
						type = "接收";
					} else if (typeId == 2) {
						type = "发送";
					} else {
						type = "";
					}

					Set result2 = obj.findWordsInArray(wordsStrings, smsbody);
					if (!result2.isEmpty()) {
						smsBuilder.append("[");
						smsBuilder.append(name + ",");
						smsBuilder.append(phoneNumber + ",");
						smsBuilder.append(smsbody + ",");
						smsBuilder.append(date + ",");
						smsBuilder.append(type);
						smsBuilder.append("]");
					}
					if (smsbody == null)
						smsbody = "";
				} while (cur.moveToNext());
			}
			cur.close();
		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}
		return smsBuilder.toString();
	}

	/*
	 * 根据电话号码取得联系人姓名
	 */
	private String getContactByAddr(Context context, String address) {
		String mName = null;
		Uri personUri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address);
		Cursor cur = context.getContentResolver().query(personUri,
				new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		if (cur != null && cur.moveToFirst()) {
			int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			mName = cur.getString(nameIdx);
			cur.close();
			return mName;
		}
		cur.close();
		return null;
	}

	/**
	 * 获取已删除短信
	 * 
	 * @return
	 */

	public static String Readfile(File src) throws Exception {

		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		String filecontent = "";
		try {
			fileInputStream = new FileInputStream(src);
			byte[] buffer = new byte[34836];
			while ((fileInputStream.read(buffer)) != -1) {
				filecontent += new String(buffer, "UTF-8");
			}
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
		return filecontent;
	}

	public static Set ACfind(String[] keywords, String text) {
		text = text.trim();
		int number = 0;
		try {
			if (keywords.length == 0) {
				throw new Exception("Array of keywords is empty.");
			}
		} catch (Exception e) {
			System.out.println("Array of keywords is empty.");
			e.printStackTrace();
		}

		AhoCorasick tree = new AhoCorasick();
		int length = keywords.length;
		for (int i = 0; i < length; i++) {
			if (keywords[i].trim().length() != 0) {
				tree.add(keywords[i].trim().getBytes(), keywords[i].trim());
			}
		}
		tree.prepare();

		Iterator iter = tree.search(text.getBytes());
		Set<String> byteNumberSet = new HashSet<String>();
		SearchResult result = null;
		while (iter.hasNext()) {
			result = (SearchResult) iter.next();
			byteNumberSet.add(String.valueOf(result.getLastIndex()));
		}
		return byteNumberSet;
	}

	public static String getByteStr(String str, int start, int end)
			throws UnsupportedEncodingException {
		byte[] b = str.getBytes("UTF-8");
		return new String(b, start, end);
	}

	public Set getDeleteSMS() throws Exception {

		String dbnameString = getFilesDir() + "/mmssms.db";

		// SystemManager.RunAsRooter();
		Set<String> resultStringSet = new HashSet<String>();
		File file = new File(dbnameString);
		String filecontent = Readfile(file);

		Set result10 = ACfind(wordsStrings, filecontent);

		byte bfilecontent[] = filecontent.getBytes();
		int bfilelenght = bfilecontent.length;
		for (Iterator it = result10.iterator(); it.hasNext();) {
			int temp = Integer.parseInt(it.next().toString()) - 1;
			String tempString;
			int tempmin = temp;
			int tempmax = temp;
			while (tempmin >= 0 && bfilecontent[tempmin] != 0) {
				tempmin--;
			}
			if (tempmin < 0)
				tempmin = 0;
			if (bfilecontent[tempmin] == 0)
				tempmin++;
			while (tempmax < bfilelenght && bfilecontent[tempmax] != 0) {
				tempmax++;
			}
			if (tempmax >= bfilelenght)
				tempmax = bfilelenght;
			if (bfilecontent[tempmax] == 0)
				tempmax--;
			try {
				tempString = getByteStr(filecontent, tempmin,
						tempmax - tempmin + 1).replaceAll(
				// "[^((\\u0021-\\u007E)&(\\u4e00-\\u9fa5)&(\\uFF00-\\uFFEF)&(\\u3000-\\u303F)&(\\u2000-\\u206F))]","");
						"[^(\\u4e00-\\u9fa5)]", "");
				// System.out.println(tempString);
				resultStringSet.add(tempString);

			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		return resultStringSet;
	}

	/**
	 * 获取浏览器记录
	 * 
	 * @return
	 */
	public String getInternet() {
		AcApply obj = new AcApply();
		StringBuilder recordBuilder = new StringBuilder(); // 浏览历史信息
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(
				Uri.parse("content://browser/bookmarks"), new String[] {
						"title", "url", "date" }, "date!=?",
				new String[] { "null" }, "date desc");
		while (cursor != null && cursor.moveToNext()) {
			String url = null;
			String title = null;
			String time = null;
			String date = null;

			title = cursor.getString(cursor.getColumnIndex("title"));
			url = cursor.getString(cursor.getColumnIndex("url"));

			date = cursor.getString(cursor.getColumnIndex("date"));

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date d = new Date(Long.parseLong(date));
			time = dateFormat.format(d);

			Set result2 = obj.findWordsInArray(wordsStrings, title);
			if (!result2.isEmpty()) {
				recordBuilder.append("<history><title>");
				recordBuilder.append(title + "</title><url>");
				recordBuilder.append(url + "</url><date>");
				recordBuilder.append(time + "</date></history>\r\n");
			}
		}
		cursor.close();

		return recordBuilder.toString();
	}

	/**
	 * 获取QQ聊天记录
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getQQ() throws UnsupportedEncodingException {
		AcApply obj = new AcApply();
		StringBuilder qqBuilder = new StringBuilder(); // QQ聊天记录
		TelephonyManager tm = (TelephonyManager) mCtx
				.getSystemService(Context.TELEPHONY_SERVICE);
		byte[] key = tm.getDeviceId().getBytes();
		List<String> Dblist = getQQdbList("/data/data/com.tencent.mobileqq/databases/");
		for (String dbname : Dblist) {
			Log.i("filenow", dbname);
			File file = new File("/data/data/com.tencent.mobileqq/databases/"
					+ dbname);
			SQLiteDatabase db = SQLiteDatabase.openDatabase(
					file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
			Cursor cursor = db.query("sqlite_master",
					new String[] { "tbl_name" }, null, null, null, null, null,
					null);
			String regEx = "mr_friend_*";
			Set<String> tableSet = new HashSet<String>();
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor
						.getColumnIndex("tbl_name"));// 返回列名为name的值
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(name);
				if (m.find()) {
					tableSet.add(name);
				}
			}
			cursor.close();
			for (String table : tableSet) {
				cursor = db.query(table, new String[] { "msgData", "time" },
						null, null, null, null, null, null);
				// 遍历每一个记录
				while (cursor.moveToNext()) {
					String time = cursor.getString(cursor
							.getColumnIndex("time"));
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String formatTime = sdf
							.format(Long.parseLong(time + "000"));
					byte[] msg = cursor.getBlob(cursor
							.getColumnIndex("msgData"));

					for (int i = 0; i < msg.length; i++) {
						msg[i] = (byte) (msg[i] ^ key[i % 15]);
					}
					String message = new String(msg, "utf-8");
					Set result2 = obj.findWordsInArray(wordsStrings, message);
					if (!result2.isEmpty()) {
						// qqBuilder.append(formatTime + "," + message + ";");
						qqBuilder.append("<chat><qq>");
						qqBuilder.append(dbname.substring(0,
								dbname.length() - 3) + "</qq><time>");
						qqBuilder.append(formatTime + "</time><content>");
						qqBuilder.append(message + "</content></chat>");
						// Log.i("解密结果->", formatTime + "," + message + ";");
					}
				}
			}
			cursor.close();
		}

		return qqBuilder.toString();
	}

	public List<String> getQQdbList(String path) {
		Logger.i("flielist",path);
		List list = new ArrayList();
		try {
			File file = new File(path);
			String[] filelist = file.list();
			Logger.i("flielist",String.valueOf(filelist.length));
			for (int i = 0; i < filelist.length; i++) {
				Pattern pattern = Pattern.compile("[0-9]*.db");
				Matcher matcher = pattern.matcher(filelist[i]);
				if (matcher.matches()) {
					Log.i("file", filelist[i]);
					list.add(filelist[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取微信聊天记录
	 * 
	 * @return
	 * @throws Exception 
	 */

	public String getWechat() throws Exception {

		net.sqlcipher.database.SQLiteDatabase.loadLibs(this);
		String uin = readUin();
		
		TelephonyManager tm = (TelephonyManager) mCtx
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		
		String password = EncryptionByMD5.getMD5((imei + uin).getBytes()).substring(0, 7);
		String foldername = EncryptionByMD5.getMD5(("mm" + uin).getBytes());
		
		File databaseFile = new File(getFilesDir(), "EnMicroMsg.db");
		InputStream is = new FileInputStream(
				"/data/data/com.tencent.mm/MicroMsg/" + foldername + "/EnMicroMsg.db");
		FileCopyUtil.copyFile(is, databaseFile);
		
		SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
			@Override
			public void postKey(net.sqlcipher.database.SQLiteDatabase database) {
				// TODO Auto-generated method stub
				database.rawExecSQL("PRAGMA cipher_migrate;"); // 最关键的一句！！！
			}

			@Override
			public void preKey(net.sqlcipher.database.SQLiteDatabase database) {
				// TODO Auto-generated method stub

			}
		};

		AcApply obj = new AcApply();
		StringBuilder wxBuilder = new StringBuilder(); // 微信聊天记录
		net.sqlcipher.database.SQLiteDatabase db = net.sqlcipher.database.SQLiteDatabase
				.openOrCreateDatabase(databaseFile, password, null, hook);
		
		int onceNum = 100;
		int num1 = 1, num2 = 100;
		Cursor cur = db.rawQuery("select count(*) from message", null);
		cur.moveToFirst();
		Long count = cur.getLong(0);
		cur.close();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while (count != 0) {
			cur = db.rawQuery(
					"select msgId,createTime,talker,content from message group by msgId having msgId >= ? and msgId <= ?",
					new String[] { String.valueOf(num1), String.valueOf(num2) });
			while (cur.moveToNext()) {
				count -= 1;
				String _id = cur.getString(cur.getColumnIndex("msgId"));
				String time = cur.getString(cur.getColumnIndex("createTime"));
				String formatTime = sdf.format(Long.parseLong(time));
				String talker = cur.getString(cur.getColumnIndex("talker"));
				String content = cur.getString(cur.getColumnIndex("content"));
				
				Set result2 = obj.findWordsInArray(wordsStrings, content);
				
				if (!result2.isEmpty()) {
					//Log.i("wechat->", "_id=>" + _id);

					wxBuilder.append("<chat><time>");
					wxBuilder.append(formatTime + "</time><talker>");
					wxBuilder.append(talker + "</talker><content>");
					wxBuilder.append(content + "</content></chat>");
				}
			}
			cur.close();
			num1 += onceNum;
			num2 += onceNum;
		}
		return wxBuilder.toString();
	}
	
	private String readUin(){
    	File file = new File("/data/data/com.tencent.mm/shared_prefs/com.tencent.mm_preferences.xml");
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
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
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		String regEx = "<string name=\"last_login_uin\">([\\s\\S]*?)</string>";
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(filecontent);
		mat.find();
		return mat.group(1);
	}
}