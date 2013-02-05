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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HTTPSUploader {

	
	// TODO: store and sync urls/user/pw with android properties
	private String uri;
	private String posturl;// = "https://www.example.com/foo.php";
	private String username;
	private String password;

	private void post(List<File> files) {
		try {

			DefaultHttpClient httpclient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(uri, -1, "https");

			httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(username, password));

			HttpPost httppost = new HttpPost(posturl);
			
		    File file = new File("c:/TRASH/zaba_1.jpg");
		    MultipartEntity mpEntity = new MultipartEntity();
		    ContentBody cbFile = new FileBody(file, "image/jpeg");
		    mpEntity.addPart("userfile", cbFile);


		    httppost.setEntity(mpEntity);
			
			InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
			reqEntity.setContentType("binary/octet-stream");
			reqEntity.setChunked(true); // Send in multiple parts if needed
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);

		} catch (FileNotFoundException e) {
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
	
	public void uploadFiles(List<String> files){
		
	}

}
