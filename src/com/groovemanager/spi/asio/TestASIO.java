package com.groovemanager.spi.asio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Test class for jsASIO
 * 
 * @author Manu Robledo
 */
public class TestASIO {
	private final static int
		HELP = 0,
		LIST = 1,
		INFO = 2,
		TEST = 3,
		PLAY = 4;
	private int action = HELP;
	private int mixerIndex = 0;
	private String filename;
	private int bufferSize;
	private int lineIndex;
	private double sampleRate = 44100.0;

	/**
	 * Main method
	 * - -mixer=[0...n]
	 * - -line=[0...n]
	 * - -source=[filename]
	 * - -samplerate=[rate]
	 * - -buffersize=[size]
	 * 
	 * @param args
	 */
	public static void main(String[] lala)
	{
		TestASIO test = new TestASIO();

		String[] args = {"-t"};

		// Detect action
		test.action = parseAction(args);
		// Continue depending on action
		switch(test.action){
			case HELP:
				test.printUsage();
				return;
			case LIST:
				test.listMixers();
				return;
			case INFO:
				test.mixerIndex = parseMixerIndex(args);
				test.listLines();
				return;
			case TEST:
				test.mixerIndex = parseMixerIndex(args);
				test.sampleRate = parseSampleRate(args);
				test.testASIO();
				return;
			case PLAY:
				test.mixerIndex = parseMixerIndex(args);
				test.lineIndex = parseLineIndex(args);
				test.filename = parseSourceFile(args);
				test.bufferSize = parseBufferSize(args);
				test.playFile();
				return;
		}
	}
	private static int parseAction(String[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if(arg.equals("-?") || arg.equals("--help") || arg.equals("-action=help")) return HELP;
			else if(arg.equals("-l") || arg.equals("--list") || arg.equals("-action=list")) return LIST;
			else if(arg.equals("-i") || arg.equals("--info") || arg.equals("-action=info")) return INFO;
			else if(arg.equals("-t") || arg.equals("--test") || arg.equals("-action=test")) return TEST;
			else if(arg.equals("-p") || arg.equals("--play") || arg.equals("-action=play")) return PLAY;
		}
		return HELP;
	}
	private static int parseMixerIndex(String[] args){
		for(int i = 0; i < args.length; i++){
			String arg = args[i];
			if(arg.startsWith("-mixer=")) return Integer.parseInt(arg.substring(7));
		}
		return 0;
	}
	private static int parseLineIndex(String[] args){
		for(int i = 0; i < args.length; i++){
			String arg = args[i];
			if(arg.startsWith("-line=")) return Integer.parseInt(arg.substring(6));
		}
		return 0;
	}
	private static int parseBufferSize(String[] args){
		for(int i = 0; i < args.length; i++){
			String arg = args[i];
			if(arg.startsWith("-buffersize=")) return Integer.parseInt(arg.substring(12));
		}
		return -1;
	}
	private static String parseSourceFile(String[] args){
		for(int i = 0; i < args.length; i++){
			String arg = args[i];
			if(arg.startsWith("-source=")) return arg.substring(8);
		}
		return "";
	}
	private static double parseSampleRate(String[] args){
		for(int i = 0; i < args.length; i++){
			String arg = args[i];
			if(arg.startsWith("-samplerate=")) return Double.parseDouble(arg.substring(12));
		}
		return 44100.0;
	}
	private void printUsage(){
		System.out.println("jsASIO Test Program\nPossible arguments:\n");
		System.out.println("  -action=help | -? | --help\nPrint this screen (Default)");
		System.out.println("  -action=list | -l | --list\nList available ASIO mixers");
		System.out.println("  -action=info | -i | --info\nPrint info about available lines on one ASIO mixer");
		System.out.println("  -action=play | -p | --play\nPlay a test file on one of the available ASIO mixers");
		System.out.println();
		System.out.println("  -mixer=[0...n]\nSelect the index of the ASIO mixer to use for <info> or <play> from the list given by <list> (Default: 0)");
		System.out.println("  -line=[0...n]\nSelect the index of the line to use for <play> from the list given by <info> (Default: 0)");
		System.out.println("  -source=[filename]\nPath to the test audio file to play");
	}
	private void listMixers(){
		ASIOMixerProvider provider = new ASIOMixerProvider();
		Mixer.Info[] mixers = provider.getMixerInfo();
		if(mixers.length == 0) System.out.println("No ASIO mixers found on your system.");
		else for(int i = 0; i < mixers.length; i++){
			System.out.println(i + ": " + mixers[i]);
		}
	}
	private void listLines(){
		ASIOMixerProvider provider = new ASIOMixerProvider();
		Mixer.Info[] mixers = provider.getMixerInfo();
		if(mixers.length == 0) System.out.println("No ASIO mixers found on your system.");
		else if(mixerIndex >= mixers.length) System.out.println("Index " + mixerIndex + " out of range.");
		else{
			System.out.println("Mixer: " + mixers[mixerIndex]);
			System.out.println();
			Mixer mixer = provider.getMixer(mixers[mixerIndex]);
			Line.Info[] targetInfo = mixer.getTargetLineInfo();
			Line.Info[] sourceInfo = mixer.getSourceLineInfo();
			if(targetInfo.length > 0){
				System.out.println("Recording lines:");
				for(int i = 0; i < targetInfo.length; i++){
					System.out.println(i + ": " + targetInfo[i]);
					AudioFormat[] formats = ((DataLine.Info)targetInfo[i]).getFormats();
					System.out.println("Supported formats:");
					for(int j = 0; j < formats.length; j++){
						System.out.println("  " + formats[j]);
					}
				}
				System.out.println();
			}
			else System.out.println("No recording lines found");
			if(sourceInfo.length > 0){
				System.out.println("Playback lines:");
				for(int i = 0; i < sourceInfo.length; i++){
					System.out.println(i + ": " + sourceInfo[i]);
					AudioFormat[] formats = ((DataLine.Info)sourceInfo[i]).getFormats();
					System.out.println("Supported formats:");
					for(int j = 0; j < formats.length; j++){
						System.out.println("  " + formats[j]);
					}
				}
				System.out.println();
			}
			else System.out.println("No playback lines found");
		}
	}
	private void testASIO(){
		ASIOMixerProvider provider = new ASIOMixerProvider();
		Mixer.Info[] mixers = provider.getMixerInfo();
		if(mixers.length == 0) System.out.println("No ASIO mixers found on your system.");
		else if(mixerIndex >= mixers.length) System.out.println("Index " + mixerIndex + " out of range.");
		else{
			System.out.println("Mixer: " + mixers[mixerIndex]);
			System.out.println();
			ASIOMixer mixer = (ASIOMixer)provider.getMixer(mixers[mixerIndex]);
			try {
				mixer.open();
				System.out.println("Calling ASIOControlPanel()...");
				ASIOStaticFunctions.ASIOControlPanel();
				System.out.print("Calling ASIOCanSampleRate(" + sampleRate + ")... ");
				if(ASIOStaticFunctions.ASIOCanSampleRate(sampleRate)) System.out.println("Yes");
				else System.out.println("No");
				System.out.print("Calling ASIOGetClockSources... ");
				ASIOClockSource[] sources = ASIOStaticFunctions.ASIOGetClockSources(100);
				System.out.println(sources.length + " sources found");
				for(int i = 0; i < sources.length; i++) System.out.println("  " + sources[i]);
				System.out.println("Calling ASIOOutputReady()...");
				ASIOStaticFunctions.ASIOOutputReady();
				System.out.print("Trying to enable Time code reader... ");
				try{
					ASIOStaticFunctions.enableTimeCodeReader();
					System.out.println("successful.");
				}
				catch(ASIOError e){
					System.out.println("not supported.");
				}
				System.out.print("Trying to disable Time code reader... ");
				try{
					ASIOStaticFunctions.disableTimeCodeReader();
					System.out.println("successful.");
				}
				catch(ASIOError e){
					System.out.println("not supported.");
				}
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (ASIOError e) {
				e.printStackTrace();
			}
		}
	}
	private void playFile(){
		ASIOMixerProvider provider = new ASIOMixerProvider();
		Mixer.Info[] mixers = provider.getMixerInfo();
		if(mixers.length == 0) System.out.println("No ASIO mixers found on your system.");
		else if(mixerIndex >= mixers.length) System.out.println("Index " + mixerIndex + " out of range.");
		else{
			System.out.println("Mixer: " + mixers[mixerIndex]);
			System.out.println();
			ASIOMixer mixer = (ASIOMixer)provider.getMixer(mixers[mixerIndex]);
			Line.Info[] lineInfos = mixer.getSourceLineInfo();
			if(lineInfos.length == 0){
				System.out.println("Error: No playback lines found.");
				return;
			}
			if(lineIndex >= lineInfos.length){
				System.out.println("Error: Line index " + lineIndex + " out of range.");
				return;
			}
			ASIODataLine line;
			try {
				line = (ASIODataLine)mixer.getLine(lineInfos[lineIndex]);
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
				return;
			}
			if(bufferSize == -1) bufferSize = mixer.getPreferredBufferSize();
			if(filename == null || filename.length() == 0) System.out.println("Error: No filename given.");
			else{
				File file = new File(filename);
				if(!file.exists()) System.out.println("Error: File " + filename + " doesn�t exist.");
				else{
					try {
						AudioInputStream is = AudioSystem.getAudioInputStream(file);
						AudioFormat targetFormat = is.getFormat();
						System.out.println("Source audio format: " + targetFormat);
						
						ASIOLineInfo info = (ASIOLineInfo)lineInfos[lineIndex];
						if(!info.isFormatSupported(targetFormat)){
							System.out.println("Format " + targetFormat + " not supported by selected line.");
							System.out.println("Looking for available conversions...");
							AudioFormat[] formats = info.getFormats();
							boolean supported = false;
							for(int i = 0; i < formats.length && !supported; i++){
								if(AudioSystem.isConversionSupported(formats[i], targetFormat)){
									System.out.println("Conversion found for " + formats[i]);
									is = AudioSystem.getAudioInputStream(formats[i], is);
									targetFormat = formats[i];
									supported = true;
								}
								else System.out.println("No conversion to " + formats[i] + " found.");
							}
							if(!supported){
								System.out.println("Error: Conversion to a supported format not possible.");
								return;
							}
						}
						line.open(targetFormat, bufferSize);
						byte[] buffer = new byte[8192];
						int read = 0;
						while(read != -1){
							read = is.read(buffer, 0, buffer.length);
							if(read != -1){
								line.start();
								line.write(buffer, 0, read);
							}
						}
						is.close();
						// TODO: drain() debuggen
						line.drain();
						line.stop();
						line.close();
						ASIOMixerProvider.freeAll();
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
						ASIOMixerProvider.freeAll();
						return;
					} catch (IOException e) {
						e.printStackTrace();
						ASIOMixerProvider.freeAll();
						return;
					} catch (LineUnavailableException e) {
						e.printStackTrace();
						ASIOMixerProvider.freeAll();
						return;
					}
				}
			}
		}
	}
}
