package at.univie.sensorium;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SensorBootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(prefs.getBoolean("sensor_autostart", false)){
			Log.d("SeattleSensors","boot completed receiver starting service");
			Intent myintent = new Intent(context, SensorService.class);
			context.startService(myintent);
		} else {
			Log.d("SeattleSensors","boot completed receiver not starting service, preference disabled");
		}
		

	}

}