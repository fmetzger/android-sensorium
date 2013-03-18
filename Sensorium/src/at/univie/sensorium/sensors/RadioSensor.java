/*
 *  This file is part of Sensorium.
 *
 *   Sensorium is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Sensorium is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Sensorium. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package at.univie.sensorium.sensors;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import at.univie.sensorium.SensorRegistry;

public class RadioSensor extends AbstractSensor {

	private TelephonyManager telephonyManager;
	private PhoneStateListener phoneStateListener;

	private SensorValue timestamp;
	private SensorValue mcc;
	private SensorValue mnc;
	private SensorValue cid;
	private SensorValue lac;
	private SensorValue networktype;
	private SensorValue signalstrength;
	
	private SensorValue subscriberid;
	
	private SensorValue roaming;
	private SensorValue servicestate;
	private SensorValue operator;


	
	public RadioSensor() {
		super();
		name = "Radio Cell Information";

		timestamp = new SensorValue(SensorValue.UNIT.MILLISECONDS, SensorValue.TYPE.TIMESTAMP);
		mcc = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.MCC);
		mnc = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.MNC);
		lac = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.LAC);
		cid = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.CID);
		networktype = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.NETWORKTYPE);
		signalstrength = new SensorValue(SensorValue.UNIT.DBM, SensorValue.TYPE.SIGNALSTRENGTH);
		
		roaming = new SensorValue(SensorValue.UNIT.OTHER, SensorValue.TYPE.ROAMING);
		servicestate = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.SERVICESTATE);
		operator = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.OPERATOR);
		
		subscriberid = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.SUBSCRIBER_ID);
		
	}

	@Override
	protected void _enable() {

		telephonyManager = ((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE));
		GsmCellLocation gsmCell = (GsmCellLocation) telephonyManager.getCellLocation();

		timestamp.setValue(System.currentTimeMillis());
		if (gsmCell != null) {
			String mccmnc = telephonyManager.getNetworkOperator();
			mcc.setValue(mccmnc.substring(0, 3));
			mnc.setValue(mccmnc.substring(3));
			cid.setValue(gsmCell.getCid());
			lac.setValue(gsmCell.getLac());
		}

		phoneStateListener = new PhoneStateListener() {
			
			@Override
			public void onServiceStateChanged(ServiceState serviceState) {
				super.onServiceStateChanged(serviceState);
				
				String state = "";
				switch(serviceState.getState()){
				case ServiceState.STATE_EMERGENCY_ONLY: 
					state = "emergency calls only";
					break;
				case ServiceState.STATE_IN_SERVICE:
					state = "in service";
					break;
				case ServiceState.STATE_OUT_OF_SERVICE:
					state = "no service";
					break;
				case ServiceState.STATE_POWER_OFF:
					state = "disabled";
					break;
				}
				servicestate.setValue(state);
				operator.setValue(serviceState.getOperatorAlphaLong());
				roaming.setValue(serviceState.getRoaming());
				timestamp.setValue(System.currentTimeMillis());
				subscriberid.setValue(telephonyManager.getSubscriberId());
				notifyListeners();
			}
			
			
			@Override
			public void onCellLocationChanged(CellLocation location) {
				GsmCellLocation gsmCell = (GsmCellLocation) location;
				timestamp.setValue(System.currentTimeMillis());

				String mccmnc = telephonyManager.getNetworkOperator();
				if ((mccmnc == null || mccmnc.equals("")) || !servicestate.getValue().equals("in service")){
					mcc.setValue("n/a");
					mnc.setValue("n/a");
					cid.setValue("n/a");
					lac.setValue("n/a");
					signalstrength.setValue("n/a");
					networktype.setValue("n/a");
					
				} else {
					mcc.setValue(mccmnc.substring(0, 3));
					mnc.setValue(mccmnc.substring(3));
					cid.setValue(gsmCell.getCid());
					lac.setValue(gsmCell.getLac());
					networktype.setValue(decodenetworktype(telephonyManager.getNetworkType()));
				}

				notifyListeners();

			}

			private String decodenetworktype(int networkType) {
				switch (networkType) {
				case (TelephonyManager.NETWORK_TYPE_CDMA):
					return "CDMA";
				case (TelephonyManager.NETWORK_TYPE_EDGE):
					return "EDGE";
				case (TelephonyManager.NETWORK_TYPE_GPRS):
					return "GPRS";
				case (TelephonyManager.NETWORK_TYPE_HSPA):
					return "HSPA";
				case (TelephonyManager.NETWORK_TYPE_HSDPA):
					return "HSDPA";
				case (TelephonyManager.NETWORK_TYPE_HSPAP):
					return "HSPA+";
				case (TelephonyManager.NETWORK_TYPE_HSUPA):
					return "HSUPA";
				case (TelephonyManager.NETWORK_TYPE_LTE):
					return "LTE";
				case (TelephonyManager.NETWORK_TYPE_UMTS):
					return "UMTS";
				case (TelephonyManager.NETWORK_TYPE_EVDO_0):
				case (TelephonyManager.NETWORK_TYPE_EVDO_A):
				case (TelephonyManager.NETWORK_TYPE_EVDO_B):
					return "EVDO";
				case (TelephonyManager.NETWORK_TYPE_UNKNOWN):
				default:
					return "unknown";
				}
			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength sStrength) {
				// In GSM networks, ASU is equal to the RSSI
				// (received signal strength indicator, see TS 27.007).
				// dBm = 2 × ASU - 113, ASU in the range of 0..31 and 99
				// In UMTS networks, ASU is equal the RSCP level
				// (received signal code power, see TS 25.125)
				// dBm = ASU - 116, ASU in the range of -5..91

				int asu = sStrength.getGsmSignalStrength();
				
				if(networktype.getValue().equals("GPRS") || networktype.getValue().equals("EDGE")){
					signalstrength.setUnit(SensorValue.UNIT.DBM);
					
					if (asu < 31) {
						signalstrength.setValue(-113 + (asu * 2));
					} else if (asu == 31) {
						signalstrength.setValue(">=51");
					} else if (asu == 99) {
						signalstrength.setValue("not detectable");
					} else {
						Log.d(SensorRegistry.TAG, "unexpected GSM signal strength value");
					}
				} else if (networktype.getValue().equals("UMTS") || networktype.getValue().equals("HSPA")
						|| networktype.getValue().equals("HSPA+") || networktype.getValue().equals("HSUPA")
						|| networktype.getValue().equals("HSDPA")){
					signalstrength.setUnit(SensorValue.UNIT.DBM);
					if (asu >= -5 && asu <= 91){
						signalstrength.setValue(asu-116);
					}
				} else {
					signalstrength.setUnit(SensorValue.UNIT.ASU);
					signalstrength.setValue(asu);
				}

				timestamp.setValue(System.currentTimeMillis());
				
				GsmCellLocation gsmCell = (GsmCellLocation) telephonyManager.getCellLocation();
				if (gsmCell != null) {
					String mccmnc = telephonyManager.getNetworkOperator();
					mcc.setValue(mccmnc.substring(0, 3));
					mnc.setValue(mccmnc.substring(3));
					cid.setValue(gsmCell.getCid());
					lac.setValue(gsmCell.getLac());
				}
				
				
				notifyListeners();
			}
		};

		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_SERVICE_STATE);
	}

	@Override
	protected void _disable() {

		if (telephonyManager != null) {
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}
}
