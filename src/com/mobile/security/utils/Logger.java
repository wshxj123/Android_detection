package com.mobile.security.utils;

import android.util.Log;

/**
 * 一个具有开关的日志工具 代替系统的Log类
 * 
 * @author zehua
 * 
 */
public class Logger {
	private static int LOGLEVEL = 7;
	private static int VERBOSE = 1;
	private static int DEBUG = 2;
	private static int INFO = 3;
	private static int WARN = 4;
	private static int ERROR = 5;
	
	public static void v(String tag,String msg){
		if(LOGLEVEL>VERBOSE)
		Log.v(tag, msg);
	}
	public static void d(String tag,String msg){
		if(LOGLEVEL>DEBUG)
		Log.d(tag, msg);
	}
	public static void i(String tag,String msg){
		if(LOGLEVEL>INFO)
		Log.i(tag, msg);
	}
	public static void w(String tag,String msg){
		if(LOGLEVEL>WARN)
		Log.w(tag, msg);
	}
	public static void e(String tag,String msg){
		if(LOGLEVEL>ERROR)
		Log.e(tag, msg);
	}
}
