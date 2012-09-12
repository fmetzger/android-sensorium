package at.univie.seattlesensors.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatterySensor extends AbstractSensor {
	
	private BroadcastReceiver batteryReceiver;
	private Intent batteryIntent;
	
	private SensorValue timestamp;
	private SensorValue level;
	private SensorValue temperature;
	private SensorValue voltage;
	private SensorValue technology;
	private SensorValue plugged;
	
	

	public BatterySensor(Context context) {
		super(context);
		name = "Battery Sensor";
		
		timestamp = new SensorValue(SensorValue.UNIT.MILLISECONDS, SensorValue.TYPE.TIMESTAMP);
		level = new SensorValue(SensorValue.UNIT.RELATIVE, SensorValue.TYPE.CHARGE);
		temperature = new SensorValue(SensorValue.UNIT.TEMPERATURE, SensorValue.TYPE.OTHER);
		voltage = new SensorValue(SensorValue.UNIT.VOLTAGE, SensorValue.TYPE.OTHER);
		technology = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.BATTERY_TECHNOLOGY);
		plugged = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.PLUGGED);
	}

	@Override
	protected void _enable() {
        batteryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                
            	timestamp.setValue(System.currentTimeMillis());
            	int plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            	
                if (plug == BatteryManager.BATTERY_PLUGGED_AC) {
                    plugged.setValue("AC");
                } else if (plug == BatteryManager.BATTERY_PLUGGED_USB) {
                    plugged.setValue("USB");
                } else if (plug == 0) {
                    plugged.setValue("unplugged");
                } else {
                    plugged.setValue("n/a");
                }
                
                technology.setValue(intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY));
                Float f = Float.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0));
                temperature.setValue(f/10);
                f = Float.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0));
                voltage.setValue(f/1000);
                
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (rawlevel >= 0 && scale > 0) {
                    level.setValue((rawlevel * 100) / scale);
                } else {
                	level.setValue(rawlevel);
                }
                notifyListeners(timestamp, level, voltage, plugged, temperature, technology);
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryIntent = context.registerReceiver(batteryReceiver, batteryLevelFilter);
        Log.d("BATT", batteryIntent.toString());
	}

	@Override
	protected void _disable() {
		if(batteryIntent != null)
			context.unregisterReceiver(batteryReceiver);
	}
	
	@XMLRPCMethod
	public Object batteryVoltage(){
		return voltage.getValue();
	}
	
	@XMLRPCMethod
	public String batteryTechnology(){
		return (String) technology.getValue();
	}
	
	@XMLRPCMethod
	public Object batteryTemperature(){
		return temperature.getValue();
	}
	
	@XMLRPCMethod
	public String batteryPlugged(){
		return (String) plugged.getValue();
	}
	
	@XMLRPCMethod
	public Object batteryLevel(){
		return level.getValue();
	}
}
