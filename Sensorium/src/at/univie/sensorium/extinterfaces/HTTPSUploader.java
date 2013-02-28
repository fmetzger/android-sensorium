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

package at.univie.sensorium.extinterfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import at.univie.sensorium.SensorRegistry;

public class HTTPSUploader extends AsyncTask<List<File>, Void, String>{
	
	// TODO: store and sync urls/user/pw with android properties
	private String host;
	private String posturl;// = "https://www.example.com/foo.php";
	private String username;
	private String password;
	
	public void setHost(String host){
		this.host = host;
	}
	public void setPosturl(String posturl){
		this.posturl = posturl;
	}
	public void setUsername(String username){
		this.username = username;
	}
	public void setPassword(String password){
		this.password = password;
	}
	
	public HTTPSUploader(String host, String posturl, String username, String password){
		this.host = host;
		this.posturl = posturl;
		this.username = username;
		this.password = password;
	}
	
	@Override
	protected String doInBackground(List<File>... params) {
		uploadFiles(params[0]);
		return "upload complete";
	}
	
    protected void onPostExecute(String result) {
        Toast.makeText(SensorRegistry.getInstance().getContext(), result, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
		Toast.makeText(SensorRegistry.getInstance().getContext(), "Starting upload...", Toast.LENGTH_SHORT).show();
    }
	
	private void uploadFiles(List<File> files) {
		try {

			DefaultHttpClient httpclient = new DefaultHttpClient();
			//HttpHost targetHost = new HttpHost(host, -1, "https");
			//httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(username, password));

			HttpPost httppost = new HttpPost(posturl);
		    MultipartEntity mpEntity = new MultipartEntity();
		    for(File file: files){
			    ContentBody cbFile = new FileBody(file, "application/json");
			    mpEntity.addPart(file.toString(), cbFile);
		    }
			//reqEntity.setChunked(true); // Send in multiple parts if needed
		    httppost.setEntity(mpEntity);
			HttpResponse response = httpclient.execute(httppost);
			
			Log.d("HTTPRESPONSE", "Response status: "+ response.getStatusLine().getStatusCode());

		} catch (IllegalArgumentException e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		}catch (FileNotFoundException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		} catch (ClientProtocolException e) {
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
