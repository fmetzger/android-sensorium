package at.univie.sensorium.sensors;

import java.util.LinkedList;
import java.util.List;

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
	private WifiManager mainWifi;
	private List<ScanResult> wifiList = null;
	
	private Handler handler = new Handler();
	
	private String APList = "";
	private SensorValue sAPList;
	
	private List<WifiDevice> scannedDevices;
	private SensorValue scannedDevicesSV;
	
	private int defaultSize = 5;
	private int scan_interval = 10; // sec	

	public WifiSensor(Context context) {
		super(context);
		name = "Wifi Scan Sensor";
		
		sAPList = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.OTHER);
		
		scannedDevicesSV = new SensorValue(SensorValue.UNIT.LIST, SensorValue.TYPE.OTHER);
		scannedDevicesSV.setValue(new LinkedList<WifiDevice>());
		
		scannedDevices = new LinkedList<WifiDevice>();
	}
	
	private Runnable scanTask = new Runnable() {
		@Override
		public void run() {			
			IntentFilter wifiFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			context.registerReceiver(wifiReceiver, wifiFilter);
			mainWifi.startScan();		        		
			//Log.d("scanTask", "restart scanning");
			
			handler.postDelayed(this, scan_interval*1000);
		}		
	};
	
	@Override
	protected void _enable() {
		mainWifi =  (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		wifiReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				APList = "";
				wifiList = mainWifi.getScanResults();
				int length = Math.min(wifiList.size(), defaultSize);
				
		        for(int i = 0; i < length; i++){
		        	ScanResult result = wifiList.get(i);
		        	String message = String.format("%s. %s \t BSSID: %s \t Signal level: %d dBm \t " +
		        			"capabilities: %s \t frequency: %.3f GHz", new Integer(i+1).toString(), 
		        			result.SSID, result.BSSID, result.level, result.capabilities, 
		        			(float)result.frequency/1000);
		        	SensorRegistry.getInstance().log("WiFi", message);
		        	APList += message + (i == (length-1) ? "" : "\n\n");
		        }
		        
		        for(int i = 0; i < wifiList.size(); i++){
		        	ScanResult result = wifiList.get(i);
		        	scannedDevices.add(new WifiDevice(result.SSID, result.BSSID, result.capabilities, 
		        			result.level, (float)result.frequency/1000));
		        }
		        scannedDevicesSV.setValue(scannedDevices);
		        sAPList.setValue(APList);
		        notifyListeners();
			}
		};			
		handler.postDelayed(scanTask, 0);
	}
	
	@Override
	protected void _disable() {
		context.unregisterReceiver(wifiReceiver);
	}
	
	public class WifiDevice{
		private SensorValue SSID = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.DEVICE_NAME);
		private SensorValue BSSID = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.MAC_ADDRESS);
		private SensorValue capabilities = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.OTHER);
		private SensorValue frequency = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.OTHER);
		private SensorValue RSSI = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.SIGNALSTRENGTH);
		
		public WifiDevice() {
		}
		
		public WifiDevice(String name, String MAC, String cap, int rssi, float freq){
			if (name != null)
				this.SSID.setValue(name);
			if (MAC != null)
				this.BSSID.setValue(MAC);
			if (cap != null)
				this.capabilities.setValue(cap);
			this.frequency.setValue(freq);
			this.RSSI.setValue(rssi);
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
			return SSID.getValueRepresentation() +"; " + BSSID.getValueRepresentation() + "; " + frequency.getValueRepresentation() + "; " + RSSI.getValueRepresentation() + "; " + capabilities.getValueRepresentation();
		}
	}
}