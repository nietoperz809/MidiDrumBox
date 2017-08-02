import com.sun.media.sound.AudioSynthesizer;

import javax.sound.midi.*;


/**
 * Experimental player
 */

public class RealtimePlayer
{
    // --Commented out by Inspection (8/2/2017 9:20 PM):private Instrument instruments[];
    // --Commented out by Inspection (8/2/2017 9:20 PM):private MidiChannel cc;
    private final ShortMessage msg = new ShortMessage();
    private Receiver receiver = null;

    public RealtimePlayer ()
    {
        try
        {
            AudioSynthesizer synthesizer = (AudioSynthesizer) MidiSystem.getSynthesizer();
            //instruments = synthesizer.getDefaultSoundbank().getInstruments();
            receiver = synthesizer.getReceiver();
            synthesizer.open();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void setInstrument(int instr)
    {
//        instr--;
//        Instrument in = instruments[instr];
//        synthesizer.loadInstrument(in);
        try
        {
            msg.setMessage(ShortMessage.PROGRAM_CHANGE, 9,
                    instr-1, 0);
            receiver.send (msg, 0);
        }
        catch (InvalidMidiDataException e)
        {
            System.out.println(e);
        }
    }

    public void play(int note)
    {
        try
        {
            msg.setMessage(ShortMessage.NOTE_ON, 9, note, 127);
            receiver.send(msg, 0);
            msg.setMessage(ShortMessage.NOTE_ON, 9, note, 0);
            receiver.send(msg, 100);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
