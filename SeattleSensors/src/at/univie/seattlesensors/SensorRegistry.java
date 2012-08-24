/*
 *  * Copyright (C) 2012 Florian Metzger
 * 
 *  This file is part of android-seattle-sensors.
 *
 *   android-seattle-sensors is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   android-seattle-sensors is distributed in the hope that it will be useful,
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;
import at.univie.seattlesensors.sensors.XMLRPCMethod;

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

	public void registerSensor(AbstractSensor sensor) {
		for(AbstractSensor s: sensors){
			if(s.getClass().equals(sensor.getClass())){
				Log.d("SeattleSensors", "Sensor of this class already present, not registering.");
				return;
			}
		}
		sensors.add(sensor);
	}

	public List<String> getSensorMethods() {
		List<String> out = new LinkedList<String>();

		for (AbstractSensor sensor : sensors) {
			if (sensor.isEnabled()) {
				Method[] methods = sensor.getClass().getMethods();
				for (Method m : methods) {
//					Log.d("REFLECTIONTEST", m.getName());
					if (m.isAnnotationPresent(XMLRPCMethod.class))
						out.add(m.getName());
				}
			}
		}

		return out;
	}

	public Object[] getSensorMethodSignature(String methodname) {

		List<String> signature = new LinkedList<String>();

		for (AbstractSensor sensor : sensors) {
			if (sensor.isEnabled()) {
				Method[] methods = sensor.getClass().getMethods();
				for (Method m : methods) {
					if (m.isAnnotationPresent(XMLRPCMethod.class)) {
						if (m.getName().equals(methodname)) {
							Class<?>[] params = m.getParameterTypes();
							Class<?> rettype = m.getReturnType();
							
							if(rettype.toString().equals("class [Ljava.lang.Object;")){
								signature.add("array");
							} else {
								signature.add(rettype.toString());
							}
							
							if(params != null && params.length != 0){
								for (Class<?> c : params) {
									signature.add(c.toString());
								}								
							} else {
								signature.add("nil");
							}

							return signature.toArray();
						}
					}
				}
			}
		}
		return null;
	}
	
	public Object callSensorMethod(String methodname) {

		for (AbstractSensor sensor : sensors) {
			if (sensor.isEnabled()) {
				Method[] methods = sensor.getClass().getMethods();
				for (Method m : methods) {
					if (m.isAnnotationPresent(XMLRPCMethod.class)) {
						if (m.getName().equals(methodname)) {
							try {
								return m.invoke(sensor);
							} catch (IllegalArgumentException e) {
								Log.d("SeattleSensors", e.toString());
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								Log.d("SeattleSensors", e.toString());
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								Log.d("SeattleSensors", e.toString());
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return null;
	}

	public void debugOut(String out) {
		if(textoutput != null)
			textoutput.append(out+"\n");
	}

	public void setDebugView(TextView t) {
		this.textoutput = t;
	}
	
	public List<AbstractSensor> getSensors(){
		return this.sensors;
	}
}
