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
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class SIMSensor extends AbstractSensor {

	private TelephonyManager telephonyManager;

	private SensorValue mcc;
	private SensorValue mnc;
	private SensorValue operator;
	private SensorValue simserial;
	private SensorValue simstate;
	private SensorValue simcountry;

	public SIMSensor() {
		super();
		setName("SIM Information");

		mcc = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.MCC);
		mnc = new SensorValue(SensorValue.UNIT.NUMBER, SensorValue.TYPE.MNC);
		operator = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.OPERATOR);
		simserial = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.SIM_SERIAL);
		simstate = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.SIM_STATE);
		simcountry = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.SIM_COUNTRY);

	}

	@Override
	protected void _enable() {
		telephonyManager = ((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE));
		GsmCellLocation gsmCell = (GsmCellLocation) telephonyManager.getCellLocation();

		if (gsmCell != null) {
			String mccmnc = telephonyManager.getSimOperator();
			mcc.setValue(mccmnc.substring(0, 3));
			mnc.setValue(mccmnc.substring(3));
			operator.setValue(telephonyManager.getSimOperatorName());
			simserial.setValue(telephonyManager.getSimSerialNumber());
			simcountry.setValue(telephonyManager.getSimCountryIso());

			switch (telephonyManager.getSimState()) {
			case (TelephonyManager.SIM_STATE_ABSENT):
				simstate.setValue("SIM_STATE_ABSENT");
				break;
			case (TelephonyManager.SIM_STATE_NETWORK_LOCKED):
				simstate.setValue("SIM_STATE_NETWORK_LOCKED");
				break;
			case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
				simstate.setValue("SIM_STATE_PIN_REQUIRED");
				break;
			case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
				simstate.setValue("SIM_STATE_PUK_REQUIRED");
				break;
			case (TelephonyManager.SIM_STATE_READY):
				simstate.setValue("SIM_STATE_READY");
				break;
			case (TelephonyManager.SIM_STATE_UNKNOWN):
				simstate.setValue("SIM_STATE_UNKNOWN");
				break;
			}
		}
	}

	@Override
	protected void _disable() {
	}
}
