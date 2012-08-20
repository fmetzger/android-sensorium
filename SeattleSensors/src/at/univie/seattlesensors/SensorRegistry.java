package at.univie.seattlesensors;

import java.util.LinkedList;
import java.util.List;

import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;

public class SensorRegistry {

	private static SensorRegistry instance = null;
	
	private List<AbstractSensor> sensors;
	private TextView textoutput;
	
	
	protected SensorRegistry() {
		sensors = new LinkedList<AbstractSensor>();

	}

	public static SensorRegistry getInstance() {
		if (instance == null) {
			instance = new SensorRegistry();
		}
		return instance;
	}
	
	public void registerSensor(AbstractSensor sensor){
		sensors.add(sensor);
	}
	
	public void disableSensor(AbstractSensor sensor){
		// NYI
	}
	
	public void disableAllSensors(){
		for (AbstractSensor sensor: sensors){
			sensor.disable();
		}
	}
	
	public List<String> getSensorMethods(){
		List<String> out = new LinkedList<String>();
		
		for (AbstractSensor sensor: sensors){
			out.addAll(sensor.getMethods());
		}
		
		return out;
	}
	
	public Object[] getSensorMethodSignature(String methodname){
		for (AbstractSensor sensor: sensors){
			if (sensor.hasMethod(methodname))
				return sensor.methodSignature(methodname);
		}
		return null;
	}
	
	public Object[] callSensorMethod(String methodname){
		
		for (AbstractSensor sensor: sensors){
			if (sensor.hasMethod(methodname))
				return sensor.callMethod(methodname);
		}
		return null;
	}
	
	public void debugOut(String out){
		textoutput.append(out);
	}
	
	public void setDebugView(TextView t){
		this.textoutput = t;
	}
}
