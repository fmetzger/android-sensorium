package at.univie.sensorium;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import at.univie.sensorium.R;

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
