# Sensorium #

Sensorium is an Android Application that collects "sensor" information from your device ("Sensor data") and provides external interfaces to gather and process the data for scientific experiments. This is done in a privacy-preserving way. Only the data and only in the level of detail you allow will be made available.

Sensorium can be remotely pre-configured to suite the needs for your experiment campaign. If you want to implement further sensors or interfaces, please do so. 

We are using some of the sensor values for [Open 3G Map][6].

All our code is available under the GNU Lesser GPL 3.0. We would appreciate it if you contributed back to the Sensorium project. 

### Currently Available Sensors ###

* Radio Cell Coverage Information Sensor
* Network Location Sensor determines location based on availability of cellular 
tower and Wifi access points. 
* GPS Location Sensor determines location using satellites.
* Wifi Scan Sensor periodically scans the current 802.11 network for all the 
access points nearby.
* Wifi Connection Sensor displays the current active Wifi connection status. 
* Bluetooth Sensor displays local Bluetooth chip information, list of bonded 
and (periodically) scanned device.

### External Interfaces ###

* Local XMLRPC. You could use this to talk to programs running outside of the Android Framework, e.g. we use this to talk to the [Seattle Testbed][1]
* automatic JSON log generation and upload to HTTPS sites

### Sensor, Interfaces and Privacy Configuration ###

Users can also configure each sensor individually to suite their privacy needs. Also, can prevent Sensorium from running at boot or disable individual interfaces,

* Full privacy: the sensor will not share any information on the external interfaces
* High-Low privacy: Depending on the exact level of privacy, some information might not be shared at all, some data points might be rounded (e.g. longitude/latitude, i.e. only your coarse location will be seen), or salted and hashed (so you only can compare these hashes against each other). 
* No privacy: All information available to the sensor will be shared.

## Develop ##

Apart from improving the core Sensorium code, you are probably mostly interesting in writing your own Sensor or Interface classes. The following might help you with that.

### Implementing New Sensors ###

1. Extend the AbstractSensor class
2. Implement your sensor reading stuff, putting all values to be published in SensorValue objects
3. Call notifyListeners() whenever your Sensors wants to have its values updated
4. Put your class name into the res/values/sensors.xml to get it loaded

### Implementing New External Interfaces ###

Currently, we either talk through XMLRPC to your locally running code or we push JSON data to Web Servers.

Have better ways of communicating your sensors? Implement it! At the moment, it would be best if you read the code to understand what you would need to do. We are still working on a small tutorial to get you started.


## Third Party Libraries ##

* An adapted version of [android-xmlrpc][2] is used to provide XMLRPC. It's available under the APL 2.0.
* Uses [google-gson][3] (APL 2.0)  to provide JSON logging support for Android 2.3.x support. 
* Apache [HTTPClient][4] and related libs (APL 2.0)  for Multipart Entity HTTP POST support in 2.3.x.


(Note: If you were looking for information on our NetSys2013 demonstration, please look [here][5].)


[1]: https://seattle.cs.washington.edu/html/
[2]: https://code.google.com/p/android-xmlrpc/
[3]: https://code.google.com/p/google-gson/
[4]: https://hc.apache.org/httpcomponents-client-ga/index.html
[5]: https://github.com/fmetzger/android-sensorium/blob/master/netsys.md
[6]: https://o3gm.cs.univie.ac.at/o3gm/