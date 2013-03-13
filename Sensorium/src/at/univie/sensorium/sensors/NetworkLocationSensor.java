/*
 *  This file is part of Sensorium.
 *
 *   Sensorium is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Sensorium is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Sensorium. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package at.univie.sensorium.sensors;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class NetworkLocationSensor extends AbstractSensor {

	private LocationManager locationManager;
	private LocationListener locationListener;

	private SensorValue longitude;
	private SensorValue latitude;
	private SensorValue altitude;
	private SensorValue accuracy;
	private SensorValue address;
	private SensorValue speed;
	
	private long timeMillis;

	public NetworkLocationSensor() {
		super();

		name = "Network Location";
		
		longitude = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.LONGITUDE);
		latitude = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.LATITUDE);
		altitude = new SensorValue(SensorValue.UNIT.METER, SensorValue.TYPE.ALTITUDE);
		accuracy = new SensorValue(SensorValue.UNIT.METER, SensorValue.TYPE.ACCURACY);
		address = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.ADDRESS);
		speed = new SensorValue(SensorValue.UNIT.METERSPERSECOND, SensorValue.TYPE.VELOCITY);
	}

	@Override
	protected void _enable() {

		locationListener = new LocationListener() {
			public void onLocationChanged(Location loc) {
				longitude.setValue(loc.getLongitude());
				latitude.setValue(loc.getLatitude());
				altitude.setValue(loc.getAltitude());
				accuracy.setValue(loc.getAccuracy());
				speed.setValue(loc.getSpeed());
				timeMillis = loc.getTime();
				
				Geocoder myLocation = new Geocoder(getContext().getApplicationContext(), Locale.getDefault());
				List<Address> list = null;
				try {
					list = myLocation.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
					if (list != null && list.size() > 0) {
						Address location = list.get(0);
						String addressText = String.format("%s, %s, %s",
								location.getMaxAddressLineIndex() > 0 ? location.getAddressLine(0) : "",
										location.getLocality(), // location.getAdminArea(), 
										location.getCountryName());
						address.setValue(addressText);
					}
					else
						address.setValue("n/a");
				} catch (IOException e) {
					e.printStackTrace();
				}
								
				notifyListeners();
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

		locationManager = ((LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
	}	

	@Override
	protected void _disable() {
		if (locationManager != null)
			locationManager.removeUpdates(locationListener);
	}
	
	protected void updateTimestamp(){
		timestamp.setValue(timeMillis);
	}
}
