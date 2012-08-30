package at.univie.seattlesensors;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import at.univie.seattlesensors.sensors.AbstractSensor;

public class SensorConfigActivity extends Activity {
	
	private ArrayAdapter<AbstractSensor> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_config);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        
		ListView sensorConfigList = (ListView) findViewById(R.id.sensorConfigListView);

		sensorConfigList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View item,
							int position, long id) {
						AbstractSensor sensor = listAdapter.getItem(position);
						if(sensor.available()){
							sensor.toggle();
							SensorConfigurationItem configItem = (SensorConfigurationItem) item
									.getTag();
							configItem.getCheckBox().setChecked(sensor.isEnabled());
						}
					}
				});
		
		
		List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();
		listAdapter = new SensorConfigurationArrayAdapter(this, sensors);
		sensorConfigList.setAdapter(listAdapter);	
        
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_sensor_config, menu);
//        return true;
//    }
//
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
