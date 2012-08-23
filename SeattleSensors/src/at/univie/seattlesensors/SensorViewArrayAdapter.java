package at.univie.seattlesensors;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import at.univie.seattlesensors.sensors.AbstractSensor;

public class SensorViewArrayAdapter  extends ArrayAdapter<AbstractSensor> {

	private LayoutInflater inflater;

	public SensorViewArrayAdapter(Context context, List<AbstractSensor> sensorList) {
		super(context, R.layout.sensor_config_item, R.id.sensorConfigText,
				sensorList);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AbstractSensor sensor = (AbstractSensor) this.getItem(position);

		CheckBox checkBox;
		TextView textView;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sensor_config_item, null);

			textView = (TextView) convertView
					.findViewById(R.id.sensorConfigText);
			checkBox = (CheckBox) convertView
					.findViewById(R.id.sensorConfigToggle);


			convertView.setTag(new SensorConfigurationItem(textView, checkBox));

			checkBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					AbstractSensor sensor = (AbstractSensor) cb.getTag();
					sensor.setState(cb.isChecked());
				}
			});
		} else {
			SensorConfigurationItem configItem = (SensorConfigurationItem) convertView
					.getTag();
			checkBox = configItem.getCheckBox();
			textView = configItem.getTextView();
		}

		checkBox.setTag(sensor);

		checkBox.setChecked(sensor.isEnabled());
		textView.setText(sensor.getName());

		return convertView;
	}

}