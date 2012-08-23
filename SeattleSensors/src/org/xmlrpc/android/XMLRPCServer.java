package org.xmlrpc.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.Socket;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class XMLRPCServer extends XMLRPCCommon {

    private static final String CRLF = "\r\n";
	private static final String RESPONSE =
		"HTTP/1.1 200 OK" + CRLF +
		"Connection: close" + CRLF +
		"Content-Type: text/xml" + CRLF +
		"Content-Length: ";

	private static final String NEWLINES = CRLF + CRLF;
	private XMLRPCSerializer iXMLRPCSerializer;

	public XMLRPCServer() {
		iXMLRPCSerializer = new XMLRPCSerializer();
	}

	public MethodCall readMethodCall(Socket socket) throws IOException, XmlPullParserException
	{
		MethodCall methodCall = new MethodCall();
		InputStream inputStream = socket.getInputStream();

		XmlPullParser pullParser = xmlPullParserFromSocket(inputStream);
		
		pullParser.nextTag();
		pullParser.require(XmlPullParser.START_TAG, null, Tag.METHOD_CALL);
		pullParser.nextTag();
		pullParser.require(XmlPullParser.START_TAG, null, Tag.METHOD_NAME);

		methodCall.setMethodName(pullParser.nextText());

		pullParser.nextTag();
		
		if (XmlPullParser.START_TAG == pullParser.getEventType()
				&& Tag.PARAMS.equals( pullParser.getName() ) ) {// if we have <params>
			
			pullParser.nextTag(); // <param>
					
			while (pullParser.getName().equals(Tag.PARAM)) {
				pullParser.require(XmlPullParser.START_TAG, null, Tag.PARAM);
				pullParser.nextTag(); // <value>

				Object param = iXMLRPCSerializer.deserialize(pullParser);
				methodCall.params.add(param); // add to return value

				pullParser.nextTag();
				pullParser.require(XmlPullParser.END_TAG, null, Tag.PARAM);
				pullParser.nextTag(); // <param> or </params>
				
			}
		}	
		return methodCall;
	}
	
    XmlPullParser xmlPullParserFromSocket(InputStream socketInputStream) throws IOException, XmlPullParserException {
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(socketInputStream));
        while ((line = br.readLine()) != null && line.length() > 0); // eat the HTTP POST headers
        
        XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
        pullParser.setInput(br);
        return pullParser;
    }
	
	public void respond(Socket socket, Object value) throws IOException {

		String content = methodResponse(value);
		String response = RESPONSE + (content.length()) + NEWLINES + content;
		OutputStream outputStream = socket.getOutputStream();
		outputStream.write(response.getBytes());
		outputStream.flush();
		outputStream.close();
		socket.close();
		Log.d(Tag.LOG, "response:" + response);
	}
	
	private String methodResponse(Object value)
	throws IllegalArgumentException, IllegalStateException, IOException {
		StringWriter bodyWriter = new StringWriter();
		serializer.setOutput(bodyWriter);
		serializer.startDocument(null, null);
		serializer.startTag(null, Tag.METHOD_RESPONSE);
		
		serializeParams(value);

		serializer.endTag(null, Tag.METHOD_RESPONSE);
		serializer.endDocument();
		
		return bodyWriter.toString();
	}
}