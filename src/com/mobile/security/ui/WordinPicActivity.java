package com.mobile.security.ui;

import java.io.File;
import java.util.ArrayList;
import com.mobile.security.R;
import com.mobile.security.engine.FileUploadAsyncTask;
import com.mobile.security.engine.FileUploadAsyncTask.DataFinishListener;
import com.mobile.security.engine.GetPicPath;
import com.mobile.security.utils.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WordinPicActivity extends Activity implements DataFinishListener{

	public static final int FILE_RESULT_CODE = 1;

	private TextView filefolderpath, picpath;
	private ArrayList<String> Flist = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_pic);
		Button choice = (Button) findViewById(R.id.choice);
		Button upButton = (Button) findViewById(R.id.upload);
		filefolderpath = (TextView) findViewById(R.id.fileText);
		picpath = (TextView) findViewById(R.id.picpath);
		

		choice.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(WordinPicActivity.this,
						FileManagerActivity.class);
				startActivityForResult(intent, FILE_RESULT_CODE);
			}
		});

		upButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				File[] allfileFiles = new File[Flist.size()];
				for (int i = 0; i < Flist.size(); i++) {
					allfileFiles[i] = new File(Flist.get(i));
				}
				FileUploadAsyncTask fuAsyncTask = new FileUploadAsyncTask(WordinPicActivity.this);
				fuAsyncTask.setFinishListener(new DataFinishListener() {
						@Override
						public void dataFinishSuccessfully(Object data) {
							// TODO Auto-generated method stub
							picpath.setText(String.valueOf(data));
						}
				});
				fuAsyncTask.execute(allfileFiles);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (FILE_RESULT_CODE == requestCode) {
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				Flist.clear();
				filefolderpath.setText(bundle.getString("file"));
				GetPicPath.listFile(bundle.getString("file"), Flist);
			}
		}
	}

	@Override
	public void dataFinishSuccessfully(Object data) {
		// TODO Auto-generated method stub
		
	}
}