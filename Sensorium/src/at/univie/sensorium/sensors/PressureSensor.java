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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

public class PressureSensor extends AbstractSensor {

	private final int scan_interval_millis = 30000; // 30s
	private Handler handler = new Handler();

	private SensorManager mSensorManager;
	private Sensor mPressure;

	private SensorValue pressure;

	public PressureSensor() {
		super();
		
		setName("Barometer");
		
		pressure = new SensorValue(SensorValue.UNIT.PRESSURE, SensorValue.TYPE.ATMOSPHERIC_PRESSURE);
	}

	@Override
	protected void _enable() {
		mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
		mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		mSensorManager.registerListener(pressurelistener, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void _disable() {
		mSensorManager.unregisterListener(pressurelistener);
	}

	private Runnable enablePressureSensor = new Runnable() {

		@Override
		public void run() {
			mSensorManager.registerListener(pressurelistener, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
		}
	};

	private SensorEventListener pressurelistener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// Sensor.TYPE_PRESSURE:
			// values[0]: Atmospheric pressure in hPa (millibar)
			pressure.setValue(event.values[0]);
			notifyListeners();
			mSensorManager.unregisterListener(this);
			handler.postDelayed(enablePressureSensor, scan_interval_millis);
		}
	};
}
