/*
 * Created on 19.04.2004
 *
 */
package com.groovemanager.spi.asio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

/**
 * This class maps the ASIOSampleType values to a JavaSound AudioFormat
 * @author Manu Robledo
 */
final class ASIOAudioFormat extends AudioFormat {
	/**
	 * Constructs a new ASIOAudioFormat of the specified ASIOSampleType,
	 * with the given sampleRate and channels
	 * @param sampleRate The sampleRate of the format
	 * @param type The ASIOSampleType constant
	 * @param channels The number of channels
	 */
	ASIOAudioFormat(float sampleRate, final int type, int channels){
		super(getEncoding(type), sampleRate, getSampleSizeInBits(type), channels, getFrameSize(type, channels),
				sampleRate, getEndianess(type));
	}
	
	/**
	 * Get the Encoding out of the given ASIOSampleType
	 * @param type The ASIOSampleType constant
	 * @return The encoding of the given ASIOSampleType.
	 * For Flaot encodings normalized to +/- 1.0 PCM_FLOAT
	 * is used as encoding name (either 32 or 64 Bit are
	 * supported)
	 */
	private static AudioFormat.Encoding getEncoding(int type){
		if(
				type == ASIOSampleType.ASIOSTFloat32LSB ||
				type == ASIOSampleType.ASIOSTFloat32MSB ||
				type == ASIOSampleType.ASIOSTFloat64LSB ||
				type == ASIOSampleType.ASIOSTFloat64MSB
		)
			return new Encoding("PCM_FLOAT");
		else return Encoding.PCM_SIGNED;
	}
	
	/**
	 * Get the sample size out of the given ASIOSampleType
	 * @param type The ASIOSampleType constant
	 * @return The sample size in Bits of the given ASIOSampleType
	 */
	private static int getSampleSizeInBits(final int type){
		if(
				type == ASIOSampleType.ASIOSTInt16LSB ||
				type == ASIOSampleType.ASIOSTInt16MSB ||
				type == ASIOSampleType.ASIOSTInt32LSB16 ||
				type == ASIOSampleType.ASIOSTInt32MSB16
		)
			return 16;
		else if(
				type == ASIOSampleType.ASIOSTInt32LSB18 ||
				type == ASIOSampleType.ASIOSTInt32MSB18
		)	
			return 18;
		else if(
				type == ASIOSampleType.ASIOSTInt32LSB20 ||
				type == ASIOSampleType.ASIOSTInt32MSB20
		)	
			return 20;
		else if(
				type == ASIOSampleType.ASIOSTInt24LSB ||
				type == ASIOSampleType.ASIOSTInt24MSB ||
				type == ASIOSampleType.ASIOSTInt32LSB24 ||
				type == ASIOSampleType.ASIOSTInt32MSB24
		)
			return 24;
		else if(
				type == ASIOSampleType.ASIOSTFloat32LSB ||
				type == ASIOSampleType.ASIOSTFloat32MSB ||
				type == ASIOSampleType.ASIOSTInt32LSB ||
				type == ASIOSampleType.ASIOSTInt32MSB
		)
			return 32;
		else if(
				type == ASIOSampleType.ASIOSTFloat64LSB ||
				type == ASIOSampleType.ASIOSTFloat64MSB
		)	
			return 64;
		
		else return AudioSystem.NOT_SPECIFIED;
	}
	
	/**
	 * Get the frame size out of the given ASIOSampleType
	 * @param type The ASIOSampleType constant
	 * @param channels The number of channels
	 * @return The frame size in Bits of the given ASIOSampleType
	 */
	private static int getFrameSize(final int type, final int channels){
		if(
				type == ASIOSampleType.ASIOSTInt32LSB16 ||
				type == ASIOSampleType.ASIOSTInt32LSB18 ||
				type == ASIOSampleType.ASIOSTInt32LSB20 ||
				type == ASIOSampleType.ASIOSTInt32LSB24 ||
				type == ASIOSampleType.ASIOSTInt32MSB16 ||
				type == ASIOSampleType.ASIOSTInt32MSB18 ||
				type == ASIOSampleType.ASIOSTInt32MSB20 ||
				type == ASIOSampleType.ASIOSTInt32MSB24
		)
			return 4 * channels;
		else{
			int size = getSampleSizeInBits(type);
			if(size == AudioSystem.NOT_SPECIFIED) return AudioSystem.NOT_SPECIFIED;
			else return size * channels / 8;
		}
	}
	
	/**
	 * Get the endianess out of the given ASIOSampleType
	 * @param type The ASIOSampleType constant
	 * @return The endianess of the given ASIOSampleType
	 */
	private static boolean getEndianess(final int type){
		if(
				type == ASIOSampleType.ASIOSTFloat32LSB ||
				type == ASIOSampleType.ASIOSTFloat64LSB ||
				type == ASIOSampleType.ASIOSTInt16LSB ||
				type == ASIOSampleType.ASIOSTInt24LSB ||
				type == ASIOSampleType.ASIOSTInt32LSB ||
				type == ASIOSampleType.ASIOSTInt32LSB16 ||
				type == ASIOSampleType.ASIOSTInt32LSB18 ||
				type == ASIOSampleType.ASIOSTInt32LSB20 ||
				type == ASIOSampleType.ASIOSTInt32LSB24
		)
			return false;
		else return true;
	}
	// For 1.4 compatibility...
	static class Encoding extends AudioFormat.Encoding{
		public Encoding(String type) {
			super(type);
		}
	}
}