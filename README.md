(Please see below for an English version of this text.)

### Guten Tag und danke, dass Sie vorbeischauen! ###

Wir w&uuml;rden gerne Sensordaten Ihres Android-Smartphones oder -Tablets f&uuml;r unsere Demo ,,Sensorium -- The Generic Sensor Framework'' (Mittwoch ab 13:00 im Raum GS-Pool) nutzen. Wenn Sie mitmachen wollen, installieren Sie bitte unsere Android-App ,,Sensorium'' auf Ihrem Ger&auml;t [aus Google Play][101]. 

Die App speichert Sensorwerte (3G, GPS, Akkustand usw.), die Sie uns erlauben auszulesen; Sie k&ouml;nnen die Aufl&ouml;sung der gesammelten Daten in der App beliebig steuern. Die oben verlinkte Version der App ist vorkonfiguriert, Daten f&uuml;r unsere offene 3G-Netzabdeckungskarte [,,Open3GMap''][102] zu sammeln. Daf&uuml;r speichern wir Ihre aktuelle GPS-Position sowie einige Details &uuml;ber die Empfangsst&auml;rke und Verbindungstechnologie Ihrer Mobilnetz-Anbindung auf unserem Server (falls Sie diese Daten zur Verf&uuml;gung stellen wollen). Weitere Details zum Datenschutz finden Sie im Folgenden.

Danke f&uuml;r Ihr Interesse! Wir freuen uns auf Ihren Besuch bei unserem Demo-Stand.
  florian.metzger@univie.ac.at, albert.rafetseder@univie.ac.at, Universit&auml;t Wien.

## Wie geht Sensorium mit den gesammelten Daten um? ##
* Zun&auml;chst werden nur Daten jener Sensoren gesammelt, die Sie freigeben. &uuml;berdies ber&uuml;cksichtigen wir den eingestellten Privacy-Level, das hei&szlig;t Sie k&ouml;nnen selbst konfigurieren, wie detailliert die gesammelten Daten sind. Am Beispiel GPS-Koordinaten: 
 * H&ouml;chste vom Ger&auml;t zu Verf&uuml;gung gestellte Genauigkeit
 * gerundete Koordinaten
 * mit Salt versehene, gehashte gerundete Koordinaten
 * kein Zugriff.
* Falls konfiguriert, werden die bereinigten Daten lokal &uuml;ber eine XML-RPC-Schnittstelle anderen Anwendungen zur Verf&uuml;gung gestellt. Damit geben Sie anderen Apps, die das Interface unterst&uuml;tzen, Zugriff auf die Sensordaten. (Ein Beispiel dazu mit der Testbed-Software [Seattle][103] demonstrieren wir gerne bei unserer Demo.) 
* Weiters werden die bereinigten Daten im JSON-Format &uuml;ber HTTP POST zu einem konfigurierbaren Server hinaufgeladen. Vorkonfiguriert ist unser Open3GMap-Server. Sie k&ouml;nnen einstellen, ob &uuml;berhaupt hinaufgeladen werden soll, ob regelm&auml;&szlig;ig, und ob der Upload &uuml;ber Ihren Datentarif erfolgen darf oder nur &uuml;ber WLAN.

## Wie geht Open3GMap mit den gesammelten Daten um? ##
Relevante Datenpunkte f&uuml;r die Netzabdeckungskarte sind GPS-Koordinaten sowie die Daten des 3G-Interfaces (MCC, MNC und LAC, Cell ID, Verbindungstechnologie und RSSI, Operator und Roaming-Status), jeweils in voller Genauigkeit. Wir erfassen weder Ihre Telefonnummer (IMSI) noch Telefon-Seriennummer (IMEI) noch IP-Adresse. Die Daten werden um der Freiwilligkeit des Zur-Verf&uuml;gung-Stellens Rechnung zu tragen als Open Data auf unserem Server zum Download angeboten. Bitte beachten Sie die m&ouml;glichen Konsequenzen f&uuml;r Ihre Privatsph&auml;re (und die anderer Leute).
Unser Server sammelt auch andere bereinigte Sensorwerte, falls Sie diese zur Verf&uuml;gung stellen. Diese werden nicht ver&ouml;ffentlicht, sondern dienen uns als Spielwiese f&uuml;r Algorithmen, die mit unvollst&auml;ndigen oder ungenauen Daten arbeiten sollen.

## Meine Frage wurde nicht beantwortet! ## 
Bitte kommen Sie uns w&auml;hrend der Demo-Session besuchen. Wir stehen gerne f&uuml;r weitere Informationen zur Verf&uuml;gung. Einige Detailinformation auf Englisch (welche Sensoren es gibt, wie die Datenschnittstellen funktionieren, wie man neue Sensoren hinzuf&uuml;gt) ist auch weiter unten in dieser README verf&uuml;gbar.



# Hello and thanks for coming here! #
We would like to use sensor data from your Android device in our demo \`\`Sensorium -- The Generic Sensor Framework'' on Wednesday March 13, starting at 1 PM, in room GS-Pool. If you want to participate, please install \`\`Sensorium'' [from Google Play][101].

The app collects sensor values (such as 3G, GPS, battery charge) that you allow us to access. You can control the level of detail of the data collected from within the app. The app comes preconfigured for collecting data for our open 3G connectivity map, [``Open3GMap''][102]. For this, we store your GPS location and details about current 3G coverage such as signal strength and access technology on our server (in case you choose to share this data with us). If you care about privacy, be sure to read on.

Thanks for considering us! We hope to meet you at our demo booth.
  florian.metzger@univie.ac.at, albert.rafetseder@univie.ac.at, University of Vienna.

## What does Sensorium do with the data it collects? ##
* First of all, Sensorium collects data only from sensors you choose to share, at the privacy level you set. You are thus in control as regards the level of detail of data collected. Consider GPS coordinates as an example:
 * Store coordinates at the highest level of detail the device gives us.
 * Round the coordinates.
 * Salt and hash the rounded coordinates.
 * Don't store coordinates at all.
* Sanitized sensor values are made accessible to other applications running on the same device over an XML-RPC interfac if you configure Sensorium to do so. We will present an example of this using the [Seattle][103] testbed software during our demo.
* Finally, sanitized values are stored on a configurable server using JSON via HTTP POSTs. The preconfigured version uses our Open3GMap server. You decide whether you want to upload at all, upload regularly, and/or only upload via WiFi to save cost on your data plan.

## What does Open3GMap doe with the data it collects? ##
Values we need for the map display are GPS coordinates and data from the 3G interface (MCC, MNC, and LAC, Cell ID, access technology, and RSSI, mobile operator and roaming status), all at the highest level of detail. We do not record your phone number (IMSI) nor phone serial number (IMEI) nor IP address. To acknowledge our volunteer contributors, we make the collected data available for download as Open Data. Please keep the effects on your (and anybody else's) privacy in mind.
We also collect other sanitized sensor data if you happen to share them. We do not make these public, but use them as a playground for algorithms that deal with incomplete or less accurate data.

## My question is not answered here! ##
Feel free to talk to us at our demo booth! Some additional information on which sensors we currently support, how the interfaces for sanitized data work, how to add new sensors etc. is available below.


[101]: https://play.google.com/store/apps/details?id=at.univie.sensorium.o3gm&feature=search_result
[102]: https://skylla.fc.univie.ac.at/~puehringer/o3gm_django/
[103]: https://seattle.cs.washington.edu/html/



# Sensorium #

Sensorium is an Android Application that collects "sensor" information from your device ("Sensor data") and provides external interfaces to gather and process the data for scientific experiments. This is done in a privacy-preserving way. Only the data and only in the level of detail you allow will be made available.

Sensorium can be remotely pre-configured to suite the needs for your experiment campaign. If you want to implement further sensors or interfaces, please do so. 

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



[1]: https://seattle.cs.washington.edu/html/
[2]: https://code.google.com/p/android-xmlrpc/
[3]: https://code.google.com/p/google-gson/
[4]: https://hc.apache.org/httpcomponents-client-ga/index.html