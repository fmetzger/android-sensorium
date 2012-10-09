package at.univie.seattlesensors.sensors;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Provides information tied to the device/model, i.e. vendor and model name, TAC, ...
 * @author fm
 *
 */
public class DeviceInfoSensor extends AbstractSensor {
	
	private SensorValue tac;
	private SensorValue vendorname;
	private SensorValue modelname;
	
	public DeviceInfoSensor(Context context) {
		super(context);

		name = "Device Info Sensor";
		tac = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.TAC);
		modelname = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.MODEL_NAME);
		vendorname = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.VENDOR_NAME);
	}

	@Override
	protected void _enable() {
		TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
		String imei = telephonyManager.getDeviceId();
		if (imei != null)
				tac.setValue(imei.substring(0, 6));
		
		vendorname.setValue(Build.MANUFACTURER);
		modelname.setValue(Build.MODEL);
	}

	@Override
	protected void _disable() {
	}
}
