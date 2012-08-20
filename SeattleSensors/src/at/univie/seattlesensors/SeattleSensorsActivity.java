package at.univie.seattlesensors;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.RadioSensor;

public class SeattleSensorsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		TextView t = (TextView) findViewById(R.id.sensoroutput);


		
		// new style stuff
		
		SensorRegistry sensorregistry = SensorRegistry.getInstance();
		sensorregistry.setDebugView(t);
		
		Context context = getApplicationContext();
		

		sensorregistry.registerSensor(new RadioSensor(context));
		
		
		Thread localServerThread = new Thread(new XMLRPCSensorServerThread());
		localServerThread.start();
		
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
}
