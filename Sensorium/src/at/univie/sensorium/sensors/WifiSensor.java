package at.univie.sensorium.sensors;

import java.util.LinkedList;
import java.util.List;

import org.xmlrpc.android.XMLRPCSerializable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import at.univie.sensorium.SensorRegistry;

public class WifiSensor extends AbstractSensor {
	
	public static BroadcastReceiver wifiReceiver;
	public static IntentFilter wifiFilter;
	public static Intent wifiIntent;
	
	private WifiManager mainWifi;
	private List<ScanResult> wifiList = null;	
	private Handler handler = new Handler();
	
	private List<WifiDevice> scannedDevices;
	private SensorValue wifiNetworks;
	
	private int defaultSize = 5;
	private int totalSize;
	private int scan_interval = 10; // sec	

	public WifiSensor() {
		super();
		setName("Wifi Scan");
		
		wifiNetworks = new SensorValue(SensorValue.UNIT.LIST, SensorValue.TYPE.WIFI_NETWORK);
		scannedDevices = new LinkedList<WifiDevice>();
	}
	
	private Runnable scanTask = new Runnable() {
		@Override
		public void run() {
			wifiFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			wifiIntent = getContext().getApplicationContext().registerReceiver(wifiReceiver, wifiFilter);
			scannedDevices.clear();
			mainWifi.startScan();		        		
			Log.d("scanTask", "restart scanning");
			
			handler.postDelayed(this, scan_interval*1000);
		}		
	};
	
	@Override
	protected void _enable() {
		mainWifi =  (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
		
		wifiReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
//				scannedDevices.clear();

				wifiList = mainWifi.getScanResults();
				totalSize = wifiList.size();
				int length = Math.min(totalSize, defaultSize);
				
		        for(int i = 0; i < length; i++){
		        	ScanResult result = wifiList.get(i);
		        	String message = String.format("%s. %s \t BSSID: %s \t Signal level: %d dBm \t " +
		        			"capabilities: %s \t frequency: %.3f GHz", Integer.valueOf(i+1).toString(), 
		        			result.SSID, result.BSSID, result.level, result.capabilities, 
		        			(float)result.frequency/1000);
		        	SensorRegistry.getInstance().log("WiFi", message);
		        }
		        
		        for(int i = 0; i < length; i++){
		        	ScanResult result = wifiList.get(i);
		        	scannedDevices.add(new WifiDevice(i+1, result.SSID, result.BSSID, result.capabilities, 
		        			result.level, (float)result.frequency/1000));
		        }
		        wifiNetworks.setValue(scannedDevices);
		        notifyListeners();
			}
		};			
		handler.postDelayed(scanTask, 0);
	}
	
	@Override
	protected void _disable() {
		if(wifiIntent != null)
			getContext().getApplicationContext().unregisterReceiver(wifiReceiver);
		handler.removeCallbacks(scanTask);
		scannedDevices.clear();
		wifiNetworks.setValue(scannedDevices);
	}
	
	public class WifiDevice implements XMLRPCSerializable,NestedSensorValue{
		private SensorValue DeviceID = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.ID);
		private SensorValue SSID = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.SSID);
		private SensorValue BSSID = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.BSSID);
		private SensorValue capabilities = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.WIFI_CAPABILITIES);
		private SensorValue frequency = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.FREQEUENCY);
		private SensorValue RSSI = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.RSSI);
        private List<SensorValue> valuelist;
		
		public WifiDevice() {
		}
		
		public WifiDevice(int id, String name, String MAC, String cap, int rssi, float freq){
			if (name != null)
				this.SSID.setValue(name);
			if (MAC != null)
				this.BSSID.setValue(MAC);
			if (cap != null)
				this.capabilities.setValue(cap);
			this.DeviceID.setValue(id);
			this.frequency.setValue(freq);
			this.RSSI.setValue(rssi);

            valuelist = new LinkedList<SensorValue>();
            valuelist.add(SSID);
            valuelist.add(BSSID);
            valuelist.add(capabilities);
            valuelist.add(DeviceID);
            valuelist.add(frequency);
            valuelist.add(RSSI);
		}
		
		public Object getID(){
			return DeviceID.getValue();
		}
		
		public String getSSID(){
			return (String) SSID.getValue();
		}
		
		public String getBSSID(){
			return (String) BSSID.getValue();
		}
		
		public String getCapabilities(){
			return (String) capabilities.getValue();
		}
		
		public Object getFreq(){
			return frequency.getValue();
		}
		
		public Object getRSSI(){
			return RSSI.getValue();
		}
		
		@Override
		public String toString() {
			return DeviceID.getValue() + ". " + SSID.getValueRepresentation() + "; BSSID: " + 
					BSSID.getValueRepresentation() + "; freq: " + frequency.getValueRepresentation() + 
					" GHz; RSS: " + RSSI.getValueRepresentation() + " dBm; " + capabilities.getValueRepresentation()
					+ (Integer.parseInt(DeviceID.getValue().toString()) == (totalSize) ? "" : '\n');
		}

		@Override
		public Object getSerializable() {
			return toString();
		}

        @Override
        public List<SensorValue> getInnerSensorValues() {
            return valuelist;
        }
    }
}