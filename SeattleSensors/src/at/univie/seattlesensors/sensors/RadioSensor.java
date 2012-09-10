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
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class RadioSensor extends AbstractSensor {

	private TelephonyManager telephonyManager;
	private PhoneStateListener phoneStateListener;

	private SensorValue timestamp;
	private SensorValue mccmnc;
	private SensorValue cid;
	private SensorValue lac;
	private SensorValue signalstrength;

	// private GsmCellLocation gsmCell;
	// private SignalStrength signalStrength;

	public RadioSensor(Context context) {
		super(context);
		name = "Radio Cell Info Sensor";

		timestamp = new SensorValue("ms");
		mccmnc = new SensorValue("mcc+mnc");
		lac = new SensorValue("lac");
		cid = new SensorValue("cid");
		signalstrength = new SensorValue("signal strength");
	}

	@Override
	protected void _enable() {

		telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
		GsmCellLocation gsmCell = (GsmCellLocation) telephonyManager.getCellLocation();

		timestamp.setValue(System.currentTimeMillis());
		if (gsmCell != null){
			mccmnc.setValue(telephonyManager.getNetworkOperator());
			cid.setValue(gsmCell.getCid());
			lac.setValue(gsmCell.getLac());
		}
		notifyListeners(timestamp, mccmnc, lac, cid, signalstrength);

		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				GsmCellLocation gsmCell = (GsmCellLocation) location;
				timestamp.setValue(System.currentTimeMillis());

				mccmnc.setValue(telephonyManager.getNetworkOperator());
				cid.setValue(gsmCell.getCid());
				lac.setValue(gsmCell.getLac());
				notifyListeners(timestamp, mccmnc, lac, cid, signalstrength);

			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength sStrength) {

				// TODO: values defined in TS27.007 chap 8.5, convert them!
				// 0 ‑113 dBm or less
				// 1 ‑111 dBm
				// 2...30 ‑109... ‑53 dBm
				// 31 ‑51 dBm or greater
				// 99 not known or not detectable
				// <ber>: integer type; channel bit error rate (in percent)
				// 0...7 as RXQUAL values in the table in 3GPP TS 45.008 [20]
				// subclause 8.2.4
				// 99 not known or not detectable

				signalstrength.setValue(sStrength.getGsmSignalStrength());
				notifyListeners(timestamp, mccmnc, lac, cid, signalstrength);
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
	public Object cellInformation() {
		return new Object[] { "timestamp", timestamp.getValue(), "mcc+mnc", mccmnc.getValue(), "lac", lac.getValue(), "cid", cid.getValue() };
	}
}
