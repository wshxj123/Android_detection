package com.mobile.security.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.util.Log;

import com.mobile.security.domain.UpdateInfo;
import com.mobile.security.ui.SplashActivity;

public class UpdateInfoService {

	private Context context;

	public UpdateInfoService(Context context) {
		this.context = context;
	}
	
	public UpdateInfo getUpdateInfo(int urlId) throws Exception{
		
		String path =context.getResources().getString(urlId);
		Log.d(SplashActivity.TAG, "update url: " + path);
		URL url = new URL(path);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(5000);
		httpURLConnection.setRequestMethod("GET");
		InputStream is = httpURLConnection.getInputStream();
		return UpdateInfoParser.getUpdateInfo(is);
	}
}
