package at.univie.sensorium.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PressureSensor extends AbstractSensor implements SensorEventListener {
	
    private SensorManager mSensorManager;
    private Sensor mPressure;
    
    private SensorValue pressure;

    public PressureSensor() {
    	setName("Barometer");
    	pressure = new SensorValue(SensorValue.UNIT.PRESSURE, SensorValue.TYPE.ATMOSPHERIC_PRESSURE);
    }
    
	@Override
	protected void _enable() {
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void _disable() {
		mSensorManager.unregisterListener(this);
	}


	@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


	@Override
	public void onSensorChanged(SensorEvent event) {
		// Sensor.TYPE_PRESSURE:
	    // values[0]: Atmospheric pressure in hPa (millibar)
		
		// TODO: implement own update timer to decrease update frequency
		pressure.setValue(event.values[0]);
		notifyListeners();
	}


}
