package com.mobile.security.adapter;

import com.mobile.security.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainUIAdapter extends BaseAdapter {
	
	private static final String[] NAMES = new String[] {"手机信息", "应用权限检测", "文本信息检测","版本更新"};
		//, "未开发", "未开发", "未开发", "未开发", "未开发"};
	
	private static final int[] ICONS = new int[] {R.drawable.widget01, R.drawable.widget02, R.drawable.widget04, R.drawable.widget05};
	//, R.drawable.widget06, R.drawable.widget07, R.drawable.widget08, R.drawable.widget09};
	
	private static ImageView imageView;
	private static TextView textView;
	
	private Context context;
	private LayoutInflater inflater;
	private SharedPreferences sp;

	public MainUIAdapter (Context context) {
		
		this.context = context;
		inflater = LayoutInflater.from(this.context);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}
	
	@Override
	public int getCount() {
		
		return NAMES.length;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		
		return arg0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		
		View view = inflater.inflate(R.layout.main_item, null);
		imageView = (ImageView) view.findViewById(R.id.iv_main_icon);
		textView = (TextView) view.findViewById(R.id.tv_main_name);
		imageView.setImageResource(ICONS[position]);
		textView.setText(NAMES[position]);
		if(position == 0){
			String name = sp.getString("lostName", "");
			if(!name.equals("")){
				textView.setText(name);
			}
		}
		return view;
	}

}
