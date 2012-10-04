package at.univie.seattlesensors;

import java.util.List;

import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;
import at.univie.seattlesensors.sensors.SensorChangeListener;
import at.univie.seattlesensors.sensors.SensorValue;

public class SensorViewItem implements SensorChangeListener {

	private TextView textViewSensorValues;
	private TextView textViewSensorUnits;
	private TextView textViewSensorTypes;
	private TextView textViewSensorName;
	private TextView textViewSensorPrivacyLevel;

	public SensorViewItem(TextView sName, TextView sPrivacyLevel, TextView sValues, TextView sUnits, TextView sTypes) {

		this.textViewSensorValues = sValues;
		this.textViewSensorUnits = sUnits;
		this.textViewSensorTypes = sTypes;
		this.textViewSensorName = sName;
		this.textViewSensorPrivacyLevel = sPrivacyLevel;
	}

	public void attachto(AbstractSensor sensor, List<AbstractSensor> sensors) {
		for (AbstractSensor s : sensors)
			s.removeListener(this);
		sensor.addListener(this);
	}
	public void updateDisplay(AbstractSensor sensor) {
		StringBuffer sValues = new StringBuffer();
		StringBuffer sUnits = new StringBuffer();
		StringBuffer sTypes = new StringBuffer();

		List<SensorValue> values = sensor.getSensorValues();

		for (SensorValue v : values) {
			sValues.append(v.getValue() + "\n");
			sUnits.append(v.getUnit().getName() + "\n");
			sTypes.append(v.getType().getName() + "\n");
		}
		textViewSensorName.setText(sensor.getName());
		textViewSensorPrivacyLevel.setText(sensor.privacyLevel());
		textViewSensorValues.setText(sValues.toString());
		textViewSensorUnits.setText(sUnits.toString());
		textViewSensorTypes.setText(sTypes.toString());
	}

	@Override
	public void sensorUpdated(AbstractSensor sensor) {
		updateDisplay(sensor);
	}

}
