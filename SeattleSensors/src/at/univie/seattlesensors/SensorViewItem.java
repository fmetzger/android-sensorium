package at.univie.seattlesensors;

import java.util.List;

import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;
import at.univie.seattlesensors.sensors.SensorChangeListener;
import at.univie.seattlesensors.sensors.SensorValue;

public class SensorViewItem implements SensorChangeListener{

	private TextView textViewSensorValues;
	private TextView textViewSensorUnits;
	private TextView textViewSensorTypes;
	private TextView textViewSensorName;

	public SensorViewItem(TextView sName, TextView sValues, TextView sUnits, TextView sTypes) {

		this.textViewSensorValues = sValues;
		this.textViewSensorUnits = sUnits;
		this.textViewSensorTypes = sTypes;
		this.textViewSensorName = sName;
		
		
	
//		updateDisplay();
	}
	
	public void attachto(AbstractSensor sensor,List<AbstractSensor>sensors){
		for(AbstractSensor s: sensors)
			s.removeListener(this);
		sensor.addListener(this);
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
	
	public void updateDisplay(AbstractSensor sensor){
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
		this.textViewSensorName.setText(sensor.getName());
		textViewSensorValues.setText(sValues.toString());
		textViewSensorUnits.setText(sUnits.toString());
		textViewSensorTypes.setText(sTypes.toString());
	}

	@Override
	public void sensorUpdated(AbstractSensor sensor) {
		updateDisplay(sensor);
	}

	
}
