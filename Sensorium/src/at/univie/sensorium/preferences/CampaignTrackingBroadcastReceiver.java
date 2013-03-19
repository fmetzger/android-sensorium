/*
 *  This file is part of Sensorium.
 *
 *   Sensorium is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Sensorium is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Sensorium. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

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
		if (extras != null) {
			referrer = extras.getString("referrer");
		}
		try {
			// decode it the second time after the split to avoid having
			// unencoded ampersands to split on?
			decoded = URLDecoder.decode(URLDecoder.decode(referrer, "UTF-8"), "UTF-8");
			Log.d(SensorRegistry.TAG, "referrer is: " + decoded);
			String[] kvpairs = decoded.split("&");
			for (String s : kvpairs) {
				String[] kv = s.split("=");
				if (kv.length == 2) {
					if (kv[0].equals("utm_source")) { // campaign name
						Log.d(SensorRegistry.TAG, "Experiment campaign name is " + kv[0]);
					} else if (kv[0].equals("utm_campaign")) { // campaign
																// settings url
						if (URLUtil.isValidUrl(kv[1])) {
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
	}
}
