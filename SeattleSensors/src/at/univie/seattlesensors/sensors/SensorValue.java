package at.univie.seattlesensors.sensors;

public class SensorValue {
	
	private Object value;
	private String unit;
	
	public static enum Type {
		LONGITUDE, LATITUDE, TIMESTAMP, LIST, STATE, NAME, RELATIVE, DISTANCE 
	}
	
	public SensorValue(Object value, String unit){
		this.value = value;
		this.unit = unit;
	}
	
	public SensorValue(SensorValue copy){
		this.value = copy.getValue();
		this.unit = copy.getUnit();
	}
	
	public SensorValue(String unit){
		this.value = "n/a";
		this.unit = unit;
	}
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
