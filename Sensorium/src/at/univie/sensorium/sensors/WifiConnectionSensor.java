package at.univie.sensorium.sensors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import at.univie.sensorium.SensorRegistry;

public class WifiConnectionSensor extends AbstractSensor {
	private WifiManager mainWifi;
	private SensorValue ssid;
	private SensorValue ssid_hidden;
	private SensorValue bssid;
	private SensorValue ip;
	private SensorValue mac;
	private SensorValue supplicant_state;
	private SensorValue rssi;
	private SensorValue speed;
	private Handler handler = new Handler();
	//private SensorValue sConnectedAP;
	String AP = "";  // the AP that is connected to
	private int scan_interval = 10; // sec

	public WifiConnectionSensor(){
		super();
		setName("Wifi Connections");
		
		ssid = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.SSID);
		ssid_hidden = new SensorValue(SensorValue.UNIT.OTHER, SensorValue.TYPE.SSID_HIDDEN);
		bssid = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.BSSID);
		ip = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.DEVICE_IP);
		mac = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.MAC_ADDRESS);
		supplicant_state = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.STATE);
		rssi = new SensorValue(SensorValue.UNIT.DBM, SensorValue.TYPE.SIGNALSTRENGTH);
		speed = new SensorValue(SensorValue.UNIT.MBPS,SensorValue.TYPE.SPEED);
		//sConnectedAP = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.WIFI_CONNECTION);
	}
	
	private Runnable scanTask = new Runnable() {
		@Override
		public void run() {			
			mainWifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = mainWifi.getConnectionInfo();  // the network that Android is connected to
			ssid.setValue(info.getSSID());
			ssid_hidden.setValue(info.getHiddenSSID());
			bssid.setValue(info.getBSSID());
			
			int addr = info.getIpAddress();
		    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
		    	addr = Integer.reverseBytes(addr);
		    }
		    byte[] ipByteArray = BigInteger.valueOf(addr).toByteArray();
		    
		    try {
		        ip.setValue(InetAddress.getByAddress(ipByteArray).getHostAddress());
		    } catch (UnknownHostException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d(SensorRegistry.TAG, sw.toString());
		    }
			mac.setValue(info.getMacAddress());
			supplicant_state.setValue(info.getSupplicantState());
			rssi.setValue(info.getRssi());
			speed.setValue(info.getLinkSpeed());
			
			
			
//			List<WifiConfiguration> configs = mainWifi.getConfiguredNetworks();
//			for (WifiConfiguration config : configs) {
//				AP += "\n" + config.toString();
//			}
//			SensorRegistry.getInstance().log("WiFi", AP);
//			sConnectedAP.setValue(AP);
			notifyListeners();
			handler.postDelayed(this, scan_interval*1000);
		}		
	};

	@Override
	protected void _enable() {
		handler.postDelayed(scanTask, 0);
	}

	@Override
	protected void _disable() {
		handler.removeCallbacks(scanTask);
        //notifyListeners();
	}
	
//	private String getSupplicantStateString(SupplicantState s){
//		
//	}
}
