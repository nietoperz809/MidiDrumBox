import com.sun.media.sound.AudioSynthesizer;

import javax.sound.midi.*;


/**
 * Experimental player
 */

public class RealtimePlayer
{
    AudioSynthesizer synthesizer;
    Sequencer sequencer;
    Sequence sequence;
    Instrument instruments[];
    MidiChannel cc;
    private final ShortMessage msg = new ShortMessage();
    private Receiver receiver = null;

    public RealtimePlayer ()
    {
        try
        {
            synthesizer = (AudioSynthesizer) MidiSystem.getSynthesizer();
            receiver = synthesizer.getReceiver();
            synthesizer.open();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

//
//        try
//        {
//            if (synthesizer == null)
//            {
//                if ((synthesizer = MidiSystem.getSynthesizer()) == null)
//                {
//                    System.out.println("getSynthesizer() failed!");
//                    return;
//                }
//            }
//            recv = synthesizer.getReceiver();
//            synthesizer.open();
//            sequencer = MidiSystem.getSequencer();
//            sequence = new Sequence(Sequence.PPQ, 940);
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//            return;
//        }
//
//        Soundbank sb = synthesizer.getDefaultSoundbank();
//        if (sb != null)
//        {
//            instruments = synthesizer.getDefaultSoundbank().getInstruments();
//            synthesizer.loadInstrument(instruments[0]);
//        }
//        MidiChannel midiChannels[] = synthesizer.getChannels();
//        cc = midiChannels[9];
//        cc.setMono(true);
//        cc.setSolo(true);
    }

    public void setInstrument(int instr)
    {
        instr--;
        Instrument in = instruments[instr];
        synthesizer.loadInstrument(in);
        cc.programChange(in.getPatch().getBank(), in.getPatch().getProgram());
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

//        cc.noteOn(note, 127);
//        try
//        {
//            Thread.sleep(50);
//        }
//        catch (InterruptedException e)
//        {
//            System.out.println(e);
//        }
//        cc.noteOff(note);
    }
}
