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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;
import at.univie.seattlesensors.sensors.BatterySensor;
import at.univie.seattlesensors.sensors.LocationSensor;
import at.univie.seattlesensors.sensors.RadioSensor;

public class SeattleSensorsActivity extends Activity {
	
	private ArrayAdapter<AbstractSensor> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		TextView t = (TextView) findViewById(R.id.sensoroutput);

		SensorRegistry sensorregistry = SensorRegistry.getInstance();
		sensorregistry.setDebugView(t);
		
		Context context = getApplicationContext();
		

		sensorregistry.registerSensor(new RadioSensor(context));
		sensorregistry.registerSensor(new LocationSensor(context));
		sensorregistry.registerSensor(new BatterySensor(context));
		
		
		ListView sensorConfigList = (ListView) findViewById(R.id.sensorConfigListView);

		sensorConfigList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View item,
							int position, long id) {
						AbstractSensor sensor = listAdapter.getItem(position);
						sensor.toggle();
						SensorConfigurationItem configItem = (SensorConfigurationItem) item
								.getTag();
						configItem.getCheckBox().setChecked(sensor.isEnabled());
					}
				});
		
		
		List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();
		listAdapter = new SensorArrayAdapter(this, sensors);
		sensorConfigList.setAdapter(listAdapter);	
		
		
		// start the XMLRPC server
		Thread localServerThread = new Thread(new XMLRPCSensorServerThread());
		localServerThread.start();
		
    }


}
