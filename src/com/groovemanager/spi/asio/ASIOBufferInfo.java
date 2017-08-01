/*
 * Created on 09.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native ASIOBufferInfo struct
 * @author Manu Robledo
 */
final class ASIOBufferInfo extends NativeClass {
	/**
	 * Constructs a new ASIOBufferInfo and creates a corresponding
	 * native instance
	 */
	ASIOBufferInfo() {
	}
	/**
	 * Constructs a new ASIOBufferInfo that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIOBufferInfo instance
	 */
	ASIOBufferInfo(long pointer) {
		super(pointer);
	}
	
	/**
	 * Tell whether this info object describes an input or output
	 * buffer
	 * @return true if it descibes an input buffer, false otherwise
	 */
	native boolean isInput();
	/**
	 * Set the isInput-Attribute
	 * @param isInput true if this info should descibe an input buffer,
	 * false otherwise
	 */
	native void setIsInput(boolean isInput);
	/**
	 * The index of the channel that this info is about. 
	 * @return The index of the channel that this info is about starting
	 * from 0.
	 */
	native int channelNum();
	/**
	 * Set the channelNum-Attribute
	 * @param num The index of the channel this info should describe
	 * starting from 0.
	 */
	native void setChannelNum(int num);
	/**
	 * The native addresses of the two ASIO buffer halfes
	 * @return An Array of size 2 containing the native addresses of
	 * the two ASIO buffer halfes
	 */
	native long[] buffers();
	protected native long createClass();
	protected native void cleanUp();
	
	public String toString(){
		return "ASIOBufferInfo{\n" +
				"\tPointer: " + getPointer() + "\n" +
				"\tisInput: " + isInput() + "\n" +
				"\tchannelNum: " + channelNum() + "\n" +
				"\tbuffers:{\n" +
				"\t\t[0]: " + buffers()[0] + "\n" +
				"\t\t[1]: " + buffers()[1] + "\n" +
				"\t}" +
				"}";
	}
}