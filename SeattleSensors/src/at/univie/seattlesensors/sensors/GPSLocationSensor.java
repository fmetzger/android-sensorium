/*
 *  * Copyright (C) 2012 Florian Metzger
 * 
 *  This file is part of android-seattle-sensors.
 *
 *   android-seattle-sensors is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   android-seattle-sensors is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with android-seattle-sensors. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package at.univie.seattlesensors.sensors;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import at.univie.seattlesensors.SensorRegistry;

public class GPSLocationSensor extends AbstractSensor {

	private LocationManager locationManager;
	private LocationListener locationListener;
	private GpsStatus.Listener gpsStatusListener;

	private SensorValue timestamp;
	private SensorValue longitude;
	private SensorValue latitude;
	private SensorValue altitude;
	private SensorValue accuracy;
	private SensorValue bearing;
	private SensorValue speed;
	private SensorValue satellites;
	
	private static final long GPS_UPDATE_TIME_INTERVAL=3000;
	
	
	public GPSLocationSensor(Context context) {
		super(context);

		name = "GPS Loc Sensor";
		
		timestamp = new SensorValue(SensorValue.UNIT.MILLISECONDS, SensorValue.TYPE.TIMESTAMP);
		longitude = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.LONGITUDE);
		latitude = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.LATITUDE);
		altitude = new SensorValue(SensorValue.UNIT.METER, SensorValue.TYPE.ALTITUDE);
		accuracy = new SensorValue(SensorValue.UNIT.METER, SensorValue.TYPE.ACCURACY);
		bearing = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.BEARING);
		speed = new SensorValue(SensorValue.UNIT.METERSPERSECOND, SensorValue.TYPE.VELOCITY);
		satellites = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.SATELLITES);
	}

	@Override
	protected void _enable() {
		
		Log.d("GPS", "ENABLING GPS");
		
		timestamp.setValue(System.currentTimeMillis());
		notifyListeners();

		locationListener = new LocationListener() {
			public void onLocationChanged(Location loc) {
				
				longitude.setValue(loc.getLongitude());
				latitude.setValue(loc.getLatitude());
				altitude.setValue(loc.getAltitude());
				accuracy.setValue(loc.getAccuracy());
				bearing.setValue(loc.getBearing());
				speed.setValue(loc.getSpeed());
				timestamp.setValue(loc.getTime());

				notifyListeners();
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
				Log.d("LocationSensor", provider
						+ " enabled, listening for updates.");
			}

			public void onProviderDisabled(String provider) {
				Log.d("LocationSensor", provider
						+ " disabled, no more updates.");
			}
		};
		
		gpsStatusListener = new Listener() {
			
			@Override
			public void onGpsStatusChanged(int event) {
				if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
					GpsStatus gpsstatus = locationManager.getGpsStatus(null);
					Iterable<GpsSatellite> gpsit = gpsstatus.getSatellites();
					int numsat = 0;
					for(GpsSatellite sat: gpsit){
						numsat++;
					}
					satellites.setValue(numsat);
					notifyListeners();
				}
				
			}
		};

		locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME_INTERVAL,
				0, locationListener);
		locationManager.addGpsStatusListener(gpsStatusListener);
	}

	@Override
	protected void _disable() {
		if (locationManager != null)
			locationManager.removeUpdates(locationListener);
		if (gpsStatusListener != null)
			locationManager.removeGpsStatusListener(gpsStatusListener);
	}

	
	@XMLRPCMethod
	public Object lastfix() {
			return timestamp.getValue();
	}
	
	@XMLRPCMethod
	public Object longitude() {
			return longitude.getValue();
	}
	
	@XMLRPCMethod
	public Object latitude() {
			return latitude.getValue();
	}
	
	@XMLRPCMethod
	public Object altitude() {
			return altitude.getValue();
	}
	
	@XMLRPCMethod
	public Object accuracy() {
			return accuracy.getValue();
	}
	
	@XMLRPCMethod
	public Object bearing() {
			return bearing.getValue();
	}
	
	@XMLRPCMethod
	public Object speed() {
			return speed.getValue();
	}
}
