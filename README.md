# Sensorium #

Android Application to connect locally available sensors to a [Seattle][1] 
installation on the device via XMLRPC. Sensors include:
* Device Info 
* Radio Cell Info Sensor 
* Network Location
* GPS Location
* Battery (or power usage)
* Wifi Scan result
* Wifi Connection Details
* Bluetooth Scan Result

## Usage ##

## Develop ##

1. Extend the AbstractSensor class
2. Implement 

## Sensors ##

To enable sensors, either altogether or individually, go to the "SETTINGS" menu. 
Under "GENERAL PREFERENCE", users can select whether to start the sensor 
application upon boot is completed
.
Users can also configure each sensor individually. As the user scrolling the 
bar underneath each sensor from left to right, the sensor will be disabled 
(leftmost), provide information with high/medium/low privacy, or full access 
(rightmost). 

Note:
* Radio Cell Info Sensor (needs to be in the range of a cellular tower?)
* Network Location Sensor determines location based on availability of cellular 
tower and Wifi access points. Hence the device has to either be in the range 
of a cellular tower or connected to a Wifi access point.
* GPS Location Sensor determines location using satellites. GPS has to be 
enabled on the device. Depending on conditions, this sensor may take a while 
to return a location result. Also works better outdoors.
* Wifi Scan Sensor periodically scans the current 802.11 network for all the 
access points nearby. Therefore Wifi capability must be enabled on the device. 
* Wifi Connection Sensor displays the current active Wifi connection status. 
E.g., supplicant status shows the detailed state of the device's negotiation 
with an access point.
* Bluetooth Sensor displays local Bluetooth chip information, list of bonded 
and (periodically) scanned device. Without doubt Bluetooth has to be enabled. 



## Third Party Libraries ##

* An adapted version of [android-xmlrpc][2] is used to provide XMLRPC. It's available under the APL 2.0.
* Uses [google-gson][3] (APL 2.0)  to provide JSON logging support for Android 2.3.x support. 
* Apache [HTTPClient][4] and related libs (APL 2.0)  for Multipart Entity HTTP POST support in 2.3.x.



[1]: https://seattle.cs.washington.edu/html/
[2]: https://code.google.com/p/android-xmlrpc/
[3]: https://code.google.com/p/google-gson/
[4]: https://hc.apache.org/httpcomponents-client-ga/index.html