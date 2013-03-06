package at.univie.sensorium.extinterfaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.stream.JsonReader;


public class PreferenceJSONLoader {
	
	SharedPreferences prefs;
	
	public PreferenceJSONLoader(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void loadPrefsFromFile(String filename){
		try {
			FileInputStream stream = new FileInputStream(new File(filename));
			loadPrefsFromStream(stream);
		} catch (FileNotFoundException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		}
		
	}

	public void loadPrefsFromStream(InputStream input){
		List<BasicNameValuePair> prefs = new LinkedList<BasicNameValuePair>(); 
		try {
			InputStreamReader isreader = new InputStreamReader(input);
			JsonReader reader = new JsonReader(isreader);
			reader.beginArray(); // do we have an array or just a single object?

		     reader.beginObject();
		     while (reader.hasNext()) {
		       String name = reader.nextName();
		       String value = reader.nextString();
		       BasicNameValuePair kv = new BasicNameValuePair(name, value);
		       prefs.add(kv);
		     }
		     reader.endObject();
		     reader.endArray();
		     
		     for(BasicNameValuePair kv: prefs){
		    	 Log.d("JSONPREFS", kv.getName() + ": " + kv.getValue());
		     }
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
	
	
	
	public void getPrefsListFromURL(String url){
		
	}
	
	public void getPrefsFromURL(String url){
		
	}
}
