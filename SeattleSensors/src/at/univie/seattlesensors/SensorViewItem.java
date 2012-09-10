package at.univie.seattlesensors;

import android.text.Html;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.SensorChangeListener;
import at.univie.seattlesensors.sensors.SensorValue;

public class SensorViewItem implements SensorChangeListener{
	
	private TextView textViewSensorValues;
	private TextView textViewSensorName;

	public SensorViewItem(TextView sName, TextView sValues) {
		this.textViewSensorValues = sValues;
		this.textViewSensorName = sName;
	}
	
	public TextView getTextViewSensorValues() {
		return textViewSensorValues;
	}

	public void setTextViewSensorValues(TextView textViewSensorValues) {
		this.textViewSensorValues = textViewSensorValues;
	}

	public TextView getTextViewSensorName() {
		return textViewSensorName;
	}

	public void setTextViewSensorName(TextView textViewSensorName) {
		this.textViewSensorName = textViewSensorName;
	}

	@Override
	public void sensorUpdated(SensorValue... values) {
		StringBuffer sb = new StringBuffer();
		
		for (SensorValue v: values){
//			sb.append("<p> <div align=\"left\">" +  v.getValue() + "<div align=\"right\"><b>" + v.getUnit() + "</b></div> </p>");
			sb.append("<p> <div align=\"left\">" +  v.getValue() + " <b>" + v.getUnit() + "</b> </p>");
		}
		
		textViewSensorValues.setText(Html.fromHtml(sb.toString()), TextView.BufferType.SPANNABLE);
	}

	
}
