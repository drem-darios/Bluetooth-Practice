package com.drem.bluetoothpractice.impl;

public enum DistanceZone {

	IMMEDIATE(0.5),
	NEAR(2.0),
	FAR(30), 
	UNKNOWN(50);
	
	private double max;

	DistanceZone(double max) {
		this.max = max;
	}

	public double getMax() {
		return max;
	}
}
