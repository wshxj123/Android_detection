package com.mobile.security.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopyUtil {

	//1.6 http协议不支持加消息头
	/**
	 * 把输入流里面的内容拷贝到文件里面
	 * @param is 输入流
	 * @param file 要写入的文件对象 
	 */
	public  static void copyFile(InputStream is, File file) throws Exception{
		byte[] buffer = new byte[1024];
		int len = 0;
		FileOutputStream fos = new FileOutputStream(file);
		while((len = is.read(buffer))!=-1){
			fos.write(buffer, 0, len);
		}
		fos.flush();
		fos.close();
		is.close();
	}
	
}
