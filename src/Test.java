import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Test
{
    private final Sequence newSeq = new Sequence(0.0f,960);
    private final Track track = newSeq.createTrack();
    private final HashMap<Long,MidiEvent> hmap = new HashMap<>();

    public Test() throws Exception
    {
        JFrame frame = new JFrame("MIDI Drumbox");
        frame.setLayout(new GridLayout(11,1));
        for (int s=0; s<10; s++)
            frame.add(makeJP(s));
        frame.add (makeControlP());
        frame.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        frame.pack(); //setSize(650, 500);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }


    public static void main (String[] args) throws Exception
    {
        new Test();
    }

    private final String[] instrumentNames = new String[]
    {
            "27 High Q (GM2)",
            "28 Slap (GM2)",
            "29 Scratch Push (GM2)",
            "30 Scratch Pull (GM2)",
            "31 Sticks (GM2)",
            "32 Square Click (GM2)",
            "33 Metronome Click (GM2)",
            "34 Metronome Bell (GM2)",
            "35 Bass Drum 2",
            "36 Bass Drum 1",
            "37 Side Stick",
            "38 Snare Drum 1",
            "39 Hand Clap",
            "40 Snare Drum 2",
            "41 Low Tom 2",
            "42 Closed Hi-hat",
            "43 Low Tom 1",
            "44 Pedal Hi-hat",
            "45 Mid Tom 2",
            "46 Open Hi-hat",
            "47 Mid Tom 1",
            "48 High Tom 2",
            "49 Crash Cymbal 1",
            "50 High Tom 1",
            "51 Ride Cymbal 1",
            "52 Chinese Cymbal",
            "53 Ride Bell",
            "54 Tambourine",
            "55 Splash Cymbal",
            "56 Cowbell",
            "57 Crash Cymbal 2",
            "58 Vibra Slap",
            "59 Ride Cymbal 2",
            "60 High Bongo",
            "61 Low Bongo",
            "62 Mute High Conga",
            "63 Open High Conga",
            "64 Low Conga",
            "65 High Timbale",
            "66 Low Timbale",
            "67 High Agogo",
            "68 Low Agogo",
            "69 Cabasa",
            "70 Maracas",
            "71 Short Whistle",
            "72 Long Whistle",
            "73 Short Guiro",
            "74 Long Guiro",
            "75 Claves",
            "76 High Wood Block",
            "77 Low Wood Block",
            "78 Mute Cuica",
            "79 Open Cuica",
            "80 Mute Triangle",
            "81 Open Triangle",
            "82 Shaker (GM2)",
            "83 Jingle Bell (GM2)",
            "84 Belltree (GM2)",
            "85 Castanets (GM2)",
            "86 Mute Surdo (GM2)",
            "87 Open Surdo (GM2)"
    };

    private JPanel makeControlP ()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //panel.setMinimumSize(new Dimension(600,10));

        JButton b1 = new JButton("Save");
        panel.add(b1);
        b1.addActionListener(e ->
        {
            File f = new File("c:\\midfile.mid");
            try
            {
                MidiSystem.write(newSeq, 1, f);
            }
            catch (IOException e1)
            {
                System.out.println(e1);
            }
        });
        JButton b2 = new JButton("Play");
        panel.add(b2);
        b2.addActionListener(e ->
        {
            try
            {
                Sequencer sequencer = MidiSystem.getSequencer();
                sequencer.open();
                sequencer.setSequence(newSeq);
                Thread.sleep(500);
                sequencer.start();
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
        });

        return panel;
    }

    private void putEvent (long key, MidiEvent ev)
    {
        System.out.println("put: "+key);
        track.add(ev);
        hmap.put(key, ev);
    }

    private void delEvent (long key)
    {
        System.out.println("del: "+key);
        MidiEvent e1 = hmap.remove(key);
        if (e1 != null)
        {
            track.remove(e1);
        }
    }

    private void readFirstTwo(JComboBox combo, AtomicInteger instrument)
    {
        String s = ((String)combo.getSelectedItem()).substring(0,2);
        instrument.set(Integer.parseInt(s));
    }

    private JPanel makeJP (int tracknumber)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));

        UIManager.put("ToggleButton.select", Color.RED);

        AtomicInteger instrument = new AtomicInteger(-1);
        JComboBox combo = new JComboBox(instrumentNames);
        combo.setSelectedIndex(tracknumber);
        readFirstTwo(combo, instrument);
        panel.add(combo);
        combo.addActionListener(e ->
        {
            readFirstTwo(combo, instrument);
        });

        for (int s = 0; s < 32; s++)
        {
            JToggleButton jb = new JToggleButton();
            jb.setMnemonic(s);
            jb.setPreferredSize(new Dimension(20,20));
            jb.addActionListener(e ->
            {
                long tick = jb.getMnemonic()*200;
                long event_id = tracknumber*100+jb.getMnemonic()*2;
                long event_id2 = event_id+1;
                if (jb.isSelected())
                {
                    try
                    {
                        ShortMessage on = new ShortMessage(ShortMessage.NOTE_ON,
                                9, instrument.get(), 127);
                        ShortMessage off = new ShortMessage(ShortMessage.NOTE_OFF,
                                9, instrument.get(), 0);
                        putEvent (event_id, new MidiEvent(on, tick));
                        putEvent (event_id2, new MidiEvent(off, tick+200));
                    }
                    catch (Exception e1)
                    {
                        System.out.println(e1);
                    }
                }
                else
                {
                    delEvent(event_id);
                    delEvent(event_id2);
                }
                System.out.println(tracknumber+" -- "+jb.getMnemonic()+" -- "+jb.isSelected());
            });
            jb.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panel.add(jb);
        }
        return panel;
    }
}
