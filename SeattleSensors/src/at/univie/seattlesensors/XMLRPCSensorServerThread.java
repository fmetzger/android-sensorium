package at.univie.seattlesensors;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.xmlrpc.android.MethodCall;
import org.xmlrpc.android.XMLRPCServer;

import android.util.Log;

public class XMLRPCSensorServerThread  implements Runnable{

	public static String SOCKET_ADDRESS = "127.0.0.1";
	public static int SOCKET_PORT = 63090;


	public XMLRPCSensorServerThread() {
	}

	public void run() {

		try {
			InetAddress localhost = InetAddress.getLocalHost();
			//Log.d("SeattleSensors:", localhost.getHostAddress());

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
					ArrayList<Object> params = call.getParams();
					if (params.size() > 0 )
					{
						server.respond(client, sensorregistry.getSensorMethodSignature((String) params.get(0)));
					} else
						server.respond(client, new Object[]{"Too few arguments"});
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

