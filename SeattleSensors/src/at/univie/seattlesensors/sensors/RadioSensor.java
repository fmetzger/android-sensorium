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

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class RadioSensor extends AbstractSensor {

	private TelephonyManager telephonyManager;
	private PhoneStateListener phoneStateListener;

	private SensorValue timestamp;
	private SensorValue mcc;
	private SensorValue mnc;
	private SensorValue cid;
	private SensorValue lac;
	private SensorValue signalstrength;


	public RadioSensor(Context context) {
		super(context);
		name = "Radio Cell Info Sensor";

		timestamp = new SensorValue(SensorValue.UNIT.MILLISECONDS, SensorValue.TYPE.TIMESTAMP);
		mcc = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.MCC);
		mnc = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.MNC);
		lac = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.LAC);
		cid = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.CID);
		signalstrength = new SensorValue(SensorValue.UNIT.DBM, SensorValue.TYPE.SIGNALSTRENGTH);
	}

	@Override
	protected void _enable() {

		telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
		GsmCellLocation gsmCell = (GsmCellLocation) telephonyManager.getCellLocation();

		timestamp.setValue(System.currentTimeMillis());
		if (gsmCell != null){
			String mccmnc = telephonyManager.getNetworkOperator();
			mcc.setValue(mccmnc.substring(0, 3));
			mnc.setValue(mccmnc.substring(3));
			cid.setValue(gsmCell.getCid());
			lac.setValue(gsmCell.getLac());
		}
		notifyListeners(timestamp, mcc, mnc, lac, cid, signalstrength);

		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				GsmCellLocation gsmCell = (GsmCellLocation) location;
				timestamp.setValue(System.currentTimeMillis());

				String mccmnc = telephonyManager.getNetworkOperator();
				mcc.setValue(mccmnc.substring(0, 3));
				mnc.setValue(mccmnc.substring(3));
				cid.setValue(gsmCell.getCid());
				lac.setValue(gsmCell.getLac());
				notifyListeners(timestamp, mcc, mnc, lac, cid, signalstrength);

			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength sStrength) {
				// values defined in TS27.007 chap 8.5, convert them!
				// 0 ‑113 dBm or less
				// 1 ‑111 dBm
				// 2...30 ‑109... ‑53 dBm
				// 31 ‑51 dBm or greater
				// 99 not known or not detectable
				// <ber>: integer type; channel bit error rate (in percent)
				// 0...7 as RXQUAL values in the table in 3GPP TS 45.008 [20]
				// subclause 8.2.4
				// 99 not known or not detectable

				int asu = sStrength.getGsmSignalStrength();
				if (asu < 31){
					signalstrength.setValue(-113+(asu*2));
				} else if(asu == 30){
					signalstrength.setValue(">=51");
				} else if(asu == 99){
					signalstrength.setValue("not detectable");
				} else {
					Log.d("SeattleSensors", "unexpected GSM signal strength value");
				}
				notifyListeners(timestamp, mcc, mnc, lac, cid, signalstrength);
			}
		};

		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
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
		return mnc.getValue();
	}
	
	@XMLRPCMethod
	public Object timestamp() {
		return timestamp.getValue();
	}
}
