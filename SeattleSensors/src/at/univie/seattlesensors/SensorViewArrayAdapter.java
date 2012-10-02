package at.univie.seattlesensors;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;

public class SensorViewArrayAdapter  extends ArrayAdapter<AbstractSensor> {

	private LayoutInflater inflater;

	public SensorViewArrayAdapter(Context context, List<AbstractSensor> sensorList) {
		super(context, R.layout.sensor_view_item, R.id.sensorValue,
				sensorList);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AbstractSensor sensor = (AbstractSensor) this.getItem(position);

		TextView sValue;
		TextView sName;
		TextView sUnit;
		TextView sType;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sensor_view_item, null);

			sValue = (TextView) convertView
					.findViewById(R.id.sensorValues);
			sName = (TextView) convertView
					.findViewById(R.id.sensorName);
			sUnit = (TextView) convertView
					.findViewById(R.id.sensorUnits);
			sType = (TextView) convertView
					.findViewById(R.id.sensorTypes);
		
			SensorViewItem svi = new SensorViewItem(sensor, sName, sValue, sUnit, sType);
			convertView.setTag(svi);

		}
		return convertView;
	}

}