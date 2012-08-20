package at.univie.seattlesensors.sensors;

import java.util.List;

import android.content.Context;

public abstract class AbstractSensor {
	
	protected Context context;
	
	public AbstractSensor(Context context){
		this.context = context;
	}
	
	public abstract void disable();
	
	public abstract List<String> getMethods();
	
	public abstract boolean hasMethod(String methodname);
	
	public abstract Object[] methodSignature(String methodname);
	
	public abstract Object[] callMethod(String methodname);

}
