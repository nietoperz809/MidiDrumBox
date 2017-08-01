/*
 * Created on 18.04.2004
 *
 */
package com.groovemanager.spi.asio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * This class is used for DataLine.Info instances of ASIODataLines
 * @author Manu Robledo
 */
class ASIOLineInfo extends DataLine.Info {
	/**
	 * The ASIOChannelInfo representing this line
	 */
	private ASIOChannelInfo cInfo;
	/**
	 * The ASIOMixer to which the line belongs.
	 */
	private ASIOMixer mixer;
	
	/**
	 * Constructs a new ASIOLineInfo object
	 * @param mixer The ASIOMixer representing the ASIO driver
	 * @param cInfo The ASIOChannelInfo representing the channel
	 * @param formats The AudioFormats supported by the line
	 * @param minSize The minimum buffer size in bytes
	 * @param maxSize The maximum buffer size in bytes
	 */
	ASIOLineInfo(ASIOMixer mixer, ASIOChannelInfo cInfo, AudioFormat[] formats, int minSize, int maxSize){
		super(getLineClass(cInfo), formats, minSize, maxSize);
		this.cInfo = cInfo;
		this.mixer = mixer;
	}
	
	/**
	 * get the interface큦 class for the super constructor
	 * @param cInfo The ASIOChannelInfo representing the channel
	 * @return TargetDataLine.class if this info represents an
	 * input line, SourceDataLine.class otherwise
	 */
	private static Class getLineClass(ASIOChannelInfo cInfo){
		if(cInfo.isInput()) return TargetDataLine.class;
		else return SourceDataLine.class;
	}
	
	/**
	 * Get the ASIOMixer related to this info큦 line
	 * @return The ASIOMixer instance which generated this Info object
	 */
	ASIOMixer getMixer(){
		return mixer;
	}
	
	/**
	 * Get the lowest channel that this line uses
	 * @return The zero-based index of the lowest channel used by
	 * the line represented by this info object
	 */
	int getFirstChannel(){
		return cInfo.channel();
	}
	
	/**
	 * Get the ASIOChannelInfo that represents this line큦 channel
	 * @return The ASIOChannelInfo that represents this line큦 channel
	 */
	ASIOChannelInfo getChannelInfo(){
		return cInfo;
	}
	
	/**
	 * Tell if this info object represents an input or an output line
	 * @return true if the line represented by this info object is
	 * an input line, false otherwise
	 */
	boolean isInput(){
		return cInfo.isInput();
	}
	/**
	 * Get an identifying name of the line represented by this info object.
	 * Will be something like "Driver XY analog out L" or "Driver YZ SPDIF In"
	 * @return A name of the line represented by this object
	 */
	public String getName(){
		return getChannelInfo().name();
	}
	public String toString() {
		return getName() + ": " + getFirstChannel() + " (" + (isInput() ? "Input)" : "Output)");
	}
}