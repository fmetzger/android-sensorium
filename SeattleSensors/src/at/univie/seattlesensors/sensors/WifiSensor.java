package at.univie.seattlesensors.sensors;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import at.univie.seattlesensors.SensorRegistry;

public class WifiSensor extends AbstractSensor {
	WifiManager mainWifi;
	BroadcastReceiver wifiReceiver;
	List<ScanResult> wifiList = null;
	String APList = "";   // all scan results put together in a String
	String AP = "";  // the AP that is connected to

	public WifiSensor(Context context) {
		super(context);
		name = "Wifi Sensor";
	}
	
	@Override
	public void enable() {
		super.enable();
		mainWifi =  (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		wifiReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				APList = "";
				wifiList = mainWifi.getScanResults();
		        for(int i = 0; i < wifiList.size(); i++){
		        	ScanResult result = wifiList.get(i);
		        	String message = String.format("%s. SSID: %s \t MAC address: %s \t Signal level: %d dBm \t capabilities: %s \t frequency: %.3f GHz", 
		        			new Integer(i+1).toString(), result.SSID, result.BSSID, result.level, result.capabilities, (float)result.frequency/1000);
		        	SensorRegistry.getInstance().log("WiFi", message);
		        	APList += message + "\n\n";
		        }
		        WifiInfo info = mainWifi.getConnectionInfo();  // the network that Android is connected to
				AP += "\n\nConnected AP Status: \n" + info.toString();
				List<WifiConfiguration> configs = mainWifi.getConfiguredNetworks();
				for (WifiConfiguration config : configs) {
					AP += "\n\n" + config.toString();
				}
				SensorRegistry.getInstance().log("WiFi", AP);
			}
		};			
		IntentFilter wifiFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		context.registerReceiver(wifiReceiver, wifiFilter);
		mainWifi.startScan();
		        
	}
	
	@Override
	public void disable() {
		super.disable();
		context.unregisterReceiver(wifiReceiver);
	}
	
	@XMLRPCMethod
	public Object[] wifiInformation() {
		if (wifiList != null)
			return new Object[] {"APList", APList};
		return null;
	}
	
}