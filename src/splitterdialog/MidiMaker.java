package splitterdialog;

import javax.sound.midi.*;
import java.io.File;

class MidiMaker
{
    private static final String[] NOTE_NAMES =
            {
                    "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
            };

    private final SplitterConfig cfg;

    public MidiMaker (SplitterConfig config)
    {
        cfg = config;
    }

    public void perform (boolean notes_only) throws Exception
    {
        Sequence sequence = MidiSystem.getSequence(new File(cfg.inputFile));
        Splitter splitter = new Splitter(sequence, cfg);

        int trackNumber = 0;
        for (Track track : sequence.getTracks())
        {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();
            for (int i = 0; i < track.size(); i++)
            {
                MidiEvent event = track.get(i);
                System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage)
                {
                    ShortMessage sm = (ShortMessage) message;
                    int channel = sm.getChannel() + 1;
                    int cmd = sm.getCommand();
                    int velocity = sm.getData2();
                    int key = sm.getData1();
                    int octave = (key / 12) - 1;
                    int note = key % 12;
                    String noteName = NOTE_NAMES[note];
                    System.out.print("Channel: " + channel + " ");
                    if (!notes_only && cmd == ShortMessage.POLY_PRESSURE)
                    {
                        splitter.insert(event, channel);
                        System.out.println("poly pressure");
                    }
                    else if (!notes_only && cmd == ShortMessage.CHANNEL_PRESSURE)
                    {
                        splitter.insert(event, channel);
                        System.out.println("pitch bend");
                    }
                    else if (!notes_only && cmd == ShortMessage.PITCH_BEND)
                    {
                        splitter.insert(event, channel);
                        System.out.println("pitch bend");
                    }
                    else if (cmd == ShortMessage.NOTE_ON && velocity > 0)
                    {
                        splitter.insert(event, channel);
                        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    }
                    else if (cmd == ShortMessage.NOTE_OFF || (cmd == ShortMessage.NOTE_ON && velocity == 0))
                    {
                        splitter.insert(event, channel);
                        System.out.println("Note off, " + noteName + octave + " key=" + key);
                    }
                    else
                    {
                        System.out.println("Command:" + sm.getCommand());
                    }
                }
                else
                {
                    if (message instanceof MetaMessage)
                    {
                        MetaMessage mm = (MetaMessage) message;
                        String ms = new String(mm.getData());
                        System.out.println("MetaMsg: " + ms);
                    }
                    else
                    {
                        System.out.println("Other message: " + message.getClass());
                    }
                }
            }
            System.out.println();
        }
        splitter.save(cfg.outputDir);
    }
}
