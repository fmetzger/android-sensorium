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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.univie.sensorium.sensors.AbstractSensor;

public class SensorViewArrayAdapter extends ArrayAdapter<AbstractSensor> {

	private LayoutInflater inflater;
	
	private List<AbstractSensor> sensors;

	public SensorViewArrayAdapter(Context context, List<AbstractSensor> sensorList) {
		super(context, R.layout.sensor_view_item, R.id.sensorValue, sensorList);
		inflater = LayoutInflater.from(context);
		this.sensors = sensorList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView sValue;
		TextView sName;
		TextView sPrivacyLevel;
		TextView sUnit;
		TextView sType;
		
		SensorViewItem svi;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sensor_view_item, null);

			sValue = (TextView) convertView.findViewById(R.id.sensorValues);
			sName = (TextView) convertView.findViewById(R.id.sensorName);
			sPrivacyLevel = (TextView) convertView.findViewById(R.id.sensorPrivacyLevel);
			sUnit = (TextView) convertView.findViewById(R.id.sensorUnits);
			sType = (TextView) convertView.findViewById(R.id.sensorTypes);

			svi = new SensorViewItem(sName, sPrivacyLevel, sValue, sUnit, sType);
			convertView.setTag(svi);
			
		} else {
			svi = (SensorViewItem) convertView.getTag();
		}
		
		svi.updateDisplay(sensors.get(position));
		svi.attachto(sensors.get(position),sensors);

		return convertView;
	}
}