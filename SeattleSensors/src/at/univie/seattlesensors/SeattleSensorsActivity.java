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
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import at.univie.seattlesensors.sensors.BatterySensor;
import at.univie.seattlesensors.sensors.DummySensor;
import at.univie.seattlesensors.sensors.GPSLocationSensor;
import at.univie.seattlesensors.sensors.NetworkLocationSensor;
import at.univie.seattlesensors.sensors.RadioSensor;

public class SeattleSensorsActivity extends Activity {
	


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seattle_sensors_main);


		
		Context context = getApplicationContext();
		
		List<Sensor> sensorlist = ((SensorManager) getSystemService(SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ALL);
		for(Sensor s: sensorlist)
			Log.d("SENSORS", s.getName());
		

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
			Log.d("SeattleSensors", "Thread already running, not spawning another one.");
		
    }
    
    
    public void startDebugView(View view) {
        Intent intentExercise = new Intent(view.getContext(), SensorDebugActivity.class);
        startActivityForResult(intentExercise, 0);
    } 

//    public void startSensorsView(View view) {
//        Intent intentExercise = new Intent(view.getContext(), SensorDebugActivity.class);
//        startActivityForResult(intentExercise, 0);
//    } 
    
    public void startSensorConfigView(View view) {
        Intent intentExercise = new Intent(view.getContext(), SensorConfigActivity.class);
        startActivityForResult(intentExercise, 0);
    } 
}
