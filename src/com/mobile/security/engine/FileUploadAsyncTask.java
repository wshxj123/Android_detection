package com.mobile.security.engine;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.widget.Toast;

public class FileUploadAsyncTask extends AsyncTask<File, Integer, String> {

	private String url = "http://173.26.101.238/receive_file.php";
	private Context context;
	private ProgressDialog pd;
	private long totalSize;

	private DataFinishListener dataFinishListener;

	public FileUploadAsyncTask(Context context) {
		this.context = context;
	}

	public void setFinishListener(DataFinishListener dataFinishListener) {
		this.dataFinishListener = dataFinishListener;
	}

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("上传....");
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected String doInBackground(File... params) {
		// 保存上传文件信息
		MultipartEntityBuilder entitys = MultipartEntityBuilder.create();
		entitys.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		entitys.setCharset(Charset.forName(HTTP.UTF_8));

		int filenum = params.length;
		entitys.addPart("num", new StringBody(String.valueOf(filenum),
				ContentType.DEFAULT_TEXT));

		for (int i = 0; i < filenum; i++) {
			File file = params[i];
			entitys.addPart("path" + String.valueOf(i), new StringBody(
					params[i].getAbsolutePath(), ContentType.DEFAULT_TEXT));
			entitys.addPart("file" + String.valueOf(i), new FileBody(file));
		}
		HttpEntity httpEntity = entitys.build();
		totalSize = httpEntity.getContentLength();
		ProgressOutHttpEntity progressHttpEntity = new ProgressOutHttpEntity(
				httpEntity, new ProgressListener() {
					@Override
					public void transferred(long transferedBytes) {
						publishProgress((int) (100 * transferedBytes / totalSize));
					}
				});
		return uploadFile(url, progressHttpEntity);
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		pd.dismiss();
		Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
		if (result != null) {
			dataFinishListener.dataFinishSuccessfully(result);
		}

	}

	public static interface DataFinishListener {
		void dataFinishSuccessfully(Object data);
	}

	/**
	 * 上传文件到服务器
	 * 
	 * @param url
	 *            服务器地址
	 * @param entity
	 *            文件
	 * @return
	 */
	public static String uploadFile(String url, ProgressOutHttpEntity entity) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		// 设置连接超时时间
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				// return "文件上传成功";
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		return "文件上传失败";
	}

}
