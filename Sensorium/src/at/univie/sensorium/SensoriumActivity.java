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

package at.univie.sensorium;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.univie.sensorium.preferences.Preferences;
import at.univie.sensorium.preferences.SensorPreferenceActivity;
import at.univie.sensorium.sensors.AbstractSensor;

public class SensoriumActivity extends Activity {

	private ArrayAdapter<AbstractSensor> listAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seattle_sensors_main);
		//SensorServiceSingleton.getInstance().bindService(this);

		ListView sensorViewList = (ListView) findViewById(R.id.sensorValues);
		List<AbstractSensor> sensors = SensorRegistry.getInstance().getSensors();
		listAdapter = new SensorViewArrayAdapter(this, sensors);
		sensorViewList.setAdapter(listAdapter);
		
		
//		showNagScreen();
//        new Handler().postDelayed(new Runnable() {
//
//            public void run() { 
//                showNagScreen();
//            }
//
//        }, 5000);// 3 Seconds
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
//		Preferences prefs = SensorRegistry.getInstance().getPreferences();
//		if (prefs == null)
//			showNagScreen();
//		else if (prefs.getBoolean(Preferences.WELCOME_SCREEN_SHOWN, false) == false && prefs.newerPrefsAvailable())
//			showNagScreen();
//		
		new Handler().postDelayed(new Runnable() {
          public void run() {
//        	  showNagScreen();
        	  Preferences prefs = SensorRegistry.getInstance().getPreferences();
        	  if (prefs.getBoolean(Preferences.WELCOME_SCREEN_SHOWN, false) == false && prefs.newerPrefsAvailable())
        		  showNagScreen();
          }
		}, 2000);
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public void onPause() {
		super.onPause();
	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivityForResult(new Intent(this, SensorPreferenceActivity.class), 0);
			return true;
		case R.id.menu_debug:
			startActivityForResult(new Intent(this, SensorDebugActivity.class), 0);
			return true;
		case R.id.menu_about:
			AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("About Sensorium");
			alertDialog.setMessage(Html.fromHtml("Sensorium shows you sensor information available on your device. It can also provide this data on external interfaces while attempting to preserve your privacy. You should take a look at the preferences to configure this to your needs. Visit <a href='https://github.com/fmetzger/android-sensorium'>https://github.com/fmetzger/android-sensorium</a> for further information. Have fun!"));
			alertDialog.show();
			((TextView)alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	private void showNagScreen(){
		
		// only show the dialog if auto upload is actually enabled (even if we are on the first run)
		final Preferences prefs = SensorRegistry.getInstance().getPreferences();

		
	    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	    alertDialog.setTitle("Sensorium Data Upload");
	    alertDialog.setIcon(R.drawable.ic_launcher);
	    alertDialog.setMessage(Html.fromHtml("Sensorium is configured to automatically upload some of the gathered data to the <a href='https://o3gm.cs.univie.ac.at/o3gm/'>Open3GMap</a> server at the University of Vienna by default. If you do not want this, please disable it now."));

	    alertDialog.setPositiveButton("Leave Enabled", new DialogInterface.OnClickListener() {

	    	@Override
	        public void onClick(DialogInterface dialog, int which) {
	    		Preferences prefs = SensorRegistry.getInstance().getPreferences();
	    		prefs.putBoolean(Preferences.WELCOME_SCREEN_SHOWN, true);
	        }
	     });
	    
	    alertDialog.setNegativeButton("Disable", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Preferences prefs = SensorRegistry.getInstance().getPreferences();
				prefs.putBoolean(Preferences.UPLOAD_AUTOMATIC_PREF, false);
				prefs.putBoolean(Preferences.WELCOME_SCREEN_SHOWN, true);
			}
		});

	    AlertDialog d = alertDialog.create();
	    d.show();
	    ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}
}
