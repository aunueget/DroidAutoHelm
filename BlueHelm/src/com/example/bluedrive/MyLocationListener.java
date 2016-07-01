package com.example.bluedrive;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {

	private boolean hasbearing;
	private double dbllatitude;
	private double dbllongitude;
	private double dblaltitude;
	private float bearing;
	private double lats[];
	private double longs[];
	private int calculatedBearing;
	private int dataCount;
	private boolean dataFull;
	private static final int COUNTMAX = 50;

	public MyLocationListener() {
		hasbearing = false;
		dbllatitude = 0;
		dbllongitude = 0;
		dblaltitude = 0;
		bearing = 0;
		calculatedBearing = 270;
		lats = new double[COUNTMAX];
		longs = new double[COUNTMAX];
		dataCount = 0;
		dataFull = false;
	}

	/**
	 * Code to run when the listener receives a new location
	 */

	@Override
	public void onLocationChanged(Location locFromGps) {

		// Toast.makeText(getApplicationContext(),
		// "Location changed, Lat: "+locFromGps.getLatitude()+" Long: "+
		// locFromGps.getLongitude(), Toast.LENGTH_SHORT).show();

		// LocationText.setText("Your Location: Latitude "
		// +locFromGps.getLatitude() + " Longitude: "
		// +locFromGps.getLongitude());

		dbllatitude = locFromGps.getLatitude();
		dbllongitude = locFromGps.getLongitude();
		dblaltitude = locFromGps.getAltitude();
		bearing = locFromGps.getBearing();
		hasbearing = locFromGps.hasBearing();
		if (this.dataCount >= MyLocationListener.COUNTMAX) {
			dataCount = 0;
		}
		lats[dataCount] = this.dblaltitude;
		longs[dataCount] = this.dbllongitude;
		dataCount++;

	}

	@Override
	public void onProviderDisabled(String provider) {
		// called when the GPS provider is turned off (user turning off the GPS
		// on the phone)
	}

	@Override
	public void onProviderEnabled(String provider) {
		// called when the GPS provider is turned on (user turning on the GPS on
		// the phone)
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// called when the status of the GPS provider changes

	}

	public boolean isHasbearing() {
		return hasbearing;
	}

	public void setHasbearing(boolean hasbearing) {
		this.hasbearing = hasbearing;
	}

	public double getDbllatitude() {
		return dbllatitude;
	}

	public void setDbllatitude(double dbllatitude) {
		this.dbllatitude = dbllatitude;
	}

	public double getDbllongitude() {
		return dbllongitude;
	}

	public void setDbllongitude(double dbllongitude) {
		this.dbllongitude = dbllongitude;
	}

	public double getDblaltitude() {
		return dblaltitude;
	}

	public void setDblaltitude(double dblaltitude) {
		this.dblaltitude = dblaltitude;
	}

	public float getBearing() {
		return bearing;
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	public int getValidCount(int count) {
		if (count >= MyLocationListener.COUNTMAX) {
			count = count - MyLocationListener.COUNTMAX;
		} else if (count < 0) {
			count += MyLocationListener.COUNTMAX;
		}
		return count;
	}

}