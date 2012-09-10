package at.univie.seattlesensors;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sensor_view_item, null);

			sValue = (TextView) convertView
					.findViewById(R.id.sensorValues);
			sName = (TextView) convertView
					.findViewById(R.id.sensorName);
			
			sName.setText(sensor.getName());

			SensorViewItem svi = new SensorViewItem(sName, sValue);
			sensor.addListener(svi);
			convertView.setTag(svi);


		} else {
			SensorViewItem viewItem = (SensorViewItem) convertView
					.getTag();
			sValue = viewItem.getTextViewSensorValues();
			sName = viewItem.getTextViewSensorName();
		}

		// fill sensors values here
		//textView.setText(sensor.getName());

		return convertView;
	}

}