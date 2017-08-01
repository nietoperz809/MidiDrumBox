/*
 * Created on 27.05.2004
 *
 */
package com.groovemanager.spi.asio;

import javax.sound.sampled.EnumControl;

/**
 * This class is an implementation of JavaSound Controls to let the user
 * select the ASIOClockSource
 * @author Manu Robledo
 */
public class ASIOClockSourceControl extends EnumControl {
	/**
	 * The possible clock sources
	 */
	ASIOClockSource[] sources;
	/**
	 * The ASIODataLine to which this Control belongs
	 */
	ASIODataLine dataLine;
	
	/**
	 * Constructs a new ASIOClockSourceControl for the given ASIODataLine
	 * with the given sources as selectable options
	 * @param line The ASIODataLine instance to which this Control belongs
	 * @param sources The selectable clock sources
	 */
	ASIOClockSourceControl(ASIODataLine line, ASIOClockSource[] sources) {
		super(Type.CLOCK_SOURCE, sources, sources[0]);
		this.sources = sources;
		dataLine = line;
	}
	/**
	 * Constructs a new ASIOClockSourceControl for the given ASIODataLine
	 * with "internal" as the only selectable option. Will be used if the
	 * query to ASIOGetClockSources fails for some reason.
	 * @param line The ASIODataLine instance to which this Control belongs
	 */
	ASIOClockSourceControl(ASIODataLine line){
		super(Type.CLOCK_SOURCE, new String[]{"Internal"}, "Internal");
		dataLine = line;
	}

	/**
	 * Set the clock source of the DataLine to the given value
	 * @see EnumControl#setValue(Object)
	 */
	public void setValue(Object value) {
		int index = 0;
		if(value instanceof String) index = 0;
		else{
			for (int i = 0; i < sources.length; i++) {
				if(sources[i] == value) index = sources[i].index();
			}
		}
		long seq = 0;
		try {
			seq = dataLine.getMixer().ASIOInit();
			ASIOStaticFunctions.ASIOSetClockSource(index);
			super.setValue(value);
		} catch (ASIOError e) {
			e.printStackTrace();
		}
		dataLine.getMixer().ASIOExit(seq);
	}
	
	/**
	 * @author Manu Robledo
	 * The Type class representing ASIOClockSourceControl
	 */
	protected static class Type extends EnumControl.Type{
		public final static Type CLOCK_SOURCE = new Type("Clock Source");
		protected Type(String name) {
			super(name);
		}
	}
	/**
	 * @see EnumControl#toString()
	 */
	public String toString() {
		return "Clock Source: " + getValue();
	}
}