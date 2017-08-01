/*
 * Created on 06.06.2004
 *
 */
package com.groovemanager.spi.asio;

import java.nio.ByteBuffer;

/**
 * This is a wrapper class for the native ASIOSampleRate struct
 * @author Manu Robledo
 */
final class ASIOSampleRate extends NativeClass {
	/**
	 * Constructs a new ASIOSampleRate and creates a corresponding
	 * native instance
	 */
	public ASIOSampleRate() {
	}
	/**
	 * Constructs a new ASIOSampleRate that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIOSampleRate instance
	 */
	public ASIOSampleRate(long pointer) {
		super(pointer);
	}
	/**
	 * Constructs a new ASIOSampleRate and creates a corresponding
	 * native instance with the given value
	 */
	public ASIOSampleRate(double value){
		setValue(value);
	}
	
	protected native long createClass();
	protected native void cleanUp();
	
	/**
	 * Get the value of this SampleRate
	 * @return The value of this SampleRate
	 */
	native double doubleValue();
	/**
	 * Set the value of this sample rate
	 * @param d The new value
	 */
	native void setValue(double d);
	/**
	 * Convert from an 8 byte DirectByteBuffer to a double value
	 * @param buffer The DirectByteBuffer created in native code
	 * @return The double value
	 */
	@SuppressWarnings("unused")
	private static double convertToDouble(ByteBuffer buffer){
		return buffer.getDouble();
	}
	/**
	 * Convert from double to a native representation
	 * @param buffer The buffer to copy the value to
	 * @param value The double value to be converted
	 */
	@SuppressWarnings("unused")
	private static void convertBack(ByteBuffer buffer, double value){
		buffer.putDouble(value);
	}
}