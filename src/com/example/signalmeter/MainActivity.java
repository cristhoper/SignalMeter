package com.example.signalmeter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.simonvt.menudrawer.MenuDrawer;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	WifiManager wifi;       
	ListView lv;

	int size = 0;
	List<ScanResult> results;

	ArrayList<HashMap<Integer, ScanResult>> arrayList = new ArrayList<HashMap<Integer,ScanResult>>();

	WifiItemAdapter adapter;
	private Timer timer;

	private MenuDrawer drawer;

	/* Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getActionBar().hide();

		drawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
		drawer.setContentView(R.layout.activity_main);
		drawer.setMenuView(R.layout.activity_menu);

		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (wifi.isWifiEnabled() == false) {
			Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
			wifi.setWifiEnabled(true);
		}

		lv = (ListView)findViewById(R.id.list);		
		adapter = new WifiItemAdapter(this, arrayList);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClick);

		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				refreshResults();

			}
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		serviceTimerDaemon(ServiceState.START);



		findViewById(R.id.control_menu).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				drawer.openMenu();	
			}
		});


		findViewById(R.id.list).setVisibility(View.VISIBLE);
		findViewById(R.id.about).setVisibility(View.GONE);
		findViewById(R.id.show_about).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				findViewById(R.id.about).setVisibility(View.VISIBLE);
				findViewById(R.id.list).setVisibility(View.GONE);
				drawer.closeMenu();
			}
		});

		findViewById(R.id.show_configuration).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				findViewById(R.id.list).setVisibility(View.VISIBLE);
				findViewById(R.id.about).setVisibility(View.GONE);
				drawer.closeMenu();

			}
		});
	}


	private void refreshResults() {
		arrayList.clear();
		results = wifi.getScanResults();
		size = results.size()-1;

		while (size >= 0) {   
			HashMap<Integer, ScanResult> itemlist = new HashMap<Integer, ScanResult>();
			itemlist.put(WifiItemAdapter.FIX_KEY, results.get(size));
			arrayList.add(itemlist);
			adapter.notifyDataSetChanged();
			size--;
		}

	}

	private enum ServiceState {
		START,
		RESTART,
		STOP
	}

	private boolean serviceTimerDaemon(ServiceState state) {
		switch (state) {
		case START:
			if(timer == null)
				timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					wifi.startScan();
				}
			}, 0, 2000);			
			return true;
		case STOP:
			if(timer != null)
				timer.cancel();
			timer = null;
			return true;
		case RESTART:
			serviceTimerDaemon(ServiceState.STOP);
			serviceTimerDaemon(ServiceState.START);
		}
		return false;
	}

	OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			serviceTimerDaemon(ServiceState.STOP);

			ScanResult item = (ScanResult) view.getTag(R.layout.row);


			// custom dialog
			final Dialog dialog = new Dialog(MainActivity.this);
			dialog.setContentView(R.layout.dialog);
			dialog.setTitle(item.SSID);

			TextView text1 = (TextView) dialog.findViewById(R.id.text_signal);
			TextView text2 = (TextView) dialog.findViewById(R.id.text_bssid);
			TextView text3 = (TextView) dialog.findViewById(R.id.text_frequency);
			TextView text4 = (TextView) dialog.findViewById(R.id.text_capabilities);

			text1.setText(item.level+"dbm");
			text2.setText("AP: " + item.BSSID);
			text3.setText("Freq: " + item.frequency);
			text4.setText("Details: " + item.capabilities);

			dialog.findViewById(R.id.close).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					serviceTimerDaemon(ServiceState.START);
					dialog.dismiss();
				}
			});

			dialog.show();
		}
	};

}