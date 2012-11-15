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

package at.univie.sensorium.sensors;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;

/**
 * Provides information tied to the device/model, i.e. vendor and model name, TAC, ...
 *
 */
public class DeviceInfoSensor extends AbstractSensor {
	
	private SensorValue tac;
	private SensorValue vendorname;
	private SensorValue modelname;
	private SensorValue totalMem;
	private SensorValue availMem;
	private SensorValue memThreshold;
	private SensorValue cpu;
	private MemoryInfo memoryInfo;
	private Handler handler = new Handler();
	private int scan_interval = 10; // sec
	
	public DeviceInfoSensor(Context context) {
		super(context);

		name = "General Device Information";
		tac = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.TAC);
		modelname = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.MODEL_NAME);
		vendorname = new SensorValue(SensorValue.UNIT.STRING, SensorValue.TYPE.VENDOR_NAME);
		
		totalMem = new SensorValue(SensorValue.UNIT.MEM, SensorValue.TYPE.TOTAL_MEM);
		availMem = new SensorValue(SensorValue.UNIT.MEM, SensorValue.TYPE.AVAL_MEM);
		memThreshold = new SensorValue(SensorValue.UNIT.MEM, SensorValue.TYPE.THD_MEM);
		cpu = new SensorValue(SensorValue.UNIT.RELATIVE, SensorValue.TYPE.CPU);
	}
	
	private Runnable memCPUTask = new Runnable() {
		@Override
		public void run() {						
			availMem.setValue(memoryInfo.availMem/1048576L);
			cpu.setValue(cpuUpdate()*100);
			
			notifyListeners();
			handler.postDelayed(this, scan_interval*1000);
		}		
	};

	@Override
	protected void _enable() {
		TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
		String imei = telephonyManager.getDeviceId();
		if (imei != null)
				tac.setValue(imei.substring(0, 6));
		
		vendorname.setValue(Build.MANUFACTURER);
		modelname.setValue(Build.MODEL);
		
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		
		totalMem.setValue(getTotalMemory());
		memThreshold.setValue(memoryInfo.threshold/1048576L);
		handler.postDelayed(memCPUTask, 0);
	}

	public long getTotalMemory() {  
		int tm=1000; 
		try { 
			RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r"); 
			String load = reader.readLine(); 
			String[] totrm = load.split(" kB"); 
			String[] trm = totrm[0].split(" "); 
			tm=Integer.parseInt(trm[trm.length-1]); 
			tm=Math.round(tm/1024); 
			reader.close();
			} 
		catch (IOException ex) { 
			ex.printStackTrace(); 
			} 
		
		return tm;
	}  
	
	public float cpuUpdate() {
		try {
	        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
	        String load = reader.readLine();

	        String[] toks = load.split(" ");

	        long idle1 = Long.parseLong(toks[5]);
	        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        try {
	            Thread.sleep(360);
	        } catch (Exception e) {}

	        reader.seek(0);
	        load = reader.readLine();
	        reader.close();

	        toks = load.split(" ");

	        long idle2 = Long.parseLong(toks[5]);
	        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return 0;
}

	@Override
	protected void _disable() {
		handler.removeCallbacks(memCPUTask);
	}
}
