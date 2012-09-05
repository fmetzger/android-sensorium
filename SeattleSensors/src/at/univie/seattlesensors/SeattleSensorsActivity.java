/*
 *  * Copyright (C) 2012 Florian Metzger
 * 
 *  This file is part of android-seattle-sensors.
 *
 *   android-seattle-sensors is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   android-seattle-sensors is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with android-seattle-sensors. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package at.univie.seattlesensors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SeattleSensorsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seattle_sensors_main);
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

	public void startDebugView(View view) {
		startActivityForResult(new Intent(view.getContext(),
				SensorDebugActivity.class), 0);
	}

//	 public void startSensorsView(View view) {
//	 startActivityForResult(new Intent(view.getContext(),
//			 SensorDebugActivity.class), 0);
//	 }

	public void startSensorConfigView(View view) {
		startActivityForResult(new Intent(view.getContext(),
				SensorConfigActivity.class), 0);
	}
	
//	public void startSensorConfigView(View view) {
//		startActivityForResult(new Intent(view.getContext(),
//				SensorPreferenceActivity.class), 0);
//	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
        		startActivityForResult(new Intent(this,
        				SensorPreferenceActivity.class), 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
