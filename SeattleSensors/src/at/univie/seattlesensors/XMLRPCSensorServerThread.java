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

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.xmlrpc.android.MethodCall;
import org.xmlrpc.android.XMLRPCServer;

import android.util.Log;

public class XMLRPCSensorServerThread  implements Runnable{

	public static final String SOCKET_ADDRESS = "127.0.0.1";
	public static final int SOCKET_PORT = 63090;
	
	public static boolean running = false;


	public XMLRPCSensorServerThread() {
	}

	public void run() {
		running = true;
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			ServerSocket socket = new ServerSocket(SOCKET_PORT, 10, localhost);
			XMLRPCServer server = new XMLRPCServer();
			
			SensorRegistry sensorregistry = SensorRegistry.getInstance();
			
			while (true) {
				Socket client = socket.accept();
				MethodCall call = server.readMethodCall(client);
				String name = call.getMethodName();
				
				
				if (name.equals("isSeattleSensor")){
					server.respond(client, new Object[] {true});
					
					
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
							server.respond(client, new Object[]{"Unknown method"});
						}
					} else
						server.respond(client, new Object[]{"Too few arguments"});
				}

				else if (name.equals("system.listMethods")){
					server.respond(client, sensorregistry.getSensorMethods().toArray());
				}
				else {
					Object [] methodresult = sensorregistry.callSensorMethod(name);
					if (methodresult != null){
						server.respond(client, methodresult);
					} else {
						server.respond(client, new Object[] {"Input not recognized"});
					}
					
				}
			}


		} catch (Exception e) {
			Log.d("SeattleSensors:", e.toString());
			e.printStackTrace();
		}
	}
}

