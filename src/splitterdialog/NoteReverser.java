package splitterdialog;

import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoteReverser
{
    public NoteReverser (String infile, String outpath, Modus mod) throws Exception
    {
        Sequence sequence = MidiSystem.getSequence(new File(infile));
        Sequence newSeq = new Sequence(sequence.getDivisionType(), sequence.getResolution());
        System.out.println(sequence.getDivisionType()+"--"+sequence.getResolution());
        for (Track track : sequence.getTracks())
        {
            Track tr2 = newSeq.createTrack();
            List<MidiEvent> noteList = new ArrayList<>();
            for (int i = 0; i < track.size(); i++)
            {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage)
                {
                    ShortMessage sm = (ShortMessage) message;
                    int cmd = sm.getCommand();
                    if (cmd == ShortMessage.NOTE_ON || cmd == ShortMessage.NOTE_OFF)
                    {
                        int key = sm.getData1();
                        noteList.add(0, event);
                        tr2.add(event);
                    }
                }
            }
            if (tr2.size() == 0)
            {
                newSeq.deleteTrack(tr2);
            }
            else if (mod == Modus.REV_TRACK)
            {
                reverse (newSeq, tr2, noteList);
            }
            else
            {
                for (int i = 0; i < tr2.size(); i++)
                {
                    MidiEvent event = tr2.get(i);
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage)
                    {
                        ShortMessage msg = (ShortMessage) message;
                        if (mod == Modus.REV_OCTAVE)
                        {
                            int key = msg.getData1();
                            int octave = (key / 16);
                            int note = (key % 16)^15;
                            msg.setMessage(msg.getStatus(), msg.getChannel(),
                                    16*octave+note, msg.getData2());
                        }
                        else if (mod == Modus.SWAP3)
                        {
                            int key = msg.getData1();
                            int octave = (key / 12);
                            int note = (key % 12)^7;
                            msg.setMessage(msg.getStatus(), msg.getChannel(),
                                    12*octave+note, msg.getData2());
                        }
                    }
                }
            }
        }
        File f = new File(outpath + "\\midifile-" +
                System.currentTimeMillis() + "-.mid");
        MidiSystem.write(newSeq, 1, f);
    }

    private void reverse (Sequence seq, Track in, List<MidiEvent> noteList) throws Exception
    {
        Track out = seq.createTrack();
        for (int i = 0; i < noteList.size(); i++)
        {
            MidiEvent event = noteList.get(i);
            MidiMessage message = event.getMessage();
            ShortMessage shrt = (ShortMessage)message;

            long newtick = noteList.get(noteList.size()-1-i).getTick();
            event.setTick(newtick);

//            int cmd = shrt.getCommand();
//            if (cmd == ShortMessage.NOTE_OFF)
//                cmd = ShortMessage.NOTE_ON;
//            else if (cmd == ShortMessage.NOTE_ON)
//                cmd = ShortMessage.NOTE_OFF;
//            shrt.setMessage(cmd, shrt.getChannel(), shrt.getData1(), shrt.getData2());

            out.add(event);
        }
        seq.deleteTrack(in);
    }

    enum Modus
    {
        REV_TRACK,
        REV_OCTAVE,
        SWAP3
    }
}
