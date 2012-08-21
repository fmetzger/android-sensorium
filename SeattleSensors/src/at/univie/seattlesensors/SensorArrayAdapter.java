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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;

public class SensorArrayAdapter extends ArrayAdapter<AbstractSensor> {

	private LayoutInflater inflater;

	public SensorArrayAdapter(Context context, List<AbstractSensor> sensorList) {
		super(context, R.layout.sensor_config_item, R.id.sensorConfigText,
				sensorList);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AbstractSensor sensor = (AbstractSensor) this.getItem(position);

		CheckBox checkBox;
		TextView textView;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sensor_config_item, null);

			textView = (TextView) convertView
					.findViewById(R.id.sensorConfigText);
			checkBox = (CheckBox) convertView
					.findViewById(R.id.sensorConfigToggle);


			convertView.setTag(new SensorConfigurationItem(textView, checkBox));

			checkBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					AbstractSensor sensor = (AbstractSensor) cb.getTag();
					sensor.setState(cb.isChecked());
				}
			});
		} else {
			SensorConfigurationItem configItem = (SensorConfigurationItem) convertView
					.getTag();
			checkBox = configItem.getCheckBox();
			textView = configItem.getTextView();
		}

		checkBox.setTag(sensor);

		checkBox.setChecked(sensor.isEnabled());
		textView.setText(sensor.getName());

		return convertView;
	}

}
