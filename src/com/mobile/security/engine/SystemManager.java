package com.mobile.security.engine;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.util.Log;

public class SystemManager extends Activity {
	/**
	 * Ӧ�ó������������ȡ RootȨ��,�豸�������ƽ�(���ROOTȨ��)
	 * 
	 * @param command
	 *            ���String apkRoot="chmod 777 "+getPackageCodePath();
	 *            RootCommand(apkRoot);
	 * @return Ӧ�ó�����/���ȡRootȨ��
	 */
	public static boolean RootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}

   public static  void RunAsRooter() 
    {  
        try {  
              Process process = Runtime.getRuntime().exec("su");  
             // process.waitFor();  
            } catch (IOException e) {  
              e.printStackTrace();  
            }  
    }  
    
	public static boolean runRootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			// Log.d(TAG, "the device is not rooted, error message�� " +
			// e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}