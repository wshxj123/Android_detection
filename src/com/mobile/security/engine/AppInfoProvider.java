package com.mobile.security.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobile.security.domain.AppInfo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * ��ȡ���еİ�װ���ֻ��ϵ�Ӧ�ó������Ϣ
 * 
 */
public class AppInfoProvider extends Activity{
	private Context context;
	private PackageManager packageManager;
	private List<String> list = new ArrayList<String>();

	public AppInfoProvider(Context context) {
		this.context = context;
		packageManager = context.getPackageManager();
	}

	/**
	 * ͨ��packagemanager������ǰ�ֻ��������еİ�װ��Ӧ�ó���
	 * 
	 * @return
	 */
	public List<AppInfo> getAppInfos(List<String> list,HashMap<String,String> mmap,Map<String,String> m) {
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		// ��ǰ�ֻ������еİ�װ�İ�����Ϣ
		List<PackageInfo> packageInfos = packageManager
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		// ���� GET_UNINSTALLED_PACKAGES ������еİ�װ���ֻ��ϵ�Ӧ�ó������Ϣ �����ػ���
		// ��������ж�ص���Ӧ�ó��� ,����û��ɾ�����Ӧ�ó��������
		
		for (PackageInfo packinfo : packageInfos) {
			AppInfo appinfo = new AppInfo();
			String packname = packinfo.packageName;
			appinfo.setPackname(packname);
			String version = packinfo.versionName;
			appinfo.setVersion(version);
			Drawable appicon = packinfo.applicationInfo
					.loadIcon(packageManager);
			appinfo.setAppicon(appicon);
			String appname = packinfo.applicationInfo.loadLabel(packageManager)
					.toString();
			if (filterApp(packinfo.applicationInfo)) {
				appinfo.setUserapp(true);
			} else {
				appinfo.setUserapp(false);
			}
			appinfo.setAppname(appname);
			appinfos.add(appinfo);
			appinfo = null;
			
			/* spinner���Ȩ�� */
			//PermissionSpinner  temp = new PermissionSpinner();
			//temp.getPermisson(packname,context,list,mmap,m);
		}
		 list.add("��С���");
		 list.add("��ȡ��ȷλ��");
		 list.add("��ȡ����״̬");
		 list.add("����ͳ��");
		 list.add("ʹ������");
		 list.add("ͨ��Ȩ��");
		 list.add("����Ȩ��");
		 list.add("ɾ��Ӧ��");
		 list.add("��ȡ������Ϣ");
		 list.add("��������");
		 list.add("��ȡ��ϵ��");
		 list.add("��Ļ��ͼ");
		 list.add("��ȡ�ղؼк���ʷ��¼");
		 list.add("��ȡ��������");
		 list.add("�����Զ�����");
		 list.add("���ղ���");
		 list.add("���ն���");
		 list.add("¼��");
		 list.add("���Ͷ���");
		 list.add("������������");
		
		 mmap.put("��С���","android.permission.BIND_APPWIDGET");
		 mmap.put("��ȡ��ȷλ��","android.permission.ACCESS_FINE_LOCATION");
		 mmap.put("��ȡ����״̬","android.permission.ACCESS_NETWORK_STATE");
		 mmap.put("����ͳ��","android.permission.BATTERY_STATS");
		 mmap.put("ʹ������","android.permission.BLUETOOTH");
		 mmap.put("ͨ��Ȩ��","android.permission.CALL_PRIVILEGED");
		 mmap.put("����Ȩ��","android.permission.CAMERA");
		 mmap.put("ɾ��Ӧ��","android.permission.DELETE_PACKAGES");
		 mmap.put("��ȡ������Ϣ","android.permission.GET_TASKS");
		 mmap.put("��������","android.permission.INTERNET");
		 mmap.put("��ȡ��ϵ��","android.permission.READ_CONTACTS");
		 mmap.put("��Ļ��ͼ","android.permission.READ_FRAME_BUFFER");
		 mmap.put("��ȡ�ղؼк���ʷ��¼","com.android.browser.permission.READ_HISTORY_BOOKMARKS");
		 mmap.put("��ȡ��������","android.permission.READ_SMS");
		 mmap.put("�����Զ�����","android.permission.RECEIVE_BOOT_COMPLETED");
		 mmap.put("���ղ���","android.permission.RECEIVE_MMS");
		 mmap.put("���ն���","android.permission.RECEIVE_SMS");
		 mmap.put("¼��","android.permission.RECORD_AUDIO");
		 mmap.put("���Ͷ���","android.permission.SEND_SMS");
		 mmap.put("������������","com.android.alarm.permission.SET_ALARM");
		return appinfos;
	}

	/**
	 * 
	 * @param info
	 *            ApplicationInfo
	 * @return ��ǰӦ�ó����Ƿ����û���Ӧ��
	 */
	public boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true; // �������һ��ϵͳ��Ӧ�õ��Ǳ��û�������.(�û�Ӧ��).
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}