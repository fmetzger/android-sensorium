package at.univie.sensorium.privacy;

import at.univie.sensorium.privacy.Privacy.PrivacyLevel;
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
