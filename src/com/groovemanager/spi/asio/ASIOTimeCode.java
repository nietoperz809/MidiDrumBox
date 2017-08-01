/*
 * Created on 13.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native ASIOTimeCode struct
 * @author Manu Robledo
 */
final class ASIOTimeCode extends NativeClass {
	/**
	 * Constructs a new ASIOTimeCode struct and creates a corresponding
	 * native instance
	 */
	ASIOTimeCode() {
	}
	/**
	 * Constructs a new ASIOTimeCode struct that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIOTimeCode instance
	 */
	ASIOTimeCode(long pointer) {
		super(pointer);
	}
	
	protected native long createClass();
	protected native void cleanUp();
	
	/**
	 * The speed-Attribute of ASIOTimeCode
	 * @return The speed-Attribute of ASIOTimeCode
	 */
	native double speed();
	private native long jtimeCodeSamples();
	
	/**
	 * The flags-Attribute of ASIOTimeCode
	 * @return The flags-Attribute of ASIOTimeCode
	 */
	native long flags();
	
	/**
	 * The timeCodeSamples-Attribute of ASIOTimeCode
	 * @return The timeCodeSamples-Attribute of ASIOTimeCode
	 */
	ASIOSamples timeCodeSamples(){
		return new ASIOSamples(jtimeCodeSamples());
	}

	/**
	 * ASIOTimeCode flag
	 */
	final static long FLAG_kTcValid = kTcValid();
	private native static long kTcValid();
	final static long FLAG_kTcRunning = kTcRunning();
	private native static long kTcRunning();
	final static long FLAG_kTcReverse = kTcReverse();
	private native static long kTcReverse();
	final static long FLAG_kTcOnspeed = kTcOnspeed();
	private native static long kTcOnspeed();
	final static long FLAG_kTcStill = kTcStill();
	private native static long kTcStill();
	final static long FLAG_kTcSpeedValid = kTcSpeedValid();
	private native static long kTcSpeedValid();

	public String toString(){
		return "ASIOTimeCode{\n" +
				"\tPointer: " + getPointer() + "\n" +
				"\tspeed: " + speed() + "\n" +
				"\ttimeCodeSamples: " + timeCodeSamples() + "\n" +
				"\tflags: " + flags() + "\n" +
				"}";
	}
}