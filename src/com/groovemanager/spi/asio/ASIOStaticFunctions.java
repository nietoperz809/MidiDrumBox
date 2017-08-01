/*
 * Created on 08.04.2004
 *
 */
package com.groovemanager.spi.asio;

import java.nio.ByteBuffer;


/**
 * This class provides wrapper methods for native ASIO functions
 * @author Manu Robledo
 */
public final class ASIOStaticFunctions {
	static{
		System.loadLibrary(ASIOMixerProvider.getLibName());
	}
	/**
	 * Wrapper for ASIOInit()
	 * @return A new ASIODriverInfo instance describing the loaded driver
	 * @throws ASIOError If ASIOInit() fails
	 */
	static ASIODriverInfo ASIOInit() throws ASIOError{
		ASIODriverInfo info = new ASIODriverInfo();
		int code = jASIOInit(info.getPointer());
		ASIOError.throwASIOError(code);
		return info;
	}
	
	/**
	 * Wrapper for ASIOExit()
	 * @throws ASIOError If ASIOExit() fails
	 */
	static void ASIOExit() throws ASIOError{
		int code = jASIOExit();
		ASIOError.throwASIOError(code);
	}
	
	/**
	 * Wrapper for ASIOStart()
	 * @throws ASIOError If ASIOStart() fails
	 */
	public static void ASIOStart() throws ASIOError{
		int code = jASIOStart();
		ASIOError.throwASIOError(code);
	}
	
	/**
	 * Wrapper for ASIOStop()
	 * @throws ASIOError If ASIOStop() fails
	 */
	static void ASIOStop() throws ASIOError{
		int code = jASIOStop();
		ASIOError.throwASIOError(code);
	}
	
	/**
	 * Wrapper for ASIOGetChannels()
	 * @return The number of input channels supported by the currently loaded
	 * driver
	 * @throws ASIOError If ASIOGetChannels() fails
	 */
	static int ASIOGetInputChannels() throws ASIOError{
		return ASIOGetChannels()[0];
	}
	
	/**
	 * Wrapper for ASIOGetChannels()
	 * @return The number of output channels supported by the currently loaded
	 * driver
	 * @throws ASIOError If ASIOGetChannels() fails
	 */
	static int ASIOGetOutputChannels() throws ASIOError{
		return ASIOGetChannels()[1];
	}
	
	/**
	 * Wrapper for ASIOGetChannels()
	 * @return An int-Array with the number of input channels at position [0]
	 * and the number of output channels at position [1]
	 * @throws ASIOError If ASIOGetChannels() fails
	 */
	static int[] ASIOGetChannels() throws ASIOError{
		int[] channels = new int[2];
		ASIOError.throwASIOError(jASIOGetChannels(channels));
		return channels;
	}
	
	/**
	 * Wrapper for ASIOGetLatencies()
	 * @return The input latency in sample frames
	 * @throws ASIOError If ASIOGetLAtencies() fails
	 */
	static int ASIOGetInputLatency() throws ASIOError{
		return ASIOGetLatencies()[0];
	}
	
	/**
	 * Wrapper for ASIOGetLatencies()
	 * @return The output latency in sample frames
	 * @throws ASIOError If ASIOGetLAtencies() fails
	 */
	static int ASIOGetOutputLatency() throws ASIOError{
		return ASIOGetLatencies()[1];
	}
	
	/**
	 * Wrapper for ASIOGetLatencies()
	 * @return An int-Array with the input latency on position [0] and
	 * the output latency on position [1] (in sample frames)
	 * @throws ASIOError If ASIOGetLAtencies() fails
	 */
	static int[] ASIOGetLatencies() throws ASIOError{
		int[] latencies = new int[2];
		ASIOError.throwASIOError(jASIOGetLatencies(latencies));
		return latencies;
	}
	
	/**
	 * Wrapper for ASIOGetBufferSize()
	 * @return The minimum buffer size supported by the currently loaded
	 * driver in sample frames
	 * @throws ASIOError If ASIOGetBufferSize() fails
	 */
	static int ASIOGetMinBufferSize() throws ASIOError{
		return ASIOGetBufferSize()[0];
	}
	
	/**
	 * Wrapper for ASIOGetBufferSize()
	 * @return The maximum buffer size supported by the currently loaded
	 * driver in sample frames
	 * @throws ASIOError If ASIOGetBufferSize() fails
	 */
	static int ASIOGetMaxBufferSize() throws ASIOError{
		return ASIOGetBufferSize()[1];
	}
	
	/**
	 * Wrapper for ASIOGetBufferSize()
	 * @return The preferred buffer size of the currently loaded
	 * driver in sample frames
	 * @throws ASIOError If ASIOGetBufferSize() fails
	 */
	static int ASIOGetPreferredBufferSize() throws ASIOError{
		return ASIOGetBufferSize()[2];
	}
	
	/**
	 * Wrapper for ASIOGetBufferSize()
	 * @return The granularity of the buffer size of the currently loaded
	 * driver in sample frames
	 * @throws ASIOError If ASIOGetBufferSize() fails
	 */
	static int ASIOGetBufferSizeGranularity() throws ASIOError{
		return ASIOGetBufferSize()[3];
	}
	
	/**
	 * Wrapper for ASIOGetBufferSize()
	 * @return An int-Array with the minimum buffer size at position [0],
	 * the maximum buffer size az position [1], the preferred buffer size
	 * at position [2] and the buffer size granularity at position [3]. 
	 * @throws ASIOError If ASIOGetBufferSize() fails
	 */
	static int[] ASIOGetBufferSize() throws ASIOError{
		int[] data = new int[4];
		ASIOError.throwASIOError(jASIOGetBufferSize(data));
		return data;
	}

	/**
	 * Wrapper for ASIOCanSampleRate()
	 * @param sampleRate The sample rate to ask the driver for
	 * @return true if the currently loaded driver supports the given rate,
	 * false otherwise
	 * @throws ASIOError If ASIOCanSampleRate() fails
	 */
	static boolean ASIOCanSampleRate(double sampleRate) throws ASIOError{
		int code = jASIOCanSampleRate(new ASIOSampleRate(sampleRate));
		if(code == ASIOError.ASE_OK) return true;
		else if(code == ASIOError.ASE_NoClock) return false;
		else ASIOError.throwASIOError(code);
		return false;
	}
	
	/**
	 * Wrapper for ASIOGetSampleRate
	 * @return The current sample rate of the currently loaded driver
	 * @throws ASIOError If ASIOGetSampleRate fails
	 */
	static double ASIOGetSampleRate() throws ASIOError{
		ASIOSampleRate rate = new ASIOSampleRate();
		int code = jASIOGetSampleRate(rate);
		ASIOError.throwASIOError(code);
		return rate.doubleValue();
	}
	
	/**
	 * Wrapper for ASIOSetSampleRate
	 * @param rate The new sample rate to set
	 * @throws ASIOError If ASIOSetSampleRate() fails
	 */
	static void ASIOSetSampleRate(double rate) throws ASIOError{
		int code = jASIOSetSampleRate(new ASIOSampleRate(rate));
		ASIOError.throwASIOError(code);
	}

	/**
	 * Wrapper for ASIOGetClockSources()
	 * @param numSources The maximum number of sources expected
	 * @return An Array of ASIOClockSource instances
	 * @throws ASIOError If ASIOGetClockSources() fails
	 */
	static ASIOClockSource[] ASIOGetClockSources(int numSources) throws ASIOError{
		long[] pointers = new long[numSources + 1];
		int code = jASIOGetClockSources(pointers);
		ASIOError.throwASIOError(code);
		ASIOClockSource[] clocks = new ASIOClockSource[(int)pointers[numSources]];
		for (int i = 0; i < clocks.length; i++) {
			clocks[i] = new ASIOClockSource(pointers[i]);
		}
		return clocks;
	}
	
	/**
	 * Wrapper for ASIOSetClockSource()
	 * @param index The index of the clock source to select
	 * @throws ASIOError If ASIOSetClockSources() fails
	 */
	static void ASIOSetClockSource(int index) throws ASIOError{
		int code = jASIOSetClockSource(index);
		ASIOError.throwASIOError(code);
	}
	
	/**
	 * Wrapper for ASIOGetChannelInfo()
	 * @param channel The number of the channel to query (zero-based)
	 * @param isInput true if querying an input channel, false otherwise
	 * @return An ASIOChannelInfo instance with infos about that channel
	 * @throws ASIOError If ASIOGetChannelinfo() fails
	 */
	static ASIOChannelInfo ASIOGetChannelinfo(int channel, boolean isInput) throws ASIOError{
		ASIOChannelInfo info = new ASIOChannelInfo();
		info.setChannel(channel);
		info.setIsInput(isInput);
		int code = jASIOGetChannelInfo(info.getPointer());
		ASIOError.throwASIOError(code);
		return info;
	}
	
	/**
	 * Wrapper for ASIOCreateBuffers()
	 * @param infos ASIOBufferInfo-Array with information of the channels to
	 * prepare the buffers for
	 * @param bufferSize The buffersize to use
	 * @return An ASIOBufferInfo-Array with the result information
	 * @throws ASIOError If ASIOCreateBuffer() fails
	 */
	static ASIOBufferInfo[] ASIOCreateBuffers(ASIOBufferInfo[] infos, int bufferSize) throws ASIOError{
		long[] infopointer = new long[infos.length];
		for (int i = 0; i < infos.length; i++) {
			infopointer[i] = infos[i].getPointer();
		}
		
		int code = jASIOCreateBuffers(infopointer, bufferSize);
		ASIOError.throwASIOError(code);
		
		for (int i = 0; i < infos.length; i++) {
			infos[i] = new ASIOBufferInfo(infopointer[i]);
		}
		
		return infos;
	}
	
	/**
	 * Wrapper for ASIODisposeBuffers() 
	 * @throws ASIOError If ASIODisposeBuffers() fails
	 */
	static void ASIODisposeBuffers() throws ASIOError{
		int code = jASIODisposeBuffers();
		ASIOError.throwASIOError(code);
	}
	
	/**
	 * Wrapper for ASIOOutputReady()
	 * @throws ASIOError If ASIOOutputReady() fails
	 */
	static void ASIOOutputReady() throws ASIOError{
		int code = jASIOOutputReady();
		ASIOError.throwASIOError(code);
	}
	
	/**
	 * Wrapper for ASIOControlPanel()
	 * @throws ASIOError If ASIOControlPanel() fails
	 */
	public static void ASIOControlPanel() throws ASIOError{
		int code = jASIOControlPanel();
		ASIOError.throwASIOError(code);
	}
	
	/**
	 * Wrapper for ASIOFuture()
	 * @param selector The selector
	 * @param pointer The address of the "void* param" parameter
	 * @throws ASIOError If ASIOFuture() fails
	 */
	static void ASIOFuture(int selector, long pointer) throws ASIOError{
		int code = jASIOFuture(selector, pointer);
		ASIOError.throwASIOError(code);
	}
	
	/**
	 * Enable the time code reader if the hardware device supports time code.
	 * See ASIO SDK...
	 * 
	 * @throws ASIOError If the hardware doesn�t support this feature
	 */
	public static void enableTimeCodeReader() throws ASIOError{
		ASIOFuture((int)kAsioEnableTimeCodeRead(), 0);
	}
	
	/**
	 * Disable the time code reader if the hardware device supports time code.
	 * See ASIO SDK...
	 * 
	 * @throws ASIOError If the hardware doesn�t support this feature
	 */
	public static void disableTimeCodeReader() throws ASIOError{
		ASIOFuture((int)kAsioDisableTimeCodeRead(), 0);
	}
	
	// native methods
	private static native int jASIOInit(long pointer);
	private static native int jASIOExit();
	private static native int jASIOStart();
	private static native int jASIOStop();
	private static native int jASIOGetChannels(int[] channels);
	private static native int jASIOGetLatencies(int[] latencies);
	private static native int jASIOGetBufferSize(int[] data);
	private static native int jASIOCanSampleRate(ASIOSampleRate rate);
	private static native int jASIOGetSampleRate(ASIOSampleRate sampleRate); 
	private static native int jASIOSetSampleRate(ASIOSampleRate sampleRate);
	private static native int jASIOGetClockSources(long[] clockPointers);
	private static native int jASIOSetClockSource(int index);
	private static native int jASIOGetChannelInfo(long pointer);
	private static native int jASIOCreateBuffers(long[] infos, int bufferSize);
	private static native int jASIODisposeBuffers();
	private static native int jASIOOutputReady();
	private static native int jASIOControlPanel();
	private static native int jASIOFuture(int selector, long pointer);
	@SuppressWarnings("unused")
	private static native long kAsioEnableTimeCodeRead();
	@SuppressWarnings("unused")
	private static native long kAsioDisableTimeCodeRead();
	@SuppressWarnings("unused")
	private static native long kAsioSetInputMonitor();
	//private static native double jgetSystemTime();
	private static native ByteBuffer createBuffer(long infoPointer, int index, int capacity);
	
	/**
	 * Get the system time reference
	 * @return The system reference time in nano seconds
	 */
	static long getSystemTime(){
		//return (long)(jgetSystemTime() * 1000);
		return (System.currentTimeMillis() % 1000) * 1000;
	}
	
	
	/**
	 * Create two ByteBuffer objects from the buffer half addresses specified
	 * in the ASIOBufferInfo 
	 * @param info The ASIOBufferInfo gotten from ASIOCreateBuffers()
	 * @param asiobuffersize The size of each half buffer in bytes
	 * @return An Array with the two direct ByteBuffers
	 */
	static ByteBuffer[] createBuffers(ASIOBufferInfo info, int asiobuffersize){
		return new ByteBuffer[]{createBuffer(info.getPointer(), 0, asiobuffersize), createBuffer(info.getPointer(), 1, asiobuffersize)};
	}
	
	/**
	 * ASIO message selector for asioMessage() 
	 */
	private static native long kAsioSelectorSupported();
	final static long MESSAGE_SELECTOR_kAsioSelectorSupported = kAsioSelectorSupported();
	private static native long kAsioEngineVersion();
	final static long MESSAGE_SELECTOR_kAsioEngineVersion = kAsioEngineVersion();
	private static native long kAsioResetRequest();
	final static long MESSAGE_SELECTOR_kAsioResetRequest = kAsioResetRequest();
	private static native long kAsioBufferSizeChange();
	final static long MESSAGE_SELECTOR_kAsioBufferSizeChange = kAsioBufferSizeChange();
	private static native long kAsioResyncRequest();
	final static long MESSAGE_SELECTOR_kAsioResyncRequest = kAsioResyncRequest();
	private static native long kAsioLatenciesChanged();
	final static long MESSAGE_SELECTOR_kAsioLatenciesChanged = kAsioLatenciesChanged();
	private static native long kAsioSupportsTimeInfo();
	final static long MESSAGE_SELECTOR_kAsioSupportsTimeInfo = kAsioSupportsTimeInfo();
	private static native long kAsioSupportsTimeCode();
	final static long MESSAGE_SELECTOR_kAsioSupportsTimeCode = kAsioSupportsTimeCode();
}