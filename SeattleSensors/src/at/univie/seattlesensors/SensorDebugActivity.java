package at.univie.seattlesensors;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
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
