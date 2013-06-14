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

//@IF_PLAY_SERVICES@
 
package at.univie.sensorium.sensors;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import at.univie.sensorium.SensorRegistry;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class FusedLocationSensor extends AbstractSensor implements LocationListener, ConnectionCallbacks {

	private LocationClient mLocationClient;
	private static final int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
	private static final int updateIntervallMillis = 60000;
	private static final int fastestUpdateIntervallMillis = 10000;

	private SensorValue longitude;
	private SensorValue latitude;
	private SensorValue altitude;
	private SensorValue accuracy;
	private SensorValue bearing;
	private SensorValue speed;
	private SensorValue address;

	public FusedLocationSensor() {
		super();

		setName("Fused Location");

		longitude = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.LONGITUDE);
		latitude = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.LATITUDE);
		altitude = new SensorValue(SensorValue.UNIT.METER, SensorValue.TYPE.ALTITUDE);
		accuracy = new SensorValue(SensorValue.UNIT.METER, SensorValue.TYPE.ACCURACY);
		bearing = new SensorValue(SensorValue.UNIT.DEGREE, SensorValue.TYPE.BEARING);
		speed = new SensorValue(SensorValue.UNIT.METERSPERSECOND, SensorValue.TYPE.VELOCITY);
		address = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.ADDRESS);
	}

	@Override
	protected void _enable() throws SensorException {
		if (playServicesAvailable()) {
			mLocationClient = new LocationClient(getContext(), this, connectionFailedListener);
			mLocationClient.connect();

			// cannot init here, wait for the onConnected callback

		} else {
			throw new SensorException("Play service not available, not enabling fused location sensor");
		}
	}

	@Override
	protected void _disable() {
		if (mLocationClient != null) {
			mLocationClient.removeLocationUpdates(this);
			mLocationClient.disconnect();
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		Location mCurrentLocation = mLocationClient.getLastLocation();

		if (mCurrentLocation != null) {
			longitude.setValue(mCurrentLocation.getLongitude());
			latitude.setValue(mCurrentLocation.getLatitude());
			altitude.setValue(mCurrentLocation.getAltitude());
			accuracy.setValue(mCurrentLocation.getAccuracy());
			bearing.setValue(mCurrentLocation.getBearing());
			speed.setValue(mCurrentLocation.getSpeed());
		}

		LocationRequest mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(priority);
		mLocationRequest.setInterval(updateIntervallMillis);
		mLocationRequest.setFastestInterval(fastestUpdateIntervallMillis);
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// Toast.makeText(getContext(), "Disconnected. Please re-connect.",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		longitude.setValue(location.getLongitude());
		latitude.setValue(location.getLatitude());
		altitude.setValue(location.getAltitude());
		accuracy.setValue(location.getAccuracy());
		bearing.setValue(location.getBearing());
		speed.setValue(location.getSpeed());

		Geocoder myLocation = new Geocoder(getContext().getApplicationContext(), Locale.getDefault());
		List<Address> list = null;
		try {
			list = myLocation.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if (list != null && list.size() > 0) {
				Address addr = list.get(0);
				String addressText = String.format("%s, %s, %s", addr.getMaxAddressLineIndex() > 0 ? addr.getAddressLine(0) : "", addr.getLocality(), // location.getAdminArea(),
						addr.getCountryName());
				address.setValue(addressText);
			} else
				address.setValue("n/a");
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d(SensorRegistry.TAG, sw.toString());
		}

		notifyListeners();

	}

	// check for play serves as shown in the android example
	private boolean playServicesAvailable() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getContext());
		if (ConnectionResult.SUCCESS == resultCode) {
			Log.d(SensorRegistry.TAG, "Google Play services is available.");
			return true;

		} else {
			String result = "";
			switch (resultCode) {
			case (ConnectionResult.DEVELOPER_ERROR):
				result = "developer error";
				break;
			case (ConnectionResult.INTERNAL_ERROR):
				result = "internal error";
				break;
			case (ConnectionResult.LICENSE_CHECK_FAILED):
				result = "license check failed";
				break;
			case (ConnectionResult.NETWORK_ERROR):
				result = "network error";
				break;
			case (ConnectionResult.RESOLUTION_REQUIRED):
				result = "resolution required";
				break;
			case (ConnectionResult.SERVICE_DISABLED):
				result = "service disabled";
				break;
			case (ConnectionResult.SERVICE_INVALID):
				result = "service invalid";
				break;
			case (ConnectionResult.SERVICE_MISSING):
				result = "service missing";
				break;
			case (ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED):
				result = "service version update required";
				break;
			case (ConnectionResult.SIGN_IN_REQUIRED):
				result = "sign in required";
				break;
			}
			Log.d(SensorRegistry.TAG, "Google Play services not available with cause: " + result + ".");
			return false;
		}
	}

	private OnConnectionFailedListener connectionFailedListener = new OnConnectionFailedListener() {
		// int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
		@Override
		public void onConnectionFailed(ConnectionResult connectionResult) { // NOOP
																			// for
																			// now
			// if (connectionResult.hasResolution()) {
			// try {
			// connectionResult.startResolutionForResult(this,
			// CONNECTION_FAILURE_RESOLUTION_REQUEST);
			// } catch (IntentSender.SendIntentException e) {
			// // Log the error
			// e.printStackTrace();
			// }
			// } else {
			// showErrorDialog(connectionResult.getErrorCode());
			// }
		}
	};
}
 
//@ENDIF_PLAY_SERVICES@