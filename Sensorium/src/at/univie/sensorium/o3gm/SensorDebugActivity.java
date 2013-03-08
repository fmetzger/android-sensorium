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

package at.univie.sensorium.o3gm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import at.univie.sensorium.o3gm.R;

public class SensorDebugActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_debug);

		((SensoriumApplication) getApplication()).bindService();

		TextView t = (TextView) findViewById(R.id.sensoroutput);

		SensorRegistry sensorregistry = SensorRegistry.getInstance();
		sensorregistry.setDebugView(t);
	}
}
