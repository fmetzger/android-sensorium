package at.univie.seattlesensors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import at.univie.seattlesensors.sensors.AbstractSensor;

public class SensorPreferenceActivity extends PreferenceActivity {

	SharedPreferences.OnSharedPreferenceChangeListener listener;
	
	CheckBoxPreference autostartPref;
	CheckBoxPreference allsensorsPref;
	Map<String,CheckBoxPreference> sensorPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(createPreferenceHierarchy());
		
		SensorServiceSingleton.getInstance().bindService(this);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs,
					String key) {
				boolean state = prefs.getBoolean(key, false);
				Log.d("SeattleSensors", key + " changed to " + state);
				
				AbstractSensor sensor = SensorRegistry.getInstance()
						.getSensorForClassname(key);
				if(sensor != null){
					if(state && !sensor.isEnabled()){
						Log.d("SeattleSensors", "trying to enable " + sensor.getName());
						sensor.enable();
					}
					else if (!state && sensor.isEnabled()){
						sensor.disable();
						Log.d("SeattleSensors", "trying to disable " + sensor.getName());
					}
						
					CheckBoxPreference tmp = sensorPrefs.get(key);
					if (sensor.isEnabled() != tmp.isChecked())
						sensorPrefs.get(key).setChecked(sensor.isEnabled());
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

		autostartPref = new CheckBoxPreference(this);
		autostartPref.setKey("sensor_autostart");
		autostartPref.setTitle("Sensor starts on boot");
		autostartPref
				.setSummary("Keeps the sensor service running at all times.");
		generalCat.addPreference(autostartPref);

		allsensorsPref = new CheckBoxPreference(this);
		allsensorsPref.setKey("all_sensors_override");
		allsensorsPref.setTitle("Sensors Privacy");
		allsensorsPref.setSummary("Enable/disable all sensors at once");
		allsensorsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Log.d("SeattleSensors", "Preference" + preference.getTitle() + "changed to " + newValue);
				if(preference.getKey().equals("all_sensors_override")){
					Log.d("SeattleSensors", "overriding individual sensor settings");
					for(CheckBoxPreference pref: sensorPrefs.values()){
						pref.setChecked((Boolean) newValue);
					}
				}
				return true;
			}
			
		});
		generalCat.addPreference(allsensorsPref);

		PreferenceCategory sensorsCat = new PreferenceCategory(this);
		sensorsCat.setTitle("Individual Sensors");
		root.addPreference(sensorsCat);

		
		List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();
		sensorPrefs = new HashMap<String,CheckBoxPreference>();
		
		for (AbstractSensor sensor : sensors) {
			CheckBoxPreference tmpPref = new CheckBoxPreference(this);
			tmpPref.setKey(sensor.getClass().getName());
			tmpPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Log.d("SeattleSensors", "Preference" + preference.getTitle() + "changed to " + newValue);
					return true;
				}
				
			});
			tmpPref.setTitle(sensor.getName());
			tmpPref.setSummary(sensor.getName());
			sensorsCat.addPreference(tmpPref);
			sensorPrefs.put(sensor.getClass().getName(), tmpPref);
		}
		return root;
	}
}
