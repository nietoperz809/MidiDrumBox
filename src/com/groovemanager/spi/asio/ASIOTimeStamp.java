/*
 * Created on 05.06.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native ASIOTimeStamp struct
 * @author Manu Robledo
 */
final class ASIOTimeStamp extends NativeClass {
	/**
	 * Constructs a new ASIOTimeStamp struct and creates a corresponding
	 * native instance
	 */
	ASIOTimeStamp() {
	}
	/**
	 * Constructs a new ASIOTimeStamp struct that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIOTimeStamp instance
	 */
	ASIOTimeStamp(long pointer) {
		super(pointer);
	}
	
	protected native long createClass();
	protected native void cleanUp();
	/**
	 * Get the value of this ASIOTimeStamp instance as double
	 * @return The value of this ASIOTimeStamp instance
	 */
	native double doubleValue();
}
