/*
 * Created on 22.04.2004
 *
 */
package com.groovemanager.spi.asio;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Control.Type;


/**
 * This class implements SourceDataLine as well as TargetDataLine
 * because input and output processing is similar in ASIO. All lines
 * provided by the ASIOMixer will be instances of this class.
 * @author Manu Robledo
 */
public class ASIODataLine implements SourceDataLine, TargetDataLine {
	/**
	 * A RingBuffer for each channel wrapped around the two buffer halfs
	 */
	DoubleHalfRingBuffer[] ringBuffers;
	/**
	 * The real asioBuffers. They are not used by the line itself,
	 * but by the ASIOMixer. It copies the content from/to the
	 * internalBuffers when bufferSwitchtimeInfo() is called
	 */
	ByteBuffer[][] asioBuffers;
	/**
	 * List of the registered LineListeners
	 */
	private ArrayList<LineListener> listeners = new ArrayList<LineListener>();
	/**
	 * The ASIOMixer instance which created this line
	 */
	private ASIOMixer mixer;
	/**
	 * THE ASIOLineInfo object corresponding to this line
	 */
	private ASIOLineInfo info;
	/**
	 * The buffersize in bytes specified to open(AudioFormat, int)
	 */
	private int desiredBufferSize = AudioSystem.NOT_SPECIFIED;
	/**
	 * Indicates if this line is open
	 */
	private boolean open;
	/**
	 * The format of this line when its running
	 */
	private AudioFormat format;
	/**
	 * Number of channels associated with this line right now
	 */
	int channels = 1;
	/**
	 * Sequence number used when opening this line. This number should
	 * be used for ASIOMixer.ASIOExit(long seq)
	 */
	private long openSeq = 0;
	/**
	 * frameSize of the current format
	 */
	private int frameSize;
	/**
	 * sample size of the current format
	 */
	private int sampleSize;
	/**
	 * indicates whether this line has been started or not
	 */
	private boolean started = false;
	/**
	 * indicates whether flus() has been called lately on this line
	 */
	private boolean flushed = false;
	/**
	 * the Controls for this line
	 */
	private Control[] controls;
	/**
	 * an empty array for flushing
	 */
	private byte[] emptyArray;
	
	static{
		System.loadLibrary(ASIOMixerProvider.getLibName());
	}
	
	/**
	 * Get the ASIOMixer to which this Line belongs
	 * @return
	 * 		The ASIOMixer to which this Line belongs
	 */
	ASIOMixer getMixer(){
		return mixer;
	}
	
	ASIODataLine(ASIOMixer mixer, ASIOLineInfo info){
		this.mixer = mixer;
		this.info = info;
	}

	public void drain() {
		if(isInput() || !isOpen()) return;
		while(available() < getBufferSize())
			try {
				Thread.sleep(0, 1);
			} catch (InterruptedException e) {
			}
	}

	public void flush() {
		if(!isOpen()) return;
		
		//int length = internalBuffers[0][0].capacity();
		int length = ringBuffers[0].size();
		if(emptyArray == null || emptyArray.length < length) emptyArray = new byte[length];
		synchronized(mixer){
			for(int i = 0; i < channels; i++){
				ringBuffers[0].rewind();
				ringBuffers[0].write(emptyArray, 0, length);
				ringBuffers[0].rewind();
				/*
				internalBuffers[i][0].rewind();
				internalBuffers[i][0].put(emptyArray, 0, length);
				internalBuffers[i][0].rewind();
				internalBuffers[i][1].rewind();
				internalBuffers[i][1].put(emptyArray, 0, length);
				internalBuffers[i][1].rewind();
				*/
			}
			flushed = true;
		}
	}
	
	public void start() {
		if(isActive()) return;
		mixer.startLine(this);
	}
	
	public void stop() {
		if(!isActive()) return;
		mixer.stopLine(this);
	}

	public boolean isRunning() {
		return isActive() && !flushed;
	}

	public boolean isActive() {
		return open && mixer.getStatus() == ASIOMixer.RUNNING && started;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public int getBufferSize() {
		//return internalBuffers[0][0].capacity() * channels;
		return ringBuffers[0].size() * channels;
	}

	public int available() {
		//return internalBuffers[0][mixer.bufferIndex].remaining() * channels;
		if(!isOpen()) return 0;
		if(isInput()) return ringBuffers[0].readAvailable() * channels;
		else return ringBuffers[0].writeAvailable() * channels;
	}

	public int getFramePosition() {
		return (int)mixer.getSamplePosition();
	}

	public long getMicrosecondPosition() {
		return mixer.getMicrosecondPosition();
	}

	/**
	 * ASIO doesn't support this. <code>AudioSystem.NOT_SPECIFIED</code> will be returned.
	 * @see javax.sound.sampled.DataLine#getLevel()
	 */
	public float getLevel() {
		return AudioSystem.NOT_SPECIFIED;
	}

	public Line.Info getLineInfo() {
		return info;
	}

	/**
	 * If no arguments are specified to <code>open()</code>, the preferred buffer
	 * size will be used and the Format will be Mono with the preferred Sample Rate
	 * @see Line#open()
	 */
	public void open() throws LineUnavailableException {
		if(open) return;
		float rate;
		if(format == null)
			rate = (float)mixer.getSampleRate();
		else 
			rate = format.getSampleRate();
		format = new ASIOAudioFormat(rate, info.getChannelInfo().type(), channels);
		if(desiredBufferSize > 0) mixer.openLine(this, desiredBufferSize / format.getFrameSize());
		else mixer.openLine(this);
	}

	public void close() {
		if(!open) return;
		mixer.closeLine(this, openSeq);
	}

	public boolean isOpen() {
		return open;
	}

	/**
	 * Will return a Clock Source Control for Selection of the Clock Source
	 * @see Line#getControls()
	 */
	public Control[] getControls() {
		if(controls != null) return controls;
		long seq = 0;
		try {
			seq = mixer.ASIOInit();
			ASIOClockSource[] sources = ASIOStaticFunctions.ASIOGetClockSources(15);
			mixer.ASIOExit(seq);
			if(sources.length > 0){
				controls = new Control[]{new ASIOClockSourceControl(this, sources)};
				return controls;
			}
		} catch (ASIOError e) {
			e.printStackTrace();
			mixer.ASIOExit(seq);
		}
		return new Control[]{new ASIOClockSourceControl(this)};
	}

	/**
	 * Only a Clock Source Control for Selection of the Clock Source is supported
	 * @see Line#isControlSupported(Type)
	 */
	public boolean isControlSupported(Type control) {
		return control == ASIOClockSourceControl.Type.CLOCK_SOURCE;
	}

	/**
	 * Will return a Clock Source Control for Selection of the Clock Source
	 * @see Line#getControl(Type)
	 */
	public Control getControl(Type control) {
		if(control == ASIOClockSourceControl.Type.CLOCK_SOURCE){
			Control[] c = getControls();
			return c[0];
		}
		return null;
	}

	public void addLineListener(LineListener listener) {
		listeners.add(listener);
	}

	public void removeLineListener(LineListener listener) {
		listeners.remove(listener);
	}

	public void open(AudioFormat format, int buffersize) throws LineUnavailableException {
		if(open) return;
		if(ASIOMixerProvider.isFullCheck())
			if(!info.isFormatSupported(format)) 
				throw new LineUnavailableException("Audio Format not supported.");
		
		this.format = format;
		channels = format.getChannels();
		desiredBufferSize = buffersize;
		open();
	}

	public void open(AudioFormat format) throws LineUnavailableException {
		if(open) return;
		if(ASIOMixerProvider.isFullCheck())
			if(!info.isFormatSupported(format)) 
				throw new LineUnavailableException("Audio Format not supported.");
		this.format = format;
		channels = format.getChannels();
		open();
	}
	
	public int read(byte[] b, int off, int len){
		// when reading starts, the last flush call is forgotten
		flushed = false;
		synchronized(mixer){
			// Don't read more than available...
			len = Math.min(len, available());
			len -= len % frameSize;
			
			// Mono reading is more efficient
			if(channels == 1){
				//internalBuffers[0][mixer.bufferIndex].get(b, off, len);
				ringBuffers[0].read(b, off, len);
			}
			else{
				// Multiplex
				int frames = len / frameSize;
				for(int i = 0; i < frames; i++){
					for(int j = 0; j < channels; j++){
						//internalBuffers[j][mixer.bufferIndex].get(b, off + i * frameSize + j * sampleSize, sampleSize);
						ringBuffers[0].read(b, off + i * frameSize + j * sampleSize, sampleSize);
					}
				}
			}
		}
		return len;
	}
	
	public int write(byte[] b, int off, int len){
		if(len % frameSize > 0) throw new IllegalArgumentException(len + " does not represent an integral number of sample frames.");
		flushed = false;
		int written = 0;
		// As long as we didn´t write all the data...
		while(len > 0){
			// Wait until there is some space inside the buffer
			while(available() == 0 && isRunning()){
				try { Thread.sleep(0, 1); }  catch (InterruptedException e) {}
			}
			// alyways look, if this method should return because of
			// a call to close(), stop() or flush()
			if(!isRunning()) return written;
				
			synchronized(mixer){
				int toWrite = Math.min(len, available());
				// Mono is more efficient
				if(channels == 1){
					//internalBuffers[0][mixer.bufferIndex].put(b, off, toWrite);
					ringBuffers[0].write(b, off, toWrite);
					written += toWrite; 
				}
				else{
					// Demultiplex
					int frames = toWrite / frameSize;
					for(int i = 0; i < frames; i++){
						for(int j = 0; j < channels; j++){
							//internalBuffers[j][mixer.bufferIndex].put(b, off + written, sampleSize);
							ringBuffers[j].write(b, off + written, sampleSize);
							written += sampleSize;
						}
					}
				}
				len -= toWrite;
			}
		}
		return written;
	}
	
	/**
	 * Get the channel index of the first channel that belongs to this line
	 * @return The zero-based index of the lowest channel that belongs to
	 * this line 
	 */
	int getChannel(){
		return info.getChannelInfo().channel();
	}

	/**
	 * Tells if this Line is used as In- or Output
	 * @return
	 * 		true if this Line is used as Input
	 * 		false otherwise
	 */
	public boolean isInput() {
		return info.getChannelInfo().isInput();
	}

	/**
	 * called from the Mixer to indicate that this line has been opened
	 * @param seq The sequence number that should be used for closing
	 * @param buffers The asioBuffers
	 */
	void opened(long seq, ByteBuffer[][] buffers) {
		openSeq = seq;
		frameSize = format.getFrameSize();
		sampleSize = frameSize / channels;
		
		// Store the asioBuffers with this line and create internal ones too
		asioBuffers = buffers;
		ByteBuffer[][] internalBuffers = new ByteBuffer[channels][2];
		ringBuffers = new DoubleHalfRingBuffer[channels];
		int bsize = buffers[0][0].capacity();
		for(int i = 0; i < channels; i++){
			internalBuffers[i][0] = ByteBuffer.allocateDirect(bsize);
			internalBuffers[i][1] = ByteBuffer.allocateDirect(bsize);
			ringBuffers[i] = new DoubleHalfRingBuffer(new DoubleHalfBuffer(internalBuffers[i][0], internalBuffers[i][1]), format.getFrameSize() / format.getChannels());
			ringBuffers[i].open();
		}
		
		open = true;
		notifyListeners(new LineEvent(this, LineEvent.Type.OPEN, getFramePosition()));
	}

	/**
	 * called from the Mixer to indicate that this line has been started
	 */
	void started() {
		started = true;
		flushed = false;
		notifyListeners(new LineEvent(this, LineEvent.Type.START, getFramePosition()));
	}
	
	/**
	 * Notify the line listeners
	 * @param e The LineEvent
	 */
	private void notifyListeners(LineEvent e){
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			LineListener listener = (LineListener)iter.next();
			listener.update(e);
		}
	}
	
	/**
	 * called from the Mixer to indicate that this line has been stopped
	 */
	void stopped(){
		if(!started) return;
		started = false;
		notifyListeners(new LineEvent(this, LineEvent.Type.STOP, getFramePosition()));
	}
	
	/**
	 * called from the Mixer to indicate that this line has been closed
	 */
	void closed(){
		if(!open) return;
		stopped();
		desiredBufferSize = AudioSystem.NOT_SPECIFIED;
		channels = 1;
		//internalBuffers = null;
		ringBuffers = null;
		open = false;
		notifyListeners(new LineEvent(this, LineEvent.Type.CLOSE, getFramePosition()));
	}
	
	public void finalize(){
		started = false;
		open = false;
	}

	public long getLongFramePosition() {
		return mixer.getSamplePosition();
	}
}