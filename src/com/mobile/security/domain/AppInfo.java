package com.mobile.security.domain;


import android.graphics.drawable.Drawable;

public class AppInfo{
	private Drawable appicon;
	private String packname;
	private String appname;
	private String version;
	private boolean userapp;

	public Drawable getAppicon() {
		return appicon;
	}

	public void setAppicon(Drawable appicon) {
		this.appicon = appicon;
	}

	public String getPackname() {
		return packname;
	}

	public void setPackname(String packname) {
		this.packname = packname;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isUserapp() {
		return userapp;
	}

	public void setUserapp(boolean userapp) {
		this.userapp = userapp;
	}

}
