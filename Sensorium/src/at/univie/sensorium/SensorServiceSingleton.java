
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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class SensorServiceSingleton {
	
	private static SensorServiceSingleton instance = null;
	
	private SensorService sensorService;
	private boolean mIsBound = false;
	
	protected SensorServiceSingleton() {
	}

	public static SensorServiceSingleton getInstance() {
		if (instance == null) {
			instance = new SensorServiceSingleton();
		}
		return instance;
	}
	

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			sensorService = ((SensorService.LocalBinder) service).getService();
			Log.d("SeattleSensors", "service connected");
		}

		public void onServiceDisconnected(ComponentName className) {
			sensorService = null;
			Log.d("SeattleSensors", "service disconnected");
		}
	};

	public void bindService(Context context) {
		if(!mIsBound){
			Log.d("SettleSensors", "application starting, binding service");
			context.bindService(new Intent(context, SensorService.class), mConnection,
					Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}
	}

	public void unbindService(Context context) {
		if (mIsBound) {
			context.unbindService(mConnection);
			mIsBound = false;
		}
	}

}
