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
 * 获取所有的安装在手机上的应用程序的信息
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
	 * 通过packagemanager变量当前手机里面所有的安装的应用程序
	 * 
	 * @return
	 */
	public List<AppInfo> getAppInfos(List<String> list,HashMap<String,String> mmap,Map<String,String> m) {
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		// 当前手机上所有的安装的包的信息
		List<PackageInfo> packageInfos = packageManager
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		// 参数 GET_UNINSTALLED_PACKAGES 会把所有的安装在手机上的应用程序的信息 都返回回来
		// 甚至包括卸载掉的应用程序 ,但是没有删除这个应用程序的数据
		
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
			
			/* spinner添加权限 */
			//PermissionSpinner  temp = new PermissionSpinner();
			//temp.getPermisson(packname,context,list,mmap,m);
		}
		 list.add("绑定小插件");
		 list.add("获取精确位置");
		 list.add("获取网络状态");
		 list.add("电量统计");
		 list.add("使用蓝牙");
		 list.add("通话权限");
		 list.add("拍照权限");
		 list.add("删除应用");
		 list.add("获取任务信息");
		 list.add("访问网络");
		 list.add("读取联系人");
		 list.add("屏幕截图");
		 list.add("读取收藏夹和历史记录");
		 list.add("读取短信内容");
		 list.add("开机自动允许");
		 list.add("接收彩信");
		 list.add("接收短信");
		 list.add("录音");
		 list.add("发送短信");
		 list.add("设置闹铃提醒");
		
		 mmap.put("绑定小插件","android.permission.BIND_APPWIDGET");
		 mmap.put("获取精确位置","android.permission.ACCESS_FINE_LOCATION");
		 mmap.put("获取网络状态","android.permission.ACCESS_NETWORK_STATE");
		 mmap.put("电量统计","android.permission.BATTERY_STATS");
		 mmap.put("使用蓝牙","android.permission.BLUETOOTH");
		 mmap.put("通话权限","android.permission.CALL_PRIVILEGED");
		 mmap.put("拍照权限","android.permission.CAMERA");
		 mmap.put("删除应用","android.permission.DELETE_PACKAGES");
		 mmap.put("获取任务信息","android.permission.GET_TASKS");
		 mmap.put("访问网络","android.permission.INTERNET");
		 mmap.put("读取联系人","android.permission.READ_CONTACTS");
		 mmap.put("屏幕截图","android.permission.READ_FRAME_BUFFER");
		 mmap.put("读取收藏夹和历史记录","com.android.browser.permission.READ_HISTORY_BOOKMARKS");
		 mmap.put("读取短信内容","android.permission.READ_SMS");
		 mmap.put("开机自动允许","android.permission.RECEIVE_BOOT_COMPLETED");
		 mmap.put("接收彩信","android.permission.RECEIVE_MMS");
		 mmap.put("接收短信","android.permission.RECEIVE_SMS");
		 mmap.put("录音","android.permission.RECORD_AUDIO");
		 mmap.put("发送短信","android.permission.SEND_SMS");
		 mmap.put("设置闹铃提醒","com.android.alarm.permission.SET_ALARM");
		return appinfos;
	}

	/**
	 * 
	 * @param info
	 *            ApplicationInfo
	 * @return 当前应用程序是否是用户的应用
	 */
	public boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true; // 代表的是一个系统的应用但是被用户升级了.(用户应用).
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}