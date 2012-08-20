package at.univie.seattlesensors;

import java.util.LinkedList;
import java.util.List;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class RadioSensor implements Sensor {
	
	private TelephonyManager telephonyManager;
	private PhoneStateListener phoneStateListener;
//	private String cellStr;
//	private String cellLatestStr;
	
	private String timestamp = "";
	private String mccmnc = "";
	private String lac = "";
	private String cid = "";
	
	private String psc = "";
	private String biterrorrate = "";
	private String signalstrength = "";
	
	
	public RadioSensor(TelephonyManager telephonyManager){
		
		SensorRegistry.getInstance().debugOut("RadioSensor starting");
		
		
		this.telephonyManager = telephonyManager; 
		
		GsmCellLocation celloc = (GsmCellLocation) telephonyManager
				.getCellLocation();
		lac = Integer.toString(celloc.getLac());
		cid = Integer.toString(celloc.getCid());
		mccmnc = telephonyManager.getNetworkOperator();
		timestamp = Long.toString(System.currentTimeMillis()/1000);
		
		SensorRegistry.getInstance().debugOut("CID: " + cid + " LAC: " + lac + " PSC: " + psc);
		
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				GsmCellLocation myLoc = (GsmCellLocation) location;
				
				lac = Integer.toString(myLoc.getLac());
				cid = Integer.toString(myLoc.getCid());
				psc = Integer.toString(myLoc.getPsc());
				timestamp = Long.toString(System.currentTimeMillis()/1000);
				
				SensorRegistry.getInstance().debugOut("CID: " + cid + " LAC: " + lac + " PSC: " + psc);
			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				biterrorrate = Integer.toString(signalStrength.getGsmBitErrorRate());
				signalstrength = Integer.toString(signalStrength.getGsmSignalStrength());
				
				SensorRegistry.getInstance().debugOut("BER: " + biterrorrate + " Signal Strength: " + signalstrength);
			}
		};
		
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CELL_LOCATION
						| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	

	public void disable() {

		if (telephonyManager != null) {
			telephonyManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}

	}


	@Override
	/**
	 * Hardcoded method list, should be dynamic in the future
	 */
	public List<String> getMethods() {
		List<String> methods = new LinkedList<String>();
		methods.add("cellInformation");
		return methods;
	}

	
	@Override
	public boolean hasMethod(String methodname) {
		if(methodname.equals("cellInformation")){
			return true;
		}
		return false;
	}
	
	
	@Override
	public Object[] methodSignature(String methodname) {
		if(methodname.equals("cellInformation")){
			return new Object[]{"array", "nil"};
		}
		
		return new Object[]{};
	}

	@Override
	/**
	 * Hardcoded switch'd method calling, should use introspection in the future
	 */
	public Object[] callMethod(String methodname) {
		Log.d("RadioSensor", "OUTER timestamp" + timestamp + "mcc+mnc" + mccmnc + "lac" + lac + "cid" + cid);
		if(methodname.equals("cellInformation")){
			Log.d("RadioSensor", "timestamp" + timestamp + "mcc+mnc" + mccmnc + "lac" + lac + "cid" + cid);
			return new Object[] {"timestamp", timestamp, "mcc+mnc", mccmnc, "lac", lac, "cid", cid};
		}
		return new Object[]{};
	}












	
	
	
//	public void createCellInfo() {
//	cellStr = cellStr.concat("\n Device Id: "+ telephonyManager.getDeviceId());
//	cellStr = cellStr.concat("\n Device SW Ver: "+ telephonyManager.getDeviceSoftwareVersion());
//	cellStr = cellStr.concat("\n Line 1 Num: "+ telephonyManager.getLine1Number());
//	cellStr = cellStr.concat("\n Net Country: "+ telephonyManager.getNetworkCountryIso());
//	cell_mccmnc = telephonyManager.getNetworkOperator();
//	cellStr = cellStr.concat("\n Net Operator: "+ cell_mccmnc);
//	cellStr = cellStr.concat("\n Net Operator: "+ telephonyManager.getNetworkOperatorName());
//	cellStr = cellStr.concat("\n Sim Country: "+ telephonyManager.getSimCountryIso());
//	cellStr = cellStr.concat("\n Sim Operator: "+ telephonyManager.getSimOperator());
//	cellStr = cellStr.concat("\n Sim Operator: "+ telephonyManager.getSimOperatorName());
//	cellStr = cellStr.concat("\n Sim Serial: "+ telephonyManager.getSimSerialNumber());
//	cellStr = cellStr.concat("\n Subscriber Id: "+ telephonyManager.getSubscriberId());
//	cellStr = cellStr.concat("\n Call State: "+ Integer.toString(telephonyManager.getCallState()));
//
//	GsmCellLocation celloc = (GsmCellLocation) telephonyManager
//			.getCellLocation();
//	cell_lac = Integer.toString(celloc.getLac());
//	cell_cid = Integer.toString(celloc.getCid());
//	cellLatestStr = "CID: " + cell_cid + " LAC: " + cell_lac + " PSC: " + celloc.getPsc();
//	cellStr = cellStr.concat("\n Cell Location: " + cellLatestStr);
//	cellStr = cellStr.concat("\n Network Type: "+ telephonyManager.getNetworkType());
//	
// 	cell_timestamp = Long.toString(System.currentTimeMillis()/1000);
//	List<NeighboringCellInfo> neighbors = telephonyManager
//			.getNeighboringCellInfo();
//	for (NeighboringCellInfo n : neighbors) {
//		cellStr = cellStr.concat("\n Neighbor Cell: " + "CID: "
//				+ n.getCid() + " LAC: " + n.getLac() + " Type: "
//				+ n.getNetworkType() + " PSC: " + n.getPsc() + "RSSI: "
//				+ n.getRssi());
//	}
//
//
//
//}

}
