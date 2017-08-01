/*
 * Created on 13.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native ASIOTime struct
 * @author Manu Robledo
 */
final class ASIOTime extends NativeClass {
	/**
	 * The timeCode-Attribute of ASIOTime
	 */
	final ASIOTimeCode timeCode;
	/**
	 * The timeInfo-Attribute of ASIOTime
	 */
	final AsioTimeInfo timeInfo;
	/**
	 * Constructs a new ASIOTime struct and creates a corresponding
	 * native instance as well as native instances for timeCode and
	 * timeInfo
	 */
	ASIOTime() {
		timeCode = new ASIOTimeCode();
		timeInfo = new AsioTimeInfo();
	}
	/**
	 * Constructs a new ASIOTime struct that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIOTime instance
	 */
	ASIOTime(long pointer) {
		super(pointer);
		timeCode = new ASIOTimeCode(jTimeCode());
		timeInfo = new AsioTimeInfo(jTimeInfo());
	}
	
	protected native long createClass();
	protected native void cleanUp();
	
	private native long jTimeInfo();
	private native long jTimeCode();
}