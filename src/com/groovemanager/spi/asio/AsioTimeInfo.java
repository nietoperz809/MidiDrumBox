/*
 * Created on 13.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native AsioTimeInfo struct
 * @author Manu Robledo
 */
final class AsioTimeInfo extends NativeClass {
	/**
	 * Constructs a new AsioTimeInfo struct and creates a corresponding
	 * native instance
	 */
	AsioTimeInfo() {
	}
	/**
	 * Constructs a new AsioTimeInfo struct that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * AsioTimeInfo instance
	 */
	AsioTimeInfo(long pointer) {
		super(pointer);
	}

	protected native long createClass();
	protected native void cleanUp();
	
	private native long jsystemTime();
	private native long jsamplePosition();
	private native long jsampleRate();

	/**
	 * The flags-Attribute of AsioTimeInfo
	 * @return The flags-Attribute of AsioTimeInfo
	 */
	native long flags();
	/**
	 * The speed-Attribute of AsioTimeInfo
	 * @return The speed-Attribute of AsioTimeInfo
	 */
	native double speed();
	
	/**
	 * The systemTime-Attribute of AsioTimeInfo
	 * @return The systemTime-Attribute of AsioTimeInfo
	 */
	ASIOTimeStamp systemTime(){
		return new ASIOTimeStamp(jsystemTime());
	}
	
	/**
	 * The samplePosition-Attribute of AsioTimeInfo
	 * @return The samplePosition-Attribute of AsioTimeInfo
	 */
	ASIOSamples samplePosition(){
		return new ASIOSamples(jsamplePosition());
	}
	
	/**
	 * The sampleRate-Attribute of AsioTimeInfo
	 * @return The sampleRate-Attribute of AsioTimeInfo
	 */
	ASIOSampleRate sampleRate(){
		return new ASIOSampleRate(jsampleRate());
	}
	
	/**
	 * ASIOTimeInfo flag
	 */
	final static long FLAG_kSystemTimeValid = kSystemTimeValid();
	private native static long kSystemTimeValid();
	final static long FLAG_kSamplePositionValid = kSamplePositionValid();
	private native static long kSamplePositionValid();
	final static long FLAG_kSampleRateValid = kSampleRateValid();
	private native static long kSampleRateValid();
	final static long FLAG_kSpeedValid = kSpeedValid();
	private native static long kSpeedValid();
	final static long FLAG_kSampleRateChanged = kSampleRateChanged();
	private native static long kSampleRateChanged();
	final static long FLAG_kClockSourceChanged = kClockSourceChanged();
	private native static long kClockSourceChanged();
}