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

package at.univie.sensorium;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.univie.sensorium.sensors.AbstractSensor;

public class SeattleSensorsActivity extends Activity {

	private ArrayAdapter<AbstractSensor> listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seattle_sensors_main);
		//SensorServiceSingleton.getInstance().bindService(this);

		ListView sensorViewList = (ListView) findViewById(R.id.sensorValues);
		List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();
		listAdapter = new SensorViewArrayAdapter(this, sensors);
		sensorViewList.setAdapter(listAdapter);
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public void onPause() {
		super.onPause();
	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivityForResult(new Intent(this, SensorPreferenceActivity.class), 0);
			return true;
		case R.id.menu_debug:
			startActivityForResult(new Intent(this, SensorDebugActivity.class), 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
