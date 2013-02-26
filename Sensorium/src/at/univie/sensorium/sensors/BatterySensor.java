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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatterySensor extends AbstractSensor {
	
	public static BroadcastReceiver batteryReceiver;
	public static Intent batteryIntent;
	
	private SensorValue timestamp;
	private SensorValue level;
	private SensorValue temperature;
	private SensorValue voltage;
	private SensorValue technology;
	private SensorValue plugged;
	
	

	public BatterySensor(Context context) {
		super(context);
		name = "Battery Sensor";
		
		timestamp = new SensorValue(SensorValue.UNIT.MILLISECONDS, SensorValue.TYPE.TIMESTAMP);
		level = new SensorValue(SensorValue.UNIT.RELATIVE, SensorValue.TYPE.CHARGE);
		temperature = new SensorValue(SensorValue.UNIT.TEMPERATURE, SensorValue.TYPE.TEMPERATURE);
		voltage = new SensorValue(SensorValue.UNIT.VOLTAGE, SensorValue.TYPE.VOLTAGE);
		technology = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.BATTERY_TECHNOLOGY);
		plugged = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.PLUGGED);
	}

	@Override
	protected void _enable() {
        batteryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                
            	timestamp.setValue(System.currentTimeMillis());
            	int plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            	
                if (plug == BatteryManager.BATTERY_PLUGGED_AC) {
                    plugged.setValue("AC");
                } else if (plug == BatteryManager.BATTERY_PLUGGED_USB) {
                    plugged.setValue("USB");
                } else if (plug == 0) {
                    plugged.setValue("unplugged");
                } else {
                    plugged.setValue("n/a");
                }
                
                technology.setValue(intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY));
                Float f = Float.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0));
                temperature.setValue(f/10);
                f = Float.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0));
                voltage.setValue(f/1000);
                
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (rawlevel >= 0 && scale > 0) {
                    level.setValue((rawlevel * 100) / scale);
                } else {
                	level.setValue(rawlevel);
                }
                notifyListeners();
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryIntent = getContext().getApplicationContext().registerReceiver(batteryReceiver, batteryLevelFilter);
	}

	@Override
	protected void _disable() {
		if(batteryIntent != null)
			getContext().getApplicationContext().unregisterReceiver(batteryReceiver);
	}
}
