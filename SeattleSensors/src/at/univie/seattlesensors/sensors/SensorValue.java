package at.univie.seattlesensors.sensors;

public class SensorValue {

	private Object value;
	private UNIT unit;
	private TYPE type;

	public static enum UNIT {
		DEGREE("°"), MILLISECONDS("ms"), METER("m"), HASH(""), STRING(""), 
		OTHER(""), NUMBER(""), RELATIVE("%"), VOLTAGE("V"), TEMPERATURE("°C"),
		STATE("state"), NAME("name"), LIST("list"), DBM("dBm"), ASU("asu");

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
		CID("cell id"), SIGNALSTRENGTH("radio signal strength"),
		NETWORKTYPE("network type"),
		DEVICE_NAME("device name"), MAC_ADDRESS("MAC address");

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
