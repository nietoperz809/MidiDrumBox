/*
 * Created on 18.04.2004
 *
 */
package com.groovemanager.spi.asio;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class serves as ServiceProvider for accessing ASIODrivers from
 * Java Sound
 * @author Manu Robledo
 */
public class ASIOMixerProvider extends MixerProvider{
	
	
	/** 
	 * speed improvement
	 * enables/disables fullCheck
	 * useful for realtime use, if all parameters are safe!
	 * 
	 * must be enabled on inquiries to get full information!
	 * 
	 * @author Jurgen Schmitz - Aug. 2006
	 */
	private static boolean fullCheck = true;
	

	
	/**
	 * The pool of Mixers. Should guarantee that for each ASIO driver there
	 * exists only one ASIOMixer instance 
	 */
	private static HashMap mixerPool = new HashMap();
	/**
	 * AsioDrivers object for accessing driver infos
	 */
	private static AsioDrivers asioDrivers = new AsioDrivers();
	/**
	 * The currently loaded ASIO driver if any
	 */
	private static ASIOMixer activeMixer;
	/**
	 * ASIODriverInfo of the currently loaded Mixer if any 
	 */
	private static ASIODriverInfo asioDriverInfo;
	/**
	 * Last error message if any 
	 */
	private static String errorMessage = "";
	/**
	 * The last used ASIOBufferInfos
	 */
	private static ASIOBufferInfo[] bufferInfos;
	/**
	 * The available ASIO drivers
	 */
	private static String[] drivers;
	/**
	 * The Info objects for the available ASIO drivers
	 */
	private static Info[] infos;
	/**
	 * Sequence number that will be incremented and returned for each ASIOInit()
	 * call 
	 */
	private static long sequence = 1;
	/**
	 * The sequence number that was actually used for the last real ASIOInit()
	 * call
	 */
	private static long initSeq = 0;
	/**
	 * List of all registered ASIOListeners
	 */
	private static ArrayList asioListeners = new ArrayList();
	/**
	 * The version of the jsasio library version
	 */
	final static String LIB_VERSION = "1.1";
	/**
	 * The name of the jsasio library
	 */
	final static String LIB_NAME = "jsasio";
	
	static{
		freeAll();
	}
	
	public static void setFullCheck(boolean fullCheck) {
		ASIOMixerProvider.fullCheck = fullCheck;
	}

	public static boolean isFullCheck() {
		return fullCheck;
	}

	/**
	 * Get the full library name to be loaded
	 * @return The library name consisting of LIB_NAME + "_" + LIB_VERSION
	 */
	static String getLibName()
	{
		//return "jsasio";
		return "jsasio_1.1";
	}


	/**
	 * Constructs a new ASIOMixerProvider
	 *
	 */
	public ASIOMixerProvider(){
	}
	
	/**
	 * This method tries to free all ASIO-related ressources. Should only be
	 * needed in special cases.
	 *
	 */
	public static void freeAll(){
		asioDrivers.removeCurrentDriver();
		String[] drivers = asioDrivers.getDriverNames();
		for (int i = 0; i < drivers.length; i++) {
			asioDrivers.loadDriver(drivers[i]);
			asioDrivers.removeCurrentDriver();
		}
		activeMixer = null;
		initSeq = 0;
	}

	/**
	 * 
	 * @see MixerProvider#isMixerSupported(Info)
	 */
	public boolean isMixerSupported(Info i){
		return i instanceof ASIOMixerInfo;
	}

	/**
	 * Get the currently loaded ASIOMixer if any
	 * @return The currently loaded ASIOMixer or null if none is loaded
	 */
	static ASIOMixer getActiveMixer(){
		return activeMixer;
	}
	
	/**
	 * Get the last used ASIOBufferInfos
	 * @return Array of the last used ASIOBufferInfos or null if none are used
	 */
	static ASIOBufferInfo[] getBufferInfo(){
		return bufferInfos;
	}
	
	/**
	 * Get the next sequence number
	 * @return The next sequence number
	 */
	private static long nextSeq(){
		if(sequence == -1 || sequence == 0) sequence = 1;
		return sequence++;
	}
	
	/**
	 * 
	 * @see MixerProvider#getMixerInfo()
	 */
	public Info[] getMixerInfo() {
		// Namen der installierten ASIO-Treiber erfragen
		if(drivers == null) drivers = asioDrivers.getDriverNames();
		// Mixer.Info-Array vorbereiten
		if(infos == null) infos = new Info[drivers.length];
		else return infos;
		
		String version;
		ASIODriverInfo info;
		
		for (int i = 0; i < drivers.length; i++) {
			version = null;
			
			if(fullCheck) {
			// Treiber muss geladen werden, um an die Version ranzukommen
				asioDrivers.loadDriver(drivers[i]);
				try {
					// Version ermitteln
					info = ASIOStaticFunctions.ASIOInit();
					version = "" + info.driverVersion();
					
				} catch (ASIOError e) {
					if(version == null)	version = "Unknown";
				}
					infos[i] = new ASIOMixerInfo(drivers[i], version);
					asioDrivers.removeCurrentDriver();
			}
			else {
				infos[i] = new ASIOMixerInfo(drivers[i], "Not Checked");
			}
		}
		return infos;
	}
	
	/**
	 * 
	 * @see MixerProvider#getMixer(Info)
	 */
	@SuppressWarnings("unchecked")
	public Mixer getMixer(Info info) {
		// Ist das �berhaupt eine ASIOInfo?
		ASIOMixerInfo i;
		try{
			i = (ASIOMixerInfo)info;
		}
		catch(ClassCastException e){
			throw new IllegalArgumentException("Not a valid ASIO Mixer");
		}
		
		// Schauen, ob wir den Mixer schon in unserem Pool haben
		ASIOMixer m = (ASIOMixer)mixerPool.get(i.getDriverName());
		if(m == null){
			// Neuen ASIOMixer erzeugen...
			m = new ASIOMixer(i);
			addASIOListener(m);
			// ... und im Pool speichern
			mixerPool.put(i.getDriverName(), m);
		}
		return m;
	}
	
	/**
	 * Get the last error message.
	 * @return The last error message or an empty String if no error occured.
	 */
	public static String getErrorMessage(){
		return errorMessage;
	}

	/**
	 * Get the ASIODriverInfo of the currently loaded driver if any.
	 * @return The ASIODriverInfo of the currently loaded driver or null if
	 * no driver is loaded
	 */
	static ASIODriverInfo getDriverInfo(){
		return asioDriverInfo;
	}
	
	/**
	 * Try to load an ASIO driver if needed
	 * @param mixer The ASIOMixer to load
	 * @return true if the driver could be loaded or had already been loaded,
	 * false otherwise
	 */
	private static boolean ASIOLoad(ASIOMixer mixer){
		if(activeMixer != null && activeMixer != mixer){
			errorMessage = "Another ASIO Driver is currently active.";
			return false;
		}
		if(activeMixer == mixer && mixer.getStatus() >= ASIOMixer.LOADED) return true;
		
		// load ASIO driver
		if(!asioDrivers.loadDriver(((ASIOMixerInfo)mixer.getMixerInfo()).getDriverName())){
			errorMessage = "ASIOLoad: Unable to load ASIO Mixer Driver.";
			return false;
		}
		
		// On success:
		activeMixer = mixer;
		mixer.setStatus(ASIOMixer.LOADED);
		return true;
	}
	
	/**
	 * Initialize and load an ASIO driver if needed
	 * @param mixer The ASIO driver to initialize
	 * @return true if the driver could be initialized or had already been
	 * initialized, false otherwise
	 * @throws ASIOError If ASIOInit() fails
	 */
	static long ASIOInit(ASIOMixer mixer) throws ASIOError{
		if(!ASIOLoad(mixer)) return -1;
		if(mixer.getStatus() >= ASIOMixer.INITIALIZED) return nextSeq();
		
		try{
			asioDriverInfo = ASIOStaticFunctions.ASIOInit();
			mixer.setDriverInfo(asioDriverInfo);
			mixer.setStatus(ASIOMixer.INITIALIZED);
			initSeq = nextSeq();
		}
		catch(ASIOError e){
			activeMixer = null;
			mixer.setStatus(ASIOMixer.UNLOADED);
			throw e;
		}
		return initSeq;
	}
	
	/**
	 * Prepare, initialize and load an ASIO driver if needed
	 * @param mixer The ASIO driver to prepare
	 * @param infos The ASIOBufferInfo objects that should be filled
	 * @param buffersize The buffer size in sample frames
	 * @return A sequence number for the use with ASIOExit()
	 * @throws ASIOError If ASIOCreateBuffers() fails
	 */
	static long ASIOPrepare(ASIOMixer mixer, ASIOBufferInfo[] infos, int buffersize) throws ASIOError{
		long seq = ASIOInit(mixer);
		if(seq == -1) return -1;
		if(mixer.getStatus() >= ASIOMixer.PREPARED) return seq;
		
		bufferInfos = ASIOStaticFunctions.ASIOCreateBuffers(infos, buffersize);
		mixer.setStatus(ASIOMixer.PREPARED);
		return seq;
	}
	
	/**
	 * Start, prepare, init and load an ASIO driver if needed
	 * @param mixer The ASIO driver to start
	 * @param infos The ASIOBufferInfos to be filled
	 * @param buffersize The buffersize
	 * @return A sequence number to be used with ASIOExit()
	 * @throws ASIOError If ASIOStart() fails
	 */
	static long ASIOStart(ASIOMixer mixer, ASIOBufferInfo[] infos, int buffersize) throws ASIOError{
		long seq = ASIOPrepare(mixer, infos, buffersize); 
		if(seq == -1) return -1;
		if(mixer.getStatus() == ASIOMixer.RUNNING) return seq;
		
		mixer.setStatus(ASIOMixer.RUNNING);
		try{
			ASIOStaticFunctions.ASIOStart();
		}
		catch(ASIOError e){
			mixer.setStatus(ASIOMixer.PREPARED);
			throw e;
		}
		return seq;
	}
	
	/**
	 * Stop an ASIO driver if needed
	 * @param mixer The ASIO driver that should be stopped
	 * @return true If the Mixer was already stopped before or if
	 * the Mixer could be stopped
	 * @throws ASIOError If ASIOStop() failed
	 */
	static boolean ASIOStop(ASIOMixer mixer) throws ASIOError{
		if(activeMixer != mixer){
			errorMessage = "Another ASIO Driver is currently active.";
			return false;
		}
		if(mixer.getStatus() <= ASIOMixer.PREPARED) return true;
		
		ASIOStaticFunctions.ASIOStop();
		mixer.setStatus(ASIOMixer.PREPARED);
		return true;
	}
	
	/**
	 * Dispose buffers and Stop the driver if needed
	 * @param mixer The ASIO driver to be stopped
	 * @return true if this Mixer�s buffers had already been disposed or
	 * if the dispose succeeded
	 * @throws ASIOError If ASIODisposeBuffers failed
	 */
	static boolean ASIOUnPrepare(ASIOMixer mixer) throws ASIOError{
		if(!ASIOStop(mixer)) return false;
		if(mixer.getStatus() <= ASIOMixer.INITIALIZED) return true;
		
		ASIOStaticFunctions.ASIODisposeBuffers();
		mixer.setStatus(ASIOMixer.INITIALIZED);
		return true;
	}

	/**
	 * Exit this driver
	 * @param mixer The ASIO driver
	 * @param seq The sequence number from the call to ASIOInit(). If this
	 * sequence number is the one provided from the real ASIOinit() call,
	 * ASIOExit() will really be called, otherwise not
	 */
	static void ASIOExit(ASIOMixer mixer, long seq){
		if(mixer == activeMixer && mixer != null && mixer.getStatus() >= ASIOMixer.LOADED && seq == initSeq) 
			ASIOUnLoad(mixer);
	}
	
	/**
	 * Unload an ASIO driver
	 * @param mixer The ASIO driver
	 */
	private static void ASIOUnLoad(ASIOMixer mixer){
		try {
			ASIOUnPrepare(mixer);
		} catch (ASIOError e) {
			e.printStackTrace();
		}
		asioDrivers.removeCurrentDriver();
		activeMixer = null;
		mixer.setStatus(ASIOMixer.UNLOADED);
	}
	
	/**
	 * Info-class for ASIOMixer instances
	 * @author Manu Robledo
	 */
	public static class ASIOMixerInfo extends Info{
		/**
		 * Name of the ASIO driver
		 */
		private String driverName;
		/**
		 * Constructs a new ASIOInfo
		 * @param name The driver name
		 * @param version The driver version
		 */
		private ASIOMixerInfo(String name, String version) {
			super(name + " (ASIO)", "Unknown", name + " ASIO Driver", version);
			driverName = name;
		}
		/**
		 * Get the Name of the ASIO driver.
		 * @return
		 * 	The Name of the ASIO driver.
		 */
		public String getDriverName(){
			return driverName;
		}
	}
	
	/**
	 * Called from the ASIO callback asioMessage() 
	 * @param selector The selector to be supported
	 * @return true if this selector is supported, false otherwise
	 */
	private static boolean selectorSupported(int selector){
		if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioEngineVersion ||
				selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioResetRequest ||
				selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioSelectorSupported ||
				selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioSupportsTimeCode ||
				selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioSupportsTimeInfo) return true;
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioBufferSizeChange) return bufferSizeChangedSupported();
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioLatenciesChanged) return latenciesChangedSupported();
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioResyncRequest) return resyncRequestSupported();
		else return false;
	}
	private static boolean bufferSizeChangedSupported(){
		for (Iterator iter = asioListeners.iterator(); iter.hasNext();) {
			ASIOListener listener = (ASIOListener) iter.next();
			if(listener.supportsBufferSizeChange()) return true;
		}
		return false;
	}
	private static boolean latenciesChangedSupported(){
		for (Iterator iter = asioListeners.iterator(); iter.hasNext();) {
			ASIOListener listener = (ASIOListener) iter.next();
			if(listener.supportsLatenciesChanged()) return true;
		}
		return false;
	}
	private static boolean resyncRequestSupported(){
		for (Iterator iter = asioListeners.iterator(); iter.hasNext();) {
			ASIOListener listener = (ASIOListener) iter.next();
			if(listener.supportsResyncRequest()) return true;
		}
		return false;
	}
	private static void resetRequest(ASIOMixer mixer){
		for (Iterator iter = asioListeners.iterator(); iter.hasNext();) {
			ASIOListener listener = (ASIOListener) iter.next();
			listener.resetRequest(mixer);
		}
	}
	private static boolean bufferSizeChanged(int newSize){
		boolean accepted = false;
		for (Iterator iter = asioListeners.iterator(); iter.hasNext();) {
			ASIOListener listener = (ASIOListener) iter.next();
			if(listener.supportsBufferSizeChange()) accepted = accepted || listener.bufferSizeChanged(activeMixer, newSize);
		}
		return accepted;
	}
	private static boolean resyncRequest(){
		boolean accepted = false;
		for (Iterator iter = asioListeners.iterator(); iter.hasNext();) {
			ASIOListener listener = (ASIOListener) iter.next();
			if(listener.supportsResyncRequest()) accepted = accepted || listener.resyncRequest(activeMixer);
		}
		return accepted;
	}
	private static boolean latenciesChanged(){
		boolean accepted = false;
		for (Iterator iter = asioListeners.iterator(); iter.hasNext();) {
			ASIOListener listener = (ASIOListener) iter.next();
			if(listener.supportsLatenciesChanged()) accepted = accepted || listener.latenciesChanged(activeMixer);
		}
		return accepted;
	}
	
	@SuppressWarnings("unused")
	private static int asioMessage(int selector, int value){
		if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioSelectorSupported){
			if(selectorSupported(value)) return 1;
			else return 0;
		}
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioEngineVersion) return 2;
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioResetRequest){
			ASIOMixer active = activeMixer;
			ASIOExit(activeMixer, initSeq);
			resetRequest(active);
			return 1;
		}
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioBufferSizeChange){
			if(bufferSizeChanged(value)) return 1;
			else return 0;
		}
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioResyncRequest){
			if(resyncRequest()) return 1;
			else return 0;
		}
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioLatenciesChanged){
			if(latenciesChanged()) return 1;
			else return 0;			 
		}
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioSupportsTimeInfo) return 1;
		else if(selector == ASIOStaticFunctions.MESSAGE_SELECTOR_kAsioSupportsTimeCode) return 0;
		else return 0;
	}
	/**
	 * Register an ASIOListener that will be notified of callbacks from any
	 * ASIOMixer
	 * @param listener The ASIOListener to be added
	 */
	@SuppressWarnings("unchecked")
	public void addASIOListener(ASIOListener listener){
		asioListeners.add(listener);
	}
	/**
	 * Removes a registered ASIOListener
	 * @param listener The ASIOListener to be removed
	 */
	public void removeASIOListener(ASIOListener listener){
		asioListeners.remove(listener);
	}
	@SuppressWarnings("unused")
	private void jSampleRateDidChange(long pointer){
		sampleRateDidChange(new ASIOSampleRate(pointer));
	}
	private void sampleRateDidChange(ASIOSampleRate newRate){
		for (Iterator iter = asioListeners.iterator(); iter.hasNext();) {
			ASIOListener listener = (ASIOListener) iter.next();
			listener.sampleRateChanged(activeMixer, newRate.doubleValue());
		}
	}
	
}