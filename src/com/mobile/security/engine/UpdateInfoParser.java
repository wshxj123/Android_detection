package com.mobile.security.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.mobile.security.domain.UpdateInfo;

public class UpdateInfoParser {

	public static UpdateInfo getUpdateInfo(InputStream is) throws Exception {
		UpdateInfo info = new UpdateInfo();
		XmlPullParser xmlPullParser = Xml.newPullParser();
		xmlPullParser.setInput(is, "utf-8");
		int type = xmlPullParser.getEventType();
		while (XmlPullParser.END_DOCUMENT != type) {
			switch (type) {
			case XmlPullParser.START_TAG:
				acquireValue(info, xmlPullParser.getName(), xmlPullParser.nextText());
				break;
			default:
				break;
			}
			type = xmlPullParser.next();
		}
		return info;
	}

	private static void acquireValue(UpdateInfo info, String name, String value) {
		
		Log.d("UpdateInfoParser", "property  name:" + name + "   value:" + value);
		if ("version".equals(name)) {
			info.setVersion(value);
		} else if ("description".equals(name)) {
			info.setDescription(value);
		} else if ("apkurl".equals(name)) {
			info.setUrl(value);
		}
	}

}
