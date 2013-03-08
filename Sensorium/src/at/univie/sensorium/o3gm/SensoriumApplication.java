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

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class SensoriumApplication extends Application {
	private SensorService sensorService;
	private boolean mIsBound = false;

	public void onCreate() {
		Log.d("SettleSensors", "application starting, binding service");
		bindService();
	}

	public void onDestroy() {
		Log.d(SensorRegistry.TAG, "application exiting, unbinding service");
		unbindService();
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			sensorService = ((SensorService.LocalBinder) service).getService();
			Log.d(SensorRegistry.TAG, "service connected");
		}

		public void onServiceDisconnected(ComponentName className) {
			sensorService = null;
			Log.d(SensorRegistry.TAG, "service disconnected");
		}
	};

	public void bindService() {
		if (!mIsBound) {
			Log.d("SettleSensors", "application starting, binding service");
			bindService(new Intent(this, SensorService.class), mConnection, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}
	}

	public void unbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}
}
