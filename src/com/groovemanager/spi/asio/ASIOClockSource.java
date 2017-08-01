/*
 * Created on 09.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native ASIOClockSource struct
 * @author Manu Robledo
 */
final class ASIOClockSource extends NativeClass {
	/**
	 * Constructs a new ASIOClockSource and creates a corresponding
	 * native instance
	 */
	ASIOClockSource() {
	}
	/**
	 * Constructs a new ASIOClockSource that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * ASIOClockSource instance
	 */
	ASIOClockSource(long pointer) {
		super(pointer);
	}
	
	/**
	 * The index-Attribute of the ASIOClockSource struct
	 * @return ASIO says: This is used to identify the clock sources
	 * when ASIOSetClockSource() is accessed, should be an index counting
	 * from zero.
	 */
	native int index();
	/**
	 * The associatedChannel-Attirbute of the ASIOClockSource struct
	 * @return ASIO Says: The first channel of an associated input group,
	 * if any.
	 */
	native int associatedChannel();
	/**
	 * The associatedGroup-Attribute of the ASIOClockSource struct
	 * @return ASIO says: the group index of that channel. Groups of channels are
	 * defined to separate, for instance analog, S/PDIF, AES/EBU,
	 * ADAT connectors etc, when present simultaneously. Note
	 * that associated channel is enumerated according to
	 * numInputs/numOutputs, meaning it is independent from a
	 * group (see also ASIOGetChannelInfo()) inputs are associated
	 * to a clock if the physical connection transfers both data and
	 * clock (like S/PDIF, AES/EBU, or ADAT inputs). If there is
	 * no input channel associated with the clock source (like Word
	 * Clock, or internal oscillator), both associatedChannel and
	 * associatedGroup should be set to -1.
	 */
	native int associatedGroup();
	/**
	 * The isCurrentClockSource-Attribute of the ASIOClockSource struct
	 * @return true if this is the current clock source, false otherwise.
	 */
	native boolean isCurrentSource();
	/**
	 * The name of this clock source (for user selection)
	 * @return The name of this clock source (for user selection)
	 */
	native String name();
	
	protected native long createClass();
	protected native void cleanUp();

	public String toString(){
		return name();
	}
	public boolean equals(Object obj) {
		if(obj instanceof ASIOClockSource){
			ASIOClockSource other = (ASIOClockSource)obj;
			return index() == other.index();
		}
		return false;
	}
}