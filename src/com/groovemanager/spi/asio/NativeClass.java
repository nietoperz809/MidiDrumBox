/*
 * Created on 08.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * An abstract class that can be used for Wrapper classes of native classes
 * or structs
 * @author Manu Robledo
 */
abstract class NativeClass {
	static{
		System.loadLibrary(ASIOMixerProvider.getLibName());
	}
	/**
	 * The native address of the native instance
	 */
	private long pointer;
	/**
	 * Indicates whether a new native instance was created with this class
	 * or not. Is needed for finalize() 
	 */
	private boolean selfCreated = false;
	
	/**
	 * Create a new instance of this class and also a corresponding new
	 * native instance
	 */
	NativeClass() {
		pointer = createClass();
		selfCreated = true;
	}
	
	/**
	 * Create an instance of this class to an already existing native
	 * instance
	 * @param pointer The address of the native instance
	 */
	NativeClass(long pointer){
		this.pointer = pointer;
	}
	
	/**
	 * Create a new native instance and return its address
	 * @return The address of the new created native instance
	 */
	protected abstract long createClass();
	/**
	 * Delete the corresponding native instance to this class
	 */
	protected abstract void cleanUp();
	
	/**
	 * Get the address of the native instance corresponding to this class
	 * @return The address of the native instance
	 */
	long getPointer(){
		return pointer;
	}

	/**
	 * If the native instance was created by this class, then delete it now!
	 */
	public void finalize(){
		if(selfCreated) cleanUp();
	}
}