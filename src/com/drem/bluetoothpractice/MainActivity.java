package com.drem.bluetoothpractice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.drem.bluetoothpractice.impl.Beacon;


public class MainActivity extends ListActivity {

	/**
	 * 61687109-905F-4436-91F8-E602F514C96D:3:1125 --> blue 
	 * 61687109-905F-4436-91F8-E602F514C96D:3:1114 --> black
	 * 61687109-905F-4436-91F8-E602F514C96D:3:1104 --> white
	 */
	private static final UUID BLACK_CAT = UUID.fromString("61687109-905F-4436-91F8-E602F514C96D");
	private static final UUID WHITE_CAT = UUID.fromString("61687109-905F-4436-91F8-E602F514C96D");
	private static final UUID BLUE_CAT = UUID.fromString("61687109-905F-4436-91F8-E602F514C96D");
	// Scan for 5 seconds
	private static final long SCAN_TIME = 5000;
	// Stop scanning for 2 seconds
	private static final long STOP_TIME = 2000;
	
	private BluetoothManager manager;
	private BluetoothAdapter adapter;
	private Map<String, Beacon> beacons = new HashMap<String, Beacon>();
	private boolean scanningEnabled = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.beacon_list);
		setProgressBarIndeterminate(true);
		
		ArrayAdapter<Beacon> listAdapter = new ArrayAdapter<Beacon>(this,
                android.R.layout.simple_list_item_1, beacons.values().toArray(new Beacon[beacons.size()]));

        setListAdapter(listAdapter);
		// Get a reference to the Bluetooth Manager.
		this.manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		adapter = manager.getAdapter();
		beacons = new HashMap<String, Beacon>();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// If bluetooth not supported or hasn't been enabled, prompt to enable
		if (adapter == null || !adapter.isEnabled()) {
			Intent bluetoothRequest = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(bluetoothRequest);
			finish();
			return;
		}
		scanningEnabled = true;
		startScanning();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// Turn off scan to save on battery if paused
		adapter.stopLeScan(leScanCallback);
		scanningEnabled = false;
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
//		for (Beacon beacon : beacons.values()) {
//			menu.add(0, beacon.getSignal(), 0, beacon.getName());
//		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		BluetoothDevice device = devices.get(item.getItemId());
		
//		datGattSon = device.connectGatt(this, true, new DatGattSonCallback());
		
		return super.onOptionsItemSelected(item);
	}
	
	private void startScanning() {
		if (scanningEnabled) {


			boolean scanStarted = adapter.startLeScan(leScanCallback);
			if (scanStarted) {
				Log.d("startScanning", "Scanning started...");
			} else {
				Log.e("startScanning", "Problem starting scan...");
			}
			setProgressBarIndeterminateVisibility(true);
			handler.postDelayed(stopScanner, SCAN_TIME);	
		}
	}
	
	private void stopScanning() {
		Log.d("stopScanning", "...Scanning ended");
		adapter.stopLeScan(leScanCallback);
		setProgressBarIndeterminateVisibility(false);
		handler.postDelayed(startScanner, STOP_TIME);	
	}
	
//	@Override
//	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//		Log.i("onLeScan", "Found device with name: " + device.getName().toString() + " and RSSI: " + rssi);
//		// Guarantees unique id for this device
//		List<ScanRecord> records = new ArrayList<ScanRecord>();
//		Beacon beacon = new Beacon(records, device.getAddress(), rssi);
//		beacons.put(device.getName(), beacon);
//		handler.sendMessage(Message.obtain(null, 0, beacon));
//		invalidateOptionsMenu();
//	}
	
	@Override
    public void onListItemClick(ListView parent, View v, int position, long id)
    {
        Beacon beacon = (Beacon)getListView().getItemAtPosition(position);
        Log.i("onListItemClick", "Item selected: " + beacon.getAddress());
    }

	@SuppressLint("HandlerLeak")
	private Handler handler =  new Handler() {
	
		@Override
		public void handleMessage(Message message) {
			Beacon beacon = (Beacon) message.obj;
            beacons.put(beacon.getAddress(), beacon);
		}
	};
	
	private Runnable startScanner = new Runnable() {
		@Override
		public void run() {
			startScanning();
		}
	};
	
	private Runnable stopScanner = new Runnable() {
		@Override
		public void run() {
			stopScanning();
		}
	};
	
	private class BeaconAdapter extends ArrayAdapter<Beacon> {
		
		public BeaconAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.beacon_list, parent, false);
            }

            Beacon beacon = getItem(position);
            //Set color based on distance
            final int textColor = getDistanceColor(beacon.getTxPower());

//            TextView nameView = (TextView) convertView.findViewById(R.id.text_name);
//            nameView.setText(beacon.getName());
//            nameView.setTextColor(textColor);
//
//            TextView tempView = (TextView) convertView.findViewById(R.id.text_distance);
//            tempView.setText(String.format("%.1f\u00B0C", beacon.getDistance()));
//            tempView.setTextColor(textColor);
//
//            TextView addressView = (TextView) convertView.findViewById(R.id.text_address);
//            addressView.setText(beacon.getAddress());
//            addressView.setTextColor(textColor);
//
//            TextView rssiView = (TextView) convertView.findViewById(R.id.text_rssi);
//            rssiView.setText(String.format("%ddBm", beacon.getSignal()));
//            rssiView.setTextColor(textColor);

            return convertView;
        }

        private int getDistanceColor(float distance) {
            //Color range from 0 - 40 degC
            float clipped = Math.max(0f, Math.min(40f, distance));

            float scaled = ((40f - clipped) / 40f) * 255f;
            int blue = Math.round(scaled);
            int red = 255 - blue;

            return Color.rgb(red, 0, blue);
        }
	}
	
	private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
            	Log.i("LeScanCallback", "Beacon detected. Updating beacon list " + scanRecord.length);
           		List<Beacon> beacons = (List<Beacon>) parseBeaconData(scanRecord);
           		ArrayAdapter<Beacon> listAdapter = new ArrayAdapter<Beacon>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, beacons);
                setListAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
               }
               
               public Collection<Beacon> parseBeaconData(byte[] scanRecord) {
                   List<Beacon> result = new ArrayList<Beacon>();
            	   int startByte = 2;
                   boolean patternFound = false;
                   for (int i = 0; i < scanRecord.length; i++) {
//                	   Log.i("blah",""+((int)scanRecord[i] & 0xff));
                   }
                   while (startByte <= 5) {
	           		   if (((int)scanRecord[startByte+2] & 0xff) == 0x02 && 
	           				   ((int)scanRecord[startByte+3] & 0xff) == 0x15) {			
	           				// iBeacon found	
	           				patternFound = true;
	           				break;
	           		   }
	           		   startByte++;
                   }
                   if (patternFound) {
                	   Beacon beacon = Beacon.createBeacon(device, rssi, scanRecord, startByte);
                	   beacons.put(device.getAddress(), beacon);
                   }
                   
                   result.addAll(beacons.values());
                   return result;
               }
           });
       }
    };
}
