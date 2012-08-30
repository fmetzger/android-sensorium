package at.univie.seattlesensors.sensors;

import android.content.Context;
import android.util.Log;


/**
 * This sensor does what all normal sensors shouldn't. And the application 
 * needs to survive all of this.
 *
 */
public class DummySensor extends AbstractSensor {

	public DummySensor(Context context) {
		super(context);
		this.name = "Test Sensor";
	}
	
	@Override
	public void enable(){
		// force an exception
		String x = null;
		System.out.print(x);
		Log.d("TEST", x);
	}

}
