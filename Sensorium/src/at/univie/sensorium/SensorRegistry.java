/*
 *  This file is part of Sensorium.
 *
 *   Sensorium is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Sensorium is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Sensorium. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package at.univie.sensorium;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import at.univie.sensorium.extinterfaces.XMLRPCSensorServerThread;
import at.univie.sensorium.logging.JSONLogger;
import at.univie.sensorium.privacy.Privacy;
import at.univie.sensorium.sensors.AbstractSensor;
import at.univie.sensorium.sensors.SensorValue;

public class SensorRegistry {

	private static SensorRegistry instance = null;

	private List<AbstractSensor> sensors;

	private StringBuffer debugBuffer;
	private int bufferedLines = 0;
	private static final int MAXDEBUGLINES = 20;
	private TextView textoutput;
	
	private Context context;

	protected SensorRegistry() {
		sensors = new LinkedList<AbstractSensor>();
		debugBuffer = new StringBuffer();

	}

	public static SensorRegistry getInstance() {
		if (instance == null) {
			instance = new SensorRegistry();
		}
		return instance;
	}

	public void startup(Context context) {
		this.context = context;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		for (AbstractSensor sensor : sensors) {
			try {
				boolean savedstate = prefs.getBoolean(sensor.getClass().getName(), false);
				Log.d("SeattleSensors", sensor.getClass().getName() + ": " + savedstate);
				if (savedstate)
					sensor.enable();
			} catch (Exception e) {
				sensor.disable();
				Log.d("SeattleSensors", e.toString());
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}

		}

	}

	public void registerSensor(AbstractSensor sensor) {
		for (AbstractSensor s : sensors) {
			if (s.getClass().equals(sensor.getClass())) {
				Log.d("SeattleSensors", "Sensor of this class already present, not registering.");
				return;
			}
		}
		sensors.add(sensor);
	}

	private AbstractSensor getSensorWithName(String classname) {
		for (AbstractSensor sensor : sensors) {
			String sensorname = sensor.getClass().getName();
			sensorname = sensorname.substring(sensorname.lastIndexOf('.') + 1);
			if (sensorname.equals(classname) && sensor.isEnabled()) {
				return sensor;
			}
		}
		return null;
	}

	public void log(String tag, String out) {
		out = out.replaceAll("\r\n|\r|\n", " ");
		if (bufferedLines >= MAXDEBUGLINES) {
			debugBuffer.deleteCharAt(debugBuffer.length() - 1);
			debugBuffer.delete(debugBuffer.lastIndexOf("\n"), debugBuffer.length() - 1);
			bufferedLines--;
		}
		debugBuffer.insert(0, "<b>" + tag + ": </b>" + out + "<br>\n");
		bufferedLines++;
		if (textoutput != null)
			textoutput.setText(Html.fromHtml(debugBuffer.toString()), TextView.BufferType.SPANNABLE);
	}

	public void setDebugView(TextView t) {
		this.textoutput = t;
	}

	public List<AbstractSensor> getSensors() {
		return this.sensors;
	}

	public AbstractSensor getSensorForClassname(String classname) {
		for (AbstractSensor sensor : sensors) {
			String name = sensor.getClass().getName();
			if (classname.lastIndexOf('.') > 0) { // we have a qualified name
				if (name.equals(classname))
					return sensor;
			} else {
				name = name.substring(name.lastIndexOf('.') + 1);
				if (name.equals(classname)) {
					return sensor;
				}
			}

		}
		return null;
	}

	private SensorValue invokeMethod(String classname, String methodname) {
		AbstractSensor sensor = getSensorForClassname(classname);
		if (sensor != null) {
			if (sensor.isEnabled()) {
				Field[] fields = sensor.getClass().getDeclaredFields();
				try {
					for (Field f : fields) {
						f.setAccessible(true);
						Object o = f.get(sensor);
						if (f.getName().equals(methodname) && o instanceof SensorValue) {
							return (SensorValue) o;
						}
					}
				} catch (IllegalArgumentException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Log.d("SeattleSensors", sw.toString());
				} catch (IllegalAccessException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Log.d("SeattleSensors", sw.toString());
				}
			}
		}
		return null;
	}

	public Object callSensorMethod(String methodname) {
		if (methodname.lastIndexOf('.') == -1) {
			Log.d("SeattleSensor", "Invalid XMLRPC method call");
			return null;
		}
		String classname = methodname.substring(0, methodname.lastIndexOf('.'));
		methodname = methodname.substring(methodname.lastIndexOf('.') + 1);
		SensorValue val = invokeMethod(classname, methodname);
		if (val != null) {
			AbstractSensor sensor = getSensorWithName(classname);
			return Privacy.anonymize(val, sensor.getPrivacylevel()).getValue();
		}
		return null;
	}

	public Object[] getSensorMethodSignature(String methodname) {
		List<String> signature = new LinkedList<String>();

		if (methodname.lastIndexOf('.') > 0) {
			String classname = methodname.substring(0, methodname.lastIndexOf('.'));
			AbstractSensor sensor = getSensorForClassname(classname);
			
			if (sensor != null && sensor.isEnabled()) {
				Field[] fields = sensor.getClass().getDeclaredFields();
				try {
					for (Field f : fields) {
						f.setAccessible(true);
						Object o = f.get(sensor);
						if (o instanceof SensorValue) {
							String fieldname = f.getName();
							if (fieldname.equals(methodname.substring(methodname.lastIndexOf('.') + 1))) {
								signature.add(methodname);

								SensorValue sv = (SensorValue) o;
								String rettype = sv.getValue().getClass().toString();

								// TODO: this will frequently be only String and
								// changing,
								// due to be set to "n/a" when no value is
								// present
								if (rettype.equals("class [Ljava.lang.Object;")) {
									signature.add("array");
								} else if (rettype.equals("class java.lang.String")) {
									signature.add("string");
								} else if (rettype.equals("class java.lang.Integer")) {
									signature.add("int");
								} else if (rettype.equals("class java.lang.Boolean")) {
									signature.add("boolean");
								} else if (rettype.equals("class java.lang.Double")) {
									signature.add("double");
								} else if (rettype.equals("class java.lang.Float")) {
									signature.add("ex:float");
								} else if (rettype.equals("class java.lang.Long")) {
									signature.add("ex:i8");
								} else if (rettype.equals("class java.lang.Byte")) {
									signature.add("ex:i1");
								} else if (rettype.equals("class java.lang.Short")) {
									signature.add("ex:i2");
								} else {
									signature.add(rettype.toString());
								}
								// add method parameters: always nil
								signature.add("ex:nil");
							}
						}
					}
				} catch (IllegalArgumentException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Log.d("SeattleSensors", sw.toString());
				} catch (IllegalAccessException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Log.d("SeattleSensors", sw.toString());
				}
			}

			if (!signature.isEmpty())
				return signature.toArray();
		}
		return null;
	}

	public List<String> getSensorMethods() {
		List<String> out = new LinkedList<String>();

		for (AbstractSensor sensor : sensors) {
			if (sensor.isEnabled()) {
				String name = sensor.getClass().getName();
				name = name.substring(name.lastIndexOf('.') + 1);
				Field[] fields = sensor.getClass().getDeclaredFields();

				try {
					for (Field f : fields) {
						f.setAccessible(true);
						Object o = f.get(sensor);
						if (o instanceof SensorValue) {
							out.add(name + "." + f.getName());
						}

					}
				} catch (IllegalArgumentException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Log.d("SeattleSensors", sw.toString());
				} catch (IllegalAccessException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Log.d("SeattleSensors", sw.toString());
				}
			}
		}
		return out;
	}

	JSONLogger jsonlogger;
	public JSONLogger getJSONLogger(){
		return jsonlogger;
	}
	public void setJSONLogger(JSONLogger jsonlogger){
		this.jsonlogger = jsonlogger;
	}
	
	
	public Context getContext(){
		return context;
	}
	
	XMLRPCSensorServerThread xmlrpcserverthread;
	Thread x;
	public void startXMLRPCInterface() {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean xmlrpc_enabled = prefs.getBoolean(SensorPreferenceActivity.INTERFACES_XMLRPC_PREF, true);
		if(xmlrpc_enabled){
			Log.d("SeattleSensors","starting XMLRPC interface");
			xmlrpcserverthread = new XMLRPCSensorServerThread();
			x = new Thread(xmlrpcserverthread);
			x.start();
			return;
		} else {
			Log.d("SeattleSensors","Not starting XMLRPC interface, preference disabled");
		}
		
		
		if (!XMLRPCSensorServerThread.running) {
			Log.d("SeattleSensors","starting XMLRPC interface");
			xmlrpcserverthread = new XMLRPCSensorServerThread();
			x = new Thread(xmlrpcserverthread);
			x.start();
			//return;
		} else {
			Log.d("SeattleSensors", "XMLRPC thread already running, not spawning another one.");
		}
	}
	public void stopXMLRPCInterface(){
		if (XMLRPCSensorServerThread.running){
			xmlrpcserverthread.stopThread();
			x.interrupt();
			try {
				x.join();
				XMLRPCSensorServerThread.running = false;
			} catch (InterruptedException e) {
				Log.d("SeattleSensors:", e.toString());
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}
		} else {
			Log.d("SeattleSensors", "XMLRPC thread not running, not attempting to stop it.");
		}
	}
}
