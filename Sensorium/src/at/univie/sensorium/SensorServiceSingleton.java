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
