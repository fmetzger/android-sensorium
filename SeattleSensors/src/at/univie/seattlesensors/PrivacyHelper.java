package at.univie.seattlesensors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import android.util.Log;
import at.univie.seattlesensors.sensors.SensorValue;

public class PrivacyHelper {

	public static enum PrivacyLevel {
		NO(4, "Full Sensor Access"), LOW(3, "Low Privacy"), MED(2, "Medium Privacy"), HIGH(1, "High Privacy"), FULL(0, "Sensor Disabled");
        
		private int value;
        private String name;

        private PrivacyLevel(int value, String name) {
                this.value = value;
                this.name = name;
        }
        
        int value(){
        	return value;
        }
        
        String getName(){
        	return name;
        }
        
        public static PrivacyLevel fromInt(int x) {
            switch(x) {
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

	public static SensorValue anonymizelocation(SensorValue val, PrivacyLevel l) {
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
	static SensorValue round(SensorValue val) {
		SensorValue ret = new SensorValue(val);
		if (val.getValue() instanceof Double) {
			double value = (Double) val.getValue();
			ret.setValue(((double) Math.round(value * 10)) / 10.0);
		}
		return ret;

	}

	static SensorValue hash(SensorValue val) {
		SensorValue ret = new SensorValue(val);
		ret.setUnit(SensorValue.UNIT.HASH);
		String sha1 = "";
		String message = (val.getValue().toString()) + val.getUnit();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(message.getBytes("UTF-8"), 0, message.length());
			byte[] sha1hash = md.digest();
		    Formatter formatter = new Formatter();
		    for (byte b : sha1hash)
		    {
		        formatter.format("%02x", b);
		    }
		    sha1 = formatter.toString();
		    formatter.close();
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

	static SensorValue salt(SensorValue val) {
		SensorValue ret = new SensorValue(val);
		String salt = Long.toString(System.currentTimeMillis()/1000000); // 1000s salt bins
		ret.setValue(val.getValue().toString()+salt);
		return ret;
	}
}
