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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.TextView;
import at.univie.sensorium.sensors.AbstractSensor;
import at.univie.sensorium.sensors.SensorChangeListener;
import at.univie.sensorium.sensors.SensorValue;

public class SensorViewItem implements SensorChangeListener {

	private TextView textViewSensorValues;
	private TextView textViewSensorUnits;
	private TextView textViewSensorTypes;
	private TextView textViewSensorName;
	private TextView textViewSensorPrivacyLevel;
	private TextView textViewSensorTimestamp;

	public SensorViewItem(TextView sName, TextView sPrivacyLevel, TextView sTimestamp, TextView sValues, TextView sUnits, TextView sTypes) {

		this.textViewSensorValues = sValues;
		this.textViewSensorUnits = sUnits;
		this.textViewSensorTypes = sTypes;
		this.textViewSensorName = sName;
		this.textViewSensorPrivacyLevel = sPrivacyLevel;
		this.textViewSensorTimestamp = sTimestamp;
	}

	public void attachto(AbstractSensor sensor, List<AbstractSensor> sensors) {
		for (AbstractSensor s : sensors)
			s.removeListener(this);
		sensor.addListener(this);
	}

	public void updateDisplay(AbstractSensor sensor) {
		StringBuilder sValues = new StringBuilder();
		StringBuilder sUnits = new StringBuilder();
		StringBuilder sTypes = new StringBuilder();

		List<SensorValue> values = sensor.getSensorValues();

		for (SensorValue v : values) {
			if (v != null) {
				if (v.getType().equals(SensorValue.TYPE.TIMESTAMP)) {
					if(v.getValue() instanceof String){
						textViewSensorTimestamp.setText((String) v.getValue());
					} else {
						textViewSensorTimestamp.setText(new SimpleDateFormat("HH:mm", Locale.US).format(new Date((Long) v.getValue())));
					}
					
					continue;
				}
				sValues.append(v.getValueRepresentation()).append("\n");
				sUnits.append(v.getUnit().getName()).append("\n");
				sTypes.append(v.getType().getName()).append("\n");
			}
		}
		textViewSensorName.setText(sensor.getName());

		textViewSensorPrivacyLevel.setText(sensor.getSensorStateDescription());
		textViewSensorValues.setText(sValues.toString());
		textViewSensorUnits.setText(" " + sUnits.toString());
		textViewSensorTypes.setText(sTypes.toString());
	}

	@Override
	public void sensorUpdated(AbstractSensor sensor) {
		updateDisplay(sensor);
	}

}
