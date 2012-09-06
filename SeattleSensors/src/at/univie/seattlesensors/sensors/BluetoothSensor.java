package at.univie.seattlesensors.sensors;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;
import at.univie.seattlesensors.SensorRegistry;

public class BluetoothSensor extends AbstractSensor {
	
	BluetoothAdapter bluetoothAdapter;
	BroadcastReceiver bluetoothReceiver;
	String bluetooth = "";
	String devices = "";

	public BluetoothSensor(Context context) {
		super(context);
		name = "Bluetooth Sensor";
	}
	
	@Override
	protected void _enable() {
		
		bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
		bluetooth = "Local device\n\tName: " + bluetoothAdapter.getName() + "\n\tAddress: " 
				+ bluetoothAdapter.getAddress();
		
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		bluetooth += "\nBonded devices: ";
		if (pairedDevices.size() > 0) {
		    for (BluetoothDevice device : pairedDevices) {
		    	bluetooth += device.getName() + "\t" + device.getAddress();
		    }
		}
		else bluetooth += "None\n";
		
		if (bluetoothAdapter.isEnabled()){ // only when bluetooth is enabled can we discover devices
			bluetoothAdapter.startDiscovery();			
			bluetoothReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {					
					String action = intent.getAction();
					if (BluetoothDevice.ACTION_FOUND.equals(action)) {
						BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						String name = device.getName();
						if (devices.contains(name) == false) {
							short rssi_min = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
							devices += "\nDiscovered: " + name + "\tMAC address: " + device.getAddress() 
									+ "\tRSSI: " + String.valueOf(rssi_min);
							Log.d("Bluetooth FOUND", devices);		            	
						}
					}
					else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
						SensorRegistry.getInstance().log("Bluetooth", bluetooth + devices);
						Log.d("Bluetooth FINISHED", "done");
						devices = "";
						bluetoothAdapter.startDiscovery();
					}
				}
			};	

			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			context.registerReceiver(bluetoothReceiver, filter);
		}
		else // if not enabled, we only get info for local device
			SensorRegistry.getInstance().log("Bluetooth", bluetooth);
	}
	
	@Override
	protected void _disable() {
		context.unregisterReceiver(bluetoothReceiver);
	}
	
	@XMLRPCMethod
	public Object[] wifiInformation() {
		if (devices != "")
			return new Object[] {"Bluetooth", bluetooth, "Discovered", devices};
		return null;
	}
}