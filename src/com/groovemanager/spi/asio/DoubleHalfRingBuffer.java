/*
 * Created on 29.06.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * A RingBuffer to be used with DoubleHalfBuffers.
 * @author Manu Robledo
 *
 */
class DoubleHalfRingBuffer {
	/**
	 * When read and write position are the same, this value indicates, whether
	 * the buffer is filled completely (false) or emptied completely (true)
	 */
	private boolean writeAhead = false;
	/**
	 * Indicates whether this buffer is in open state or not
	 */
	private boolean open;
	/**
	 * The granularity in which data must be read or written. Data will only be
	 * available in blocks of this value.
	 */
	final private int granularity;
	/**
	 * A view to the source buffer used for reading
	 */
	final private DoubleHalfBuffer readBuffer,
	/**
	 * A view to the source buffer used for writing
	 */
	writeBuffer;
	/**
	 * Construct a new DoubleHalfRingBuffer out of the given buffer
	 * @param buffer The buffer to be used as source for this ring buffer
	 * @param granularity The buffer granularity in bytes
	 */
	public DoubleHalfRingBuffer(DoubleHalfBuffer buffer, int granularity) {
		this.granularity = granularity;
		if(buffer.remaining() % granularity > 0) throw new IllegalArgumentException("Granularity doens´t match buffer capacity.");
		readBuffer = buffer.slice();
		writeBuffer = buffer.slice();
	}
	/**
	 * Get the number of bytes currently available for reading
	 * @return The number of bytes avilable for reading
	 */
	public synchronized int readAvailable(){
		if(!isOpen()) return 0;
		int readPos = readBuffer.position();
		int writePos = writeBuffer.position();
		if(readPos < writePos) return writePos - readPos;
		else if(readPos > writePos || writeAhead) return readBuffer.remaining() + writePos;
		else return 0;
	}
	/**
	 * Get the number of bytes currently available for writing
	 * @return The number of bytes available for writing
	 */
	public synchronized int writeAvailable(){
		int readPos = readBuffer.position();
		int writePos = writeBuffer.position();
		if(writePos < readPos) return readPos - writePos;
		else if(writePos > readPos || !writeAhead) return writeBuffer.remaining() + readPos;
		else return 0;
	}
	/**
	 * Try to read <code>len</code> bytes from this buffer starting at the
	 * current position into <code>b</code> starting at position
	 * <code>off</code>. The currently avilable bytes will be read the number
	 * of bytes read will be returned.
	 * @param b The byte array to transfer the data to
	 * @param off The offset inside the target array
	 * @param len The number of bytes to read
	 * @return The number of bytes read
	 */
	public int read(byte[] b, int off, int len){
		return read(b, off, len, false);
	}
	/**
	 * Try to read <code>len</code> bytes from this buffer starting at the
	 * current position into <code>b</code> starting at position
	 * <code>off</code>. If <code>overwrite</code> is true, the bytes will be
	 * read independent of the number of available bytes for reading. If this
	 * read operation exceeds the current write position (buffer underrun), the
	 * write position will be discarded and set to the end of the read data.
	 * @param b The byte array to transfer the data to
	 * @param off The offset inside the target array
	 * @param len The number of bytes to read
	 * @param overwrite true, if the given number of bytes should be read even
	 * if this causes a buffer underrun
	 * @return The number of bytes read 
	 */
	public int read(byte[] b, int off, int len, boolean overwrite){
		if(!isOpen()) return 0;
		len -= len % granularity;
		if(off + len > b.length) throw new IllegalArgumentException("Can´t read " + len + " bytes beginning at offset " + off + " into Array of length " + b.length);
		synchronized(this){
			int readPos = readBuffer.position();
			if(!overwrite) len = Math.min(len, readAvailable());
			else len = Math.min(len, size());
			if(len == 0) return 0;
			
			int available = readBuffer.remaining();
			if(available < len){
				readBuffer.get(b, off, available);
				readBuffer.rewind();
				readBuffer.get(b, off + available, len - available);
				if(readBuffer.position() == readBuffer.limit()) readBuffer.rewind();
			}
			else{
				readBuffer.get(b, off, len);
				if(readBuffer.position() == readBuffer.limit()) readBuffer.rewind();
			}
			if(overwrite){
				if(len == size() ||
					readBuffer.position() == writeBuffer.position() ||
					(readPos < writeBuffer.position() && readPos + len > writeBuffer.position()) ||
					(readPos > writeBuffer.position() && readPos + len - size() >= writeBuffer.position()) ||
					(readPos == writeBuffer.position() && !writeAhead)
				){
					writeBuffer.position(readBuffer.position());
					writeAhead = false;
				}
			}
			else{
				if(readBuffer.position() == writeBuffer.position()) writeAhead = false;
			}
			notify();
		}
		return len;
	}
	/**
	 * Try to write <code>len</code> bytes from <code>b</code> starting at
	 * index <code>off</code> into this buffer starting at the current position.
	 * This method will not return before the given number of bytes has been
	 * written or the buffer has been closed. This means that if the given
	 * number of bytes can not be written at once, this methods waits until
	 * another thread empties the needed part of this buffer by reading from it.
	 * @param b The array containing the data to write
	 * @param off Start position inside the given array
	 * @param len The number of bytes to write
	 * @return The number of bytes written
	 */
	public int write(byte[] b, int off, int len){
		return write(b, off, len, false);
	}
	/**
	 * Try to write <code>len</code> bytes from <code>b</code> starting at
	 * index <code>off</code> into this buffer starting at the current position.
	 * This method will not return before the given number of bytes has been
	 * written or the buffer has been closed. If <code>overwrite</code> is set
	 * to true, this method will write the data immediately independent of the
	 * number of bytes available for writing. If the read position is exceeded
	 * by this operation (buffer overflow), the read position will be set after
	 * the last written data.
	 * @param b The array containing the data to write
	 * @param off Start position inside the given array
	 * @param len The number of bytes to write
	 * @param overwrite true, if the given number of bytes should be written
	 * even if this causes a buffer overflow
	 * @return The number of bytes written
	 */
	public int write(byte[] b, int off, int len, boolean overwrite){
		len -= len % granularity;
		if(off + len > b.length) throw new IllegalArgumentException("Can´t write " + len + " bytes beginning at offset " + off + " from Array of length " + b.length);
		int written = 0;
		synchronized(this){
			while(len > written && isOpen()){
				while(writeAvailable() == 0 && !overwrite) try{ wait(1000); } catch(InterruptedException e){}
				int toWrite;
				int writePos = writeBuffer.position();
				if(overwrite) toWrite = Math.min(len, size());
				else toWrite = Math.min(len - written, writeAvailable());
				int available = writeBuffer.remaining();
				if(available <= toWrite){
					writeBuffer.put(b, off + written, available);
					written += available;
					writeBuffer.rewind();
					writeBuffer.put(b, off + written, toWrite - available);
					written += toWrite - available;
					if(writeBuffer.position() == writeBuffer.limit()) writeBuffer.rewind();
				}
				else{
					writeBuffer.put(b, off + written, toWrite);
					written += toWrite;
					if(writeBuffer.position() == writeBuffer.limit()) writeBuffer.rewind();
				}
				if(toWrite > 0){
					if(overwrite){
						if(toWrite == size() ||
							(writePos < readBuffer.position() && writePos + toWrite > readBuffer.position()) ||
							(writePos > readBuffer.position() && writePos + toWrite - size() >= readBuffer.position()) ||
							(writePos == readBuffer.position() && writeAhead)
						){
							readBuffer.position(writeBuffer.position());
						}
					}
					writeAhead = true;
				}
			}
			if(len > 0 && !isOpen()){
				len = Math.min(len, writeAvailable());
				int available = writeBuffer.remaining();
				if(available <= len){
					writeBuffer.put(b, off + written, available);
					written += available;
					writeBuffer.rewind();
					writeBuffer.put(b, off + written, len - available);
					written += len - available;
				}
				else{
					writeBuffer.put(b, off + written, len);
					written += len;
				}
				writeAhead = true;
			}
		}
		return written;
	}
	/**
	 * Open this buffer
	 *
	 */
	public void open(){
		open = true;
	}
	/**
	 * Tells whether this buffer is currently open or not
	 * @return true, if this buffer is open, false otherwise
	 */
	public boolean isOpen(){
		return open;
	}
	/**
	 * Close this buffer
	 *
	 */
	public void close(){
		open = false;
	}
	/**
	 * Get this buffer´s total size
	 * @return This buffer´s total size in bytes 
	 */
	public int size(){
		return readBuffer.capacity();
	}
	/**
	 * Empty this buffer
	 *
	 */
	public synchronized void flush(){
		writeBuffer.rewind();
		readBuffer.rewind();
		writeAhead = false;
	}
	/**
	 * Rewind this buffer
	 *
	 */
	public synchronized void rewind(){
		readBuffer.rewind();
		writeBuffer.rewind();
		writeAhead = false;
	}
	/**
	 * Set the read and write position of this Buffer
	 * @param readPos The new read position
	 * @param writePos The new write position
	 * @param writeAhead Only needed, if <code>readPos == writePos</code>:<br>
	 * true, if the writePos is ahead of the readPos and the buffer is therefore
	 * filled, false if the buffer is empty. 
	 */
	public synchronized void setPositions(int readPos, int writePos, boolean writeAhead){
		writeBuffer.position(writePos);
		readBuffer.position(readPos);
		if(writePos == readPos) this.writeAhead = writeAhead;
		else writeAhead = true;
	}
}
