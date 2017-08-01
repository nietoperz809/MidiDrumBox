/*
 * Created on 14.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * Collection of the ASIOSampleType constants
 * @author Manu Robledo
 *
 */
final class ASIOSampleType {
	final static int
		ASIOSTInt16MSB = ASIOSTInt16MSB(),
		ASIOSTInt16LSB = ASIOSTInt16LSB(),
		ASIOSTInt24MSB = ASIOSTInt24MSB(),
		ASIOSTInt24LSB = ASIOSTInt24LSB(),
		ASIOSTInt32MSB = ASIOSTInt32MSB(),
		ASIOSTInt32LSB = ASIOSTInt32LSB(),
		ASIOSTInt32MSB16 = ASIOSTInt32MSB16(),
		ASIOSTInt32LSB16 = ASIOSTInt32LSB16(),
		ASIOSTInt32MSB18 = ASIOSTInt32MSB18(),
		ASIOSTInt32LSB18 = ASIOSTInt32LSB18(),
		ASIOSTInt32MSB20 = ASIOSTInt32MSB20(),
		ASIOSTInt32LSB20 = ASIOSTInt32LSB20(),
		ASIOSTInt32MSB24 = ASIOSTInt32MSB24(),
		ASIOSTInt32LSB24 = ASIOSTInt32LSB24(),
		ASIOSTFloat32MSB = ASIOSTFloat32MSB(),
		ASIOSTFloat32LSB = ASIOSTFloat32LSB(),
		ASIOSTFloat64MSB = ASIOSTFloat64MSB(),
		ASIOSTFloat64LSB = ASIOSTFloat64LSB();
	
	private native static int ASIOSTInt16MSB();
	private native static int ASIOSTInt24MSB();
	private native static int ASIOSTInt32MSB();
	private native static int ASIOSTFloat32MSB();
	private native static int ASIOSTFloat64MSB();
	private native static int ASIOSTInt32MSB16();
	private native static int ASIOSTInt32MSB18();
	private native static int ASIOSTInt32MSB20();
	private native static int ASIOSTInt32MSB24();
	private native static int ASIOSTInt16LSB();
	private native static int ASIOSTInt24LSB();
	private native static int ASIOSTInt32LSB();
	private native static int ASIOSTFloat32LSB();
	private native static int ASIOSTFloat64LSB();
	private native static int ASIOSTInt32LSB16();
	private native static int ASIOSTInt32LSB18();
	private native static int ASIOSTInt32LSB20();
	private native static int ASIOSTInt32LSB24();
		
	private ASIOSampleType() {
	}
}