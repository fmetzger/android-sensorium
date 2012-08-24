package at.univie.seattlesensors;

import android.widget.TextView;

public class SensorViewItem {

	public SensorViewItem(TextView sValue, TextView sUnit) {
		this.textViewSensorValue = sValue;
		this.textViewSensorUnit = sUnit;
	}
	
	private TextView textViewSensorValue;
	public TextView getTextViewSensorValue() {
		return textViewSensorValue;
	}

	public void setTextViewSensorValue(TextView textViewSensorValue) {
		this.textViewSensorValue = textViewSensorValue;
	}

	public TextView getTextViewSensorUnit() {
		return textViewSensorUnit;
	}

	public void setTextViewSensorUnit(TextView textViewSensorUnit) {
		this.textViewSensorUnit = textViewSensorUnit;
	}

	private TextView textViewSensorUnit;
}
