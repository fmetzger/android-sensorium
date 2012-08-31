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

package at.univie.seattlesensors;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import at.univie.seattlesensors.sensors.BatterySensor;
import at.univie.seattlesensors.sensors.DummySensor;
import at.univie.seattlesensors.sensors.GPSLocationSensor;
import at.univie.seattlesensors.sensors.NetworkLocationSensor;
import at.univie.seattlesensors.sensors.RadioSensor;

public class SeattleSensorsActivity extends Activity {

	private SensorService mBoundService;
	private boolean mIsBound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seattle_sensors_main);

		Context context = getApplicationContext();
		//
		// List<Sensor> sensorlist = ((SensorManager)
		// getSystemService(SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ALL);
		// for(Sensor s: sensorlist)
		// Log.d("SENSORS", s.getName());

		SensorRegistry sensorregistry = SensorRegistry.getInstance();
		sensorregistry.registerSensor(new RadioSensor(context));
		sensorregistry.registerSensor(new NetworkLocationSensor(context));
		sensorregistry.registerSensor(new GPSLocationSensor(context));
		sensorregistry.registerSensor(new BatterySensor(context));
		sensorregistry.registerSensor(new DummySensor(context));
		sensorregistry.startup();

		// start the XMLRPC server
		if (!XMLRPCSensorServerThread.running)
			(new Thread(new XMLRPCSensorServerThread())).start();
		else
			Log.d("SeattleSensors",
					"Thread already running, not spawning another one.");

	}
	

	public void startDebugView(View view) {
		Intent intentExercise = new Intent(view.getContext(),
				SensorDebugActivity.class);
		startActivityForResult(intentExercise, 0);
	}

	// public void startSensorsView(View view) {
	// Intent intentExercise = new Intent(view.getContext(),
	// SensorDebugActivity.class);
	// startActivityForResult(intentExercise, 0);
	// }

	public void startSensorConfigView(View view) {
		Intent intentExercise = new Intent(view.getContext(),
				SensorConfigActivity.class);
		startActivityForResult(intentExercise, 0);
	}
	
/*
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((SensorService.LocalBinder) service).getService();

			// Tell the user about this for our demo.
			Toast.makeText(SeattleSensorsActivity.this, "service connected",
					Toast.LENGTH_SHORT).show();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBoundService = null;
			Toast.makeText(SeattleSensorsActivity.this, "service disconnected",
					Toast.LENGTH_SHORT).show();
		}
	};

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		bindService(new Intent(SeattleSensorsActivity.this, SensorService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}
*/
}
