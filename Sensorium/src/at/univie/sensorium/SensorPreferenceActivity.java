package at.univie.sensorium;

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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import at.univie.sensorium.sensors.AbstractSensor;

public class SensorPreferenceActivity extends PreferenceActivity {

	SharedPreferences.OnSharedPreferenceChangeListener listener;

	CheckBoxPreference autostartPref;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(createPreferenceHierarchy());

		SensorServiceSingleton.getInstance().bindService(this);
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

		PreferenceCategory sensorsCat = new PreferenceCategory(this);
		sensorsCat.setTitle("Individual Sensors");
		root.addPreference(sensorsCat);

		List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();

		for (AbstractSensor sensor : sensors) {
			SensorPreference sPref = new SensorPreference(this, sensor);
			sPref.setKey(sensor.getClass().getName() + "-level");
//			sPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//				@Override
//				public boolean onPreferenceChange(Preference preference, Object newValue) {
//					Log.d("SeattleSensors", "Preference " + preference.getTitle() + " changed to " + newValue);
//					//TODO: notify the sensor listeners on this preference change!
//					return true;
//				}
//			});
			sensorsCat.addPreference(sPref);
		}
		return root;
	}
}
