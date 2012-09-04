package at.univie.seattlesensors;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class SeattleSensorsApplication extends Application {
	private SensorService sensorService;
	private boolean mIsBound;

	public void onCreate() {
		Log.d("SettleSensors", "application starting, binding service");
		bindService();
	}
	
	public void onDestroy(){
		Log.d("SeattleSensors", "application exiting, unbinding service");
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

	private void bindService() {
		bindService(new Intent(this, SensorService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	private void unbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

}
