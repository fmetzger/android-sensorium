package at.univie.sensorium.extinterfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.stream.JsonReader;

import android.util.Log;


public class PreferenceJSONLoader {
	
	public PreferenceJSONLoader(){
		
	}

	public void loadPrefsFromFile(String filename){
		File prefsFile = new File(filename);
		FileReader fw;
		List<BasicNameValuePair> prefs = new LinkedList<BasicNameValuePair>(); 
		try {
			fw = new FileReader(prefsFile);
			JsonReader reader = new JsonReader(fw);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public void getPrefsListFromURL(String url){
		
	}
	
	public void getPrefsFromURL(String url){
		
	}
}
