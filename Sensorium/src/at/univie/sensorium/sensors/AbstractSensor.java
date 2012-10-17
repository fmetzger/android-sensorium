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

package at.univie.sensorium.sensors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import at.univie.sensorium.PrivacyHelper;
import at.univie.sensorium.SensorRegistry;
import at.univie.sensorium.PrivacyHelper.PrivacyLevel;

public abstract class AbstractSensor {

	private boolean enabled = false;
	private List<SensorChangeListener> listeners;
	private String description = "";
	private PrivacyHelper.PrivacyLevel plevel;

	protected Context context;
	protected String name = "";

	public AbstractSensor(Context context) {
		this.context = context;
		this.listeners = new LinkedList<SensorChangeListener>();
		this.plevel = PrivacyHelper.PrivacyLevel.FULL;
	}

	public void enable() {
		if (!enabled) {
			try {
				_enable();

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				prefs.edit().putBoolean(this.getClass().getName(), true).commit();

				int state = prefs.getInt(this.getClass().getName() + "-level", PrivacyHelper.PrivacyLevel.FULL.value());
				setPrivacylevel(PrivacyLevel.fromInt(state));

				enabled = true;
				notifyListeners();
			} catch (Exception e) {
				disable();
				Log.d("SeattleSensors", "Caught exception while enabling " + name + ": " + e.toString());
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}
		}
	}

	protected abstract void _enable();

	public void disable() {
		// if(enabled){
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			prefs.edit().putBoolean(this.getClass().getName(), false).commit();
			enabled = false;

			_disable();
			unsetallValues();
			notifyListeners();
		} catch (Exception e) {
			Log.d("SeattleSensors", "Caught exception while disabling " + name + ": " + e.toString());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		}
		// }
	}

	protected abstract void _disable();

	public void toggle() {
		if (enabled)
			disable();
		else
			enable();
	}

	public void setState(boolean newState) {
		if (newState && !enabled)
			enable();
		else if (!newState && enabled)
			disable();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public List<SensorValue> getSensorValues() {
		List<SensorValue> values = new LinkedList<SensorValue>();

		Field[] fields = this.getClass().getDeclaredFields();

		try {
			for (Field f : fields) {
				f.setAccessible(true);
				Object o = f.get(this);
				if (o instanceof SensorValue) {
					values.add((SensorValue) o);
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
		return values;
	}

	public void addListener(SensorChangeListener s) {
		this.listeners.add(s);
	}

	public void removeListener(SensorChangeListener s) {
		this.listeners.remove(s);
	}

	protected void notifyListeners() {
		for (SensorChangeListener l : listeners) {
			l.sensorUpdated(this);
		}

		StringBuilder sb = new StringBuilder();
		for (SensorValue val : getSensorValues()) {
			sb.append(val.getValue() + " " + val.getUnit().getName() + "; ");
		}
		SensorRegistry.getInstance().log(this.getClass().getCanonicalName(), sb.toString());

		Log.d("SeattleSensors", sb.toString());
	}

	public PrivacyHelper.PrivacyLevel getPrivacylevel() {
		return plevel;
	}

	public void setPrivacylevel(PrivacyHelper.PrivacyLevel privacylevel) {
		this.plevel = privacylevel;
		notifyListeners();
	}


	private void unsetallValues() {
		for (SensorValue s : getSensorValues()) {
			s.unsetValue();
		}
	}
}
