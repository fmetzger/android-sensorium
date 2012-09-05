package at.univie.seattlesensors;

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import at.univie.seattlesensors.sensors.AbstractSensor;

public class SensorPreferenceActivity extends PreferenceActivity {
	
	SharedPreferences.OnSharedPreferenceChangeListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(createPreferenceHierarchy());
		
		SharedPreferences prefs = 
			    PreferenceManager.getDefaultSharedPreferences(this);
		
		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			   Log.d("SeattleSensors",key + " changed");
				List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();
				for(AbstractSensor sensor: sensors){
					if (sensor.getClass().getName().equals(key))
							sensor.toggle();
				}
			  }
			};

			prefs.registerOnSharedPreferenceChangeListener(listener);
	}

	private PreferenceScreen createPreferenceHierarchy() {
		// Root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
				this);

		PreferenceCategory generalCat = new PreferenceCategory(this);
		generalCat.setTitle("General Preferences");
		root.addPreference(generalCat);

		CheckBoxPreference autostartPref = new CheckBoxPreference(this);
		autostartPref.setKey("sensor_autostart");
		autostartPref.setTitle("Sensor starts on boot");
		autostartPref
				.setSummary("Keeps the sensor service running at all times.");
		generalCat.addPreference(autostartPref);

		CheckBoxPreference allsensorsPref = new CheckBoxPreference(this);
		allsensorsPref.setKey("all_sensors_override");
		allsensorsPref.setTitle("Sensors Privacy");
		allsensorsPref
				.setSummary("Keeps the sensor service running at all times.");
		generalCat.addPreference(allsensorsPref);

		PreferenceCategory sensorsCat = new PreferenceCategory(this);
		sensorsCat.setTitle("Individual Sensors");
		root.addPreference(sensorsCat);

		List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();
		for(AbstractSensor sensor: sensors){
			CheckBoxPreference tmpPref = new CheckBoxPreference(this);
			tmpPref.setKey(sensor.getClass().getName());
			tmpPref.setTitle(sensor.getName());
			tmpPref.setSummary(sensor.getName());
			sensorsCat.addPreference(tmpPref);
		}
//		
//		CheckBoxPreference sensor1Pref = new CheckBoxPreference(this);
//		sensor1Pref.setKey("sensor_1");
//		sensor1Pref.setTitle("Sensor starts on boot");
//		sensor1Pref
//				.setSummary("Keeps the sensor service running at all times.");
//		sensorsCat.addPreference(sensor1Pref);
//
//		CheckBoxPreference sensor2Pref = new CheckBoxPreference(this);
//		sensor2Pref.setKey("sensor_2");
//		sensor2Pref.setTitle("Sensor starts on boot");
//		sensor2Pref
//				.setSummary("Keeps the sensor service running at all times.");
//		sensorsCat.addPreference(sensor2Pref);
//
//		CheckBoxPreference sensor3Pref = new CheckBoxPreference(this);
//		sensor3Pref.setKey("sensor_3");
//		sensor3Pref.setTitle("Sensor starts on boot");
//		sensor3Pref
//				.setSummary("Keeps the sensor service running at all times.");
//		sensorsCat.addPreference(sensor3Pref);
		return root;
	}
}
