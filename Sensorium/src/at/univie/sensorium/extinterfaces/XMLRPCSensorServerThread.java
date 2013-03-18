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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.xmlrpc.android.MethodCall;
import org.xmlrpc.android.XMLRPCServer;

import android.util.Log;
import at.univie.sensorium.SensorRegistry;

public class XMLRPCSensorServerThread implements Runnable {

	public static final String SOCKET_ADDRESS = "127.0.0.1";
	private int[] portArray = new int[] { 63090, 63091, 63092, 63093, 63094, 63095, 63096, 63097, 63098, 63099 };
	public static int SOCKET_PORT;
	public static boolean running = false;

	private volatile boolean isstopped = false;

	public XMLRPCSensorServerThread() {
	}

	public void run() {
		running = true;
		isstopped = false;
		int i;
		for (i = 0; i < portArray.length; i++) { // TODO: this is the culprit
													// when trying to cancel the
													// thread (loops over the
													// next 10 ports and
													// blocking them before
													// failing
			if (isstopped) { // escape the loop if we stopped XMLRPC
				break;
			}
			SOCKET_PORT = portArray[i];
			Log.d(SensorRegistry.TAG, "XMLRPC Server bonding on port... " + SOCKET_PORT);

			try {
				InetAddress localhost = InetAddress.getLocalHost();
				ServerSocket socket = new ServerSocket(SOCKET_PORT, 10, localhost);
				socket.setSoTimeout(5000); // wait for 10s at max to allow
											// interrupting the thread
				XMLRPCServer server = new XMLRPCServer();
				Log.d(SensorRegistry.TAG, "XMLRPC Server listening on port " + SOCKET_PORT);

				SensorRegistry sensorregistry = SensorRegistry.getInstance();

				while (!isstopped) {
					try {
						Socket client = socket.accept();
						MethodCall call = server.readMethodCall(client);
						String name = call.getMethodName();

						if (name.equals("isSeattleSensor")) {
							server.respond(client, true);
						}

						else if (name.equals("system.methodSignature")) {
							ArrayList<Object> params = call.getParams();
							if (params.size() > 0) {
								String methodname = (String) params.get(0);

								Object[] methodsignature = sensorregistry.getSensorMethodSignature(methodname);

								if (methodsignature != null)
									server.respond(client, methodsignature);
								else {
									server.respond(client, "Unknown method");
								}
							} else
								server.respond(client, "Too few arguments");
						}

						else if (name.equals("system.listMethods")) {
							server.respond(client, sensorregistry.getSensorMethods().toArray());
						} else {
							Object methodresult = sensorregistry.callSensorMethod(name);
							if (methodresult != null) {
								server.respond(client, methodresult);
							} else {
								server.respond(client, "Input not recognized or no information returned or sensor disabled");
							}

						}
					} catch (SocketTimeoutException e) {
						Log.d(SensorRegistry.TAG, "Listening socket timeout");
						if (isstopped)
							socket.close();
							running = false;
					}

				}
			} catch (Exception e) {
				Log.d("SeattleSensors:", e.toString());
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d(SensorRegistry.TAG, sw.toString());
			}
		}

		if (i == portArray.length) {
			Log.e(SensorRegistry.TAG, "Could not locate a port in XMLRPC Server Thread!");
		}
	}

	public void stopThread() {
		isstopped = true;
	}

}
