/*
 * Created on 06.06.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native ASIOSamples struct
 * @author Manu Robledo
 */
final class ASIOSamples extends NativeClass {
	/**
	 * Constructs a new ASIOSamples struct and creates a corresponding
	 * native instance
	 */
	public ASIOSamples() {
	}
	/**
	 * Constructs a new ASIOSamples struct that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIOSamples instance
	 */
	public ASIOSamples(long pointer) {
		super(pointer);
	}
	
	protected native long createClass();
	protected native void cleanUp();
	
	/**
	 * Get the value of this ASIOSamples instance as double
	 * @return The value of this ASIOSamples instance
	 */
	native double doubleValue();
}
