package at.univie.seattlesensors;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SensorDebugActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_debug);

		SensorServiceSingleton.getInstance().bindService(this);

		TextView t = (TextView) findViewById(R.id.sensoroutput);

		SensorRegistry sensorregistry = SensorRegistry.getInstance();
		sensorregistry.setDebugView(t);
	}
}
