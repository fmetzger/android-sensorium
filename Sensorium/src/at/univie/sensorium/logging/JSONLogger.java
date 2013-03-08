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

package at.univie.sensorium.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import at.univie.sensorium.extinterfaces.HTTPSUploader;
import at.univie.sensorium.o3gm.SensorRegistry;
import at.univie.sensorium.preferences.Preferences;
import at.univie.sensorium.privacy.Privacy;
import at.univie.sensorium.sensors.AbstractSensor;
import at.univie.sensorium.sensors.SensorChangeListener;
import at.univie.sensorium.sensors.SensorValue;

import com.google.gson.stream.JsonWriter;


/**
 * Create one JSON output stream for every sensor
 * Use https://code.google.com/p/google-gson/ for compatibility with Android 2.x
 * 
 *
 */
public class JSONLogger implements SensorChangeListener{
	
	private List<AbstractSensor> sensors;
	
	private Map<String, JsonWriter> jsonMap;
	private Map<String, FileWriter> writerMap;
	private List<File> files;
	File extDir;
	
	
	public JSONLogger() {
	}
	
	public void init(List<AbstractSensor> sensors){
		this.sensors = sensors;
		init();
	}
	
	private void init(){
		jsonMap = new HashMap<String, JsonWriter>();
		writerMap = new HashMap<String, FileWriter>();
		files = new LinkedList<File>();
		// TODO: needs to check if there is external storage, else die (toast message?) gracefully
		extDir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/sensorium");
		extDir.mkdirs();
		
		for(AbstractSensor sensor: sensors){
			sensor.addListener(this);
		}
	}
	
	private JsonWriter getWriterForName(String sensorname){
		JsonWriter writer = jsonMap.get(sensorname);
		if (writer == null){
			try {
				String filename = sensorname.substring(sensorname.lastIndexOf('.')+1) + ".json";
				File extFile = new File(extDir, filename);
				if (extFile.exists()) {
					  //get the first free filename.number
					int i = 0;
					boolean done = false;
					while (!done) {				  
						File f = new File(extDir, filename+"."+String.valueOf(i++));
						if (f.exists())
							continue;
						extFile.renameTo(f);
						extFile = new File(extDir, filename); // reset extFile to original pointer
						done = true;
					  }
					}
				FileWriter fw = new FileWriter(extFile);
				writer = new JsonWriter(fw);
				writer.beginArray();
				jsonMap.put(sensorname, writer);
				writerMap.put(sensorname, fw);
				files.add(extFile);
			} catch (FileNotFoundException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			} catch (IOException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}
		}
		
		return writer;
	}
	
	private void writeObject(AbstractSensor sensor){
		JsonWriter jw = getWriterForName(sensor.getClass().getName());
		List<SensorValue> valuelist = sensor.getSensorValues();
		
		try {
			jw.beginObject();
			jw.name("privacy-level").value(sensor.getPrivacylevel().name());
			for(SensorValue value: valuelist){
				SensorValue privatized = Privacy.anonymize(value, sensor.getPrivacylevel());
				jw.name(privatized.getType().getName()).value(privatized.getValueRepresentation());
			}
			jw.endObject();
			writerMap.get(sensor.getClass().getName()).flush();
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		}

	}

	@Override
	public void sensorUpdated(AbstractSensor sensor) {
		writeObject(sensor);
	}

	public void finalize(){
		for (AbstractSensor s: SensorRegistry.getInstance().getSensors()){
			s.removeListener(this);
		}
		for(JsonWriter js: jsonMap.values()){
			try {
				js.endArray();
			} catch (IOException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}
		}
		for(FileWriter fw: writerMap.values()){
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}

		}
	}
	
	public void upload(){
		Preferences prefs = SensorRegistry.getInstance().getPreferences();
		String uploadurl = prefs.getString(Preferences.UPLOAD_URL_PREF, "");
		upload(uploadurl);
	}
	
	public void upload(String uploadurl){
		finalize(); // close the json objects
		Preferences prefs = SensorRegistry.getInstance().getPreferences();
		String uploadusername = prefs.getString(Preferences.UPLOAD_USERNAME, "");
		String uploadpassword = prefs.getString(Preferences.UPLOAD_PASSWORD, "");
		new HTTPSUploader(uploadurl, uploadusername, uploadpassword).execute(files);
		init(); // restart the logging
	}
	
	
	public void autoupload(String url, int interval, boolean wifionly){
		this.interval = interval;
		this.wifionly = wifionly;
		handler.postDelayed(runnable, Math.max((long) interval * 1000, 300000));
	}
	
	public void cancelautoupload(){
		handler.removeCallbacks(runnable); // TODO: check if this really stops the queue
	}
	
	private int interval;
	private boolean wifionly;
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {

			ConnectivityManager connManager = (ConnectivityManager) SensorRegistry.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!wifionly || (wifionly && mWifi.isConnected())) {
				SensorRegistry.getInstance().getJSONLogger().upload();
			}
			handler.postDelayed(this, Math.max((long) interval * 1000, 300000));
		}
	};
}
