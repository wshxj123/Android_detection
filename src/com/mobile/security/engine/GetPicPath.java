package com.mobile.security.engine;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GetPicPath {

	public static void listFile(String filePath, List<String> Flist) {
		
		File file = new File(filePath);
		
		if (file.exists()) {
			if (file.isFile()) {
				if (file.toString()
						.substring(file.toString().lastIndexOf(".") + 1)
						.toLowerCase().matches("png|jpg|jpeg|bmp")) {
					//System.out.println(file.getAbsolutePath());
					Flist.add(file.getAbsolutePath());
				}
			} else if (file.isDirectory()) {
				File[] fileArray = file.listFiles();
				for (File subFile : fileArray) {
					listFile(subFile.toString(), Flist);
				}
			}
		}
	}

	public static void main(String args[]) {
		ArrayList<String> Flist = new ArrayList<String>();
		listFile("/home/elaine/Downloads/fileselectdemo", Flist);
		System.out.println(Flist.size()+"---------------------------");
		for(String file:Flist){
			System.out.println(file);
		}
		
	}
}