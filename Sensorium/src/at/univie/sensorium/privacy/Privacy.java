package at.univie.sensorium.privacy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;
import android.util.Log;
import at.univie.sensorium.sensors.SensorValue;

public class Privacy {

	public static enum PrivacyLevel {
		NO(4, "Full Sensor Access"), LOW(3, "Low Privacy"), MED(2, "Medium Privacy"), HIGH(1, "High Privacy"), FULL(0, "Sensor Disabled");

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
			case 4:
				return NO;
			case 3:
				return LOW;
			case 2:
				return MED;
			case 1:
				return HIGH;
			case 0:
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
		switch (val.getType()) {
		case LATITUDE:
		case LONGITUDE:
			return LocationPrivacy.anonymizeValue(val, l);

		case CID:
		case LAC:
		case MCC:
		case MNC:
		case NETWORKTYPE:
			return anonymizeValue(val, l);
		case SIGNALSTRENGTH:
			return anonymizesignalstrength(val, l);

		default:
			Log.d("SeattleSensors", "No known privacy methods for type " + val.getType().getName());
			return val;
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
			Log.d("SeattleSensors", e.toString());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		} catch (UnsupportedEncodingException e) {
			Log.d("SeattleSensors", e.toString());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		}

		ret.setValue(sha1);
		return ret;
	}

	protected static SensorValue salt(SensorValue val) {
		SensorValue ret = new SensorValue(val);
		String salt = Long.toString(System.currentTimeMillis() / 1000000); // 1000s
																			// salt
																			// bins
		ret.setValue(val.getValue().toString() + salt);
		return ret;
	}
}
