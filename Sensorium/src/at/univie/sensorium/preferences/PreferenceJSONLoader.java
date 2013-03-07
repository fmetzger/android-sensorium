package at.univie.sensorium.preferences;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import at.univie.sensorium.R;

import com.google.gson.stream.JsonReader;

public class PreferenceJSONLoader {

	Context context;
	SharedPreferences prefs;

	public PreferenceJSONLoader(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void loadDefaultPreferences() {
		loadPrefsFromStream(context.getResources().openRawResource(R.raw.defaultpreferences));
	}

	
	String urlstring;
	public void loadCampaignPreferences(final String u) {
		urlstring = u;
		Thread x = new Thread(prefretriever);
		x.start();
		
	}
	
	
	private Runnable prefretriever = new Runnable() {

		@Override
		public void run() {
			try {
				URL url = new URL(urlstring);
				URLConnection urlConnection = url.openConnection();
				urlConnection.setConnectTimeout(1000);
				loadPrefsFromStream(urlConnection.getInputStream());
				Log.d("SeattleSensors", "Done loading remote preferences");
			} catch (IOException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}
		}
	};
	

	private void loadPrefsFromStream(InputStream input) {
		List<BasicNameValuePair> preferencelist = new LinkedList<BasicNameValuePair>();
		try {
			InputStreamReader isreader = new InputStreamReader(input);
			JsonReader reader = new JsonReader(isreader);
			reader.beginArray(); // do we have an array or just a single object?

			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				String value = reader.nextString();
				BasicNameValuePair kv = new BasicNameValuePair(name, value);
				preferencelist.add(kv);
			}
			reader.endObject();
			reader.endArray();
			reader.close();

			for (BasicNameValuePair kv : preferencelist) {
//				if (prefs.contains(kv.getName())){ // if we preconfigure, this will always be false, so dont use it
				Log.d("SeattleSensors", "Setting pref " + kv.getName() + " from remote config to: " + kv.getValue());
				
				if(kv.getValue().toLowerCase(Locale.US).equals("true")){
					prefs.edit().putBoolean(kv.getName(), true).commit();
					Log.d("SeattleSensors", kv.getName() + " boolean true");
				} else if (kv.getValue().toLowerCase(Locale.US).equals("false")){
					prefs.edit().putBoolean(kv.getName(), false).commit();
					Log.d("SeattleSensors", kv.getName() + " boolean false");
				} else if (Integer.getInteger(kv.getValue()) != null){
					prefs.edit().putInt(kv.getName(), Integer.getInteger(kv.getValue())).commit();
					Log.d("SeattleSensors", kv.getName() + " int " + Integer.getInteger(kv.getValue()));
				} else { // value is String
					prefs.edit().putString(kv.getName(), kv.getValue()).commit();
					Log.d("SeattleSensors", kv.getName() + " String " + kv.getValue());
				}
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

}
