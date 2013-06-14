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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import android.util.Base64;
import android.util.Log;
import at.univie.sensorium.SensorRegistry;
import at.univie.sensorium.preferences.Preferences;
import at.univie.sensorium.sensors.SensorValue;

public class Privacy {

	public static enum PrivacyLevel {
		NO(0, "Full Sensor Access"), LOW(1, "Low Privacy"), MED(2, "Medium Privacy"), HIGH(3, "High Privacy"), FULL(4, "Sensor not visible");

		private int value;
		private String name;

		private PrivacyLevel(int value, String name) {
			this.value = value;
			this.name = name;
		}

		public int value() {
			return value;
		}

		@Override
		public String toString() {
			return name;
		}

		public static PrivacyLevel fromInt(int x) {
			switch (x) {
			case 0:
				return NO;
			case 1:
				return LOW;
			case 2:
				return MED;
			case 3:
				return HIGH;
			case 4:
				return FULL;
			}
			return null;
		}
	}

	protected static SensorValue anonymizeValue(SensorValue val, PrivacyLevel l) {
		switch (l) {
		case NO:
			return val;
		case LOW:
		case MED:
			return hash(val);
		case HIGH:
			return hash(salt(val));
		case FULL:
		default:
			val.setValue("n/a");
			return val;
		}
	}

	protected static SensorValue anonymizestrict(SensorValue val, PrivacyLevel l) {
		val.setValue("n/a");
		return val;
	}

	protected static SensorValue anonymizesignalstrength(SensorValue val, PrivacyLevel l) {
		switch (l) {
		case NO:
			return val;
		case LOW:
			SensorValue ret = new SensorValue(val);
			if (val.getValue() instanceof Integer && (Integer) val.getValue() >= -70) {
				ret.setValue("high");
			} else {
				ret.setValue("low");
			}
			return ret;
		case MED:
			return hash(val);
		case HIGH:
			return hash(salt(val));
		case FULL:
		default:
			val.setValue("n/a");
			return val;
		}
	}

	public static SensorValue anonymize(SensorValue val, PrivacyLevel l) {
		SensorValue retval = new SensorValue(val);
		switch (retval.getType()) {
		case LATITUDE:
		case LONGITUDE:
			return LocationPrivacy.anonymizeValue(retval, l);

		case ADDRESS:
			return LocationPrivacy.anonymizeAddress(retval, l);

		case CID:
		case LAC:
		case MCC:
		case MNC:
		case NETWORKTYPE:
			return anonymizeValue(retval, l);
		case SIGNALSTRENGTH:
			return anonymizesignalstrength(retval, l);
		case SIM_SERIAL:
		case SUBSCRIBER_ID:
			return anonymizestrict(retval, l);

		default:
			// Log.d(SensorRegistry.TAG, "No known privacy methods for type " +
			// val.getType().getName());
			return retval;
		}
	}

	protected static SensorValue hash(SensorValue val) {
		SensorValue ret = new SensorValue(val);
		ret.setUnit(SensorValue.UNIT.HASH);
		String sha1 = "";
		String message = (val.getValue().toString()) + val.getUnit() + val.getType();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(message.getBytes("UTF-8"), 0, message.length());
			byte[] sha1hash = md.digest();
			sha1 = Base64.encodeToString(sha1hash, Base64.NO_WRAP | Base64.NO_PADDING);
		} catch (NoSuchAlgorithmException e) {
			Log.d(SensorRegistry.TAG, e.toString());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d(SensorRegistry.TAG, sw.toString());
		} catch (UnsupportedEncodingException e) {
			Log.d(SensorRegistry.TAG, e.toString());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d(SensorRegistry.TAG, sw.toString());
		}

		ret.setValue(sha1);
		return ret;
	}

	protected static SensorValue salt(SensorValue val) {
		// load stored seed or generate a new one
		String salt = SensorRegistry.getInstance().getPreferences().getString(Preferences.PRIVACY_HASH, "");
		if (salt.equals("")) {
			SecureRandom random = new SecureRandom();
			salt = (new BigInteger(130, random)).toString(32);
			SensorRegistry.getInstance().getPreferences().putString(Preferences.PRIVACY_HASH, salt);
		}
		Log.d("Sensorium", "Salt is " + salt);

		SensorValue ret = new SensorValue(val);
		ret.setValue(salt + val.getValue().toString());
		return ret;
	}
}
