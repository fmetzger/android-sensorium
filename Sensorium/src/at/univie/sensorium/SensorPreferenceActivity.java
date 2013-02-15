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

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import at.univie.sensorium.extinterfaces.HTTPSUploaderDialogPreference;
import at.univie.sensorium.sensors.AbstractSensor;

public class SensorPreferenceActivity extends PreferenceActivity {

	SharedPreferences.OnSharedPreferenceChangeListener listener;

	CheckBoxPreference autostartPref;
	CheckBoxPreference xmlrpcPref;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(createPreferenceHierarchy());
		((SensoriumApplication) getApplication()).bindService();
	}

	private PreferenceScreen createPreferenceHierarchy() {
		// Root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

		PreferenceCategory generalCat = new PreferenceCategory(this);
		generalCat.setTitle("General Preferences");
		root.addPreference(generalCat);

		autostartPref = new CheckBoxPreference(this);
		autostartPref.setKey("sensor_autostart");
		autostartPref.setTitle("Sensor starts on boot");
		autostartPref.setSummary("Keeps the sensor service running at all times.");
		generalCat.addPreference(autostartPref);
		
		PreferenceCategory interfacesCat = new PreferenceCategory(this);
		interfacesCat.setTitle("External Interfaces");
		root.addPreference(interfacesCat);
		
		xmlrpcPref = new CheckBoxPreference(this);
		xmlrpcPref.setKey("xmlrpc_enabled");
		xmlrpcPref.setTitle("Enable XMLRPC");
		xmlrpcPref.setSummary("Make sensor data available through localhost XMLRPC.");
		xmlrpcPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (newValue instanceof Boolean && (Boolean) newValue == true){
					SensorRegistry.getInstance().startXMLRPCInterface();
					Toast.makeText(SensorRegistry.getInstance().getContext(), "Starting XMLRPC interface.", Toast.LENGTH_SHORT).show();
				} else{
					SensorRegistry.getInstance().stopXMLRPCInterface();
					Toast.makeText(SensorRegistry.getInstance().getContext(), "Stopping XMLRPC interface.", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
		
		interfacesCat.addPreference(xmlrpcPref);
		
		DialogPreference uploadPref = new HTTPSUploaderDialogPreference(this, null);
		interfacesCat.addPreference(uploadPref);

		PreferenceCategory sensorsCat = new PreferenceCategory(this);
		sensorsCat.setTitle("Individual Sensors");
		root.addPreference(sensorsCat);

		List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();

		for (AbstractSensor sensor : sensors) {
			SensorPreference sPref = new SensorPreference(this, sensor);
			sPref.setKey(sensor.getClass().getName() + "-level");
			sensorsCat.addPreference(sPref);
		}
		return root;
	}
}
