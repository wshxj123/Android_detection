package com.mobile.security.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

public class DownloadTask {

	public static File getFile (String path, String filePath, ProgressDialog progressDialog) throws Exception {
		
		URL url = new URL(path);
		HttpURLConnection hucon = (HttpURLConnection) url.openConnection();
		hucon.setConnectTimeout(2000);
		hucon.setRequestMethod("GET");
		if (hucon.getResponseCode() == HttpURLConnection.HTTP_OK) {
			int total = hucon.getContentLength();
			progressDialog.setMax(total);
			
			InputStream is = hucon.getInputStream();
			File file = new File(filePath);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			int process = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				process += len;
				progressDialog.setProgress(process);
			}
			fos.flush();
			is.close();
			fos.close();
			return file;
		}
		return null;
	}
}
