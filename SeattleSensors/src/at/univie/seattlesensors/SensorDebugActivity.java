package at.univie.seattlesensors;

import java.lang.reflect.Method;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.BluetoothSensor.Device;

public class SensorDebugActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_debug);

        SensorServiceSingleton.getInstance().bindService(this);
        
		TextView t = (TextView) findViewById(R.id.sensoroutput);

		SensorRegistry sensorregistry = SensorRegistry.getInstance();
		sensorregistry.setDebugView(t);
		
		List<String> methods = sensorregistry.getSensorMethods();
		for (String m : methods) Log.d("getSensorMethods", m);
		
		Object [] methodsignature = sensorregistry.getSensorMethodSignature("batteryVoltage");
		for (Object o : methodsignature) Log.d("getSensorMethodSignature", o.toString());
		
		//Object [] results = sensorregistry.callSensorMethod("batteryVoltage");
		//for (Object o : results) Log.d("callSensorMethod", o.toString());
		
		Object [] results = sensorregistry.callSensorMethod("getScannedDev");
		if (results != null) {
			List<Device> dev = (List<Device>) results[0];
			if (!dev.isEmpty()) {
				for (Device d : dev) {
					Log.d("getScannedDev", d.getDevName() + "\t" + d.getMAC() + "\t" + String.valueOf(d.getRSSI()));
				}
			}
			Log.d("getScannedDev", results[1].toString()); // timestamp
		}
		
    }
}
