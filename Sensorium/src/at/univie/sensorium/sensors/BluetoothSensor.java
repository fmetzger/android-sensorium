package at.univie.sensorium.sensors;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import at.univie.sensorium.SensorRegistry;

public class BluetoothSensor extends AbstractSensor {
	
	public static BroadcastReceiver bluetoothReceiver;
	public static Intent bluetoothIntent;
	
	private Handler handler = new Handler();
	private BluetoothAdapter bluetoothAdapter;

	private SensorValue localDeviceName;
	private SensorValue localMAC;
	
	private List<BtDevice> bondedDevices;
	private SensorValue sBondedDevices;
	
	private List<BtDevice> scannedDevices;
	private SensorValue sScannedDevices;
	
	private String bluetooth = "";
	private SensorValue sBluetooth;
	
	private String devices = "";
	private SensorValue sDevices;
	
	
	
	private int scan_interval = 10; // sec

	public BluetoothSensor(Context context) {
		super(context);
		name = "Bluetooth Sensor";
		
		localDeviceName = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.DEVICE_NAME);
		localMAC = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.MAC_ADDRESS);
		bondedDevices = new LinkedList<BtDevice>();
		scannedDevices = new LinkedList<BtDevice>();
		sBondedDevices = new SensorValue(SensorValue.UNIT.LIST, SensorValue.TYPE.BONDED_DEV);
		sScannedDevices = new SensorValue(SensorValue.UNIT.LIST, SensorValue.TYPE.SCANNED_DEV);
		sDevices = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.DEVICE_NAME);
		sBluetooth = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.OTHER);
	}
	
	private Runnable scanTask = new Runnable() {
		@Override
		public void run() {			
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			bluetoothIntent = context.getApplicationContext().registerReceiver(bluetoothReceiver, filter);
			bluetoothAdapter.startDiscovery();	        		
			Log.d("scanTask", "restart bluetooth scanning");
			
			handler.postDelayed(this, scan_interval*1000);
		}		
	};
	
	@Override
	protected void _enable() {
		
		bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
		bluetooth = "Local device\n\tName: " + bluetoothAdapter.getName() 
				+ "\n\tAddress: " + bluetoothAdapter.getAddress();
		
		localDeviceName.setValue(bluetoothAdapter.getName()); 
		localMAC.setValue(bluetoothAdapter.getAddress());		
		
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		bluetooth += "\nBonded devices: ";
		String bonded = "";
		if (pairedDevices.size() > 0) {
		    for (BluetoothDevice device : pairedDevices) {
		    	bluetooth += device.getName() + "\t" + device.getAddress();
		    	bondedDevices.add(new BtDevice(device.getName(), device.getAddress()));
		    	bonded += device.getName() + "\t/" + device.getAddress() + "\n";
		    }
		}
		else {
			bluetooth += "None\n";
			bonded += "None";
		}
		sBluetooth.setValue(bluetooth);
		sBondedDevices.setValue(bonded);
		
		if (bluetoothAdapter.isEnabled()){ // only when bluetooth is enabled can we discover devices			
			bluetoothReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {					
					String action = intent.getAction();
					if (BluetoothDevice.ACTION_FOUND.equals(action)) {
						BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						String name = device.getName();
						if (name != null && devices.contains(name) == false) {
							short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
							devices += name + "\t/" + device.getAddress() 
									+ "\tRSSI: " + String.valueOf(rssi) + " dBm\n";
							scannedDevices.add(new BtDevice(name, device.getAddress(), rssi));
						}
					}
					else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
						context.unregisterReceiver(this);
						SensorRegistry.getInstance().log("Bluetooth", bluetooth + devices);
						Log.d("Bluetooth FINISHED", "done");
						
						notifyListeners();
						devices = "";
						scannedDevices.clear();						
//						bluetoothAdapter.startDiscovery();
					}
					
					sDevices.setValue(devices);
					sScannedDevices.setValue(scannedDevices);
				}
			};	
			handler.postDelayed(scanTask, 0);
		}
		else // if not enabled, we only get info for local device
			SensorRegistry.getInstance().log("Bluetooth", bluetooth);
	}
	
	@Override
	protected void _disable() {
		if(bluetoothIntent != null)
			context.getApplicationContext().unregisterReceiver(bluetoothReceiver);
	}
	
	public class BtDevice{
		private SensorValue devName = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.DEVICE_NAME);
		private SensorValue MACAddr = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.MAC_ADDRESS);
		private SensorValue RSSI = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.SIGNALSTRENGTH);
		
		public BtDevice() {
		}
		
		public BtDevice(String devName, String MAC){
			if (devName != null)
				this.devName.setValue(devName);
			if (MAC != null)
				this.MACAddr.setValue(MAC);
			
			this.RSSI.setValue(9999); // impossible value for RSSI
		}
		
		public BtDevice(String devName, String MAC, int rssi){
			if (devName != null)
				this.devName.setValue(devName);
			if (MAC != null)
				this.MACAddr.setValue(MAC);
			this.RSSI.setValue(9999);
		}
		
		public String getDevName(){
			return (String) devName.getValue();
		}
		
		public String getMAC(){
			return (String) MACAddr.getValue();
		}
		
		public Object getRSSI(){
			return RSSI.getValue();
		}
		
		@Override
		public String toString() {
			return devName.getValueRepresentation() + "; " + MACAddr.getValueRepresentation() + "; " + RSSI.getValueRepresentation();
		}
	}
}