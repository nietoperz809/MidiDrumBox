import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Drumbox extends JPanel
{
    private final HashMap<Long,MidiEvent> noteMap = new HashMap<>();
    private static final int STEPS = 32;
    private static final int LINES = 10;
    private static final int NOTELENGTH = 40;

    static int instanceNumber = 0;

    /**
     * Constructor: Build complete frame and show it
     * @throws Exception If smth. gone wrong
     */
    public Drumbox () throws Exception
    {
        setLayout(new GridLayout(11,1));
        for (int s=0; s<LINES; s++)
            add(makeDrumLinePanel(s));
        add (makeControlPanel());
        setVisible(true);
        instanceNumber++;
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

    /**
     * Create control panel
     * @return The panel
     */
    private JPanel makeControlPanel ()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JSlider slider;
        slider = new JSlider();
        slider.setMinimum(100);
        slider.setMaximum(1000);
        slider.setMinorTickSpacing(25);
        slider.setMajorTickSpacing(100);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);

        JTextField loops = new JTextField();
        loops.setPreferredSize(new Dimension(30,30));
        loops.setText("1");

        JButton b1 = new JButton("Save");
        panel.add(b1);
        b1.addActionListener(e ->
        {
            Sequence sq = createMIDI(slider, loops);
            File f = new File("c:\\midfile.mid");
            try
            {
                MidiSystem.write(sq, 1, f);
                JOptionPane.showMessageDialog(this, "Saved to: "+f.getAbsolutePath(), "Drum Tool", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException e1)
            {
                JOptionPane.showMessageDialog(this, "Saving fail: "+e1, "Drum Tool", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton b2 = new JButton("Play");
        panel.add(b2);
        b2.addActionListener(e ->
        {
            Sequence sq = createMIDI(slider, loops);
            try
            {
                Sequencer sequencer = MidiSystem.getSequencer();
                sequencer.open();
                sequencer.setSequence(sq);
                Thread.sleep(100);
                sequencer.start();
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
        });

        panel.add(slider);

        panel.add (new JLabel("Loop:"));
        panel.add (loops);

        return panel;
    }

    /**
     * Put event into hashmap and track
     * @param key Event key
     * @param ev The Event himself
     */
    private void putEvent (long key, MidiEvent ev)
    {
        System.out.println("put: "+key);
        noteMap.put(key, ev);
    }

    /**
     * Delete Event from hashmap and track
     * @param key Event key
     */
    private void deleteEvent (long key)
    {
        MidiEvent e1 = noteMap.remove(key);
        if (e1 != null)
        {
            System.out.println("del: "+key);
        }
    }

    /**
     * Reads instrument number from selected Combobox item and store into Integer object
     * @param combo Source
     * @param instrument Destination
     */
    private void readFirstTwo(JComboBox combo, AtomicInteger instrument)
    {
        String s = ((String)combo.getSelectedItem()).substring(0,2);
        instrument.set(Integer.parseInt(s));
    }

    /**
     * Create a new Sequence that can be played or saved
     * @param slider Speed slider
     * @return The sequence
     */
    private Sequence createMIDI (JSlider slider, JTextField loops)
    {
        Sequence seq = null;
        try
        {
            seq = new Sequence(0.0f,960);
        }
        catch (InvalidMidiDataException e)
        {
            return null;
        }
        Track tr = seq.createTrack();
        for (int s=0; s<Integer.parseInt(loops.getText()); s++)
        {
            for (Map.Entry<Long, MidiEvent> e : noteMap.entrySet())
            {
                MidiEvent ev = e.getValue();
                ShortMessage msg = (ShortMessage) ev.getMessage();
                MidiEvent clone = new MidiEvent(msg, 0);
                long tick = (ev.getTick() + s*STEPS) * slider.getValue();
                if (msg.getCommand() == ShortMessage.NOTE_ON)
                {
                    clone.setTick(tick);
                }
                else
                {
                    clone.setTick(tick + NOTELENGTH);
                }
                tr.add(clone);
            }
        }
        return seq;
    }

    /**
     * Create Panel for one Instrument
     * @param lineNumber Y-coordinate of panel
     * @return The panel
     */
    private JPanel makeDrumLinePanel (int lineNumber)
    {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));

        AtomicInteger instrument = new AtomicInteger(-1);
        JComboBox<String> combo = new JComboBox<>(instrumentNames);
        combo.setSelectedIndex(lineNumber);
        readFirstTwo(combo, instrument);
        panel.add(combo);
        combo.addActionListener(e ->
        {
            readFirstTwo(combo, instrument);
        });

        JButton but = new JButton("Clear");
        panel.add(but);
        but.addActionListener(e ->
        {
            for (int i=2; i<(STEPS+2); i++)
            {
                JToggleButton b1 = (JToggleButton)panel.getComponent(i);
                if (b1.isSelected())
                    b1.doClick();
            }
        });

        for (int s = 0; s < STEPS; s++)
        {
            JToggleButton jb = new JToggleButton();
            jb.setMnemonic(s);
            jb.setPreferredSize(new Dimension(20,20));
            jb.addActionListener(e ->
            {
                long event_id = lineNumber*100+jb.getMnemonic()*2;
                long event_id2 = event_id+1;
                if (jb.isSelected())
                {
                    try
                    {
                        ShortMessage on = new ShortMessage(ShortMessage.NOTE_ON,
                                9, instrument.get(), 127);
                        ShortMessage off = new ShortMessage(ShortMessage.NOTE_OFF,
                                9, instrument.get(), 0);
                        putEvent (event_id, new MidiEvent(on, jb.getMnemonic()));
                        putEvent (event_id2, new MidiEvent(off, jb.getMnemonic()));
                    }
                    catch (Exception e1)
                    {
                        System.out.println(e1);
                    }
                }
                else
                {
                    deleteEvent(event_id);
                    deleteEvent(event_id2);
                }
            });
            jb.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            panel.add(jb);
        }
        return panel;
    }
}
