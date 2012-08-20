package at.univie.seattlesensors;

import java.util.List;

public interface Sensor {
	
	public void disable();
	
	public List<String> getMethods();
	
	public boolean hasMethod(String methodname);
	
	public Object[] methodSignature(String methodname);
	
	public Object[] callMethod(String methodname);

}
