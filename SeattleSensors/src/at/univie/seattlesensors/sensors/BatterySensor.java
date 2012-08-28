package at.univie.seattlesensors.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import at.univie.seattlesensors.SensorRegistry;

public class BatterySensor extends AbstractSensor {
	
	private BroadcastReceiver batteryReceiver;
	
	private int rawlevel = -1;
	private int level = -1;
	private int scale = -1;
	private int plugged = -1;
	private int temperature = -1;
	private int voltage = -1;
	private String technology = "";

	public BatterySensor(Context context) {
		super(context);
		name = "Battery Sensor";
		enable();
	}

	@Override
	public void enable() {
        batteryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                
            	plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
                temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
                voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                SensorRegistry.getInstance().log("BATTERY", level + "%, plugged: "+plugged+" volt: "+ voltage);
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryReceiver, batteryLevelFilter);
        
        super.enable();
	}

	@Override
	public void disable() {
		context.unregisterReceiver(batteryReceiver);
		super.disable();
	}
	
	@XMLRPCMethod
	public int batteryVoltage(){
		return voltage;
	}
	
	@XMLRPCMethod
	public String batteryTechnology(){
		return technology;
	}
	
	@XMLRPCMethod
	public int batteryTemperature(){
		return temperature;
	}
	
	@XMLRPCMethod
	public int batteryPlugged(){
		return plugged;
	}
	
	@XMLRPCMethod
	public int batteryLevel(){
		return rawlevel;
	}
	
	@XMLRPCMethod
	public Object[] batteryInformation(){
		return new Object[]{"technology", technology, "voltage", voltage, "temperature", temperature, "plugged", plugged, "rawlevel", rawlevel, "scale", scale, "level", level};
	}

}
