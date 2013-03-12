package at.univie.sensorium.preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import at.univie.sensorium.o3gm.SensorRegistry;

public class CampaignTrackingBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		   String referrer = "";
		   Bundle extras = intent.getExtras();
		   if(extras != null){
		      referrer = extras.getString("referrer");
		   }
		   Log.d(SensorRegistry.TAG,"Referer is: "+referrer);

	}

}
