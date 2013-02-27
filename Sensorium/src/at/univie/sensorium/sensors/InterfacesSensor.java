package at.univie.sensorium.sensors;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import at.univie.sensorium.SensorRegistry;

public class InterfacesSensor extends AbstractSensor {

	private SensorValue ipaddresses;

	private ConnectivityManager connectivityManager;
	private IntentFilter interfaceFilter;
	private Intent interfaceIntent;

	private InterfaceAsyncTask ipaddress;

	BroadcastReceiver interfaceReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			ipaddress = new InterfaceAsyncTask();
			ipaddress.execute();
		}
	};

	public InterfacesSensor() {
		super();
		name = "Network Interfaces";
		ipaddresses = new SensorValue(SensorValue.UNIT.LIST, SensorValue.TYPE.DEVICE_IP);
		

	}

	@Override
	protected void _enable() {
		connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		interfaceFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		interfaceIntent = getContext().getApplicationContext().registerReceiver(interfaceReceiver, interfaceFilter);

		Log.d("SEATTLE DET STATE", connectivityManager.getActiveNetworkInfo().getDetailedState().toString());
		// Log.d("SEATTLE EXTRA",
		// connectivityManager.getActiveNetworkInfo().getExtraInfo());
		Log.d("SEATTLE TYPE", Integer.toString(connectivityManager.getActiveNetworkInfo().getType()));
		Log.d("SEATTLE TYPENAME", connectivityManager.getActiveNetworkInfo().getTypeName());

		ipaddress = new InterfaceAsyncTask();
		ipaddress.execute();
	}

	@Override
	protected void _disable() {
		// TODO Auto-generated method stub

	}

	private static class InterfaceAsyncTask extends AsyncTask {

		public LinkedList<String> addresses;

		@Override
		protected Object doInBackground(Object... params) {
			ConnectivityManager connMananger = (ConnectivityManager) SensorRegistry.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connMananger.getActiveNetworkInfo();
			LinkedList<String> addresses = new LinkedList<String>();
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
							// Log.d("SEATTLE IP Name",
							// inetAddress.getHostName());
							// Log.d("SEATTLE IP",
							// inetAddress.getCanonicalHostName());
							// Log.d("SEATTLE IP",
							// inetAddress.getHostAddress());
							addresses.add(inetAddress.getHostAddress());
						}
					}
				}
			} catch (SocketException ex) {
				Log.d("SEATTLE IP", ex.toString());
			}
			return null;
		}
	}

}
