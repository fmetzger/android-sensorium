package org.xmlrpc.android;

import org.apache.http.HttpStatus;

public class XMLRPCException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7499675036625522379L;

	private int httpStatusCode = HttpStatus.SC_OK;

	public XMLRPCException(Exception e) {
		super(e);
	}

	public XMLRPCException(String string) {
		super(string);
	}

	public XMLRPCException(String string, int httpStatusCode) {
		this(string);
		this.httpStatusCode = httpStatusCode;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
