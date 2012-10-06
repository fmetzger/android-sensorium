package at.univie.seattlesensors.sensors;

public class SensorValue {

	private Object value;
	private UNIT unit;
	private TYPE type;

	public static enum UNIT {
		DEGREE("°"), MILLISECONDS("ms"), METER("m"), HASH(" "), STRING(" "), 
		OTHER(" "), NUMBER(" "), RELATIVE("%"), VOLTAGE("V"), TEMPERATURE("°C"),METERSPERSECOND("m/s"),
		STATE("state"), NAME("name"), LIST("list"), DBM("dBm"), MBPS("Mbps"), ASU("asu");

		private String name;

		private UNIT(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static enum TYPE {
		LATITUDE("latitude"), LONGITUDE("longitude"), TIMESTAMP("timestamp"),
		ALTITUDE("altitude"), RELATIVE_DISTANCE("distance"), ACCURACY("accuracy"),
		CHARGE("charged"), OTHER(""), BATTERY_TECHNOLOGY(""), PLUGGED("power source"),
		MCCMNC("country code + network code"), LAC("location area code"),
		MCC("mobile country code"), MNC("mobile network code"),
		CID("cell id"), SIGNALSTRENGTH("received signal strength"),SATELLITES("satellites"),
		NETWORKTYPE("network type"),TAC("TAC"),MODEL_NAME("model"),VENDOR_NAME("vendor"),
		DEVICE_NAME("device name"), MAC_ADDRESS("MAC address"),BEARING("bearing"),VELOCITY("speed"),
		WIFI_CONNECTION("Wifi connection"), SSID("SSID"), SSID_HIDDEN("SSID hidden"),
		BSSID("BSSID"), DEVICE_IP("device IP"), STATE("Supplicant State"), SPEED("link speed"),
		ROAMING("roaming"),SERVICESTATE("radio state"),OPERATOR("operator"),
		BONDED_DEV("bonded device(s)"), SCANNED_DEV("scanned device(s)");

		private String name;

		private TYPE(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public SensorValue(Object value, UNIT unit, TYPE type) {
		this.value = value;
		this.unit = unit;
	}

	public SensorValue(SensorValue copy) {
		this.value = copy.getValue();
		this.unit = copy.getUnit();
		this.type = copy.getType();
	}

	public SensorValue(UNIT unit, TYPE type) {
		this.value = "n/a";
		this.unit = unit;
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public UNIT getUnit() {
		return unit;
	}

	public void setUnit(UNIT unit) {
		this.unit = unit;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
}
