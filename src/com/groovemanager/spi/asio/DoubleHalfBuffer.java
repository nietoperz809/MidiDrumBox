/*
 * Created on 29.06.2004
 *
 */
package com.groovemanager.spi.asio;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * This class can be used to concatenate two ByteBuffers into one. Since
 * java.nio.ByteBuffer cannot be subclassed from outside the package, this
 * class copies the most important methods from ByteBuffer.
 * @author Manu Robledo
 * @see ByteBuffer
 *
 */
class DoubleHalfBuffer {
	/**
	 * The first buffer
	 */
	private final ByteBuffer firstBuffer,
	/**
	 * The second buffer
	 */
	secondBuffer;
	/**
	 * Indicates whether this buffer is readOnly or not
	 */
	private boolean readOnly;
	/**
	 * This buffer�s current limit 
	 */
	private int limit,
	/**
	 * This buffer�s current position
	 */
	position;
	/**
	 * This buffer�s capacity
	 */
	private final int capacity;
	/**
	 * The first buffer�s limit
	 */
	private final int firstLimit;
	/**
	 * Create a new DoubleHalfBuffer out of the given buffers
	 * @param first The first buffer
	 * @param second The second buffer
	 * @param readOnly true, if the resulting buffer should be read only,
	 * false otherwise 
	 */
	public DoubleHalfBuffer(ByteBuffer first, ByteBuffer second, boolean readOnly) {
		firstBuffer = first;
		secondBuffer = second;
		this.readOnly = readOnly;
		firstLimit = firstBuffer.limit();
		limit = capacity = firstLimit + secondBuffer.limit();
		position = 0;
	}
	/**
	 * Create a new DoubleHalfBuffer out of the given buffers that will be read
	 * only if, and only if at least one of the given buffers is read only 
	 * @param first The first buffer
	 * @param second The second buffer
	 */
	public DoubleHalfBuffer(ByteBuffer first, ByteBuffer second){
		this(first, second, first.isReadOnly() || second.isReadOnly());
	}
	/**
	 * Creates a new, read-only buffer that shares this buffer's content. 
	 * @return The new buffer
	 * @see ByteBuffer#asReadOnlyBuffer()
	 */
	public DoubleHalfBuffer asReadOnlyBuffer(){
		return new DoubleHalfBuffer(firstBuffer, secondBuffer, true);
	}
	/**
	 * Creates a new buffer that shares this buffer's content. 
	 * @return The new buffer
	 * @see ByteBuffer#duplicate()
	 */
	public DoubleHalfBuffer duplicate(){
		return new DoubleHalfBuffer(firstBuffer, secondBuffer, readOnly);
	}
	/**
	 * Absolute get method. Reads the byte at the given position.
	 * @param position The position fromo which the byte will be read
	 * @return The byte at the given position
	 * @see ByteBuffer#get(int)
	 */
	public byte get(int position){
		if(position >= limit) throw new BufferUnderflowException();
		if(position < firstLimit) return firstBuffer.get(position);
		else return secondBuffer.get(position - firstLimit);
	}
	/**
	 * Relative get method. Reads the byte at this buffer's current position,
	 * and then increments the position. 
	 * @return The byte at the buffer's current position
	 * @see ByteBuffer#get()
	 */
	public byte get(){
		byte b = get(position);
		position++;
		return b;
	}
	/**
	 * Relative bulk get method. This method transfers bytes from this buffer
	 * into the given destination array. An invocation of this method of the
	 * form <code>src.get(a)</code> behaves in exactly the same way as the
	 * invocation of <code>src.get(a, 0, a.length)</code>.
	 * @param dst The destination array to transfer the data to
	 * @return This buffer
	 */
	public DoubleHalfBuffer get(byte[] dst){
		return get(dst, 0, dst.length);
	}
	/**
	 * Relative bulk get method. This method transfers <code>length</code> bytes
	 * from this buffer into the given destination array starting at
	 * <code>offset</code>.
	 * @param dst The destination array to transfer the data to
	 * @param offset The offset within the array of the first byte to be
	 * written; must be non-negative and no larger than dst.length
	 * @return This buffer
	 */
	public DoubleHalfBuffer get(byte[] dst, int offset, int length){
		if(remaining() < length) throw new BufferUnderflowException();
		if(offset < 0 || length < 0 || offset + length > dst.length) throw new IndexOutOfBoundsException();
		if(position >= firstLimit){
			secondBuffer.position(position - firstLimit);
			secondBuffer.get(dst, offset, length);
			secondBuffer.rewind();
		}
		else if(position + length <= firstLimit){
			firstBuffer.position(position);
			firstBuffer.get(dst, offset, length);
			firstBuffer.rewind();
		}
		else{
			firstBuffer.position(position);
			firstBuffer.get(dst, offset, firstLimit - position);
			firstBuffer.rewind();
			
			secondBuffer.rewind();
			secondBuffer.get(dst, offset + firstLimit - position, length - firstLimit + position);
			secondBuffer.rewind();
		}
		position += length;
		return this;
	}
	/**
	 * Absolute put method. Writes the given byte into this buffer at the given
	 * position.
	 * @param position The position at which the byte will be written
	 * @param value The byte value to be written
	 * @return This buffer
	 */
	public DoubleHalfBuffer put(int position, byte value){
		if(readOnly) throw new ReadOnlyBufferException();
		if(position >= limit) throw new BufferOverflowException();
		if(position < firstLimit) firstBuffer.put(position, value);
		else secondBuffer.put(position - firstLimit, value);
		return this;
	}
	/**
	 * Relative put method. Writes the given byte into this buffer at the
	 * current position, and then increments the position. 
	 * @param value The byte to be written
	 * @return This buffer
	 */
	public DoubleHalfBuffer put(byte value){
		put(position, value);
		position++;
		return this;
	}
	/**
	 * Relative bulk put method. 
	 * This method transfers the entire content of the given source
	 * byte array into this buffer. An invocation of this method of the form
	 * <code>dst.put(a)</code> behaves in exactly the same way as the invocation
	 * <code>dst.put(a, 0, a.length)</code>.
	 * @param src The array to copy the data from 
	 * @return This buffer
	 */
	public DoubleHalfBuffer put(byte[] src){
		return put(src, 0, src.length);
	}
	/**
	 * Relative bulk put method. This method transfers <code>length</code> bytes
	 * from the given array starting at <code>offset</code> to this buffer 
	 * starting at the current position.
	 * @param src The array to copy the data from 
	 * @param offset Start position inside the given array
	 * @param length Number of bytes to put.
	 * @return This buffer
	 */
	public DoubleHalfBuffer put(byte[] src, int offset, int length){
		if(readOnly) throw new ReadOnlyBufferException();
		if(remaining() < length) throw new BufferOverflowException();
		if(offset < 0 || length < 0 || offset + length > src.length) throw new IndexOutOfBoundsException();
		if(position >= firstLimit){
			secondBuffer.position(position - firstLimit);
			secondBuffer.put(src, offset, length);
			secondBuffer.rewind();
		}
		else if(position + length <= firstLimit){
			firstBuffer.position(position);
			firstBuffer.put(src, offset, length);
			firstBuffer.rewind();
		}
		else{
			firstBuffer.position(position);
			firstBuffer.put(src, offset, firstLimit - position);
			firstBuffer.rewind();
			
			secondBuffer.rewind();
			secondBuffer.put(src, offset + firstLimit - position, length - firstLimit + position);
			secondBuffer.rewind();
		}
		position += length;
		return this;
	}
	/**
	 * Creates a new buffer whose content is a shared subsequence of this
	 * buffer's content. The content of the new buffer will start at this
	 * buffer's current position. Changes to this buffer's content will be
	 * visible in the new buffer, and vice versa; the two buffers' position and
	 * limit values will be independent.<br>
	 * The new buffer's position will be zero, its capacity and its limit will
	 * be the number of bytes remaining in this buffer. The new buffer will be
	 * read-only if, and only if, this buffer is read-only. 
	 * @return The new buffer
	 */
	public DoubleHalfBuffer slice(){
		if(position >= firstLimit){
			secondBuffer.position(position - firstLimit);
			secondBuffer.limit(limit - firstLimit);
			DoubleHalfBuffer result = new DoubleHalfBuffer(ByteBuffer.wrap(new byte[0]), secondBuffer.slice(), readOnly);
			secondBuffer.limit(capacity - firstLimit);
			secondBuffer.rewind();
			return result;
		}
		else if(limit <= firstLimit){
			firstBuffer.position(position);
			firstBuffer.limit(limit);
			DoubleHalfBuffer result = new DoubleHalfBuffer(firstBuffer.slice(), ByteBuffer.wrap(new byte[0]), readOnly);
			firstBuffer.limit(firstLimit);
			firstBuffer.rewind();
			return result;
		}
		else{
			firstBuffer.position(position);
			secondBuffer.rewind();
			secondBuffer.limit(limit - firstLimit);
			DoubleHalfBuffer result = new DoubleHalfBuffer(firstBuffer.slice(), secondBuffer.slice(), readOnly);
			secondBuffer.limit(capacity - firstLimit);
			firstBuffer.rewind();
			return result;
		}
	}
	/**
	 * Get his buffer�s total capacity
	 * @return This buffer�s total capcity in bytes
	 */
	public int capacity(){
		return capacity;
	}
	/**
	 * Clear this buffer by resetting its position to zero and its limit to its
	 * capacity.
	 * @return This buffer
	 */
	public DoubleHalfBuffer clear(){
		position = 0;
		limit = capacity;
		return this;
	}
	/**
	 * Flips this buffer. The limit is set to the current position and then the
	 * position is set to zero. If the mark is defined then it is discarded.
	 * @return This buffer
	 */
	public DoubleHalfBuffer flip(){
		limit = position;
		position = 0;
		return this;
	}
	/**
	 * Tells whether there are any elements between the current position and the
	 * limit.
	 * @return true, if there is at least one byte left in the buffer, false
	 * otherwise
	 */
	public boolean hasRemaining(){
		return position < limit;
	}
	/**
	 * Tells whether this buffer is read-only or not.
	 * @return true, if this buffer is read-only, false otherwise
	 */
	public boolean isReadOnly(){
		return readOnly;
	}
	/**
	 * Get this buffer�s current limit
	 * @return This buffer�s current limit
	 */
	public int limit(){
		return limit;
	}
	/**
	 * Set this buffer�s limit
	 * @param limit The new limit
	 * @return This buffer
	 */
	public DoubleHalfBuffer limit(int limit){
		this.limit = limit;
		return this;
	}
	/**
	 * Get his buffer�s current position
	 * @return This buffer�s current position
	 */
	public int position(){
		return position;
	}
	/**
	 * Set this buffer�s position
	 * @param pos The new position
	 * @return This buffer
	 */
	public DoubleHalfBuffer position(int pos){
		position = pos;
		return this;
	}
	/**
	 * Get the number of bytes remaining between this buffer�s current position
	 * and its limit
	 * @return The number of bytes remining in this buffer
	 */
	public int remaining(){
		return Math.max(limit - position, 0);
	}
	/**
	 * Set this buffer�s position to zero
	 * @return This buffer
	 */
	public DoubleHalfBuffer rewind(){
		position = 0;
		return this;
	}
	public String toString() {
		return super.toString() + " cap: " + capacity + ", pos: " + position + ", lim: " + limit;
	}
}
