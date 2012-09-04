package at.univie.seattlesensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SensorBootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SeattleSensors","boot completed receiver starting service");
		Intent myintent = new Intent(context, SensorService.class);
		context.startService(myintent);
	}

}