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
import at.univie.seattlesensors.SensorRegistry;

public class RadioSensor extends AbstractSensor {
	
	private TelephonyManager telephonyManager;
	private PhoneStateListener phoneStateListener;
	
	private long timestamp = 0L;
	private GsmCellLocation gsmCell;
	private SignalStrength signalStrength;
	
	
	public RadioSensor(Context context){
		super(context);
		name = "Radio Cell Info Sensor";
		
		enable();

	}


	@Override
	public void enable() {
		
		telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

		gsmCell = (GsmCellLocation) telephonyManager
				.getCellLocation();
		timestamp = System.currentTimeMillis();
		
		SensorRegistry.getInstance().log("RADIO", "MCC+MNC: " + telephonyManager.getNetworkOperator() + "CID: " + gsmCell.getCid() + " LAC: " + gsmCell.getLac() + " PSC: " + gsmCell.getPsc());
		
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				gsmCell = (GsmCellLocation) location;
				timestamp = System.currentTimeMillis();
				
				SensorRegistry.getInstance().log("RADIO", "MCC+MNC: " + telephonyManager.getNetworkOperator() + "CID: " + gsmCell.getCid() + " LAC: " + gsmCell.getLac() + " PSC: " + gsmCell.getPsc());
			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength sStrength) {
				signalStrength = sStrength;
				SensorRegistry.getInstance().log("RADIO", "BER: " + signalStrength.getGsmBitErrorRate() + " Signal Strength: " + signalStrength.getGsmSignalStrength());
			}
		};
		
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CELL_LOCATION
						| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		
		super.enable();
	}

	@Override
	public void disable() {

		if (telephonyManager != null) {
			telephonyManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
		
		super.disable();
	}

	@XMLRPCMethod
	public Object[] cellInformation(){
		if(telephonyManager != null && gsmCell != null)
			return new Object[] {"timestamp", timestamp, "mcc+mnc", telephonyManager.getNetworkOperator(), "lac", gsmCell.getLac(), "cid", gsmCell.getCid()};
		return null;
	}
}
