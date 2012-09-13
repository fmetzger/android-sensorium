package at.univie.seattlesensors.sensors;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import at.univie.seattlesensors.SensorRegistry;

public class BluetoothSensor extends AbstractSensor {
	
	private BluetoothAdapter bluetoothAdapter;
	private BroadcastReceiver bluetoothReceiver;
	private Intent bluetoothIntent;
	String bluetooth = "";
	String devices = "";

	private SensorValue localDeviceName;
	private SensorValue localMAC;
	private List<Device> bondedDevices;
	private List<Device> scannedDevices;

	public BluetoothSensor(Context context) {
		super(context);
		name = "Bluetooth Sensor";
		
		localDeviceName = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.DEVICE_NAME);
		localMAC = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.MAC_ADDRESS);
		bondedDevices = new LinkedList<Device>();
		scannedDevices = new LinkedList<Device>();
	}
	
	@Override
	protected void _enable() {
		
		bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
		bluetooth = "Local device\n\tName: " + bluetoothAdapter.getName() 
				+ "\n\tAddress: " + bluetoothAdapter.getAddress();
		
		localDeviceName.setValue(bluetoothAdapter.getName()); 
		localMAC.setValue(bluetoothAdapter.getAddress());		
		notifyListeners(localDeviceName, localMAC);
		
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		bluetooth += "\nBonded devices: ";
		if (pairedDevices.size() > 0) {
		    for (BluetoothDevice device : pairedDevices) {
		    	bluetooth += device.getName() + "\t" + device.getAddress();
		    	bondedDevices.add(new Device(device.getName(), device.getAddress()));
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
						if (name != null && devices.contains(name) == false) {
							short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
							devices += "\nDiscovered: " + name + "\tMAC address: " + device.getAddress() 
									+ "\tRSSI: " + String.valueOf(rssi);
							//Log.d("Bluetooth FOUND", devices + "\n");
							scannedDevices.add(new Device(name, device.getAddress(), rssi));
						}
					}
					else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
						SensorRegistry.getInstance().log("Bluetooth", bluetooth + devices);
						Log.d("Bluetooth FINISHED", "done");
						devices = "";
						scannedDevices.clear();
						bluetoothAdapter.startDiscovery();
					}
				}
			};	

			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			bluetoothIntent = context.registerReceiver(bluetoothReceiver, filter);
		}
		else // if not enabled, we only get info for local device
			SensorRegistry.getInstance().log("Bluetooth", bluetooth);
	}
	
	@Override
	protected void _disable() {
		if(bluetoothIntent != null)
			context.unregisterReceiver(bluetoothReceiver);
	}
	
	@XMLRPCMethod
	public String getLocalDeviceName() {
		return (String) localDeviceName.getValue();
	}
	
	@XMLRPCMethod
	public String getLocalMAC() {
		return (String) localMAC.getValue();
	}
	
	@XMLRPCMethod
	public List<Device> getBondedDev(){
		return bondedDevices;
	}
	
	@XMLRPCMethod
	public List<Device> getScannedDev(){
		if (scannedDevices.isEmpty()) return null;
		else return scannedDevices;
	}
	
	@XMLRPCMethod
	public Object[] bluetoothInformation() {
		if (devices != "")
			return new Object[] {"Bluetooth", bluetooth, "Discovered", devices};
		return null;
	}
	
	public class Device{
		private SensorValue devName = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.DEVICE_NAME);
		private SensorValue MACAddr = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.MAC_ADDRESS);
		private SensorValue RSSI = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.SIGNALSTRENGTH);
		
		public Device() {
		}
		
		public Device(String devName, String MAC){
			if (devName != null)
				this.devName.setValue(devName);
			if (MAC != null)
				this.MACAddr.setValue(MAC);
			
			this.RSSI.setValue(9999); // impossible value for RSSI
		}
		
		public Device(String devName, String MAC, int rssi){
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
	}
}