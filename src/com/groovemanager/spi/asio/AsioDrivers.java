/*
 * Created on 14.03.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This is a wrapper class for the native AsioDrivers class
 * @author Manu Robledo
 */
final class AsioDrivers extends NativeClass{
	/**
	 * Number of maximum expected drivers
	 */
	private final static int MAX_DRIVERS = 50;
	
	/**
	 * Constructs a new AsioDrivers instance and creates a
	 * corresponding native instance
	 */
	AsioDrivers(){
	}
	
	/**
	 * Constructs a new AsioDrivers instance that corresponds to an
	 * existing native instance, which is specified by the pointer
	 * @param pointer The native address of the native
	 * AsioDrivers instance
	 */
	AsioDrivers(long pointer){
		super(pointer);
	}
	
	/**
	 * Load the driver with the given name
	 * @param name The name of the driver that should be loaded.
	 * The name should be derived from getDriverNames()
	 * @return true if the driver could be loaded, false otherwise
	 */
	native boolean loadDriver(String name);
	
	private native int prepareDriverNames(int maxDrivers);
	private native String getDriverName(int i);
	
	/**
	 * Get the name of the currently loaded driver if any
	 * @return The name of the currently loaded driver or an
	 * empty String, if no driver is loaded.
	 */
	native String getCurrentDriverName();
	/**
	 * Get the index of the currently loaded driver if any
	 * @return The index of the currently loaded driver or -1
	 * if no driver is loaded.
	 */
	native int getCurrentDriverIndex();
	/**
	 * Unload the currently loaded driver if any
	 */
	native void removeCurrentDriver();

	/**
	 * Get the names of all installed ASIO drivers for user
	 * selection
	 */
	String[] getDriverNames(){
		int size = prepareDriverNames(MAX_DRIVERS);
		String[] names = new String[size];
		for (int i = 0; i < names.length; i++) {
			names[i] = getDriverName(i);
		}
		return names;
	}

	protected native long createClass();
	protected native void cleanUp();
}