package at.univie.seattlesensors.sensors;

public interface SensorChangeListener {
	
	public void sensorUpdated(SensorValue... values);

}
