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

package at.univie.sensorium.privacy;

import at.univie.sensorium.sensors.SensorValue;

public class LocationPrivacy extends Privacy {
	
	protected static SensorValue anonymizeValue(SensorValue val, PrivacyLevel l) {
		switch (l) {
		case NO:
			return val;
		case LOW:
			return round(val);
		case MED:
			return hash(round(val));
		case HIGH:
			return hash(salt(round(val)));
		case FULL:
		default:
			val.setValue("n/a");
			return val;
		}
	}
	
	/*
	 * 
	 * 1° longitude between 0km (pole 90°) and 111.3km (equator 0°) europe/us ~
	 * 45° \approx 79km 1° latitude \approx 111km across the globe
	 * 
	 * -> round both to the first decimal place i.e. 11km long, 8km lat bins
	 */
	private static SensorValue round(SensorValue val) {
		SensorValue ret = new SensorValue(val);
		if (val.getValue() instanceof Double) {
			double value = (Double) val.getValue();
			ret.setValue(((double) Math.round(value * 10)) / 10.0);
		}
		return ret;

	}

}
