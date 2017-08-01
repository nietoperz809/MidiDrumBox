/*
 * Created on 09.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native ASIOChannelInfo struct
 * @author Manu Robledo
 */
final class ASIOChannelInfo extends NativeClass {
	/**
	 * Constructs a new ASIOChannelInfo and creates a corresponding
	 * native instance
	 */
	ASIOChannelInfo() {
	}
	/**
	 * Constructs a new ASIOChannelInfo that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIOChannelInfo instance
	 */
	ASIOChannelInfo(long pointer) {
		super(pointer);
	}
	
	/**
	 * The index of the channel that this info is about. 
	 * @return The index of the channel that this info is about starting
	 * from 0.
	 */
	native int channel();
	/**
	 * Tell whether this info object describes an input or output
	 * channel
	 * @return true if it descibes an input channel, false otherwise
	 */
	native boolean isInput();
	/**
	 * The isActive-Attribute of the ASIOChannelInfo-struct
	 * @return ASIO says: true if the channel is active as it was
	 * installed by ASIOCreateBuffers(), false otherwise
	 */
	native boolean isActive();
	/**
	 * The channelGroup-Attribute of the ASIOChannelinfo-struct
	 * @return ASIO says: the channel group that this channel belongs
	 * to. For drivers which support different types of channels, like
	 * analog, S/PDIF, AES/EBU, ADAT etc. interfaces, there should be
	 * a reasonable grouping of these types. Groups are always independent
	 * from a channel index, that is a channel index always counts from 0
	 * to numInputs/numOutputs regardless of the group it may belong to.
	 * There will always be at least one group (group 0).
	 */
	native int channelGroup();
	/**
	 * The type-Attribute of the ASIOChannelInfo-struct
	 * @return The ASIOSympleType constant describing the AudioFormat of
	 * this channel.
	 */
	native int type();
	/**
	 * The type-Attribute of the ASIOChannelInfo-struct
	 * @return A name describing the type of channel in question. Used
	 * to allow for user selection and enabling of specific channels.
	 */
	native String name();
	/**
	 * Set the channel-Attribute
	 * @param channel The index of the channel this info should describe
	 * starting from 0.
	 */
	native void setChannel(int channel);
	/**
	 * Set the isInput-Attribute
	 * @param isInput true if this info should descibe an input channel,
	 * false otherwise
	 */
	native void setIsInput(boolean isInput);
	
	protected native long createClass();
	protected native void cleanUp();
	
	public String toString(){
		return "ASIOChannelInfo{\n" +
				"\tPointer: " + getPointer() + "\n" +
				"\tchannel: " + channel() + "\n" +
				"\tisInput: " + isInput() + "\n" +
				"\tisActive: " + isActive() + "\n" +
				"\tchannelGroup: " + channelGroup() + "\n" +
				"\ttype: " + type() + "\n" +
				"\tname: " + name() + "\n" +
				"}";
	}
}