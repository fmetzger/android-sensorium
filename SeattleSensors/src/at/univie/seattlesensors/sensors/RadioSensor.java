package at.univie.seattlesensors.sensors;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import at.univie.seattlesensors.SensorRegistry;

public class RadioSensor extends AbstractSensor {
	
	private TelephonyManager telephonyManager;
	private PhoneStateListener phoneStateListener;
	
	private long timestamp = 0L;
	private GsmCellLocation gsmCell;
	private SignalStrength signalStrength;
	
	
	public RadioSensor(Context context){
		super(context);
		
		telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
		
		SensorRegistry.getInstance().debugOut("RadioSensor starting");
		
		gsmCell = (GsmCellLocation) telephonyManager
				.getCellLocation();
		timestamp = System.currentTimeMillis();
		
		SensorRegistry.getInstance().debugOut("MCC+MNC: " + telephonyManager.getNetworkOperator() + "CID: " + gsmCell.getCid() + " LAC: " + gsmCell.getLac() + " PSC: " + gsmCell.getPsc());
		
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				gsmCell = (GsmCellLocation) location;
				timestamp = System.currentTimeMillis();
				
				SensorRegistry.getInstance().debugOut("MCC+MNC: " + telephonyManager.getNetworkOperator() + "CID: " + gsmCell.getCid() + " LAC: " + gsmCell.getLac() + " PSC: " + gsmCell.getPsc());
			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength sStrength) {
				signalStrength = sStrength;
				SensorRegistry.getInstance().debugOut("BER: " + signalStrength.getGsmBitErrorRate() + " Signal Strength: " + signalStrength.getGsmSignalStrength());
			}
		};
		
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CELL_LOCATION
						| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	

	@Override
	public void disable() {

		if (telephonyManager != null) {
			telephonyManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}

	}

	/**
	 * Hardcoded method list, should be dynamic in the future
	 */
	@Override
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

	/**
	 * Hardcoded switch'd method calling, should use introspection in the future
	 */
	@Override
	public Object[] callMethod(String methodname) {
		if(methodname.equals("cellInformation")){
			return new Object[] {"timestamp", timestamp, "mcc+mnc", telephonyManager.getNetworkOperator(), "lac", gsmCell.getLac(), "cid", gsmCell.getCid()};
		}
		return new Object[]{};
	}
}
