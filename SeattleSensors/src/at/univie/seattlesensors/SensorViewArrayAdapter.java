package at.univie.seattlesensors;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;

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
		AbstractSensor sensor = (AbstractSensor) this.getItem(position);

		TextView sValue;
		TextView sName;
		TextView sUnit;
		TextView sType;
		
		SensorViewItem svi;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sensor_view_item, null);

			sValue = (TextView) convertView.findViewById(R.id.sensorValues);
			sName = (TextView) convertView.findViewById(R.id.sensorName);
			sUnit = (TextView) convertView.findViewById(R.id.sensorUnits);
			sType = (TextView) convertView.findViewById(R.id.sensorTypes);

			svi = new SensorViewItem(sName, sValue, sUnit, sType);
			convertView.setTag(svi);
			
		} else {
			svi = (SensorViewItem) convertView.getTag();
		}
		
		svi.updateDisplay(sensors.get(position));
		svi.attachto(sensors.get(position),sensors);

		return convertView;
	}
}