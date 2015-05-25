package com.mobile.security.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.telephony.TelephonyManager;

public class AndroidDeviceInfo {

	private TelephonyManager telephonyManager;
	/**
	 * 国际移动用户识别码
	 */
	private String IMSI;
	private Context cxt;

	public AndroidDeviceInfo(Context context) {
		cxt = context;
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * 获取电话号码
	 */
	public String getNativePhoneNumber() {
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;
	}
	
	// 获取手机CPU信息  
	private String getCpuInfo() {  
	    String str1 = "/proc/cpuinfo";  
	    String str2 = "";  
	    String[] cpuInfo = { "", "" }; // 1-cpu型号 //2-cpu频率  
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
	    return "\n\ncpu型号：" + cpuInfo[0] + "\ncpu频率：" + cpuInfo[1];  
	}// 和内存信息同理,cpu信息可通过读取/proc/cpuinfo文件来得到,其中第一行为cpu型号,第二行为cpu频率。  

	/**
	 * 获取手机服务商信息
	 */
	public String getProvidersName() {
		String ProvidersName = "N/A";
		try {
			IMSI = telephonyManager.getSubscriberId();
			// IMSI号前面3位460是国家,紧接着后面2位00 02是中国移动,01是中国联通,03是中国电信。
			System.out.println(IMSI);
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				ProvidersName = "中国移动";
			} else if (IMSI.startsWith("46001")) {
				ProvidersName = "中国联通";
			} else if (IMSI.startsWith("46003")) {
				ProvidersName = "中国电信";
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

		sb.append("\n设备ID： " + tm.getDeviceId());
		sb.append("\n设备软件版本号 ： " + tm.getDeviceSoftwareVersion());
		sb.append("\n设备型号：" + android.os.Build.MODEL);
		sb.append("\n系统版本号：" + android.os.Build.VERSION.RELEASE);
		
		//sb.append("\n\n手机号： " + tm.getLine1Number());
		//sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		//sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		//sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		sb.append("\n网络类型：" + tm.getNetworkType());
		//sb.append("\n手机制式： " + tm.getPhoneType());
		//sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		//sb.append("\nSimOperator = " + tm.getSimOperator());
		sb.append("\n运营商名称： " + getProvidersName());
		//sb.append("\n服务商名称：" + tm.getSimOperatorName());
		//sb.append("\nSIM卡序号 ：" + tm.getSimSerialNumber());
		//sb.append("\nSimState = " + tm.getSimState());
		//sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		//sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
		//sb.append(getCpuInfo() );

		return sb.toString();
	}

}
