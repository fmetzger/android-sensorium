package at.univie.sensorium.preferences;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import at.univie.sensorium.SensorRegistry;

public class CampaignTrackingBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		   String referrer = "";
		   String decoded = "";
		   Bundle extras = intent.getExtras();
		   if(extras != null){
		      referrer = extras.getString("referrer");
		   }
		   try {
			decoded = URLDecoder.decode(URLDecoder.decode(referrer, "UTF-8"), "UTF-8"); // decode it the second time after the split to avoid having unencoded ampersands to split on?
			String [] kvpairs = decoded.split("&");
			for (String s: kvpairs){
				String [] kv = s.split("=");
				if (kv.length == 2){
					if(kv[0].equals("utm_source")){ // campaign name
						Log.d(SensorRegistry.TAG, "Experiment campaign name is " + kv[0]);
					} else if (kv[0].equals("utm_campaign")){ // campaign settings url
						if(URLUtil.isValidUrl(kv[1])){
							Log.d(SensorRegistry.TAG, "Loading experiment preferences from " + kv[1]);
							SensorRegistry.getInstance().getPreferences().loadCampaignPreferences(kv[1]);
						}
					}
				}
					
			}
		} catch (UnsupportedEncodingException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d(SensorRegistry.TAG, sw.toString());
		}
		   Log.d(SensorRegistry.TAG,"referrer is: "+decoded);
	}

}
