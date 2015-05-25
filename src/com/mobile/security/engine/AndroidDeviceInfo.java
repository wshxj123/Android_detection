package com.mobile.security.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.telephony.TelephonyManager;

public class AndroidDeviceInfo {

	private TelephonyManager telephonyManager;
	/**
	 * �����ƶ��û�ʶ����
	 */
	private String IMSI;
	private Context cxt;

	public AndroidDeviceInfo(Context context) {
		cxt = context;
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * ��ȡ�绰����
	 */
	public String getNativePhoneNumber() {
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;
	}
	
	// ��ȡ�ֻ�CPU��Ϣ  
	private String getCpuInfo() {  
	    String str1 = "/proc/cpuinfo";  
	    String str2 = "";  
	    String[] cpuInfo = { "", "" }; // 1-cpu�ͺ� //2-cpuƵ��  
	    String[] arrayOfString;  
	    try {  
	        FileReader fr = new FileReader(str1);  
	        BufferedReader localBufferedReader = new BufferedReader(fr, 8192);  
	        str2 = localBufferedReader.readLine();  
	        arrayOfString = str2.split("\\s+");  
	        for (int i = 2; i < arrayOfString.length; i++) {  
	            cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";  
	        }  
	        str2 = localBufferedReader.readLine();  
	        arrayOfString = str2.split("\\s+");  
	        cpuInfo[1] += arrayOfString[2];  
	        localBufferedReader.close();  
	    } catch (IOException e) {  
	    }  
	    // Log.i(TAG, "cpuinfo:" + cpuInfo[0] + " " + cpuInfo[1]);  
	    return "\n\ncpu�ͺţ�" + cpuInfo[0] + "\ncpuƵ�ʣ�" + cpuInfo[1];  
	}// ���ڴ���Ϣͬ��,cpu��Ϣ��ͨ����ȡ/proc/cpuinfo�ļ����õ�,���е�һ��Ϊcpu�ͺ�,�ڶ���ΪcpuƵ�ʡ�  

	/**
	 * ��ȡ�ֻ���������Ϣ
	 */
	public String getProvidersName() {
		String ProvidersName = "N/A";
		try {
			IMSI = telephonyManager.getSubscriberId();
			// IMSI��ǰ��3λ460�ǹ���,�����ź���2λ00 02���й��ƶ�,01���й���ͨ,03���й����š�
			System.out.println(IMSI);
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				ProvidersName = "�й��ƶ�";
			} else if (IMSI.startsWith("46001")) {
				ProvidersName = "�й���ͨ";
			} else if (IMSI.startsWith("46003")) {
				ProvidersName = "�й�����";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ProvidersName;
	}

	public String getPhoneInfo() {
		TelephonyManager tm = (TelephonyManager) cxt
				.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuilder sb = new StringBuilder();

		sb.append("\n�豸ID�� " + tm.getDeviceId());
		sb.append("\n�豸����汾�� �� " + tm.getDeviceSoftwareVersion());
		sb.append("\n�豸�ͺţ�" + android.os.Build.MODEL);
		sb.append("\nϵͳ�汾�ţ�" + android.os.Build.VERSION.RELEASE);
		
		//sb.append("\n\n�ֻ��ţ� " + tm.getLine1Number());
		//sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		//sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		//sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		sb.append("\n�������ͣ�" + tm.getNetworkType());
		//sb.append("\n�ֻ���ʽ�� " + tm.getPhoneType());
		//sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		//sb.append("\nSimOperator = " + tm.getSimOperator());
		sb.append("\n��Ӫ�����ƣ� " + getProvidersName());
		//sb.append("\n���������ƣ�" + tm.getSimOperatorName());
		//sb.append("\nSIM����� ��" + tm.getSimSerialNumber());
		//sb.append("\nSimState = " + tm.getSimState());
		//sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		//sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
		//sb.append(getCpuInfo() );

		return sb.toString();
	}

}
