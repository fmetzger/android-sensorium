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

package at.univie.sensorium.sensors;

import android.content.Context;
import android.util.Log;


/**
 * This sensor does what all normal sensors shouldn't. And the application 
 * needs to survive all of this.
 *
 */
public class DummySensor extends AbstractSensor {

	public DummySensor(Context context) {
		super(context);
		this.name = "Test Sensor";
	}
	
	@Override
	protected void _enable(){
		// force an exception
		String x = null;
		System.out.print(x);
		Log.d("TEST", x);
	}

	@Override
	protected void _disable() {
		return;
	}

}
