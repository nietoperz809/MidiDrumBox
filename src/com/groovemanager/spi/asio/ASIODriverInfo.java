/*
 * Created on 08.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native ASOPDriverInfo struct
 * @author Manu Robledo
 */
final class ASIODriverInfo extends NativeClass{
	/**
	 * Constructs a new ASIODriverInfo and creates a corresponding
	 * native instance
	 */
	ASIODriverInfo(){
		setAsioVersion(2);
		setSysRef(0);
	}
	/**
	 * Constructs a new ASIODriverInfo that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIODriverInfo instance
	 */
	ASIODriverInfo(long pointer) {
		super(pointer);
	}
	
	/**
	 * Set the asio version
	 * @param v the asio version. sould always be 2
	 */
	private native void setAsioVersion(int v);
	/**
	 * Set the sysRef. On windows systems this should be the
	 * application큦 main window handle. On Mac, 0 is used.
	 * 0 works on windwos too.
	 * @param l The main application window handle (on windows)
	 * or 0.
	 */
	private native void setSysRef(long l);
	/**
	 * The asio version
	 * @return The asio version. Should always be 2.
	 */
	native int asioVersion();
	/**
	 * The driver version
	 * @return The driver version (format is driver specific)
	 */
	native int driverVersion();
	/**
	 * The driver큦 name.
	 * @return The driver큦 name
	 */
	native String name();
	/**
	 * Should contain a user message describing the type of error
	 * that occured during ASIOInit() ifa ny
	 * @return The user message of an error that occured during
	 * ASIOInit() if any
	 */
	native String errorMessage();
	/**
	 * The sysRef. On windows systems this should be the
	 * application큦 main window handle. On Mac, 0 is used.
	 * 0 works on windwos too.
	 * @return The main application window handle (on windows)
	 * or 0.
	 */
	native long sysRef();
	
	protected native long createClass();
	protected native void cleanUp();

	public String toString(){
		return "ASIODriverInfo{\n" +
				"\tPointer: " + getPointer() + "\n" +
				"\tasioVersion: " + asioVersion() + "\n" +
				"\tdriverVersion: " + driverVersion() + "\n" +
				"\tname: " + name() + "\n" +
				"\terrorMessage: " + errorMessage() + "\n" +
				"\tsysRef: " + sysRef() + "\n" +
				"}";
	}
}
