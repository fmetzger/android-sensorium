package at.univie.seattlesensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import at.univie.seattlesensors.sensors.BatterySensor;
import at.univie.seattlesensors.sensors.DummySensor;
import at.univie.seattlesensors.sensors.GPSLocationSensor;
import at.univie.seattlesensors.sensors.NetworkLocationSensor;
import at.univie.seattlesensors.sensors.RadioSensor;

public class SensorAutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
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

}