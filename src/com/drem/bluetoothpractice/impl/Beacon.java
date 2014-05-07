package com.drem.bluetoothpractice.impl;

import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class Beacon {

	private int signalPower, major, minor;
	private int txPower;
	private String name, address;
	private UUID uuid;
	
	public static Beacon createBeacon(BluetoothDevice device, int rssi, byte[] scanRecord, int index) {
		Beacon beacon = new Beacon();
		beacon.signalPower = rssi;
		beacon.txPower = 0;
		Log.i("BABLABA", "Index: "+ index);
		beacon.major = (scanRecord[index+20] & 0xff) * 0x100 + (scanRecord[index+21] & 0xff);
   		beacon.minor = (scanRecord[index+22] & 0xff) * 0x100 + (scanRecord[index+23] & 0xff);
   		beacon.txPower = (int)scanRecord[index+24]; // this one is signed
   		// split out uuid bytes
   		byte[] uuidBytes = new byte[16];
		System.arraycopy(scanRecord, index+4, uuidBytes, 0, 16); 
		beacon.uuid = UUID.nameUUIDFromBytes(uuidBytes);
        if (device != null) {
            beacon.address = device.getAddress();
            beacon.name = device.getName();
            Log.i("bth", bytesToHex(scanRecord));
        }
        
        return beacon;
	}
	
	public String getName() {
		if (name == null) {
			return "Unknown";
		}
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getSignal() {
		return signalPower;
	}
	
	public int getTxPower() {
		return txPower;
	}
	
	public int getSignalPower() {
		return signalPower;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public String getUuid() {
		return uuid.toString();
	}

	public double getDistance() {
		if (signalPower == 0) {
			return -1.0;
		}

		double ratio = signalPower*1.0/txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio,10);
		}
		else {
			double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;	
			return accuracy;
		}
	}
	
	public String getUniqueName() {
		return uuid.toString() +":"+ major +":"+ minor;
	}
	@Override
	public String toString() {
		return getName() + " ID: " + getUniqueName() + " Distance: " + String.format("%.2f", getDistance());
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	private static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
