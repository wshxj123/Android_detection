package com.mobile.security.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mobile.security.domain.AppInfo;
import com.mobile.security.ui.GetPermissionActivity;
import com.mobile.security.utils.Logger;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

public class PermissionSpinner {
	public void getPermisson(String pkgName, Context context,
			List<String> list, HashMap<String, String> mmap,
			Map<String, String> m) {

		try {
			PackageManager pm = context.getPackageManager();
			PermissionGroupInfo pgi;
			PackageInfo pkgInfo = pm.getPackageInfo(pkgName,
					PackageManager.GET_PERMISSIONS);
			String[] sharedPkgList = pkgInfo.requestedPermissions;
			if (sharedPkgList != null) {
				for (int i = 0; i < sharedPkgList.length; i++) {
					String permName = sharedPkgList[i];
					// Logger.v("permName",permName);
					// 解析permission名称
					if (m.containsKey(permName)) {
						String stringarray[] = m.get(sharedPkgList[i]).split(
								"，", 2);
						if (!list.contains(stringarray[0])) {
						list.add(stringarray[0]);
						mmap.put(stringarray[0], permName);
						}
					}
				}
			}
		} catch (NameNotFoundException e) {
			Log.e("##ddd", "Couldn't retrieve permissions for package"
					+ pkgName);
		}
	}

	/**
	 * 获取具有特定权限的应用
	 */
	public List<AppInfo> listPackages(String Competence, Context context) {
		List<AppInfo> AppInfolist = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		Intent query = new Intent(Intent.ACTION_MAIN);
		query.addCategory("android.intent.category.LAUNCHER");
		List<PackageInfo> packs = pm.getInstalledPackages(0);
		for (int j = 0; j < packs.size() - 1; j++) {
			PackageInfo p = packs.get(j);
			String[] permission;
			try {
				permission = pm.getPackageInfo(p.packageName,
						PackageManager.GET_PERMISSIONS).requestedPermissions;// 获取权限列表
				// 判断权限列表是否为空,当不为空的时候进行遍历
				if (permission != null) {
					for (int k = 0; k < permission.length; k++) {
						if (permission[k].equals(Competence)) {
							AppInfo appinfo = new AppInfo();
							String packname = p.packageName;
							appinfo.setPackname(packname);
							String version = p.versionName;
							appinfo.setVersion(version);
							Drawable appicon = p.applicationInfo.loadIcon(pm);
							appinfo.setAppicon(appicon);
							String appname = p.applicationInfo.loadLabel(pm)
									.toString();
							if (filterApp(p.applicationInfo)) {
								appinfo.setUserapp(true);
							} else {
								appinfo.setUserapp(false);
							}
							appinfo.setAppname(appname);
							AppInfolist.add(appinfo);
							break;
						}
					}
				}

			} catch (NameNotFoundException e) {
				e.printStackTrace();
				System.out.println(e.getMessage() + "获取权限列表错误");
			}
		}
		return AppInfolist;
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