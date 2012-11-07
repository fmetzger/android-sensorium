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

import android.app.Application;
import android.util.Log;

public class SensoriumApplication extends Application {
	public void onCreate() {
		Log.d("SettleSensors", "application starting, binding service");
		SensorServiceSingleton.getInstance().bindService(this);
//		bindService();
	}
	
	public void onDestroy(){
		Log.d("SeattleSensors", "application exiting, unbinding service");
		SensorServiceSingleton.getInstance().unbindService(this);
	}
}
