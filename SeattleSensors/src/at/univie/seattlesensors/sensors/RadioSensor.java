/*
 *  * Copyright (C) 2012 Florian Metzger
 * 
 *  This file is part of android-seattle-sensors.
 *
 *   android-seattle-sensors is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   android-seattle-sensors is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with android-seattle-sensors. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package at.univie.seattlesensors.sensors;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import at.univie.seattlesensors.PrivacyHelper;

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
	
	private SensorValue roaming;
	private SensorValue servicestate;
	private SensorValue operator;


	
	public RadioSensor(Context context) {
		super(context);
		name = "Radio Cell Info Sensor";

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
		
	}

	@Override
	protected void _enable() {

		telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
		GsmCellLocation gsmCell = (GsmCellLocation) telephonyManager.getCellLocation();

		timestamp.setValue(System.currentTimeMillis());
		if (gsmCell != null) {
			String mccmnc = telephonyManager.getNetworkOperator();
			mcc.setValue(mccmnc.substring(0, 3));
			mnc.setValue(mccmnc.substring(3));
			cid.setValue(gsmCell.getCid());
			lac.setValue(gsmCell.getLac());
		}
//		notifyListeners(timestamp, mcc, mnc, lac, cid, signalstrength);
		notifyListeners();

		phoneStateListener = new PhoneStateListener() {
			
			@Override
			public void onServiceStateChanged(ServiceState serviceState) {
				// TODO Auto-generated method stub
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
				notifyListeners();
			}
			
			
			@Override
			public void onCellLocationChanged(CellLocation location) {
				GsmCellLocation gsmCell = (GsmCellLocation) location;
				timestamp.setValue(System.currentTimeMillis());

				String mccmnc = telephonyManager.getNetworkOperator();
				Log.d("STRING", "mccmnc is \"" + mccmnc+"\"");
				if (mccmnc != null && !mccmnc.equals("")){
					Log.d("STRING", mccmnc);
					mcc.setValue(mccmnc.substring(0, 3));
					mnc.setValue(mccmnc.substring(3));
					cid.setValue(gsmCell.getCid());
					lac.setValue(gsmCell.getLac());
					networktype.setValue(decodenetworktype(telephonyManager.getNetworkType()));
				} else {
					mcc.setValue("n/a");
					mnc.setValue("n/a");
					cid.setValue("n/a");
					lac.setValue("n/a");
					networktype.setValue("n/a");
				}

//				notifyListeners(timestamp, PrivacyHelper.anonymize(mcc, getPrivacylevel()), PrivacyHelper.anonymize(mnc, getPrivacylevel()), PrivacyHelper.anonymize(lac, getPrivacylevel()), PrivacyHelper.anonymize(cid, getPrivacylevel()), PrivacyHelper.anonymize(networktype, getPrivacylevel()), PrivacyHelper.anonymize(signalstrength, getPrivacylevel()));
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
						Log.d("SeattleSensors", "unexpected GSM signal strength value");
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
//				notifyListeners(timestamp, PrivacyHelper.anonymize(mcc, getPrivacylevel()), PrivacyHelper.anonymize(mnc, getPrivacylevel()), PrivacyHelper.anonymize(lac, getPrivacylevel()), PrivacyHelper.anonymize(cid, getPrivacylevel()), PrivacyHelper.anonymize(networktype, getPrivacylevel()), PrivacyHelper.anonymize(signalstrength, getPrivacylevel()));
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

	@XMLRPCMethod
	public Object mcc() {
		return mcc.getValue();
	}

	@XMLRPCMethod
	public Object mnc() {
		return mnc.getValue();
	}

	@XMLRPCMethod
	public Object lac() {
		return lac.getValue();
	}

	@XMLRPCMethod
	public Object cid() {
		return cid.getValue();
	}

	@XMLRPCMethod
	public Object signalstrength() {
		return signalstrength.getValue();
	}

	@XMLRPCMethod
	public Object timestamp() {
		return timestamp.getValue();
	}
	
	@XMLRPCMethod
	public Object networktype() {
		return networktype.getValue();
	}
}
