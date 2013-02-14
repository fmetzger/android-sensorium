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

package at.univie.sensorium;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import at.univie.sensorium.logging.JSONLogger;
import at.univie.sensorium.sensors.BatterySensor;
import at.univie.sensorium.sensors.BluetoothSensor;
import at.univie.sensorium.sensors.DeviceInfoSensor;
import at.univie.sensorium.sensors.GPSLocationSensor;
import at.univie.sensorium.sensors.InterfacesSensor;
import at.univie.sensorium.sensors.NetworkLocationSensor;
import at.univie.sensorium.sensors.RadioSensor;
import at.univie.sensorium.sensors.WifiConnectionSensor;
import at.univie.sensorium.sensors.WifiSensor;

public class SensorService extends Service {
	private SensorRegistry registry;

	private static final int NOTIFICATION = 42;
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("LocalService", "Received bind intent");
		startSensors();
		return mBinder;
	}
	public class LocalBinder extends Binder {
		SensorService getService() {
			return SensorService.this;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("LocalService", "Received start id " + startId + ": " + intent);
		startSensors();
		return START_STICKY;
	}
	
	private void startSensors(){
		registry = SensorRegistry.getInstance();
		registry.registerSensor(new DeviceInfoSensor(this));
		registry.registerSensor(new InterfacesSensor(this));
		registry.registerSensor(new RadioSensor(this));

		registry.registerSensor(new NetworkLocationSensor(this));
		registry.registerSensor(new GPSLocationSensor(this));
		registry.registerSensor(new BatterySensor(this));
//		registry.registerSensor(new DummySensor(this));
		registry.registerSensor(new WifiSensor(this));
		registry.registerSensor(new WifiConnectionSensor(this));
		registry.registerSensor(new BluetoothSensor(this));
		registry.startup(this);

		// start the XMLRPC server
		if (!XMLRPCSensorServerThread.running)
			(new Thread(new XMLRPCSensorServerThread())).start();
		else
			Log.d("SeattleSensors",
					"Thread already running, not spawning another one.");

		// (temporarily) attach the JSON writer
		registry.setJSONLogger(new JSONLogger());
		registry.getJSONLogger().init(registry.getSensors());
	}
	
	@Override
	public void onCreate() {
		Notification sensorsNot = new Notification();
		sensorsNot.icon = R.drawable.ic_launcher;
		sensorsNot.when = System.currentTimeMillis();
		Intent notificationIntent = new Intent(this, SensoriumActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		sensorsNot.setLatestEventInfo(this, "SeattleSensors", "running", contentIntent);
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION, sensorsNot);
	}



	@Override
	public void onDestroy() {
	registry.getJSONLogger().finalize();
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(NOTIFICATION);
		Log.d("SeattleSensors", "SeattleSensors stopped");
	}

	public SensorRegistry getSensorRegistry() {
		return registry;
	}
}
