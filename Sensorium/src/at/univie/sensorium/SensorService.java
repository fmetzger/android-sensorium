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

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import at.univie.sensorium.logging.JSONLogger;
import at.univie.sensorium.preferences.HTTPSUploaderDialogPreference;
import at.univie.sensorium.preferences.Preferences;
import at.univie.sensorium.sensors.AbstractSensor;

public class SensorService extends Service {
	private SensorRegistry registry;
	private static final int NOTIFICATION = 42;
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("LocalService", "Received bind intent: " + intent);
		init();
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
		init();
		return START_STICKY;
	}

	private void init() {
		
		Preferences preferences = new Preferences(this);
		registry = SensorRegistry.getInstance();
		registry.setPreferences(preferences);
		
		preferences.loadCampaignPreferences("http://homepage.univie.ac.at/florian.metzger/defaultpreferences.json");
		
		
		
		startSensors();
		registry.startup(this);
		startExtInterfaces();
	}

	private void startSensors() {
		Resources res = getResources();
		String[] sensorclassnames = res.getStringArray(R.array.sensors);

		for (String classname : sensorclassnames) {
			Log.d("SENSORS", classname);
			try {
				AbstractSensor s = (AbstractSensor) Class.forName(classname).newInstance();
				SensorRegistry.getInstance().registerSensor(s);
			} catch (ClassNotFoundException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			} catch (InstantiationException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			} catch (IllegalAccessException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}
		}
	}

	private void startExtInterfaces() {
		registry.startXMLRPCInterface();
		createJSONLoggerUploader();
		registry.getJSONLogger().init(registry.getSensors());
	}

	private void createJSONLoggerUploader() {
		registry.setJSONLogger(new JSONLogger());
		Preferences prefs = SensorRegistry.getInstance().getPreferences();
		if (prefs.getBoolean(Preferences.UPLOAD_AUTOMATIC_PREF, false)) {
			int interval = prefs.getInt(Preferences.UPLOAD_INTERVAL_PREF, 3600);
			boolean wifi = prefs.getBoolean(Preferences.UPLOAD_WIFI_PREF, false);
			String url = prefs.getString(Preferences.UPLOAD_URL_PREF, "");
			registry.getJSONLogger().autoupload(url, interval, wifi);
		}
	}

	@Override
	public void onCreate() {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, SensoriumActivity.class),PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		builder.setContentIntent(contentIntent)
		            .setSmallIcon(R.drawable.ic_launcher)
		            .setWhen(System.currentTimeMillis())
		            .setAutoCancel(true)
		            .setContentTitle("SeattleSensors")
		            .setContentText("running");
		Notification n = builder.build();
		nm.notify(NOTIFICATION, n);
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
