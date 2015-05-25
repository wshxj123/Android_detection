package com.mobile.security.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopyUtil {

	//1.6 httpЭ�鲻֧�ּ���Ϣͷ
	/**
	 * ����������������ݿ������ļ�����
	 * @param is ������
	 * @param file Ҫд����ļ����� 
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
