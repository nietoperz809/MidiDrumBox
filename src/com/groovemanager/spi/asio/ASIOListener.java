/*
 * Created on 05.06.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * ASIO listeners can be added to ASIOMixer instances or generally to
 * an ASIOMixerProvider instance for being notified of some events
 * the ASIO driver may generate.
 * @author Manu Robledo
 */
public interface ASIOListener {
	/**
	 * Indicates that the sample rate has changed. May be because of
	 * external sync or because of opening an ASIODataLine with a new
	 * AudioFormat
	 * @param mixer The ASIOMixer representing the ASIO driver
	 * @param newRate The new sample rate
	 */
	public void sampleRateChanged (ASIOMixer mixer, double newRate);
	/**
	 * Is called when the driver needs a reset. It will automatically be
	 * closed.
	 * @param mixer The ASIOMixer representing the ASIO driver
	 */
	public void resetRequest (ASIOMixer mixer);
	/**
	 * Ask if this ASIOListener will react to the bufferSizeChanged()
	 * call. If it ignores calls to bufferSizeChanged(), false should
	 * be returned. 
	 * @return true if this listener may react to calls to bufferSizeChanged(),
	 * false otherwise
	 */
	public boolean supportsBufferSizeChange ();
	/**
	 * Indicates that the preferred buffer size of the driver has changed.
	 * @param mixer The ASIOMixer representing the ASIO driver
	 * @param newSize The new buffersize in sample frames. Please note that this
	 * value means a size in frames, not a number of bytes. 
	 * @return true if this listener has accepted the message, false otherwise.
	 */
	public boolean bufferSizeChanged (ASIOMixer mixer, int newSize);
	/**
	 * Ask if this ASIOListener will react to the resyncRequest()
	 * call. If it ignores calls to resyncRequest(), false should
	 * be returned. 
	 * @return true if this listener may react to calls to resyncRequest(),
	 * false otherwise
	 */
	public boolean supportsResyncRequest ();
	/**
	 * Indicates that the driver has lost its sync because of some reason
	 * and needs resyncing (usually stop and restart)
	 * @param mixer The ASIOMixer representing the ASIO driver
	 * @return true if this listener has accepted the message, false otherwise.
	 */
	public boolean resyncRequest (ASIOMixer mixer);
	/**
	 * Ask if this ASIOListener will react to the latenciesChanged()
	 * call. If it ignores calls to latenciesChanged(), false should
	 * be returned. 
	 * @return true if this listener may react to calls to latenciesChanged(),
	 * false otherwise
	 */
	public boolean supportsLatenciesChanged ();
	/**
	 * Indicates that the input - and/or output-latencies have changed.
	 * The latency values can be queried by ASIOMixer.getInputLatency() and
	 * ASIOMixer.getOutputLatency()
	 * @param mixer The ASIOMixer representing the ASIO driver
	 * @return true if this listener has accepted the message, false otherwise.
	 */
	public boolean latenciesChanged (ASIOMixer mixer);
}