package com.example.signalmeter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiItemAdapter extends BaseAdapter {

	public static final int FIX_KEY = 99;
	
	private ArrayList<HashMap<Integer, ScanResult>> itemlist = new ArrayList<HashMap<Integer,ScanResult>>();
	private LayoutInflater layoutInflater;

	private ViewHolder mHolder;
	
	private static class ViewHolder {
		TextView ssidView, signalView;
		ImageView imageSignalView;
	}
	
	public WifiItemAdapter(Context ctx, ArrayList<HashMap<Integer,ScanResult>> itemlist) {
		this.itemlist = itemlist;
		layoutInflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		return itemlist.size();
	}

	@Override
	public ScanResult getItem(int position) {
		return itemlist.get(position).get(FIX_KEY);		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ScanResult item = getItem(position);
		
		if(item == null) return convertView;
			
		if(convertView==null){
			convertView = layoutInflater.inflate(R.layout.row, null);
			mHolder = new ViewHolder();
			mHolder.ssidView= (TextView) convertView.findViewById(R.id.text_ssid);
			mHolder.signalView = (TextView) convertView.findViewById(R.id.text_signal);
			mHolder.imageSignalView = (ImageView) convertView.findViewById(R.id.image_signal);
			
			
			if(item.SSID.length()>0)
				mHolder.ssidView.setText(item.SSID);
			else
				mHolder.ssidView.setText(item.BSSID);
			mHolder.signalView.setText(item.level+" dBm");
			
			
//			if(item.level >= -30){
				mHolder.imageSignalView.setImageResource(R.drawable.wifi_04);
//			}
//			else if(item.level >= -60){
//				mHolder.imageSignalView.setImageResource(R.drawable.wifi_03);
//			}
//			else if(item.level >= -90){
//				mHolder.imageSignalView.setImageResource(R.drawable.wifi_02);
//			}
//			else{
//				mHolder.imageSignalView.setImageResource(R.drawable.wifi_01);
//			}
			
			convertView.setTag(mHolder);
			convertView.setTag(R.layout.row,item);
		}
		else{
			mHolder = (ViewHolder)convertView.getTag();
		}

		return convertView;
	}

}
