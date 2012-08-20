/*
 *  * Copyright (C) 2012 Florian Metzger
 * 
 *  This file is part of android-seattle-sensors.
 *
 *   Foobar is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with android-seattle-sensors. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

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
