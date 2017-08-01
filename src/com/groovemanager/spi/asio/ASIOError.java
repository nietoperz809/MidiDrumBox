/*
 * Created on 08.04.2004
 *
 */
package com.groovemanager.spi.asio;

/**
 * This class is a wrapper for the ASIOError constants
 * It extends Exceptions so everytime a native method returns an 
 * ASIOError, which is not ASE_OK or ASE_SUCCESS an ASIOError 
 * will be thrown
 * @author Manu Robledo
 */
public final class ASIOError extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ASIOError constants
	 */
	final static int
		ASE_OK,
		ASE_SUCCESS,
		ASE_NotPresent,
		ASE_HWMalfunction,
		ASE_InvalidParameter,
		ASE_InvalidMode,
		ASE_SPNotAdvancing,
		ASE_NoClock,
		ASE_NoMemory;
	/**
	 * The ASIOError constant of this error
	 */
	private int code;
	
	/**
	 * Get the ASIOError constant of this ASIOError
	 * @return The ASIOError constant which caused this ASIOError
	 */
	int getCode(){
		return code;
	}
	
	static{
		System.loadLibrary(ASIOMixerProvider.getLibName());
		ASE_OK = ASE_OK();
		ASE_SUCCESS = ASE_SUCCESS();
		ASE_NotPresent = ASE_NotPresent();
		ASE_HWMalfunction = ASE_HWMalfunction();
		ASE_InvalidParameter = ASE_InvalidParameter();
		ASE_InvalidMode = ASE_InvalidMode();
		ASE_SPNotAdvancing = ASE_SPNotAdvancing();
		ASE_NoClock = ASE_NoClock();
		ASE_NoMemory = ASE_NoMemory();
	}
	
	/**
	 * Private constructor. Should only be called from
	 * ASIOError.throwASIOError()
	 * @param code The ASIOError constant
	 */
	private ASIOError(int code) {
		super("ASIO Error: " + getMessage(code));
		this.code = code;
	}
	
	private static native int ASE_OK();
	private static native int ASE_SUCCESS();
	private static native int ASE_NotPresent();
	private static native int ASE_HWMalfunction();
	private static native int ASE_InvalidParameter();
	private static native int ASE_InvalidMode();
	private static native int ASE_SPNotAdvancing();
	private static native int ASE_NoClock();
	private static native int ASE_NoMemory();
	
	/**
	 * Get a qualified error message for a given ASIOError constant
	 * @param code The ASIOError constant
	 * @return A corresponding error message
	 */
	static String getMessage(int code){
		if(code == ASE_OK) return "Everything OK.";
		else if(code == ASE_SUCCESS) return "Success.";
		else if(code == ASE_NotPresent) return "Hardware Input or Output is not present or available.";
		else if(code == ASE_HWMalfunction) return "Hardware is malfunctioning";
		else if(code == ASE_InvalidParameter) return "Input parameter invalid.";
		else if(code == ASE_InvalidMode) return "Hardware is in a bad mode or used in a bad mode.";
		else if(code == ASE_SPNotAdvancing) return "Hardware is not running when sample position is inquired.";
		else if(code == ASE_NoClock) return "Sample clock or rate cannot be determined or is not present.";
		else if(code == ASE_NoMemory) return "Not enough memory for completing the request.";
		else return "Unknown Error Code: " + code;
	}
	
	/**
	 * Throw an ASIOError if the constant is not ASE_OK and not ASE_SUCCESS
	 * @param code The ASIOError constant
	 * @throws ASIOError if the given constant is not a success constant
	 */
	static void throwASIOError(int code)
	{
		if (code != ASE_OK && code != ASE_SUCCESS)
		{
			System.out.println(new ASIOError(code));
		}
	}
}