/*
 *  * Copyright (C) 2012 Florian Metzger
 * 
 *  This file is part of android-seattle-sensors.
 *
 *   android-seattle-sensors is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   android-seattle-sensors is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with android-seattle-sensors. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package at.univie.seattlesensors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.xmlrpc.android.MethodCall;
import org.xmlrpc.android.XMLRPCServer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class XMLRPCSensorServerThread  implements Runnable{

	public static final String SOCKET_ADDRESS = "127.0.0.1";
//	public static final int SOCKET_PORT = 63090;
	private int[] portArray = new int[] {63090, 63091, 63092, 
			63093, 63094, 63095, 63096, 63097, 63098, 63099};
	public static int SOCKET_PORT;
	public static boolean running = false;


	public XMLRPCSensorServerThread() {
	}

	public void run() {
		running = true;
		int i;
		for (i = 0; i < portArray.length; i++) {
			SOCKET_PORT = portArray[i];
			Log.d("SeattleSensors", "XMLRPC Server bonding on port... " + SOCKET_PORT);
			
			try {
				InetAddress localhost = InetAddress.getLocalHost();
				ServerSocket socket = new ServerSocket(SOCKET_PORT, 10, localhost);
				XMLRPCServer server = new XMLRPCServer();
				Log.d("SeattleSensors", "XMLRPC Server listening on port " + SOCKET_PORT);

				SensorRegistry sensorregistry = SensorRegistry.getInstance();

				while (true) {
					Socket client = socket.accept();
					MethodCall call = server.readMethodCall(client);
					String name = call.getMethodName();


					if (name.equals("isSeattleSensor")){
						server.respond(client, true);
					}

					else if (name.equals("system.methodSignature")){
						ArrayList<Object> params = call.getParams();
						if (params.size() > 0 )
						{
							String methodname = (String) params.get(0);

							Object [] methodsignature = sensorregistry.getSensorMethodSignature(methodname);

							if (methodsignature != null)
								server.respond(client, methodsignature);
							else{
								server.respond(client, "Unknown method");
							}
						} else
							server.respond(client, "Too few arguments");
					}

					else if (name.equals("system.listMethods")){
						server.respond(client, sensorregistry.getSensorMethods().toArray());
					}
					else {
						Object methodresult = sensorregistry.callSensorMethod(name);
						if (methodresult != null){
							server.respond(client, methodresult);
						} else {
							server.respond(client,"Input not recognized or no information returned or sensor disabled");
						}

					}
				}
				
			} catch (Exception e) {
				Log.d("SeattleSensors:", e.toString());
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}
			
		}
		
		if (i == portArray.length) {
			Log.e("SeattleSensors", "Could not locate a port in XMLRPC Server Thread!");
		}
		
	}
}

