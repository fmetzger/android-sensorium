package at.univie.seattlesensors;

import java.util.List;

import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;
import at.univie.seattlesensors.sensors.SensorChangeListener;
import at.univie.seattlesensors.sensors.SensorValue;

public class SensorViewItem implements SensorChangeListener{
	
	private AbstractSensor sensor;
	
	private TextView textViewSensorValues;
	private TextView textViewSensorUnits;
	private TextView textViewSensorTypes;
	private TextView textViewSensorName;

	public SensorViewItem(AbstractSensor sensor, TextView sName, TextView sValues, TextView sUnits, TextView sTypes) {
		this.sensor = sensor;
		sensor.addListener(this);
		
		this.textViewSensorValues = sValues;
		this.textViewSensorUnits = sUnits;
		this.textViewSensorTypes = sTypes;
		this.textViewSensorName = sName;
		
		this.textViewSensorName.setText(this.sensor.getName());
		
		updateDisplay();
	}
	
	public TextView getTextViewSensorValues() {
		return textViewSensorValues;
	}
	
	public TextView getTextViewSensorUnits() {
		return textViewSensorUnits;
	}
	
	public TextView getTextViewSensorTypes() {
		return textViewSensorTypes;
	}

	public TextView getTextViewSensorName() {
		return textViewSensorName;
	}
	
	private void updateDisplay(){
		StringBuffer sValues = new StringBuffer();
		StringBuffer sUnits = new StringBuffer();
		StringBuffer sTypes = new StringBuffer();
		
//		for (SensorValue v: values){
////			sb.append("<p> <div align=\"left\">" +  v.getValue() + "<div align=\"right\"><b>" + v.getUnit() + "</b></div> </p>");
//			sb.append("<p> <div align=\"left\">" +  v.getValue() + " <b>" + v.getUnit().getName() + "</b> </p>");
//		}
		
		List<SensorValue> values = sensor.getSensorValues();
		
		for (SensorValue v: values){
			sValues.append(v.getValue()+"\n");
			sUnits.append(v.getUnit().getName()+"\n");
			sTypes.append(v.getType().getName()+"\n");
		}
		
//		textViewSensorValuesc.setText(Html.fromHtml(sb.toString()), TextView.BufferType.SPANNABLE);
		textViewSensorValues.setText(sValues.toString());
		textViewSensorUnits.setText(sUnits.toString());
		textViewSensorTypes.setText(sTypes.toString());
	}

	@Override
	public void sensorUpdated() {
		updateDisplay();
	}

	
}
