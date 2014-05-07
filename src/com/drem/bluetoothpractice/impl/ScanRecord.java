package com.drem.bluetoothpractice.impl;


public class ScanRecord {

	private int length;
    private int type;
    private byte[] data;

    public ScanRecord(int length, int type, byte[] data) {
        this.length = length;
        this.type = type;
        this.data = data;
    }

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
