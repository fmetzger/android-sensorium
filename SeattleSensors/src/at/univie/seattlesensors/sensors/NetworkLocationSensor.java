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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import at.univie.seattlesensors.PrivacyHelper;

public class NetworkLocationSensor extends AbstractSensor {

	private LocationManager locationManager;
	private LocationListener locationListener;

	private SensorValue timestamp;
	private SensorValue longitude;
	private SensorValue latitude;
	private SensorValue altitude;
	private SensorValue accuracy;

	public NetworkLocationSensor(Context context) {
		super(context);

		name = "Network Loc Sensor";
		
		timestamp = new SensorValue(SensorValue.UNIT.MILLISECONDS, SensorValue.TYPE.TIMESTAMP);
		longitude = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.LONGITUDE);
		latitude = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.LATITUDE);
		altitude = new SensorValue(SensorValue.UNIT.METER, SensorValue.TYPE.ALTITUDE);
		accuracy = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.ACCURACY);
	}

	@Override
	protected void _enable() {

		locationListener = new LocationListener() {
			public void onLocationChanged(Location loc) {
				longitude.setValue(loc.getLongitude());
				latitude.setValue(loc.getLatitude());
				altitude.setValue(loc.getAltitude());
				accuracy.setValue(loc.getAccuracy());
				timestamp.setValue(System.currentTimeMillis());
				
				notifyListeners(timestamp, PrivacyHelper.anonymizelocation(longitude, getPrivacylevel()), PrivacyHelper.anonymizelocation(latitude, getPrivacylevel()), altitude, accuracy);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
				Log.d("LocationSensor", provider + " enabled, listening for updates.");
			}

			public void onProviderDisabled(String provider) {
				Log.d("LocationSensor", provider + " disabled, no more updates.");
			}
		};

		locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	@Override
	protected void _disable() {
		if (locationManager != null)
			locationManager.removeUpdates(locationListener);
	}

	@XMLRPCMethod
	public Object[] networklocationInformation() {
			return new Object[] { "timestamp", timestamp.getValue(), "long", longitude.getValue(), "lat", latitude.getValue(), "alt", altitude.getValue(), "accuracy", accuracy.getValue() };
	}
}
